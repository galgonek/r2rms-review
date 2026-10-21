package cz.iocb.nextprot.load;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import cz.iocb.nextprot.load.NeXtProt.Server;



/*
 * Inserts rows into a table in batches. The loaders store no row twice: before a row with a natural primary key is
 * set, isNew() tells whether its key is new, also across the loads that fill the same table, such as the loads of the
 * chromosome files. A row whose key contains an id assigned by the loader, such as the id of a name, needs no check.
 */
public class Table implements AutoCloseable
{
    private static final int batchSize = 10000;
    private static final HashMap<String, Keys> tableKeys = new HashMap<>();

    private final PreparedStatement statement;
    private final Keys keys;
    private int count = 0;


    /*
     * Prepares the insertion into the table given as its name followed by the list of its columns.
     */
    public Table(Connection connection, Server type, String table) throws SQLException
    {
        String name = table.replaceFirst("[ (].*", "");

        System.out.println("   store " + name);

        StringBuilder values = new StringBuilder(" values(?");

        for(int i = 0; i < table.length(); i++)
            if(table.charAt(i) == ',')
                values.append(",?");

        values.append(")");

        String sql = switch(type)
        {
            case POSTGRESQL, MARIADB -> "insert into nextprot." + table + values;
            case VIRTUOSO -> "insert into nextprot.nextprot." + table + values;
        };

        statement = connection.prepareStatement(sql);
        keys = tableKeys.computeIfAbsent(name, _ -> new Keys());
    }


    /*
     * Records the key of a row and tells whether it is new, i.e. whether the row is to be stored.
     */
    public boolean isNew(int key)
    {
        return keys.add(Integer.toUnsignedLong(key));
    }


    /*
     * Records the key of a row consisting of two integers and tells whether it is new.
     */
    public boolean isNew(int key1, int key2)
    {
        return keys.add((long) key1 << 32 | Integer.toUnsignedLong(key2));
    }


    /*
     * Records the key of a row consisting of an integer and a literal and tells whether it is new.
     */
    public boolean isNew(int key, String literal)
    {
        return isNew(key, keys.getLiteralId(literal));
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
            keys.finish();
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
