package cz.iocb.nextprot.load;

import static cz.iocb.nextprot.load.Terminology.terms;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.jena.rdf.model.Model;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;
import cz.iocb.nextprot.load.NeXtProt.Mode;
import cz.iocb.nextprot.load.NeXtProt.Server;



public class Schema
{
    static ObjectIntHashMap<String> sources = new ObjectIntHashMap<String>();
    static ObjectIntHashMap<String> databases = new ObjectIntHashMap<String>();
    static ObjectIntHashMap<String> entities = new ObjectIntHashMap<String>();


    private static void loadSources(Model model, Connection connection, Server type, Mode mode)
            throws IOException, SQLException
    {
        // @formatter:off
        new DataLoader(model, connection, type,
                "?source rdf:type :Source "
                        + "optional { ?source rdfs:comment ?comment }"
                        + "optional { ?source rdfs:seeAlso ?reference }",
                "source_bases(id, comment, reference" + (mode == Mode.INDIRECT ? ", iri" : "") + ")")
        // @formatter:on
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("source", "http://nextprot.org/rdf/source/", sources, mode));
                setValue(2, getLiteralValue("comment"));
                setValue(3, getLiteralValue("reference"));

                if(mode == Mode.INDIRECT)
                    setValue(4, getStringID("source", "http://nextprot.org/rdf/source/"));
            }
        }.load();
    }


    private static void loadDatabases(Model model, Connection connection, Server type, Mode mode)
            throws IOException, SQLException
    {
        // @formatter:off
        new DataLoader(model, connection, type,
                "?database rdf:type :Database "
                        + "optional { ?database rdfs:seeAlso ?reference }",
                "database_bases(id, reference" + (mode == Mode.INDIRECT ? ", iri" : "") + ")")
        // @formatter:on
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("database", "http://nextprot.org/rdf/db/", databases, mode));
                setValue(2, getLiteralValue("reference"));

                if(mode == Mode.INDIRECT)
                    setValue(3, getStringID("database", "http://nextprot.org/rdf/db/"));
            }
        }.load();


        new DataLoader(model, connection, type, "?database rdf:type :Database; rdfs:comment ?comment",
                "database_comments(db, comment)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("database", "http://nextprot.org/rdf/db/", databases, mode));
                setValue(2, getLiteralValue("comment"));
            }
        }.load();


        new DataLoader(model, connection, type, "?database rdf:type :Database; :category ?category",
                "database_categories(db, category)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("database", "http://nextprot.org/rdf/db/", databases, mode));
                setValue(2, getLiteralValue("category"));
            }
        }.load();
    }


    private static void loadSchema(Model model, Connection connection, Server type, Mode mode)
            throws IOException, SQLException
    {
        // @formatter:off
        new DataLoader(model, connection, type,
                "{select distinct ?entity "
                        + "{ { ?entity ?P ?O } union { ?S ?P ?entity } "
                        + "filter(strstarts(str(?entity), 'http://nextprot.org/rdf#'))}}"
                        + "optional { ?entity rdf:type ?type filter(strstarts(str(?type), 'http://nextprot.org/rdf#'))}"
                        + "optional { ?entity rdfs:label ?label }"
                        + "optional { ?entity rdfs:comment ?comment }"
                        + "optional { ?entity rdfs:seeAlso ?reference }",
                "schema_bases(id, type, label, comment, reference" + (mode == Mode.INDIRECT ? ", iri" : "") + ")")
        // @formatter:on
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("entity", "http://nextprot.org/rdf#", entities, mode));
                setValue(2, getID("type", "http://nextprot.org/rdf#", entities, mode));
                setValue(3, getLiteralValue("label"));
                setValue(4, getLiteralValue("comment"));
                setValue(5, getLiteralValue("reference"));

                if(mode == Mode.INDIRECT)
                    setValue(6, getStringID("entity", "http://nextprot.org/rdf#"));
            }
        }.load();


        new DataLoader(model, connection, type,
                "?entity rdf:type owl:Class filter(strstarts(str(?entity), 'http://nextprot.org/rdf#'))",
                "schema_classes(entity)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("entity", "http://nextprot.org/rdf#", entities, mode));
            }
        }.load();


        new DataLoader(model, connection, type,
                "?entity rdfs:subClassOf owl:Thing filter(strstarts(str(?entity), 'http://nextprot.org/rdf#'))",
                "schema_thing_subclasses(entity)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("entity", "http://nextprot.org/rdf#", entities, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?entity :notIn ?notin", "schema_restrictions(entity, notin)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("entity", "http://nextprot.org/rdf#", entities, mode));
                setValue(2, getID("notin", "http://nextprot.org/rdf#", entities, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?entity :related ?related", "schema_related_terms(entity, related)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("entity", "http://nextprot.org/rdf#", entities, mode));
                setValue(2, getID("related", "http://nextprot.org/rdf/terminology/", terms, mode));
            }
        }.load();


        new DataLoader(model, connection, type,
                "?entity rdfs:subClassOf ?parent filter(strstarts(str(?entity), 'http://nextprot.org/rdf#')) "
                        + "filter(strstarts(str(?parent), 'http://nextprot.org/rdf#'))",
                "schema_parent_classes(entity, parent)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("entity", "http://nextprot.org/rdf#", entities, mode));
                setValue(2, getID("parent", "http://nextprot.org/rdf#", entities, mode));
            }
        }.load();
    }


    static void load(String path, Connection connection, Server type, Mode mode) throws IOException, SQLException
    {
        Model model = DataLoader.getModel(path, "schema.ttl.gz");
        model.add(DataLoader.getModel(path, "inferredSubClassOf-a.ttl.gz"));
        model.add(DataLoader.getModel(path, "inferredSubClassOf-b.ttl.gz"));

        loadSources(model, connection, type, mode);
        loadDatabases(model, connection, type, mode);
        loadSchema(model, connection, type, mode);

        model.close();
    }
}
