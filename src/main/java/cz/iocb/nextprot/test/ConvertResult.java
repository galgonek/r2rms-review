package cz.iocb.nextprot.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Vector;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;



public class ConvertResult
{
    public static void convert(File input, File output) throws SAXException, IOException, ParserConfigurationException
    {
        output.mkdirs();

        for(final File file : input.listFiles())
        {
            if(file.isDirectory())
            {
                convert(file, new File(output, file.getName()));
            }
            else if(file.getName().endsWith(".xml"))
            {
                try(FileInputStream i = new FileInputStream(file))
                {
                    try(FileOutputStream o = new FileOutputStream(new File(output, file.getName())))
                    {
                        try
                        {
                            convert(i, o);
                        }
                        catch(SAXParseException e)
                        {
                            System.err.println(file.getAbsolutePath());
                        }
                    }
                }
            }
        }
    }



    public static void convert(InputStream input, OutputStream output)
            throws SAXException, IOException, ParserConfigurationException
    {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        ArrayList<String> variables = new ArrayList<String>();
        ArrayList<Vector<String>> results = new ArrayList<Vector<String>>();

        DefaultHandler handler = new DefaultHandler()
        {
            Vector<String> result;
            int varIndex;
            StringBuilder data;
            String datatype;
            String lang;

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes)
                    throws SAXException
            {
                if(qName.equalsIgnoreCase("result"))
                {
                    result = new Vector<String>(variables.size());
                    result.setSize(variables.size());
                }
                else if(qName.equalsIgnoreCase("variable"))
                {
                    variables.add(attributes.getValue("name"));
                }
                else if(qName.equalsIgnoreCase("binding"))
                {
                    varIndex = variables.indexOf(attributes.getValue("name"));
                }
                else if(qName.equalsIgnoreCase("literal"))
                {
                    lang = attributes.getValue("xml:lang");
                    datatype = attributes.getValue("datatype");
                    data = new StringBuilder();
                }
                else if(qName.equalsIgnoreCase("uri") || qName.equalsIgnoreCase("bnode"))
                {
                    data = new StringBuilder();
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException
            {
                if(qName.equalsIgnoreCase("result") && result != null)
                    results.add(result);

                if(varIndex == -1)
                    return;

                String node = null;

                if(qName.equalsIgnoreCase("uri"))
                    node = escapeIRI(data.toString());
                else if(qName.equalsIgnoreCase("bnode"))
                    node = "_:" + data.toString();
                else if(!qName.equalsIgnoreCase("literal"))
                    return;
                else if(lang != null)
                    node = escapeLiteral(data.toString()) + "@" + lang;
                else if("http://www.w3.org/2001/XMLSchema#double".equals(datatype))
                    node = escapeLiteral(Double.toString(Double.parseDouble(data.toString()))) + "^^"
                            + escapeIRI(datatype);
                else if("http://www.w3.org/2001/XMLSchema#decimal".equals(datatype)) // workaround
                    node = escapeLiteral(data.toString()) + "^^"
                            + escapeIRI("http://www.w3.org/2001/XMLSchema#integer");
                else if(datatype != null)
                    node = escapeLiteral(data.toString()) + "^^" + escapeIRI(datatype);
                else
                    node = escapeLiteral(data.toString()) + "^^" + escapeIRI("http://www.w3.org/2001/XMLSchema#string");

                if(result != null)
                {
                    if(result.get(varIndex) != null && !result.get(varIndex).equals(node))
                        throw new SAXException("unexpected value");

                    result.set(varIndex, node);
                }
            }

            @Override
            public void characters(char ch[], int start, int length)
            {
                if(data != null)
                    data.append(new String(ch, start, length));
            }
        };


        saxParser.parse(input, handler);


        results.sort(new Comparator<Vector<String>>()
        {
            @Override
            public int compare(Vector<String> a, Vector<String> b)
            {
                if(a.size() != b.size())
                    return a.size() - b.size();

                for(int i = 0; i < a.size(); i++)
                {
                    if(a.get(i) == null && b.get(i) == null)
                        continue;

                    if(a.get(i) == null)
                        return -1;

                    if(b.get(i) == null)
                        return 1;

                    int cmp = a.get(i).compareTo(b.get(i));

                    if(cmp != 0)
                        return cmp;
                }

                return 0;
            }
        });


        output.write(variables.stream().map(v -> "?" + v).collect(Collectors.joining("\t", "", "\r\n")).getBytes());

        for(Vector<String> result : results)
            output.write(result.stream().collect(Collectors.joining("\t", "", "\r\n")).getBytes());
    }


    public static String escapeIRI(String value)
    {
        try
        {
            return "<" + new URI(value).toString() + ">";
        }
        catch(URISyntaxException e)
        {
            return "<" + value + ">";
        }
    }


    public static String escapeLiteral(String value)
    {
        StringBuilder builder = new StringBuilder();

        builder.append("\"");

        for(char val : value.toCharArray())
        {
            if(val == '\\')
                builder.append("\\\\");
            else if(val == '\t')
                builder.append("\\t");
            else if(val == '\r')
                builder.append("\\r");
            else if(val == '\n')
                builder.append("\\n");
            else if(val == '"')
                builder.append("\\\"");
            else
                builder.append(val);
        }

        builder.append("\"");

        return builder.toString();
    }


    public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException
    {
        convert(new File("/home/galgonek/log-18/2026-02-26"), new File("/home/galgonek/log-18/2026-02-26.out"));
        //convert(new File("/home/galgonek/log.dina"), new File("/home/galgonek/log.dina.out"));
    }
}
