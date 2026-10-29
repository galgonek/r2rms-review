package cz.iocb.nextprot.load;

import static cz.iocb.nextprot.load.Chromosome.annotations;
import static cz.iocb.nextprot.load.Chromosome.isoforms;
import static cz.iocb.nextprot.load.NeXtProt.nextprot;
import java.io.IOException;
import java.sql.SQLException;



public class Expression
{
    private static void loadExpressions(String path, String file, String predicate, String table, Targets targets)
            throws IOException, SQLException
    {
        TripleDispatcher dispatcher = new TripleDispatcher();

        Table expressions = dispatcher.table(targets, table + "(isoform, annotation)");

        dispatcher.on(nextprot + predicate, (isoform, annotation) -> {
            if(!expressions.isNew(isoforms.getId(isoform), annotations.getId(annotation)))
                return;

            expressions.setValue(1, isoforms, isoform);
            expressions.setValue(2, annotations, annotation);
            expressions.add();
        });

        dispatcher.load(path, file);
    }


    public static void load(String path, Targets targets) throws IOException, SQLException
    {
        loadExpressions(path, "expression_low.ttl.gz", "lowExpression", "isoform_low_expressions", targets);
        loadExpressions(path, "expression_medium.ttl.gz", "mediumExpression", "isoform_medium_expressions", targets);
        loadExpressions(path, "expression_high.ttl.gz", "highExpression", "isoform_high_expressions", targets);
    }
}
