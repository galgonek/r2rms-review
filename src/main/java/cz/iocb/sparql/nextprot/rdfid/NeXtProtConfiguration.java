package cz.iocb.sparql.nextprot.rdfid;

import static cz.iocb.sparql.engine.database.SqlType.INT8;
import static cz.iocb.sparql.engine.mapping.classes.BuiltinClasses.box;
import static java.util.Arrays.asList;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import cz.iocb.sparql.engine.config.SparqlDatabaseConfiguration;
import cz.iocb.sparql.engine.database.DatabaseSchema;
import cz.iocb.sparql.engine.database.DatabaseTable;
import cz.iocb.sparql.engine.database.GroupedStatistics;
import cz.iocb.sparql.engine.database.TableColumn;
import cz.iocb.sparql.engine.mapping.JoinTableQuadMapping.JoinColumns;
import cz.iocb.sparql.engine.mapping.TermMapping;



/**
 * NeXtProt configuration over the {@code nextprot} schema loaded in the rdfid mode, where the table {@code terms}
 * numbers all the terms of the data, kept in boxes of the type {@code sparql.rdfbox}, and the single table
 * {@code triples} holds the triples as the numbers of their subjects, predicates and objects, as a dictionary-encoded
 * triple store keeps them. One quad mapping maps them all: it joins the table of the triples to the table of the terms
 * once for the subject, once for the predicate and once for the object.
 */
public class NeXtProtConfiguration extends SparqlDatabaseConfiguration
{
    /**
     * Database schema holding the NeXtProt tables.
     */
    static final String schema = "nextprot";


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
     * Registers the quad mapping of the tables of the triples and of the terms, in the graph of the other modes, which
     * the default graph includes too. The table of the terms is a dictionary table, so the engine reads the numbers of
     * the constant terms of a query when it translates the query; its terms are unique, which the engine has to be
     * told, because the hash index they are found by cannot say it. The statistics of the table of the triples are kept
     * per predicate by the loader (see {@code statistics.sql}), and the engine consults them when it orders the joins.
     */
    private void addQuadMappings()
    {
        DatabaseTable triples = new DatabaseTable(schema, "triples");
        DatabaseTable terms = new DatabaseTable(schema, "terms");

        TableColumn id = new TableColumn("id", INT8);
        TermMapping term = createTermMapping(box, "term");

        getDatabaseSchema().addPrimaryKeys(terms, List.of(getColumn(terms, "term")));

        addQuadMapping(asList(triples, terms, terms, terms),
                asList(new JoinColumns(0, new TableColumn("subject", INT8), 1, id),
                        new JoinColumns(0, new TableColumn("predicate", INT8), 2, id),
                        new JoinColumns(0, new TableColumn("object", INT8), 3, id)),
                0, createIriMapping("<http://nextprot.org/rdf>"), 1, term, 2, term, 3, term);

        addDictionaryTable(terms);

        addTableStatistics(triples, new GroupedStatistics(triples, (TableColumn) getColumn(triples, "predicate"),
                new DatabaseTable(schema, "triples_statistics"), new DatabaseTable(schema, "triples_values")));

        setJoinOptimizer(true);
    }
}
