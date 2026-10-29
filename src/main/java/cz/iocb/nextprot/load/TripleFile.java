package cz.iocb.nextprot.load;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.lang.LabelToNode;
import org.apache.jena.riot.system.MapWithScope.Allocator;
import org.apache.jena.riot.system.MapWithScope.ScopePolicy;
import org.apache.jena.riot.system.StreamRDFBase;
import cz.iocb.sparql.engine.mapping.datatypes.BuiltinDatatypes;
import cz.iocb.sparql.engine.mapping.datatypes.Datatype;



/*
 * Writes the triples of all the data files, in the rdf mode, into a file of SQL statements that store them into the
 * table nextprot.loaded_triples(subject, predicate, object) of PostgreSQL, each term in the text form of an rdfbox; the
 * file is gzipped if its name ends with .gz. The triples are written by threads of their own, which read the data files
 * in parallel, each writing the triples of its file in chunks: a chunk is a copy statement (and a gzip member in a
 * gzipped file), and the chunks follow in the order in which they are done. A triple occurring several times is written
 * each time; the database makes the triples distinct and turns their terms into rdfboxes after they are loaded
 * (sql/rdf/distinct.sql).
 *
 * A literal of a datatype that the engine handles is written in its canonical lexical form, as the other modes store
 * the values of the literals, so that the terms are the same in all the modes.
 *
 * The labels of blank nodes are local to their file, so the blank nodes of a file get the integer blank nodes of the
 * segment given by the number of the file, numbered in the order of their first occurrence.
 */
public class TripleFile implements AutoCloseable
{
    private static final String copy = "copy nextprot.loaded_triples (subject, predicate, object) from stdin;\n";
    private static final String xsdString = XSDDatatype.XSDstring.getURI();
    private static final Map<String, Datatype> datatypes = Stream.of(BuiltinDatatypes.xsdBooleanType,
            BuiltinDatatypes.xsdByteType, BuiltinDatatypes.xsdUnsignedByteType, BuiltinDatatypes.xsdShortType,
            BuiltinDatatypes.xsdUnsignedShortType, BuiltinDatatypes.xsdIntType, BuiltinDatatypes.xsdUnsignedIntType,
            BuiltinDatatypes.xsdLongType, BuiltinDatatypes.xsdUnsignedLongType, BuiltinDatatypes.xsdIntegerType,
            BuiltinDatatypes.xsdNonPositiveIntegerType, BuiltinDatatypes.xsdNegativeIntegerType,
            BuiltinDatatypes.xsdNonNegativeIntegerType, BuiltinDatatypes.xsdPositiveIntegerType,
            BuiltinDatatypes.xsdDecimalType, BuiltinDatatypes.xsdFloatType, BuiltinDatatypes.xsdDoubleType,
            BuiltinDatatypes.xsdDateTimeType, BuiltinDatatypes.xsdDateType, BuiltinDatatypes.xsdDayTimeDurationType)
            .collect(Collectors.toMap(datatype -> datatype.getTypeIri().getValue(), datatype -> datatype));
    private static final int chunkSize = 8 << 20;

    private final OutputStream output;
    private final boolean gzip;
    private final ExecutorService executor;
    private final List<Future<?>> tasks = new ArrayList<>();


    /*
     * Opens the file and starts writing the triples of the data files in the given directory.
     */
    public TripleFile(String file, String path) throws IOException
    {
        output = new FileOutputStream(file);
        gzip = file.endsWith(".gz");

        write("set client_encoding to 'UTF8';\nbegin;\n");

        List<Path> files = new ArrayList<>();

        try(DirectoryStream<Path> directory = Files.newDirectoryStream(Path.of(path), "*.ttl.gz"))
        {
            directory.forEach(files::add);
        }

        files.sort(null);

        List<Integer> order = new ArrayList<>();

        for(int i = 0; i < files.size(); i++)
            order.add(i);

        // the largest files first, so that no large file is left to the end
        order.sort(Comparator.comparingLong((Integer i) -> files.get(i).toFile().length()).reversed());

        int threads = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(threads, Thread.ofPlatform().daemon().factory());

        for(int i : order)
            tasks.add(executor.submit(() -> writeTriples(files.get(i), i)));

        executor.shutdown();
    }


    /*
     * Waits until the triples of all the data files are written, ends the transaction and closes the file.
     */
    @Override
    public void close() throws IOException
    {
        try
        {
            for(Future<?> task : tasks)
                task.get();

            write("commit;\n");
        }
        catch(InterruptedException e)
        {
            throw new InterruptedIOException();
        }
        catch(ExecutionException e)
        {
            if(e.getCause() instanceof UncheckedIOException cause)
                throw cause.getCause();

            throw new IOException(e.getCause());
        }
        finally
        {
            executor.shutdownNow();
            output.close();
        }
    }


    /*
     * Writes the triples of a data file, whose blank nodes are in the given segment.
     */
    private Void writeTriples(Path file, int segment) throws IOException
    {
        StringBuilder rows = new StringBuilder(chunkSize + (chunkSize >> 4));
        BlankNodes blankNodes = new BlankNodes(segment);

        try(InputStream in = new BufferedInputStream(new GZIPInputStream(Files.newInputStream(file), 65536)))
        {
            RDFParser.source(in).lang(Lang.TURTLE).labelToNode(new LabelToNode(blankNodes, blankNodes))
                    .parse(new StreamRDFBase()
                    {
                        @Override
                        public void triple(Triple triple)
                        {
                            appendTerm(rows, triple.getSubject());
                            rows.append('\t');
                            appendTerm(rows, triple.getPredicate());
                            rows.append('\t');
                            appendTerm(rows, triple.getObject());
                            rows.append('\n');

                            if(rows.length() >= chunkSize)
                                writeChunk(rows);
                        }
                    });
        }

        if(!rows.isEmpty())
            writeChunk(rows);

        return null;
    }


    /*
     * Writes the rows as a copy statement and empties them.
     */
    private void writeChunk(StringBuilder rows)
    {
        try
        {
            write(copy + rows + "\\.\n");
            rows.setLength(0);
        }
        catch(IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }


    /*
     * Writes the text as a whole, as one gzip member in a gzipped file.
     */
    private void write(String text) throws IOException
    {
        byte[] data = text.getBytes(UTF_8);

        if(gzip)
        {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream(data.length / 4);

            try(GZIPOutputStream stream = new GZIPOutputStream(buffer, 65536))
            {
                stream.write(data);
            }

            data = buffer.toByteArray();
        }

        synchronized(output)
        {
            output.write(data);
        }
    }


    /*
     * Appends the text form of an RDF term, escaped for the text format of copy.
     */
    private static void appendTerm(StringBuilder out, Node node)
    {
        if(node.isURI())
        {
            out.append('<');
            appendEscaped(out, node.getURI());
            out.append('>');
        }
        else if(node.isBlank())
        {
            out.append("_:").append(node.getBlankNodeLabel());
        }
        else if(node.isLiteral())
        {
            out.append('"');
            appendLexical(out, getLexicalForm(node));
            out.append('"');

            if(!node.getLiteralLanguage().isEmpty())
            {
                out.append('@');
                appendEscaped(out, node.getLiteralLanguage());

                if(node.getLiteralBaseDirection() != null)
                    out.append("--").append(node.getLiteralBaseDirection().direction());
            }
            else if(!node.getLiteralDatatypeURI().equals(xsdString))
            {
                out.append("^^<");
                appendEscaped(out, node.getLiteralDatatypeURI());
                out.append('>');
            }
        }
        else
        {
            throw new IllegalArgumentException("unsupported RDF term: " + node);
        }
    }


    /*
     * Returns the lexical form of a literal, the canonical one for a valid literal of a datatype that the engine
     * handles, as the engine does with the literals that a remote service returns.
     */
    private static String getLexicalForm(Node node)
    {
        String lexical = node.getLiteralLexicalForm();
        Datatype datatype = datatypes.get(node.getLiteralDatatypeURI());

        if(datatype != null && datatype.isValidForm(lexical) && !datatype.isCanonicalForm(lexical))
            return datatype.getCanonicalLexicalForm(lexical);

        return lexical;
    }


    /*
     * Appends a text escaped for the text format of copy, in which a backslash is an escape character.
     */
    private static void appendEscaped(StringBuilder out, String text)
    {
        for(int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);

            switch(c)
            {
                case '\\' -> out.append("\\\\");
                case '\t' -> out.append("\\t");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\0' -> throw new IllegalArgumentException("text with the NUL character: " + text);
                default -> out.append(c);
            }
        }
    }


    /*
     * Appends a lexical form escaped for a quoted literal of rdfbox, whose escapes are escaped again for copy.
     */
    private static void appendLexical(StringBuilder out, String lexical)
    {
        for(int i = 0; i < lexical.length(); i++)
        {
            char c = lexical.charAt(i);

            switch(c)
            {
                case '\\' -> out.append("\\\\\\\\");
                case '"' -> out.append("\\\\\"");
                case '\t' -> out.append("\\\\t");
                case '\n' -> out.append("\\\\n");
                case '\r' -> out.append("\\\\r");
                case '\0' -> throw new IllegalArgumentException("literal with the NUL character: " + lexical);
                default -> out.append(c);
            }
        }
    }


    /*
     * Allocates the blank nodes of a file, labelled ones and anonymous ones alike, as the integer blank nodes of its
     * segment in the order of their first occurrence; the label of a node is its text form without the leading _:.
     */
    private static class BlankNodes implements ScopePolicy<String, Node, Node>, Allocator<String, Node, Node>
    {
        private final Map<String, Node> labels = new HashMap<>();
        private final long segment;
        private int count = 0;


        private BlankNodes(int segment)
        {
            this.segment = (long) segment << 32;
        }


        @Override
        public Map<String, Node> getScope(Node scope)
        {
            return labels;
        }


        @Override
        public void clear()
        {
            labels.clear();
        }


        @Override
        public Node alloc(Node scope, String label)
        {
            return create();
        }


        @Override
        public Node create()
        {
            long value = segment | Integer.toUnsignedLong(count++);

            return NodeFactory.createBlankNode("i" + HexFormat.of().toHexDigits(value));
        }


        @Override
        public void reset()
        {
        }
    }
}
