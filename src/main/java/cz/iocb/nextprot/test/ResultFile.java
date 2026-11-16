package cz.iocb.nextprot.test;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;



/*
 * Reads a file of query results in the SPARQL XML results format (with the triple terms and the base directions of
 * SPARQL 1.2), passing its solutions on one by one, so that a result of any size can be read.
 */
final class ResultFile
{
    /*
     * Receives the solutions of a result in their order, each as the terms of its bound variables by the names of the
     * variables.
     */
    interface Handler
    {
        void solution(SortedMap<String, Term> solution);
    }


    /*
     * The head of a result: its variables, and the value of the result of an ASK query, null for solutions.
     */
    record Head(List<String> variables, Boolean value)
    {
    }


    /*
     * A file that cannot be read as a result.
     */
    static class InvalidResultException extends Exception
    {
        private static final long serialVersionUID = 1L;


        InvalidResultException(String message)
        {
            super(message);
        }
    }


    private static final String its = "http://www.w3.org/2005/11/its";

    // the deepest nesting of triple terms read, far beyond what an engine writes, so that the reading cannot overflow
    private static final int maxDepth = 100;

    // a message of the JDK parser about namespaces or attributes, which it leaves as a key with its arguments
    private static final Pattern messageKey = Pattern
            .compile("http://www\\.w3\\.org/TR/1999/REC-xml-names-19990114#" + "(\\w+)\\?(\\S*)");

    // the limits that the JDK puts on the documents it parses, which a result of millions of solutions need not keep
    private static final String[] limits = { "jdk.xml.entityExpansionLimit", "jdk.xml.totalEntitySizeLimit",
            "jdk.xml.maxGeneralEntitySizeLimit", "jdk.xml.maxParameterEntitySizeLimit", "jdk.xml.maxElementDepth",
            "jdk.xml.maxXMLNameLimit", "jdk.xml.elementAttributeLimit" };

    private static final ThreadLocal<XMLInputFactory> factories = ThreadLocal.withInitial(ResultFile::factory);


    private ResultFile()
    {
    }


    static Head read(Path file, Handler handler) throws IOException, InvalidResultException
    {
        try(InputStream input = new BufferedInputStream(Files.newInputStream(file), 1 << 16))
        {
            XMLStreamReader reader = factories.get().createXMLStreamReader(input);

            try
            {
                while(reader.hasNext() && reader.next() != START_ELEMENT)
                    continue;

                if(!reader.isStartElement())
                    throw new InvalidResultException("no root element");

                if(!reader.getLocalName().equals("sparql"))
                    throw new InvalidResultException("the root element is " + reader.getLocalName() + ", not sparql");

                List<String> variables = new ArrayList<>();
                Boolean value = null;

                while(reader.hasNext())
                {
                    if(reader.next() != START_ELEMENT)
                        continue;

                    switch(reader.getLocalName())
                    {
                        case "variable" -> variables.add(name(reader));
                        case "result" -> handler.solution(solution(reader));
                        case "boolean" -> value = bool(reader.getElementText());
                        default ->
                                {
                                }
                    }
                }

                return new Head(variables, value);
            }
            finally
            {
                reader.close();
            }
        }
        catch(XMLStreamException e)
        {
            String message = e.getMessage().replaceAll("\\s+", " ").trim();
            Matcher matcher = messageKey.matcher(message);

            if(matcher.find())
                message = matcher.replaceFirst(Matcher.quoteReplacement(
                        matcher.group(1).replaceAll("([a-z])([A-Z])", "$1 $2").toLowerCase(Locale.ROOT) + " ("
                                + matcher.group(2).replace("&", ", ") + ")"));

            throw new InvalidResultException(message);
        }
    }


    /*
     * Reads a solution, from the start of its result element to its end.
     */
    private static SortedMap<String, Term> solution(XMLStreamReader reader)
            throws XMLStreamException, InvalidResultException
    {
        SortedMap<String, Term> solution = new TreeMap<>();

        while(reader.nextTag() == START_ELEMENT)
        {
            if(!reader.getLocalName().equals("binding"))
                throw invalid(reader, "unexpected element " + reader.getLocalName() + " in a result");

            String variable = name(reader);

            if(reader.nextTag() != START_ELEMENT)
                throw invalid(reader, "nothing bound to ?" + variable);

            Term term = term(reader, 0);

            if(reader.nextTag() != END_ELEMENT)
                throw invalid(reader, "several terms bound to ?" + variable);

            if(solution.put(variable, term) != null)
                throw invalid(reader, "?" + variable + " bound twice in a solution");
        }

        return solution;
    }


    /*
     * Reads a term, from the start of its element to its end.
     */
    private static Term term(XMLStreamReader reader, int depth) throws XMLStreamException, InvalidResultException
    {
        switch(reader.getLocalName())
        {
            case "uri":
                return new Term.Iri(reader.getElementText().trim());

            case "bnode":
                return new Term.BlankNode(reader.getElementText().trim());

            case "literal":
            {
                String datatype = reader.getAttributeValue(null, "datatype");
                String language = reader.getAttributeValue(XMLConstants.XML_NS_URI, "lang");
                String direction = reader.getAttributeValue(its, "dir");
                String lexical = reader.getElementText();

                if(language != null && language.isEmpty())
                    language = null;

                return new Term.Literal(lexical, language == null ? datatype : null, language,
                        language == null ? null : direction);
            }

            case "triple":
            {
                if(depth == maxDepth)
                    throw invalid(reader, "triple terms nested deeper than " + maxDepth);

                Term subject = null;
                Term predicate = null;
                Term object = null;

                while(reader.nextTag() == START_ELEMENT)
                {
                    String part = reader.getLocalName();

                    if(reader.nextTag() != START_ELEMENT)
                        throw invalid(reader, "an empty " + part + " of a triple term");

                    Term term = term(reader, depth + 1);

                    if(reader.nextTag() != END_ELEMENT)
                        throw invalid(reader, "several terms in the " + part + " of a triple term");

                    switch(part)
                    {
                        case "subject" -> subject = term;
                        case "predicate" -> predicate = term;
                        case "object" -> object = term;
                        default -> throw invalid(reader, "unexpected element " + part + " in a triple term");
                    }
                }

                if(subject == null || predicate == null || object == null)
                    throw invalid(reader, "an incomplete triple term");

                return new Term.Triple(subject, predicate, object);
            }

            default:
                throw invalid(reader, "unexpected element " + reader.getLocalName() + " as a term");
        }
    }


    private static String name(XMLStreamReader reader) throws InvalidResultException
    {
        String name = reader.getAttributeValue(null, "name");

        if(name == null)
            throw invalid(reader, reader.getLocalName() + " without a name");

        return name;
    }


    private static Boolean bool(String text) throws InvalidResultException
    {
        return switch(text.trim())
        {
            case "true" -> true;
            case "false" -> false;
            default -> throw new InvalidResultException("invalid boolean result '" + text.trim() + "'");
        };
    }


    private static InvalidResultException invalid(XMLStreamReader reader, String message)
    {
        return new InvalidResultException(message + " at line " + reader.getLocation().getLineNumber());
    }


    private static XMLInputFactory factory()
    {
        XMLInputFactory factory = XMLInputFactory.newDefaultFactory();
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);
        factory.setProperty(XMLInputFactory.IS_COALESCING, true);
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);

        for(String limit : limits)
            factory.setProperty(limit, "0");

        return factory;
    }
}
