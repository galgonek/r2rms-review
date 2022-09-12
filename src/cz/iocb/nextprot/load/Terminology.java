package cz.iocb.nextprot.load;

import static cz.iocb.nextprot.load.Schema.entities;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.jena.rdf.model.Model;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;
import cz.iocb.nextprot.load.NeXtProt.Mode;
import cz.iocb.nextprot.load.NeXtProt.Server;



public class Terminology
{
    static ObjectIntHashMap<String> terms = new ObjectIntHashMap<String>();


    public static void loadTerminology(Model model, Connection connection, Server type, Mode mode)
            throws IOException, SQLException
    {
        new DataLoader(model, connection, type, "?term rdf:type ?type",
                "terminology_bases(id, type" + (mode == Mode.INDIRECT ? ", iri" : "") + ")")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("term", "http://nextprot.org/rdf/terminology/", terms, mode));
                setValue(2, getID("type", "http://nextprot.org/rdf#", entities, mode));

                if(mode == Mode.INDIRECT)
                    setValue(3, getStringID("term", "http://nextprot.org/rdf/terminology/"));
            }
        }.load();


        new DataLoader(model, connection, type, "?term :childOf ?parent", "terminology_parents(term, parent)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("term", "http://nextprot.org/rdf/terminology/", terms, mode));
                setValue(2, getID("parent", "http://nextprot.org/rdf/terminology/", terms, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?term :related ?related", "terminology_related_terms(term, related)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("term", "http://nextprot.org/rdf/terminology/", terms, mode));
                setValue(2, getID("related", "http://nextprot.org/rdf/terminology/", terms, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?term rdfs:label ?label", "terminology_labels(term, label)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("term", "http://nextprot.org/rdf/terminology/", terms, mode));
                setValue(2, getLiteralValue("label"));
            }
        }.load();
    }


    static void load(String path, Connection connection, Server type, Mode mode) throws IOException, SQLException
    {
        Model model = DataLoader.getModel(path, "terminology.ttl.gz");
        model.add(DataLoader.getModel(path, "inferredChildOf-a.ttl.gz"));
        model.add(DataLoader.getModel(path, "inferredChildOf-b.ttl.gz"));

        loadTerminology(model, connection, type, mode);

        model.close();
    }
}
