package cz.iocb.sparql.nextprot.rdfmixed;

import static cz.iocb.sparql.engine.database.SqlType.INT8;
import static cz.iocb.sparql.engine.mapping.classes.BuiltinClasses.box;
import static cz.iocb.sparql.engine.mapping.classes.BuiltinClasses.reference;
import static cz.iocb.sparql.engine.mapping.classes.DerivedClass.subtract;
import static cz.iocb.sparql.engine.mapping.classes.DerivedClass.unionize;
import static java.util.Arrays.asList;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import cz.iocb.sparql.engine.config.EngineSettings.JoinOptimizerMode;
import cz.iocb.sparql.engine.config.SparqlDatabaseConfiguration;
import cz.iocb.sparql.engine.database.DatabaseSchema;
import cz.iocb.sparql.engine.database.DatabaseTable;
import cz.iocb.sparql.engine.database.GroupedStatistics;
import cz.iocb.sparql.engine.database.TableColumn;
import cz.iocb.sparql.engine.mapping.ConstantIriMapping;
import cz.iocb.sparql.engine.mapping.JoinTableQuadMapping.JoinColumns;
import cz.iocb.sparql.engine.mapping.TermMapping;
import cz.iocb.sparql.engine.mapping.classes.ResourceClass;



/**
 * NeXtProt configuration over the {@code nextprot} schema loaded in the rdfmixed mode, where the table
 * {@code resources} numbers the IRIs and the blank nodes of the data, kept in boxes of the type {@code sparql.rdfbox},
 * the table {@code resource_triples} holds the triples whose objects are such resources as the numbers of their
 * subjects, predicates and objects, and the table {@code value_triples} holds the other triples, whose objects are
 * literals, as the numbers of their subjects and predicates with the objects in boxes, as a triple store that numbers
 * the resources and keeps the literals in place does. Two quad mappings map them: each joins a table of triples to the
 * table of the resources once for the subject and once for the predicate, the first one for the object as well.
 */
public class NeXtProtConfiguration extends SparqlDatabaseConfiguration
{
    /**
     * Database schema holding the NeXtProt tables.
     */
    static final String schema = "nextprot";

    /**
     * The class of the boxes of the numbered terms: an IRI or a blank node.
     */
    static final ResourceClass resource = unionize(Set.of(reference), box);

    /**
     * The class of the boxes of the objects that are not numbered: any term but an IRI and a blank node.
     */
    static final ResourceClass value = subtract(box, reference);


    /**
     * Creates the configuration (JNDI-compatible constructor).
     */
    public NeXtProtConfiguration(String service, DataSource connectionPool, DatabaseSchema schema) throws SQLException
    {
        super(service, connectionPool, schema);

        addPrefixes();
        addQuadMappings();
    }


    /**
     * Registers the prefixes used by the NeXtProt queries.
     */
    private void addPrefixes()
    {
        addPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        addPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        addPrefix("owl", "http://www.w3.org/2002/07/owl#");
        addPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");

        addPrefix("", "http://nextprot.org/rdf#");
        addPrefix("dc", "http://purl.org/dc/elements/1.1/");
        addPrefix("up", "http://purl.uniprot.org/core/");
        addPrefix("entry", "http://nextprot.org/rdf/entry/");
        addPrefix("isoform", "http://nextprot.org/rdf/isoform/");
        addPrefix("annotation", "http://nextprot.org/rdf/annotation/");
        addPrefix("evidence", "http://nextprot.org/rdf/evidence/");
        addPrefix("xref", "http://nextprot.org/rdf/xref/");
        addPrefix("publication", "http://nextprot.org/rdf/publication/");
        addPrefix("identifier", "http://nextprot.org/rdf/identifier/");
        addPrefix("cv", "http://nextprot.org/rdf/terminology/");
        addPrefix("gene", "http://nextprot.org/rdf/gene/");
        addPrefix("source", "http://nextprot.org/rdf/source/");
        addPrefix("db", "http://nextprot.org/rdf/db/");
        addPrefix("context", "http://nextprot.org/rdf/context/");
        addPrefix("interaction", "http://nextprot.org/rdf/interaction/");
        addPrefix("skos", "http://www.w3.org/2004/02/skos/core#");
        addPrefix("uniprot", "http://purl.uniprot.org/uniprot/");
        addPrefix("unipage", "http://www.uniprot.org/uniprot/");
        addPrefix("proteoform", "http://nextprot.org/rdf/proteoform/");
        addPrefix("chebi", "http://purl.obolibrary.org/obo/");
        addPrefix("drugbankdrugs", "http://wifo5-04.informatik.uni-mannheim.de/drugbank/resource/drugs/");
    }


    /**
     * Registers the quad mappings of the two tables of the triples, in the graph of the other modes, which the default
     * graph includes too. The boxes of the resources and the boxes of the other objects are mapped by disjoint classes,
     * so a triple pattern reads only the table that can hold its object. The table of the resources is a dictionary
     * table, so the engine reads the numbers of the constant terms of a query when it translates the query; its terms
     * are unique, which the engine has to be told, because the hash index they are found by cannot say it. The tables
     * of the triples are probed tables: a predicate mostly has objects of one kind, which a query does not tell, so the
     * engine asks each table for a triple of the predicate of a pattern when it translates the query, and reads only
     * the table that has one. The statistics of the tables of the triples are kept per predicate by the loader (see
     * {@code statistics.sql}), and the engine consults them when it orders the joins.
     */
    private void addQuadMappings()
    {
        DatabaseTable resources = new DatabaseTable(schema, "resources");
        DatabaseTable resourceTriples = new DatabaseTable(schema, "resource_triples");
        DatabaseTable valueTriples = new DatabaseTable(schema, "value_triples");

        TableColumn id = new TableColumn("id", INT8);
        ConstantIriMapping graph = createIriMapping("<http://nextprot.org/rdf>");
        TermMapping term = createTermMapping(resource, "term");

        getDatabaseSchema().addPrimaryKeys(resources, List.of(getColumn(resources, "term")));

        addQuadMapping(asList(resourceTriples, resources, resources, resources),
                asList(new JoinColumns(0, new TableColumn("subject", INT8), 1, id),
                        new JoinColumns(0, new TableColumn("predicate", INT8), 2, id),
                        new JoinColumns(0, new TableColumn("object", INT8), 3, id)),
                0, graph, 1, term, 2, term, 3, term);

        addQuadMapping(asList(valueTriples, resources, resources),
                asList(new JoinColumns(0, new TableColumn("subject", INT8), 1, id),
                        new JoinColumns(0, new TableColumn("predicate", INT8), 2, id)),
                0, graph, 1, term, 2, term, 0, createTermMapping(value, "object"));

        addDictionaryTable(resources);
        addProbedTable(resourceTriples);
        addProbedTable(valueTriples);

        for(DatabaseTable triples : List.of(resourceTriples, valueTriples))
            addTableStatistics(triples,
                    new GroupedStatistics(triples, (TableColumn) getColumn(triples, "predicate"),
                            new DatabaseTable(schema, triples.getName() + "_statistics"),
                            new DatabaseTable(schema, triples.getName() + "_values")));

        setEngineSettings(getEngineSettings().withJoinOptimizer(JoinOptimizerMode.SETTINGS));
    }
}
