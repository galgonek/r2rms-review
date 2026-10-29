package cz.iocb.nextprot.load;

import static cz.iocb.nextprot.load.NeXtProt.nextprot;
import static cz.iocb.nextprot.load.NeXtProt.rdf;
import static cz.iocb.nextprot.load.NeXtProt.rdfs;
import static cz.iocb.nextprot.load.Nodes.getLiteralValue;
import static cz.iocb.nextprot.load.Schema.entities;
import java.io.IOException;
import java.sql.SQLException;
import org.apache.jena.graph.Node;



public class Terminology
{
    static Ids terms = new Ids("http://nextprot.org/rdf/terminology/");


    static void load(String path, Targets targets) throws IOException, SQLException
    {
        TripleDispatcher dispatcher = new TripleDispatcher();

        Values types = dispatcher.values(rdf + "type");

        Table parents = dispatcher.table(targets, "terminology_parents(term, parent)");
        Table relatedTerms = dispatcher.table(targets, "terminology_related_terms(term, related)");
        Table labels = dispatcher.table(targets, "terminology_labels(term, label)");

        dispatcher.on(nextprot + "childOf", (term, parent) -> {
            if(!parents.isNew(terms.getId(term), terms.getId(parent)))
                return;

            parents.setValue(1, terms, term);
            parents.setValue(2, terms, parent);
            parents.add();
        });

        dispatcher.on(nextprot + "related", (term, related) -> {
            if(!relatedTerms.isNew(terms.getId(term), terms.getId(related)))
                return;

            relatedTerms.setValue(1, terms, term);
            relatedTerms.setValue(2, terms, related);
            relatedTerms.add();
        });

        dispatcher.on(rdfs + "label", (term, label) -> {
            if(!labels.isNew(terms.getId(term), getLiteralValue(label)))
                return;

            labels.setValue(1, terms, term);
            labels.setValue(2, getLiteralValue(label));
            labels.add();
        });

        dispatcher.after(() -> {
            try(Table table = targets.table("terminology_bases(id, type, iri)"))
            {
                for(Node term : types.nodes())
                {
                    if(!table.isNew(terms.getId(term)))
                        continue;

                    table.setValue(1, terms, term);
                    table.setValue(2, entities, types.first(term));
                    table.setValue(3, terms.getLocalPart(term));
                    table.add();
                }
            }
        });

        dispatcher.load(path, "terminology.ttl.gz", "inferredChildOf-a.ttl.gz", "inferredChildOf-b.ttl.gz");
    }
}
