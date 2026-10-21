package cz.iocb.nextprot.load;

import static cz.iocb.nextprot.load.Chromosome.annotations;
import static cz.iocb.nextprot.load.Chromosome.isoforms;
import static cz.iocb.nextprot.load.NeXtProt.nextprot;
import static cz.iocb.nextprot.load.Nodes.getID;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import cz.iocb.nextprot.load.NeXtProt.Mode;
import cz.iocb.nextprot.load.NeXtProt.Server;



public class Expression
{
    private static void loadExpressions(String path, String file, String predicate, String table, Connection connection,
            Server type, Mode mode) throws IOException, SQLException
    {
        TripleDispatcher dispatcher = new TripleDispatcher();

        Table expressions = dispatcher.table(connection, type, table + "(isoform, annotation)");

        dispatcher.on(nextprot + predicate, (isoform, annotation) -> {
            expressions.setValue(1, getID(isoform, "http://nextprot.org/rdf/isoform/", isoforms, mode));
            expressions.setValue(2, getID(annotation, "http://nextprot.org/rdf/annotation/", annotations, mode));
            expressions.add();
        });

        dispatcher.load(path, file);
    }


    public static void load(String path, Connection connection, Server type, Mode mode) throws IOException, SQLException
    {
        loadExpressions(path, "expression_low.ttl.gz", "lowExpression", "isoform_low_expressions", connection, type,
                mode);
        loadExpressions(path, "expression_medium.ttl.gz", "mediumExpression", "isoform_medium_expressions", connection,
                type, mode);
        loadExpressions(path, "expression_high.ttl.gz", "highExpression", "isoform_high_expressions", connection, type,
                mode);
    }
}
