package cz.iocb.nextprot.load;

import java.io.IOException;
import org.apache.jena.graph.Node;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;
import cz.iocb.nextprot.load.NeXtProt.Mode;



/*
 * The entities whose IRIs share a prefix, such as the isoforms, and the integer ids that are assigned to the local
 * parts of their IRIs on their first use. A column referring to an entity holds the local part in the direct mode and
 * the id in the indirect mode, whereas the keys of the stored rows use the ids in both modes.
 */
public class Ids
{
    private final String prefix;
    private final ObjectIntHashMap<String> ids = new ObjectIntHashMap<>();


    public Ids(String prefix)
    {
        this.prefix = prefix;
    }


    /*
     * Returns the local part of the IRI after the prefix, or null for a null node.
     */
    public String getLocalPart(Node node) throws IOException
    {
        return Nodes.getStringID(node, prefix);
    }


    /*
     * Returns the id of the IRI, which is assigned on its first use.
     */
    public int getId(Node node) throws IOException
    {
        String value = getLocalPart(node);
        int id = ids.getIfAbsent(value, -1);

        if(id == -1)
        {
            id = ids.size();
            ids.put(value, id);
        }

        return id;
    }


    /*
     * Returns the value of a column referring to the IRI: the local part in the direct mode and the id in the indirect
     * mode, or null for a null node.
     */
    public Object getValue(Node node, Mode mode) throws IOException
    {
        if(node == null)
            return null;

        return mode == Mode.DIRECT ? getLocalPart(node) : getId(node);
    }
}
