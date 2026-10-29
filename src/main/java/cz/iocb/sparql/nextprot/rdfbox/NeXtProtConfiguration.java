package cz.iocb.sparql.nextprot.rdfbox;

import static cz.iocb.sparql.engine.mapping.classes.BuiltinClasses.box;
import java.sql.SQLException;
import javax.sql.DataSource;
import cz.iocb.sparql.engine.config.SparqlDatabaseConfiguration;
import cz.iocb.sparql.engine.database.DatabaseSchema;
import cz.iocb.sparql.engine.database.DatabaseTable;



/**
 * NeXtProt configuration over the {@code nextprot} schema loaded in the rdf mode, where all the triples of the data are
 * stored as they are in the single table {@code triples}, whose subject, predicate and object are boxes of the type
 * {@code sparql.rdfbox}, so one quad mapping maps them all.
 */
public class NeXtProtConfiguration extends SparqlDatabaseConfiguration
{
    /**
     * Database schema holding the NeXtProt table.
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
     * Registers the quad mapping of the table of the triples, in the graph of the other modes, which the default graph
     * includes too.
     */
    private void addQuadMappings()
    {
        DatabaseTable table = new DatabaseTable(schema, "triples");

        addQuadMapping(table, createIriMapping("<http://nextprot.org/rdf>"), createTermMapping(box, "subject"),
                createTermMapping(box, "predicate"), createTermMapping(box, "object"));
    }
}
