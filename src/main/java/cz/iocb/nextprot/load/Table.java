package cz.iocb.nextprot.load;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;



/*
 * Inserts rows into a table of the target. The loaders store no row twice: before a row with a natural primary key is
 * set, isNew() tells whether its key is new, also across the loads that fill the same table, such as the loads of the
 * chromosome files. A row whose key contains an id assigned by the loader, such as the id of a name, needs no check.
 */
public abstract class Table implements AutoCloseable
{
    private static final HashMap<String, Keys> tableKeys = new HashMap<>();

    private final Keys keys;


    /*
     * Prepares the insertion into the table given as its name followed by the list of its columns.
     */
    protected Table(String table)
    {
        String name = getName(table);

        System.out.println("   store " + name);

        keys = tableKeys.computeIfAbsent(name, _ -> new Keys());
    }


    /*
     * Returns the name of the table given as its name followed by the list of its columns.
     */
    protected static String getName(String table)
    {
        return table.replaceFirst("[ (].*", "");
    }


    /*
     * Returns the number of the columns of the table given as its name followed by the list of its columns.
     */
    protected static int getColumnCount(String table)
    {
        int count = 1;

        for(int i = 0; i < table.length(); i++)
            if(table.charAt(i) == ',')
                count++;

        return count;
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
    public abstract void add() throws IOException, SQLException;


    /*
     * Stores the rows that have been added and not stored yet and releases the resources of the table.
     */
    protected abstract void finish() throws IOException, SQLException;


    @Override
    public void close() throws IOException, SQLException
    {
        try
        {
            finish();
        }
        finally
        {
            keys.finish();
        }
    }


    public abstract void setValue(int idx, Object value) throws IOException, SQLException;


    public abstract void setValue(int idx, int value) throws IOException, SQLException;


    public abstract void setValue(int idx, Integer value) throws IOException, SQLException;


    public abstract void setValue(int idx, Double value) throws IOException, SQLException;


    public abstract void setValue(int idx, String value) throws IOException, SQLException;
}
