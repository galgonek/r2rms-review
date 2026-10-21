package cz.iocb.nextprot.load;

import java.util.Arrays;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;
import org.eclipse.collections.impl.set.mutable.primitive.LongHashSet;



/*
 * The primary keys of the rows stored into a table by all the loads, so that no row is stored twice: the keys of the
 * finished loads are kept in a sorted array and the keys of the current load in a hash set, which is merged into the
 * array when the load finishes. A key is a long built from one or two integers; a literal part of a key is replaced by
 * an integer assigned to it on its first use.
 */
class Keys
{
    private long[] stored = new long[0];
    private LongHashSet current = new LongHashSet();
    private final ObjectIntHashMap<String> literals = new ObjectIntHashMap<>();


    /*
     * Records the key and tells whether it is new, i.e. whether no row with the key has been stored yet.
     */
    boolean add(long key)
    {
        if(Arrays.binarySearch(stored, key) >= 0)
            return false;

        return current.add(key);
    }


    /*
     * Returns the integer that stands for the literal in the keys.
     */
    int getLiteralId(String literal)
    {
        int id = literals.getIfAbsent(literal, -1);

        if(id == -1)
        {
            id = literals.size();
            literals.put(literal, id);
        }

        return id;
    }


    /*
     * Merges the keys of the current load into the keys of the finished loads.
     */
    void finish()
    {
        if(current.isEmpty())
            return;

        long[] added = current.toSortedArray();
        long[] merged = new long[stored.length + added.length];
        current = new LongHashSet();

        int i = 0;
        int j = 0;
        int k = 0;

        while(i < stored.length && j < added.length)
            merged[k++] = stored[i] < added[j] ? stored[i++] : added[j++];

        while(i < stored.length)
            merged[k++] = stored[i++];

        while(j < added.length)
            merged[k++] = added[j++];

        stored = merged;
    }
}
