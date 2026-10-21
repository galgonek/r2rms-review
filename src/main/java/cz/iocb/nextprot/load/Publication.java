package cz.iocb.nextprot.load;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.jena.rdf.model.Model;
import cz.iocb.nextprot.load.NeXtProt.Mode;
import cz.iocb.nextprot.load.NeXtProt.Server;



public class Publication
{
    public static void load(String path, String file, Connection connection, Server type, Mode mode)
            throws IOException, SQLException
    {
        Model model = DataLoader.getModel(path, file);

        // @formatter:off
        new DataLoader(model, connection, type,
                "?publication rdf:type :Publication "
                        + "bind (exists { ?publication rdf:type :LargeScalePublication } as ?large)"
                        + "optional { ?publication :title ?title }"
                        + "optional { ?publication :journal ?journal }"
                        + "optional { ?publication :year ?year }"
                        + "optional { ?publication :volume ?volume }"
                        + "optional { ?publication :issue ?issue }"
                        + "optional { ?publication :pubType ?pubType }"
                        + "optional { ?publication :firstPage ?firstPage }"
                        + "optional { ?publication :lastPage ?lastPage }"
                        + "optional { ?publication :publisher ?publisher }"
                        + "optional { ?publication :city ?city }",
                "publication_bases"
                        + "(id, large, title, journal, year, volume, issue, pub_type, first_page, last_page, publisher, city)")
        // @formatter:on
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getIntID("publication", "http://nextprot.org/rdf/publication/"));
                setValue(2, getBooleanValue("large"));
                setValue(3, getLiteralValue("title"));
                setValue(4, getLiteralValue("journal"));
                setValue(5, getLiteralValue("year"));
                setValue(6, getLiteralValue("volume"));
                setValue(7, getLiteralValue("issue"));
                setValue(8, getLiteralValue("pubType"));
                setValue(9, getLiteralValue("firstPage"));
                setValue(10, getLiteralValue("lastPage"));
                setValue(11, getLiteralValue("publisher"));
                setValue(12, getLiteralValue("city"));
            }
        }.load();


        new DataLoader(model, connection, type, "?publication :from ?link", "publication_links(publication, link)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getIntID("publication", "http://nextprot.org/rdf/publication/"));
                setValue(2, getLiteralValue("link"));
            }
        }.load();


        // @formatter:off
        new DataLoader(model, connection, type,
                "?publication :author ?author "
                        + "bind (exists { ?author rdf:type :Person } as ?person)"
                        + "optional { ?author :name ?name }"
                        + "optional { ?author :suffix ?suffix }",
                "publication_authors(id, publication, person, name, suffix)")
        // @formatter:on
        {
            int id = 0;

            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, id++);
                setValue(2, getIntID("publication", "http://nextprot.org/rdf/publication/"));
                setValue(3, getBooleanValue("person"));
                setValue(4, getLiteralValue("name"));
                setValue(5, getLiteralValue("suffix"));
            }
        }.load();


        // @formatter:off
        new DataLoader(model, connection, type,
                "?publication :editor ?editor. ?editor rdf:type :Person. "
                        + "optional {?editor :name ?name}",
                "publication_editors(id, publication, name)")
        // @formatter:on
        {
            int id = 0;

            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, id++);
                setValue(2, getIntID("publication", "http://nextprot.org/rdf/publication/"));
                setValue(3, getLiteralValue("name"));
            }
        }.load();

        model.close();
    }


    public static void load(String path, Connection connection, Server type, Mode mode) throws IOException, SQLException
    {
        load(path, "publication.ttl.gz", connection, type, mode);
    }
}
