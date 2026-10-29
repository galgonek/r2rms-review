package cz.iocb.nextprot.load;

import static cz.iocb.nextprot.load.NeXtProt.nextprot;
import static cz.iocb.nextprot.load.Nodes.getIntID;
import static cz.iocb.nextprot.load.Terminology.terms;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import org.apache.jena.graph.Node;
import cz.iocb.nextprot.load.NeXtProt.Mode;



public class Context
{
    public static void load(String path, String file, Target target, Mode mode)
            throws IOException, SQLException
    {
        TripleDispatcher dispatcher = new TripleDispatcher();

        LinkedHashSet<Node> contexts = dispatcher.instances(nextprot + "ExperimentalContext");
        Values methods = dispatcher.values(nextprot + "detectionMethod");
        Values metadata = dispatcher.values(nextprot + "metadata");
        Values diseases = dispatcher.values(nextprot + "disease");
        Values tissues = dispatcher.values(nextprot + "tissue");
        Values lines = dispatcher.values(nextprot + "cellLine");
        Values stages = dispatcher.values(nextprot + "developmentalStage");

        dispatcher.after(() -> {
            try(Table table = target.table("context_bases(id, metadata, method, disease, tissue, line, stage)"))
            {
                for(Node context : contexts)
                {
                    Integer id = getIntID(context, "http://nextprot.org/rdf/context/");

                    if(!table.isNew(id))
                        continue;

                    table.setValue(1, id);
                    table.setValue(2, getIntID(metadata.first(context), "http://nextprot.org/rdf/publication/"));
                    table.setValue(3, terms.getValue(methods.first(context), mode));
                    table.setValue(4, terms.getValue(diseases.first(context), mode));
                    table.setValue(5, terms.getValue(tissues.first(context), mode));
                    table.setValue(6, terms.getValue(lines.first(context), mode));
                    table.setValue(7, terms.getValue(stages.first(context), mode));
                    table.add();
                }
            }
        });

        dispatcher.load(path, file);
    }


    public static void load(String path, Target target, Mode mode) throws IOException, SQLException
    {
        load(path, "experimentalcontext.ttl.gz", target, mode);
    }
}
