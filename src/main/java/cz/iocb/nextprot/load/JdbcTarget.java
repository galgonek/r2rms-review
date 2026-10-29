package cz.iocb.nextprot.load;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
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
    public Table table(String table) throws SQLException
    {
        return new JdbcTable(table);
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


    private class JdbcTable extends Table
    {
        private final PreparedStatement statement;
        private int count = 0;


        private JdbcTable(String table) throws SQLException
        {
            super(table);

            StringBuilder values = new StringBuilder(" values(?");

            for(int i = 1; i < getColumnCount(table); i++)
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
        public void add() throws SQLException
        {
            statement.addBatch();

            if(++count % batchSize == 0)
                statement.executeBatch();
        }


        @Override
        protected void finish() throws SQLException
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


        @Override
        public void setValue(int idx, Object value) throws SQLException
        {
            statement.setObject(idx, value);
        }


        @Override
        public void setValue(int idx, int value) throws SQLException
        {
            statement.setInt(idx, value);
        }


        @Override
        public void setValue(int idx, Integer value) throws SQLException
        {
            if(value != null)
                statement.setInt(idx, value);
            else
                statement.setNull(idx, Types.INTEGER);
        }


        @Override
        public void setValue(int idx, Double value) throws SQLException
        {
            if(value != null)
                statement.setDouble(idx, value);
            else
                statement.setNull(idx, Types.DOUBLE);
        }


        @Override
        public void setValue(int idx, String value) throws SQLException
        {
            if(value != null)
                statement.setString(idx, value);
            else
                statement.setNull(idx, Types.VARCHAR);
        }
    }
}
