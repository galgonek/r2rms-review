package cz.iocb.nextprot.load;

import java.io.IOException;
import java.math.BigInteger;
import org.apache.jena.datatypes.DatatypeFormatException;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Node;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;
import cz.iocb.nextprot.load.NeXtProt.Mode;



/*
 * Converts the nodes of the triples into the values of the columns. A null node, i.e. a missing optional value, gives
 * null. The literal values are converted as the Literal interface of Jena converts them.
 */
public final class Nodes
{
    private Nodes()
    {
    }


    /*
     * Returns the local part of the IRI after the prefix in the direct mode, or the integer id of the local part,
     * which is assigned on its first use, in the indirect mode.
     */
    public static Object getID(Node node, String prefix, ObjectIntHashMap<String> map, Mode mode) throws IOException
    {
        if(node == null)
            return null;

        String value = getStringID(node, prefix);

        if(mode == Mode.DIRECT)
            return value;

        int id = map.getIfAbsent(value, Integer.MIN_VALUE);

        if(id == Integer.MIN_VALUE)
        {
            id = map.size();
            map.put(value, id);
        }

        return id;
    }


    /*
     * Returns the local part of the IRI after the prefix as an integer.
     */
    public static Integer getIntID(Node node, String prefix) throws IOException
    {
        if(node == null)
            return null;

        return Integer.parseInt(getStringID(node, prefix));
    }


    /*
     * Returns the local part of the IRI after the prefix.
     */
    public static String getStringID(Node node, String prefix) throws IOException
    {
        if(node == null)
            return null;

        String iri = node.getURI();

        if(!iri.startsWith(prefix))
            throw new IOException("wrong value: " + iri);

        return iri.substring(prefix.length());
    }


    /*
     * Returns the id that the map assigns to the node, such as a blank node of a name.
     */
    public static int getMappedID(Node node, ObjectIntHashMap<Node> map) throws IOException
    {
        int id = map.getIfAbsent(node, Integer.MIN_VALUE);

        if(id == Integer.MIN_VALUE)
            throw new IOException("unknown node: " + node);

        return id;
    }


    /*
     * Returns the lexical form of the literal.
     */
    public static String getLiteralValue(Node node)
    {
        if(node == null)
            return null;

        return node.getLiteralLexicalForm();
    }


    public static Integer getIntValue(Node node)
    {
        if(node == null)
            return null;

        if(isPlain(node))
            return Integer.parseInt(node.getLiteralLexicalForm());

        Object value = node.getLiteralValue();

        if(value instanceof Integer || value instanceof Long || value instanceof Short || value instanceof Byte
                || value instanceof BigInteger)
        {
            BigInteger integer = value instanceof BigInteger big ? big :
                    BigInteger.valueOf(((Number) value).longValue());

            if(integer.bitLength() < Integer.SIZE)
                return integer.intValue();
        }

        throw new DatatypeFormatException("not an int value: " + node);
    }


    public static Double getDoubleValue(Node node)
    {
        if(node == null)
            return null;

        if(isPlain(node))
            return Double.parseDouble(node.getLiteralLexicalForm());

        if(node.getLiteralValue() instanceof Number number)
            return number.doubleValue();

        throw new DatatypeFormatException("not a number: " + node);
    }


    public static Boolean getBooleanValue(Node node)
    {
        if(node == null)
            return null;

        if(isPlain(node))
        {
            String value = node.getLiteralLexicalForm();

            if(value.equals("true"))
                return true;
            else if(value.equals("false"))
                return false;
        }
        else if(node.getLiteralValue() instanceof Boolean value)
        {
            return value;
        }

        throw new DatatypeFormatException("not a boolean value: " + node);
    }


    /*
     * Tests whether the literal is a string, with or without a language tag.
     */
    private static boolean isPlain(Node node)
    {
        return node.getLiteralDatatype() == XSDDatatype.XSDstring || !node.getLiteralLanguage().isEmpty();
    }
}
