package cz.iocb.sparql.nextprot.combined;

import static cz.iocb.sparql.nextprot.combined.NeXtProtConfiguration.schema;
import static java.util.Arrays.asList;
import cz.iocb.chemweb.server.sparql.config.SparqlDatabaseConfiguration;
import cz.iocb.chemweb.server.sparql.database.Table;
import cz.iocb.chemweb.server.sparql.database.TableColumn;
import cz.iocb.chemweb.server.sparql.mapping.ConstantIriMapping;
import cz.iocb.chemweb.server.sparql.mapping.JoinTableQuadMapping.JoinColumns;
import cz.iocb.chemweb.server.sparql.mapping.NodeMapping;



public class Expression
{
    public static void addResourceClasses(SparqlDatabaseConfiguration config)
    {
    }


    public static void addQuadMappings(SparqlDatabaseConfiguration config)
    {
        ConstantIriMapping graph = config.createIriMapping("<http://nextprot.org/rdf>");

        {
            Table table = new Table(schema, "isoform_low_expressions");
            NodeMapping subject = config.createIriMapping("isoform", "iri");

            config.addQuadMapping(
                    asList(new Table(schema, "isoform_bases"), table, new Table(schema, "annotation_bases")),
                    asList(new JoinColumns(new TableColumn("id"), new TableColumn("isoform")),
                            new JoinColumns(new TableColumn("annotation"), new TableColumn("id"))),
                    graph, subject, config.createIriMapping(":lowExpression"),
                    config.createIriMapping("annotation", "iri"));
        }

        {
            Table table = new Table(schema, "isoform_medium_expressions");
            NodeMapping subject = config.createIriMapping("isoform", "iri");

            config.addQuadMapping(
                    asList(new Table(schema, "isoform_bases"), table, new Table(schema, "annotation_bases")),
                    asList(new JoinColumns(new TableColumn("id"), new TableColumn("isoform")),
                            new JoinColumns(new TableColumn("annotation"), new TableColumn("id"))),
                    graph, subject, config.createIriMapping(":mediumExpression"),
                    config.createIriMapping("annotation", "iri"));
        }

        {
            Table table = new Table(schema, "isoform_high_expressions");
            NodeMapping subject = config.createIriMapping("isoform", "iri");

            config.addQuadMapping(
                    asList(new Table(schema, "isoform_bases"), table, new Table(schema, "annotation_bases")),
                    asList(new JoinColumns(new TableColumn("id"), new TableColumn("isoform")),
                            new JoinColumns(new TableColumn("annotation"), new TableColumn("id"))),
                    graph, subject, config.createIriMapping(":highExpression"),
                    config.createIriMapping("annotation", "iri"));
        }
    }
}
