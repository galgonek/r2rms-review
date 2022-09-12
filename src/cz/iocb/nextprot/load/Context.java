package cz.iocb.nextprot.load;

import static cz.iocb.nextprot.load.Terminology.terms;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.jena.rdf.model.Model;
import cz.iocb.nextprot.load.NeXtProt.Mode;
import cz.iocb.nextprot.load.NeXtProt.Server;



public class Context
{
    public static void load(String path, String file, Connection connection, Server type, Mode mode)
            throws IOException, SQLException
    {
        Model model = DataLoader.getModel(path, file);

        // @formatter:off
        new DataLoader(model, connection, type,
                "?context rdf:type :ExperimentalContext. "
                        + "optional { ?context :detectionMethod ?method }"
                        + "optional { ?context :metadata ?metadata }"
                        + "optional { ?context :disease ?disease }"
                        + "optional { ?context :tissue ?tissue }"
                        + "optional { ?context :cellLine ?line }"
                        + "optional { ?context :developmentalStage ?stage }",
                "context_bases(id, metadata, method, disease, tissue, line, stage)")
        // @formatter:on
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getIntID("context", "http://nextprot.org/rdf/context/"));
                setValue(2, getIntID("metadata", "http://nextprot.org/rdf/publication/"));
                setValue(3, getID("method", "http://nextprot.org/rdf/terminology/", terms, mode));
                setValue(4, getID("disease", "http://nextprot.org/rdf/terminology/", terms, mode));
                setValue(5, getID("tissue", "http://nextprot.org/rdf/terminology/", terms, mode));
                setValue(6, getID("line", "http://nextprot.org/rdf/terminology/", terms, mode));
                setValue(7, getID("stage", "http://nextprot.org/rdf/terminology/", terms, mode));
            }
        }.load();

        model.close();
    }


    public static void load(String path, Connection connection, Server type, Mode mode) throws IOException, SQLException
    {
        load(path, "experimentalcontext.ttl.gz", connection, type, mode);
    }
}
