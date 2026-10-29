package cz.iocb.nextprot.load;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import cz.iocb.nextprot.load.NeXtProt.Server;



/*
 * Stores the rows into a database connected by JDBC, in batches. The rows are stored in one transaction, except in
 * Virtuoso, which commits every statement.
 */
public class JdbcTarget implements Target
{
    private static final int batchSize = 10000;

    private final Connection connection;
    private final Server type;


    public JdbcTarget(String url, Server type) throws SQLException
    {
        this.connection = DriverManager.getConnection(url);
        this.type = type;

        if(type != Server.VIRTUOSO)
            connection.setAutoCommit(false);
    }


    @Override
    public Sink sink(String table) throws SQLException
    {
        return new JdbcSink(table);
    }


    @Override
    public void commit() throws SQLException
    {
        if(type != Server.VIRTUOSO)
            connection.commit();
    }


    @Override
    public void close() throws SQLException
    {
        connection.close();
    }


    private class JdbcSink implements Sink
    {
        private final PreparedStatement statement;
        private int count = 0;


        private JdbcSink(String table) throws SQLException
        {
            StringBuilder values = new StringBuilder(" values(?");

            for(int i = 1; i < Table.getColumnCount(table); i++)
                values.append(",?");

            values.append(")");

            String sql = switch(type)
            {
                case POSTGRESQL, MARIADB -> "insert into nextprot." + table + values;
                case VIRTUOSO -> "insert into nextprot.nextprot." + table + values;
            };

            statement = connection.prepareStatement(sql);
        }


        @Override
        public void add(Object[] values) throws SQLException
        {
            for(int i = 0; i < values.length; i++)
                statement.setObject(i + 1, values[i]);

            statement.addBatch();

            if(++count % batchSize == 0)
                statement.executeBatch();
        }


        @Override
        public void close() throws SQLException
        {
            try
            {
                if(count % batchSize != 0)
                    statement.executeBatch();
            }
            finally
            {
                statement.close();
            }
        }
    }
}
