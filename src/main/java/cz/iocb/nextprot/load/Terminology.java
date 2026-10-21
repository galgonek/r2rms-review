package cz.iocb.nextprot.load;

import static cz.iocb.nextprot.load.NeXtProt.nextprot;
import static cz.iocb.nextprot.load.NeXtProt.rdf;
import static cz.iocb.nextprot.load.NeXtProt.rdfs;
import static cz.iocb.nextprot.load.Nodes.getID;
import static cz.iocb.nextprot.load.Nodes.getLiteralValue;
import static cz.iocb.nextprot.load.Nodes.getStringID;
import static cz.iocb.nextprot.load.Schema.entities;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.jena.graph.Node;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;
import cz.iocb.nextprot.load.NeXtProt.Mode;
import cz.iocb.nextprot.load.NeXtProt.Server;



public class Terminology
{
    static ObjectIntHashMap<String> terms = new ObjectIntHashMap<>();


    static void load(String path, Connection connection, Server type, Mode mode) throws IOException, SQLException
    {
        TripleDispatcher dispatcher = new TripleDispatcher();

        Values types = dispatcher.values(rdf + "type");

        Table parents = dispatcher.table(connection, type, "terminology_parents(term, parent)");
        Table relatedTerms = dispatcher.table(connection, type, "terminology_related_terms(term, related)");
        Table labels = dispatcher.table(connection, type, "terminology_labels(term, label)");

        dispatcher.on(nextprot + "childOf", (term, parent) -> {
            parents.setValue(1, getID(term, "http://nextprot.org/rdf/terminology/", terms, mode));
            parents.setValue(2, getID(parent, "http://nextprot.org/rdf/terminology/", terms, mode));
            parents.add();
        });

        dispatcher.on(nextprot + "related", (term, related) -> {
            relatedTerms.setValue(1, getID(term, "http://nextprot.org/rdf/terminology/", terms, mode));
            relatedTerms.setValue(2, getID(related, "http://nextprot.org/rdf/terminology/", terms, mode));
            relatedTerms.add();
        });

        dispatcher.on(rdfs + "label", (term, label) -> {
            labels.setValue(1, getID(term, "http://nextprot.org/rdf/terminology/", terms, mode));
            labels.setValue(2, getLiteralValue(label));
            labels.add();
        });

        dispatcher.after(() -> {
            try(Table table = new Table(connection, type,
                    "terminology_bases(id, type" + (mode == Mode.INDIRECT ? ", iri" : "") + ")"))
            {
                for(Node term : types.nodes())
                {
                    table.setValue(1, getID(term, "http://nextprot.org/rdf/terminology/", terms, mode));
                    table.setValue(2, getID(types.first(term), nextprot, entities, mode));

                    if(mode == Mode.INDIRECT)
                        table.setValue(3, getStringID(term, "http://nextprot.org/rdf/terminology/"));

                    table.add();
                }
            }
        });

        dispatcher.load(path, "terminology.ttl.gz", "inferredChildOf-a.ttl.gz", "inferredChildOf-b.ttl.gz");
    }
}
