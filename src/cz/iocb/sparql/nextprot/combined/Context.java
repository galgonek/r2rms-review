package cz.iocb.sparql.nextprot.combined;

import static cz.iocb.sparql.nextprot.combined.NeXtProtConfiguration.schema;
import static java.util.Arrays.asList;
import cz.iocb.chemweb.server.sparql.config.SparqlDatabaseConfiguration;
import cz.iocb.chemweb.server.sparql.database.Table;
import cz.iocb.chemweb.server.sparql.database.TableColumn;
import cz.iocb.chemweb.server.sparql.mapping.ConstantIriMapping;
import cz.iocb.chemweb.server.sparql.mapping.JoinTableQuadMapping.JoinColumns;
import cz.iocb.chemweb.server.sparql.mapping.NodeMapping;
import cz.iocb.chemweb.server.sparql.mapping.classes.IntegerUserIriClass;



public class Context
{
    public static void addResourceClasses(SparqlDatabaseConfiguration config)
    {
        config.addIriClass(new IntegerUserIriClass("context", "integer", "http://nextprot.org/rdf/context/"));
    }


    public static void addQuadMappings(SparqlDatabaseConfiguration config)
    {
        ConstantIriMapping graph = config.createIriMapping("<http://nextprot.org/rdf>");

        {
            Table table = new Table(schema, "context_bases");
            NodeMapping subject = config.createIriMapping("context", "id");

            config.addQuadMapping(table, graph, subject, config.createIriMapping("rdf:type"),
                    config.createIriMapping(":ExperimentalContext"));
            config.addQuadMapping(table, graph, subject, config.createIriMapping(":metadata"),
                    config.createIriMapping("publication", "metadata"));
            config.addQuadMapping(asList(table, new Table(schema, "terminology_bases")),
                    asList(new JoinColumns(new TableColumn("method"), new TableColumn("id"))), graph, subject,
                    config.createIriMapping(":detectionMethod"), config.createIriMapping("terminology", "iri"));
            config.addQuadMapping(asList(table, new Table(schema, "terminology_bases")),
                    asList(new JoinColumns(new TableColumn("disease"), new TableColumn("id"))), graph, subject,
                    config.createIriMapping(":disease"), config.createIriMapping("terminology", "iri"));
            config.addQuadMapping(asList(table, new Table(schema, "terminology_bases")),
                    asList(new JoinColumns(new TableColumn("tissue"), new TableColumn("id"))), graph, subject,
                    config.createIriMapping(":tissue"), config.createIriMapping("terminology", "iri"));
            config.addQuadMapping(asList(table, new Table(schema, "terminology_bases")),
                    asList(new JoinColumns(new TableColumn("line"), new TableColumn("id"))), graph, subject,
                    config.createIriMapping(":cellLine"), config.createIriMapping("terminology", "iri"));
            config.addQuadMapping(asList(table, new Table(schema, "terminology_bases")),
                    asList(new JoinColumns(new TableColumn("stage"), new TableColumn("id"))), graph, subject,
                    config.createIriMapping(":developmentalStage"), config.createIriMapping("terminology", "iri"));
        }
    }
}
