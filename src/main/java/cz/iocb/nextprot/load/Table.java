package cz.iocb.nextprot.load;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import cz.iocb.nextprot.load.NeXtProt.Server;



/*
 * Inserts rows into a table in batches; a row that is already present, by its primary key, is skipped.
 */
public class Table implements AutoCloseable
{
    private static final int batchSize = 10000;

    private final PreparedStatement statement;
    private int count = 0;


    /*
     * Prepares the insertion into the table given as its name followed by the list of its columns.
     */
    public Table(Connection connection, Server type, String table) throws SQLException
    {
        System.out.println("   store " + table.replaceFirst("\\(.*", ""));

        StringBuilder values = new StringBuilder(" values(?");

        for(int i = 0; i < table.length(); i++)
            if(table.charAt(i) == ',')
                values.append(",?");

        values.append(")");

        String sql = switch(type)
        {
            case POSTGRESQL -> "insert into nextprot." + table + values + " on conflict do nothing";
            case VIRTUOSO -> "insert soft nextprot.nextprot." + table + values;
            case MARIADB -> "insert ignore into nextprot." + table + values;
        };

        statement = connection.prepareStatement(sql);
    }


    /*
     * Adds the row whose values have been set.
     */
    public void add() throws SQLException
    {
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


    public void setValue(int idx, Object value) throws SQLException
    {
        statement.setObject(idx, value);
    }


    public void setValue(int idx, int value) throws SQLException
    {
        statement.setInt(idx, value);
    }


    public void setValue(int idx, Integer value) throws SQLException
    {
        if(value != null)
            statement.setInt(idx, value);
        else
            statement.setNull(idx, Types.INTEGER);
    }


    public void setValue(int idx, Double value) throws SQLException
    {
        if(value != null)
            statement.setDouble(idx, value);
        else
            statement.setNull(idx, Types.DOUBLE);
    }


    public void setValue(int idx, String value) throws SQLException
    {
        if(value != null)
            statement.setString(idx, value);
        else
            statement.setNull(idx, Types.VARCHAR);
    }
}
