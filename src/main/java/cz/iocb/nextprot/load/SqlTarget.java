package cz.iocb.nextprot.load;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.zip.GZIPOutputStream;
import cz.iocb.nextprot.load.NeXtProt.Server;



/*
 * Writes the rows as SQL statements into a file, gzipped if its name ends with .gz, in the form that the client of the
 * given kind of database server loads the fastest. The rows of a table are written in blocks, each stored by a single
 * statement: by copy in PostgreSQL, by an insert of all the rows of the block in MariaDB, and by a call of a procedure
 * inserting the rows given as a JSON array in Virtuoso. The clients read the files as follows:
 *
 *   psql -v ON_ERROR_STOP=1 -U nextprot -d nextprot -f nextprot.sql
 *   mariadb -u nextprot -p nextprot < nextprot.sql
 *   isql 1111 nextprot password VERBOSE=OFF < nextprot.sql
 *
 * The files for PostgreSQL and MariaDB store the rows in a single transaction. Virtuoso commits every statement, as
 * when the rows are stored by JDBC.
 *
 * The rows are handed over in batches to a thread of the file, which writes the statements, so that writing several
 * files at once does not hold up the reading of the data.
 */
public abstract class SqlTarget implements Target
{
    private static final int batchSize = 1000;
    private static final int pendingBatches = 64;

    private final Writer writer;
    private final int blockSize;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Semaphore pending = new Semaphore(pendingBatches);
    private volatile Throwable failure;


    /*
     * Opens the file, whose blocks of rows are to be written once they have reached the given number of characters.
     */
    protected SqlTarget(String file, int blockSize) throws IOException
    {
        OutputStream output = new FileOutputStream(file);

        if(file.endsWith(".gz"))
            output = new GZIPOutputStream(output, 65536);

        this.writer = new BufferedWriter(new OutputStreamWriter(output, UTF_8), 65536);
        this.blockSize = blockSize;
    }


    /*
     * Opens the file of SQL statements for the given kind of database server.
     */
    public static SqlTarget create(String file, Server type) throws IOException
    {
        return switch(type)
        {
            case POSTGRESQL -> new PostgresqlTarget(file);
            case MARIADB -> new MariadbTarget(file);
            case VIRTUOSO -> new VirtuosoTarget(file);
        };
    }


    @Override
    public Sink sink(String table)
    {
        return new SqlSink(table);
    }


    @Override
    public void commit() throws IOException
    {
        submit(() -> {
            writeCommit();
            writer.flush();
        });
    }


    /*
     * Closes the file once the thread has written everything submitted.
     */
    @Override
    public void close() throws IOException
    {
        executor.close();
        writer.close();
        checkFailure();
    }


    /*
     * Submits a task to the thread of the file, which runs the tasks in the order of their submission. The thread is
     * given only a limited number of tasks in advance, so that the rows do not pile up in memory.
     */
    private void submit(Task task) throws IOException
    {
        checkFailure();
        pending.acquireUninterruptibly();

        executor.execute(() -> {
            try
            {
                if(failure == null)
                    task.run();
            }
            catch(Throwable throwable)
            {
                failure = throwable;
            }
            finally
            {
                pending.release();
            }
        });
    }


    /*
     * Reports the failure of a task run by the thread of the file.
     */
    private void checkFailure() throws IOException
    {
        if(failure instanceof IOException exception)
            throw new IOException(exception.getMessage(), exception);
        else if(failure != null)
            throw new IOException("writing of the SQL file failed", failure);
    }


    protected void write(CharSequence text) throws IOException
    {
        writer.append(text);
    }


    /*
     * Writes the statements making the stored rows permanent.
     */
    protected abstract void writeCommit() throws IOException;


    /*
     * Writes the statements storing a block of rows into the table given as its name followed by the list of its
     * columns.
     */
    protected abstract void writeBlock(String table, CharSequence rows) throws IOException;


    /*
     * Appends a row given by the literals of its values to a block of rows.
     */
    protected abstract void appendRow(StringBuilder rows, String[] values);


    protected abstract String getNullLiteral();


    protected abstract String getStringLiteral(String value) throws IOException;


    protected abstract String getDoubleLiteral(double value) throws IOException;


    protected abstract String getBooleanLiteral(boolean value);


    /*
     * Returns the literal of a finite double written with an exponent, so that it is read as a double rather than as a
     * decimal number.
     */
    protected static String getFiniteDoubleLiteral(double value) throws IOException
    {
        if(!Double.isFinite(value))
            throw new IOException("double not expressible in SQL: " + value);

        String literal = Double.toString(value);

        return literal.indexOf('E') < 0 ? literal + "E0" : literal;
    }


    /*
     * Returns the literal of a value of a column.
     */
    private String getLiteral(Object value) throws IOException
    {
        return switch(value)
        {
            case null -> getNullLiteral();
            case String string -> getStringLiteral(string);
            case Integer integer -> integer.toString();
            case Double number -> getDoubleLiteral(number);
            case Boolean bool -> getBooleanLiteral(bool);
            default -> throw new IOException("value of unexpected type: " + value);
        };
    }


    /*
     * Writes the rows as the data of copy statements in the text format, in a single transaction.
     */
    private static class PostgresqlTarget extends SqlTarget
    {
        private PostgresqlTarget(String file) throws IOException
        {
            super(file, 1 << 20);

            write("set client_encoding to 'UTF8';\nbegin;\n");
        }


        @Override
        protected void writeCommit() throws IOException
        {
            write("commit;\n");
        }


        @Override
        protected void writeBlock(String table, CharSequence rows) throws IOException
        {
            write("copy nextprot." + table + " from stdin;\n");
            write(rows);
            write("\\.\n");
        }


        @Override
        protected void appendRow(StringBuilder rows, String[] values)
        {
            rows.append(String.join("\t", values)).append('\n');
        }


        @Override
        protected String getNullLiteral()
        {
            return "\\N";
        }


        @Override
        protected String getStringLiteral(String value) throws IOException
        {
            if(value.indexOf('\0') >= 0)
                throw new IOException("string with the NUL character: " + value);

            return value.replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n").replace("\r", "\\r");
        }


        @Override
        protected String getDoubleLiteral(double value)
        {
            return Double.toString(value);
        }


        @Override
        protected String getBooleanLiteral(boolean value)
        {
            return value ? "t" : "f";
        }
    }


    /*
     * Writes the rows as inserts of several rows, in a single transaction. The strings are written with the escape
     * sequences of the default SQL mode.
     */
    private static class MariadbTarget extends SqlTarget
    {
        private MariadbTarget(String file) throws IOException
        {
            super(file, 1 << 20);

            write("set names utf8mb4;\nstart transaction;\n");
        }


        @Override
        protected void writeCommit() throws IOException
        {
            write("commit;\n");
        }


        @Override
        protected void writeBlock(String table, CharSequence rows) throws IOException
        {
            write("insert into nextprot." + table + " values");
            write(rows);
            write(";\n");
        }


        @Override
        protected void appendRow(StringBuilder rows, String[] values)
        {
            if(!rows.isEmpty())
                rows.append(',');

            rows.append('(').append(String.join(", ", values)).append(')');
        }


        @Override
        protected String getNullLiteral()
        {
            return "NULL";
        }


        @Override
        protected String getStringLiteral(String value)
        {
            return "'" + value.replace("\\", "\\\\").replace("'", "\\'").replace("\0", "\\0").replace("\n", "\\n")
                    .replace("\r", "\\r") + "'";
        }


        @Override
        protected String getDoubleLiteral(double value) throws IOException
        {
            return getFiniteDoubleLiteral(value);
        }


        @Override
        protected String getBooleanLiteral(boolean value)
        {
            return value ? "1" : "0";
        }
    }


    /*
     * Writes the rows as calls of procedures that insert the rows given as a JSON array, one procedure per table,
     * created before its first call and dropped at the commit. A block is kept far below 28 kB, as the larger
     * statements take tens of milliseconds more. isql must not expand the $ macros in the strings.
     */
    private static class VirtuosoTarget extends SqlTarget
    {
        private final LinkedHashSet<String> procedures = new LinkedHashSet<>();


        private VirtuosoTarget(String file) throws IOException
        {
            super(file, 16000);

            write("SET MACRO_SUBSTITUTION OFF;\n");
        }


        @Override
        protected void writeCommit() throws IOException
        {
            for(String procedure : procedures)
                write("drop procedure " + procedure + ";\n");

            procedures.clear();

            write("commit work;\n");
        }


        @Override
        protected void writeBlock(String table, CharSequence rows) throws IOException
        {
            String procedure = "nextprot.nextprot.load_" + Table.getName(table);

            if(procedures.add(procedure))
            {
                String[] values = new String[Table.getColumnCount(table)];

                for(int i = 0; i < values.length; i++)
                    values[i] = "r[i][" + i + "]";

                write("create procedure " + procedure + " (in s varchar) { declare r any; declare i integer; "
                        + "r := json_parse(s); for(i := 0; i < length(r); i := i + 1) insert into nextprot.nextprot."
                        + table + " values(" + String.join(", ", values) + "); };\n");
            }

            write(procedure + "('[");
            write(rows);
            write("]');\n");
        }


        @Override
        protected void appendRow(StringBuilder rows, String[] values)
        {
            if(!rows.isEmpty())
                rows.append(',');

            rows.append('[').append(String.join(",", values)).append(']');
        }


        @Override
        protected String getNullLiteral()
        {
            return "null";
        }


        /*
         * Returns the JSON string escaped for an SQL string literal, in which a backslash is an escape character.
         */
        @Override
        protected String getStringLiteral(String value)
        {
            StringBuilder literal = new StringBuilder(value.length() + 2).append('"');

            for(int i = 0; i < value.length(); i++)
            {
                char c = value.charAt(i);

                switch(c)
                {
                    case '"' -> literal.append("\\\\\"");
                    case '\\' -> literal.append("\\\\\\\\");
                    case '\'' -> literal.append("''");
                    default ->
                    {
                        if(c < 0x20)
                            literal.append("\\\\u").append(Integer.toHexString(c | 0x10000), 1, 5);
                        else
                            literal.append(c);
                    }
                }
            }

            return literal.append('"').toString();
        }


        @Override
        protected String getDoubleLiteral(double value) throws IOException
        {
            return getFiniteDoubleLiteral(value);
        }


        @Override
        protected String getBooleanLiteral(boolean value)
        {
            return value ? "1" : "0";
        }
    }


    /*
     * A task run by the thread of the file.
     */
    @FunctionalInterface
    private interface Task
    {
        void run() throws IOException;
    }


    /*
     * Hands the rows over to the thread of the file, which collects them into blocks.
     */
    private class SqlSink implements Sink
    {
        private final String table;
        private final StringBuilder block = new StringBuilder();
        private List<Object[]> batch = new ArrayList<>(batchSize);


        private SqlSink(String table)
        {
            this.table = table;
        }


        @Override
        public void add(Object[] values) throws IOException
        {
            batch.add(values);

            if(batch.size() == batchSize)
                submitBatch();
        }


        @Override
        public void close() throws IOException
        {
            if(!batch.isEmpty())
                submitBatch();

            submit(() -> {
                if(!block.isEmpty())
                    writeBlock();
            });
        }


        private void submitBatch() throws IOException
        {
            List<Object[]> rows = batch;
            batch = new ArrayList<>(batchSize);

            submit(() -> {
                for(Object[] values : rows)
                {
                    String[] literals = new String[values.length];

                    for(int i = 0; i < values.length; i++)
                        literals[i] = getLiteral(values[i]);

                    appendRow(block, literals);

                    if(block.length() >= blockSize)
                        writeBlock();
                }
            });
        }


        private void writeBlock() throws IOException
        {
            SqlTarget.this.writeBlock(table, block);
            block.setLength(0);
        }
    }
}
