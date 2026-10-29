package cz.iocb.nextprot.load;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.jena.graph.Node;
import cz.iocb.nextprot.load.Target.Sink;



/*
 * Inserts rows into a table of all the targets. The loaders store no row twice: before a row with a natural primary
 * key is set, isNew() tells whether its key is new, also across the loads that fill the same table, such as the loads
 * of the chromosome files. A row whose key contains an id assigned by the loader, such as the id of a name, needs no
 * check.
 *
 * The rows are given as in the indirect mode, and the targets of the direct mode get them without the column iri, which
 * is the last column of a table that has it. A column referring to an entity is set by the node of the entity, as it
 * holds the local part of the IRI in the direct mode and the id of the IRI in the indirect mode.
 */
public class Table implements AutoCloseable
{
    private static final HashMap<String, Keys> tableKeys = new HashMap<>();
    private static final Object unset = new Object();

    private final String table;
    private final Keys keys;
    private final Sink[] directSinks;
    private final Sink[] indirectSinks;
    private final int directColumns;
    private final Object[] direct;
    private Object[] indirect;


    /*
     * Prepares the insertion into the table given as its name followed by the list of its columns in the indirect
     * mode.
     */
    Table(String table, List<Target> directTargets, List<Target> indirectTargets) throws IOException, SQLException
    {
        String name = getName(table);
        String directTable = table.replaceFirst(",\\s*iri\\)$", ")");
        int columns = getColumnCount(table);

        System.out.println("   store " + name);

        this.table = table;
        this.keys = tableKeys.computeIfAbsent(name, _ -> new Keys());
        this.directColumns = getColumnCount(directTable);

        directSinks = new Sink[directTargets.size()];

        for(int i = 0; i < directSinks.length; i++)
            directSinks[i] = directTargets.get(i).sink(directTable);

        indirectSinks = new Sink[indirectTargets.size()];

        for(int i = 0; i < indirectSinks.length; i++)
            indirectSinks[i] = indirectTargets.get(i).sink(table);

        direct = newRow(columns);
        indirect = newRow(columns);
    }


    /*
     * Returns the name of the table given as its name followed by the list of its columns.
     */
    static String getName(String table)
    {
        return table.replaceFirst("[ (].*", "");
    }


    /*
     * Returns the number of the columns of the table given as its name followed by the list of its columns.
     */
    static int getColumnCount(String table)
    {
        int count = 1;

        for(int i = 0; i < table.length(); i++)
            if(table.charAt(i) == ',')
                count++;

        return count;
    }


    private static Object[] newRow(int length)
    {
        Object[] row = new Object[length];
        Arrays.fill(row, unset);

        return row;
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


    public void setValue(int idx, Object value)
    {
        direct[idx - 1] = value;
        indirect[idx - 1] = value;
    }


    /*
     * Sets the value of a column referring to the entity given by its node, or to no entity for a null node.
     */
    public void setValue(int idx, Ids ids, Node node) throws IOException
    {
        if(directSinks.length > 0)
            direct[idx - 1] = node == null ? null : ids.getLocalPart(node);

        if(indirectSinks.length > 0)
            indirect[idx - 1] = node == null ? null : ids.getId(node);
    }


    /*
     * Adds the row whose values have been set.
     */
    public void add() throws IOException, SQLException
    {
        if(directSinks.length > 0)
        {
            Object[] row = new Object[directColumns];

            for(int i = 0; i < row.length; i++)
                row[i] = checkValue(direct, i);

            for(Sink sink : directSinks)
                sink.add(row);

            Arrays.fill(direct, unset);
        }

        if(indirectSinks.length > 0)
        {
            for(int i = 0; i < indirect.length; i++)
                checkValue(indirect, i);

            for(Sink sink : indirectSinks)
                sink.add(indirect);

            indirect = newRow(indirect.length);
        }
    }


    /*
     * Returns the value of a column of the row, which must have been set.
     */
    private Object checkValue(Object[] row, int column)
    {
        if(row[column] == unset)
            throw new IllegalStateException("value of column " + (column + 1) + " not set: " + table);

        return row[column];
    }


    @Override
    public void close() throws IOException, SQLException
    {
        try
        {
            for(Sink sink : directSinks)
                sink.close();

            for(Sink sink : indirectSinks)
                sink.close();
        }
        finally
        {
            keys.finish();
        }
    }
}
