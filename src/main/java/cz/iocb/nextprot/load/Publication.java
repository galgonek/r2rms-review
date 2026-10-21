package cz.iocb.nextprot.load;

import static cz.iocb.nextprot.load.NeXtProt.nextprot;
import static cz.iocb.nextprot.load.NeXtProt.rdf;
import static cz.iocb.nextprot.load.Nodes.getIntID;
import static cz.iocb.nextprot.load.Nodes.getLiteralValue;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import cz.iocb.nextprot.load.NeXtProt.Mode;
import cz.iocb.nextprot.load.NeXtProt.Server;



public class Publication
{
    public static void load(String path, String file, Connection connection, Server type, Mode mode)
            throws IOException, SQLException
    {
        TripleDispatcher dispatcher = new TripleDispatcher();

        LinkedHashSet<Node> publications = dispatcher.instances(nextprot + "Publication");
        Node largeScalePublication = NodeFactory.createURI(nextprot + "LargeScalePublication");
        Node person = NodeFactory.createURI(nextprot + "Person");

        Values types = dispatcher.values(rdf + "type");
        Values titles = dispatcher.values(nextprot + "title");
        Values journals = dispatcher.values(nextprot + "journal");
        Values years = dispatcher.values(nextprot + "year");
        Values volumes = dispatcher.values(nextprot + "volume");
        Values issues = dispatcher.values(nextprot + "issue");
        Values pubTypes = dispatcher.values(nextprot + "pubType");
        Values firstPages = dispatcher.values(nextprot + "firstPage");
        Values lastPages = dispatcher.values(nextprot + "lastPage");
        Values publishers = dispatcher.values(nextprot + "publisher");
        Values cities = dispatcher.values(nextprot + "city");
        Values authors = dispatcher.values(nextprot + "author");
        Values editors = dispatcher.values(nextprot + "editor");
        Values names = dispatcher.values(nextprot + "name");
        Values suffixes = dispatcher.values(nextprot + "suffix");

        Table links = dispatcher.table(connection, type, "publication_links(publication, link)");

        dispatcher.on(nextprot + "from", (publication, link) -> {
            Integer id = getIntID(publication, "http://nextprot.org/rdf/publication/");

            if(!links.isNew(id, getLiteralValue(link)))
                return;

            links.setValue(1, id);
            links.setValue(2, getLiteralValue(link));
            links.add();
        });

        dispatcher.after(() -> {
            try(Table table = new Table(connection, type, "publication_bases(id, large, title, journal, year, "
                    + "volume, issue, pub_type, first_page, last_page, publisher, city)"))
            {
                for(Node publication : publications)
                {
                    Integer id = getIntID(publication, "http://nextprot.org/rdf/publication/");

                    if(!table.isNew(id))
                        continue;

                    table.setValue(1, id);
                    table.setValue(2, types.contains(publication, largeScalePublication));
                    table.setValue(3, getLiteralValue(titles.first(publication)));
                    table.setValue(4, getLiteralValue(journals.first(publication)));
                    table.setValue(5, getLiteralValue(years.first(publication)));
                    table.setValue(6, getLiteralValue(volumes.first(publication)));
                    table.setValue(7, getLiteralValue(issues.first(publication)));
                    table.setValue(8, getLiteralValue(pubTypes.first(publication)));
                    table.setValue(9, getLiteralValue(firstPages.first(publication)));
                    table.setValue(10, getLiteralValue(lastPages.first(publication)));
                    table.setValue(11, getLiteralValue(publishers.first(publication)));
                    table.setValue(12, getLiteralValue(cities.first(publication)));
                    table.add();
                }
            }

            try(Table table = new Table(connection, type, "publication_authors(id, publication, person, name, suffix)"))
            {
                int id = 0;

                for(Node publication : authors.nodes())
                {
                    for(Node author : authors.get(publication))
                    {
                        table.setValue(1, id++);
                        table.setValue(2, getIntID(publication, "http://nextprot.org/rdf/publication/"));
                        table.setValue(3, types.contains(author, person));
                        table.setValue(4, getLiteralValue(names.first(author)));
                        table.setValue(5, getLiteralValue(suffixes.first(author)));
                        table.add();
                    }
                }
            }

            try(Table table = new Table(connection, type, "publication_editors(id, publication, name)"))
            {
                int id = 0;

                for(Node publication : editors.nodes())
                {
                    for(Node editor : editors.get(publication))
                    {
                        if(!types.contains(editor, person))
                            continue;

                        table.setValue(1, id++);
                        table.setValue(2, getIntID(publication, "http://nextprot.org/rdf/publication/"));
                        table.setValue(3, getLiteralValue(names.first(editor)));
                        table.add();
                    }
                }
            }
        });

        dispatcher.load(path, file);
    }


    public static void load(String path, Connection connection, Server type, Mode mode) throws IOException, SQLException
    {
        load(path, "publication.ttl.gz", connection, type, mode);
    }
}
