package cz.iocb.nextprot.load;

import static cz.iocb.nextprot.load.NeXtProt.rdf;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.system.StreamRDFBase;
import cz.iocb.nextprot.load.NeXtProt.Server;



/*
 * Reads gzipped Turtle files as streams of triples and passes every triple to the handlers registered for its
 * predicate, so that the files are loaded in one pass and without holding them in memory as models. A handler
 * registered for a type gets the rdf:type triples whose object is the type. The handlers must not depend on the order
 * of the triples: what needs triples of several predicates collects them by values() or instances() and does its work
 * in an action registered by after(), which runs once all the files have been read.
 */
public class TripleDispatcher
{
    @FunctionalInterface
    public static interface Handler
    {
        void handle(Node subject, Node object) throws IOException, SQLException;
    }


    @FunctionalInterface
    public static interface TripleHandler
    {
        void handle(Node subject, Node predicate, Node object) throws IOException, SQLException;
    }


    @FunctionalInterface
    public static interface Action
    {
        void run() throws IOException, SQLException;
    }


    private static final String typePredicate = rdf + "type";

    private final HashMap<String, List<Handler>> handlers = new HashMap<>();
    private final HashMap<String, List<Handler>> typeHandlers = new HashMap<>();
    private final List<TripleHandler> tripleHandlers = new ArrayList<>();
    private final List<Action> actions = new ArrayList<>();
    private final LinkedHashMap<String, Values> collections = new LinkedHashMap<>();
    private final HashMap<String, LinkedHashSet<Node>> instances = new HashMap<>();
    private final List<Table> tables = new ArrayList<>();


    /*
     * Registers a handler of the triples with the given predicate.
     */
    public void on(String predicate, Handler handler)
    {
        handlers.computeIfAbsent(predicate, _ -> new ArrayList<>()).add(handler);
    }


    /*
     * Registers a handler of the rdf:type triples with the given type.
     */
    public void onType(String type, Handler handler)
    {
        typeHandlers.computeIfAbsent(type, _ -> new ArrayList<>()).add(handler);
    }


    /*
     * Registers a handler of all the triples.
     */
    public void onEvery(TripleHandler handler)
    {
        tripleHandlers.add(handler);
    }


    /*
     * Registers an action to run once all the files have been read.
     */
    public void after(Action action)
    {
        actions.add(action);
    }


    /*
     * Prepares the insertion into a table that the handlers fill while the files are read; the table is closed once
     * the actions registered by after() have run.
     */
    public Table table(Connection connection, Server type, String table) throws SQLException
    {
        Table result = new Table(connection, type, table);
        tables.add(result);

        return result;
    }


    /*
     * Collects the objects of the predicate at its subjects; the predicate is collected once for all the callers.
     */
    public Values values(String predicate)
    {
        return collections.computeIfAbsent(predicate, _ -> {
            Values values = new Values(predicate);
            on(predicate, values::add);
            return values;
        });
    }


    /*
     * Collects the subjects of the predicate at its objects; the predicate is collected once for all the callers.
     */
    public Values inverse(String predicate)
    {
        return collections.computeIfAbsent("^" + predicate, k -> {
            Values values = new Values(k);
            on(predicate, (subject, object) -> values.add(object, subject));
            return values;
        });
    }


    /*
     * Collects the instances of the type, in the order in which they were read; the type is collected once for all
     * the callers.
     */
    public LinkedHashSet<Node> instances(String type)
    {
        return instances.computeIfAbsent(type, _ -> {
            LinkedHashSet<Node> nodes = new LinkedHashSet<>();
            onType(type, (subject, _) -> nodes.add(subject));
            return nodes;
        });
    }


    /*
     * Passes the triple to its handlers.
     */
    private void dispatch(Node subject, Node predicate, Node object) throws IOException, SQLException
    {
        for(TripleHandler handler : tripleHandlers)
            handler.handle(subject, predicate, object);

        List<Handler> list = handlers.get(predicate.getURI());

        if(list != null)
            for(Handler handler : list)
                handler.handle(subject, object);

        if(object.isURI() && predicate.getURI().equals(typePredicate))
        {
            List<Handler> types = typeHandlers.get(object.getURI());

            if(types != null)
                for(Handler handler : types)
                    handler.handle(subject, object);
        }
    }


    /*
     * Reads a gzipped Turtle file of the directory.
     */
    private void read(String path, String file) throws IOException, SQLException
    {
        System.out.println("load " + file);

        try(InputStream in = new BufferedInputStream(
                new GZIPInputStream(new FileInputStream(new File(path, file)), 65536)))
        {
            RDFParser.source(in).lang(Lang.TURTLE).parse(new StreamRDFBase()
            {
                @Override
                public void triple(Triple triple)
                {
                    try
                    {
                        dispatch(triple.getSubject(), triple.getPredicate(), triple.getObject());
                    }
                    catch(IOException | SQLException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        catch(RuntimeException e)
        {
            if(e.getCause() instanceof IOException cause)
                throw cause;
            else if(e.getCause() instanceof SQLException cause)
                throw cause;
            else
                throw e;
        }
    }


    /*
     * Reads the gzipped Turtle files of the directory as one graph, runs the actions registered by after(), closes the
     * tables filled by the handlers and reports the collected values from which the first one was chosen at some
     * nodes.
     */
    public void load(String path, String... files) throws IOException, SQLException
    {
        for(String file : files)
            read(path, file);

        for(Action action : actions)
            action.run();

        for(Table table : tables)
            table.close();

        for(Values values : collections.values())
            if(values.getAmbiguous() > 0)
                System.out.println("   warning: " + values.getAmbiguous() + " nodes with several values of "
                        + values.getPredicate() + ", the first one used");
    }
}
