package cz.iocb.nextprot.load;

import static cz.iocb.nextprot.load.NeXtProt.nextprot;
import static cz.iocb.nextprot.load.NeXtProt.rdf;
import static cz.iocb.nextprot.load.NeXtProt.rdfs;
import static cz.iocb.nextprot.load.Nodes.getLiteralValue;
import static cz.iocb.nextprot.load.Schema.entities;
import java.io.IOException;
import java.sql.SQLException;
import org.apache.jena.graph.Node;
import cz.iocb.nextprot.load.NeXtProt.Mode;



public class Terminology
{
    static Ids terms = new Ids("http://nextprot.org/rdf/terminology/");


    static void load(String path, Target target, Mode mode) throws IOException, SQLException
    {
        TripleDispatcher dispatcher = new TripleDispatcher();

        Values types = dispatcher.values(rdf + "type");

        Table parents = dispatcher.table(target, "terminology_parents(term, parent)");
        Table relatedTerms = dispatcher.table(target, "terminology_related_terms(term, related)");
        Table labels = dispatcher.table(target, "terminology_labels(term, label)");

        dispatcher.on(nextprot + "childOf", (term, parent) -> {
            if(!parents.isNew(terms.getId(term), terms.getId(parent)))
                return;

            parents.setValue(1, terms.getValue(term, mode));
            parents.setValue(2, terms.getValue(parent, mode));
            parents.add();
        });

        dispatcher.on(nextprot + "related", (term, related) -> {
            if(!relatedTerms.isNew(terms.getId(term), terms.getId(related)))
                return;

            relatedTerms.setValue(1, terms.getValue(term, mode));
            relatedTerms.setValue(2, terms.getValue(related, mode));
            relatedTerms.add();
        });

        dispatcher.on(rdfs + "label", (term, label) -> {
            if(!labels.isNew(terms.getId(term), getLiteralValue(label)))
                return;

            labels.setValue(1, terms.getValue(term, mode));
            labels.setValue(2, getLiteralValue(label));
            labels.add();
        });

        dispatcher.after(() -> {
            try(Table table = target.table("terminology_bases(id, type" + (mode == Mode.INDIRECT ? ", iri" : "") + ")"))
            {
                for(Node term : types.nodes())
                {
                    if(!table.isNew(terms.getId(term)))
                        continue;

                    table.setValue(1, terms.getValue(term, mode));
                    table.setValue(2, entities.getValue(types.first(term), mode));

                    if(mode == Mode.INDIRECT)
                        table.setValue(3, terms.getLocalPart(term));

                    table.add();
                }
            }
        });

        dispatcher.load(path, "terminology.ttl.gz", "inferredChildOf-a.ttl.gz", "inferredChildOf-b.ttl.gz");
    }
}
