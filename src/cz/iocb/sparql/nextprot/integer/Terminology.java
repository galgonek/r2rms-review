package cz.iocb.sparql.nextprot.integer;

import static cz.iocb.chemweb.server.sparql.mapping.classes.BuiltinClasses.xsdString;
import static cz.iocb.sparql.nextprot.integer.NeXtProtConfiguration.schema;
import cz.iocb.chemweb.server.sparql.config.SparqlDatabaseConfiguration;
import cz.iocb.chemweb.server.sparql.database.Table;
import cz.iocb.chemweb.server.sparql.database.TableColumn;
import cz.iocb.chemweb.server.sparql.mapping.ConstantIriMapping;
import cz.iocb.chemweb.server.sparql.mapping.NodeMapping;
import cz.iocb.chemweb.server.sparql.mapping.classes.MapUserIriClass;



public class Terminology
{
    public static void addResourceClasses(SparqlDatabaseConfiguration config)
    {
        config.addIriClass(new MapUserIriClass("terminology", "integer", new Table("nextprot", "terminology_bases"),
                new TableColumn("id"), new TableColumn("iri"), "http://nextprot.org/rdf/terminology/", 0));
    }


    public static void addQuadMappings(SparqlDatabaseConfiguration config)
    {
        ConstantIriMapping graph = config.createIriMapping("<http://nextprot.org/rdf>");

        {
            Table table = new Table(schema, "terminology_bases");
            NodeMapping subject = config.createIriMapping("terminology", "id");

            config.addQuadMapping(table, graph, subject, config.createIriMapping("rdf:type"),
                    config.createIriMapping("schema", "type"));
        }

        {
            Table table = new Table(schema, "terminology_parents");
            NodeMapping subject = config.createIriMapping("terminology", "term");

            config.addQuadMapping(table, graph, subject, config.createIriMapping(":childOf"),
                    config.createIriMapping("terminology", "parent"));
        }

        {
            Table table = new Table(schema, "terminology_related_terms");
            NodeMapping subject = config.createIriMapping("terminology", "term");

            config.addQuadMapping(table, graph, subject, config.createIriMapping(":related"),
                    config.createIriMapping("terminology", "related"));
        }

        {
            Table table = new Table(schema, "terminology_labels");
            NodeMapping subject = config.createIriMapping("terminology", "term");

            config.addQuadMapping(table, graph, subject, config.createIriMapping("rdfs:label"),
                    config.createLiteralMapping(xsdString, "label"));
        }
    }
}
