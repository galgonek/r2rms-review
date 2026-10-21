package cz.iocb.nextprot.load;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import org.apache.jena.graph.Node;
import cz.iocb.nextprot.load.TripleDispatcher.Handler;



/*
 * Values of a predicate collected while the triples are read: the objects of the predicate at its subjects or, for an
 * inverse collection, the subjects at its objects. The values at a node form a set, as in an RDF graph, and both the
 * nodes and their values keep the order in which they were read.
 */
public class Values
{
    private final String predicate;
    private final LinkedHashMap<Node, Object> values = new LinkedHashMap<>();
    private int ambiguous = 0;


    Values(String predicate)
    {
        this.predicate = predicate;
    }


    /*
     * Adds a value to the values of a node, which are kept as a node while there is only one of them.
     */
    @SuppressWarnings("unchecked")
    void add(Node node, Node value)
    {
        Object current = values.putIfAbsent(node, value);

        if(current == null || current.equals(value))
            return;

        if(current instanceof Node single)
        {
            values.put(node, new ArrayList<>(List.of(single, value)));
        }
        else
        {
            List<Node> list = (List<Node>) current;

            if(!list.contains(value))
                list.add(value);
        }
    }


    /*
     * Returns the values at the node.
     */
    @SuppressWarnings("unchecked")
    public List<Node> get(Node node)
    {
        Object value = values.get(node);

        if(value == null)
            return List.of();
        else if(value instanceof Node single)
            return List.of(single);
        else
            return (List<Node>) value;
    }


    /*
     * Returns the first value at the node, or null if there is none. A node with several values is counted, so that
     * the loader can report how many nodes had a value chosen from several ones.
     */
    public Node first(Node node)
    {
        return first(node, _ -> true);
    }


    /*
     * Returns the first of the selected values at the node, or null if there is none. A node with several selected
     * values is counted, so that the loader can report how many nodes had a value chosen from several ones.
     */
    public Node first(Node node, Predicate<Node> selected)
    {
        Node first = null;

        for(Node value : get(node))
        {
            if(!selected.test(value))
                continue;

            if(first != null)
            {
                ambiguous++;
                break;
            }

            first = value;
        }

        return first;
    }


    /*
     * Tests whether the node has the value.
     */
    public boolean contains(Node node, Node value)
    {
        return get(node).contains(value);
    }


    /*
     * Returns the nodes that have a value, in the order in which they were read.
     */
    public Set<Node> nodes()
    {
        return values.keySet();
    }


    /*
     * Passes every pair of a node and its value to the handler, in the order in which they were read.
     */
    public void forEach(Handler handler) throws IOException, SQLException
    {
        for(Node node : values.keySet())
            for(Node value : get(node))
                handler.handle(node, value);
    }


    String getPredicate()
    {
        return predicate;
    }


    int getAmbiguous()
    {
        return ambiguous;
    }
}
