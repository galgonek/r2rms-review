package cz.iocb.nextprot.load;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.LinkedHashSet;
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
 */
public abstract class SqlTarget implements Target
{
    private final Writer writer;
    private final int blockSize;


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
    public Table table(String table)
    {
        return new SqlTable(table);
    }


    @Override
    public void commit() throws IOException
    {
        writeCommit();
        writer.flush();
    }


    @Override
    public void close() throws IOException
    {
        writer.close();
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


    private class SqlTable extends Table
    {
        private final String table;
        private final String[] values;
        private final StringBuilder rows = new StringBuilder();


        private SqlTable(String table)
        {
            super(table);

            this.table = table;
            this.values = new String[getColumnCount(table)];
        }


        @Override
        public void add() throws IOException
        {
            for(int i = 0; i < values.length; i++)
                if(values[i] == null)
                    throw new IllegalStateException("value of column " + (i + 1) + " not set: " + table);

            appendRow(rows, values);
            Arrays.fill(values, null);

            if(rows.length() >= blockSize)
                flush();
        }


        @Override
        protected void finish() throws IOException
        {
            if(!rows.isEmpty())
                flush();
        }


        private void flush() throws IOException
        {
            writeBlock(table, rows);
            rows.setLength(0);
        }


        @Override
        public void setValue(int idx, Object value) throws IOException
        {
            values[idx - 1] = getLiteral(value);
        }


        @Override
        public void setValue(int idx, int value)
        {
            values[idx - 1] = Integer.toString(value);
        }


        @Override
        public void setValue(int idx, Integer value) throws IOException
        {
            values[idx - 1] = getLiteral(value);
        }


        @Override
        public void setValue(int idx, Double value) throws IOException
        {
            values[idx - 1] = getLiteral(value);
        }


        @Override
        public void setValue(int idx, String value) throws IOException
        {
            values[idx - 1] = getLiteral(value);
        }
    }
}
