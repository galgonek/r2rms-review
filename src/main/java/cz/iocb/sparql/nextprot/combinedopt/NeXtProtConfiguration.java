package cz.iocb.sparql.nextprot.combinedopt;

import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import cz.iocb.sparql.engine.database.DatabaseSchema;
import cz.iocb.sparql.engine.database.DatabaseTable;



/**
 * NeXtProt configuration of the combined variant (see {@link cz.iocb.sparql.nextprot.combined.NeXtProtConfiguration})
 * whose joins the engine chooses itself, from the statistics of the tables, instead of leaving them to the planner of
 * the database (see {@link cz.iocb.sparql.engine.config.SparqlDatabaseConfiguration#setJoinOptimizer}). The tables
 * giving the IRIs of the entities for their numbers are dictionary tables (see
 * {@link cz.iocb.sparql.engine.config.SparqlDatabaseConfiguration#addDictionaryTable}): the engine reads the numbers of
 * the constant IRIs of a query when it translates the query, so that the tables referring to the entities are
 * restricted, and estimated, by the numbers, and the query does not join the dictionary for its constants.
 */
public class NeXtProtConfiguration extends cz.iocb.sparql.nextprot.combined.NeXtProtConfiguration
{
    /**
     * The tables giving the IRIs of the entities for their numbers: each has its numbers in the primary key
     * {@code id} and its IRIs in the unique column {@code iri}.
     */
    private static final List<String> dictionaryTables = List.of("annotation_bases", "database_bases",
            "entry_bases", "evidence_bases", "gene_bases", "isoform_bases", "proteoform_bases", "schema_bases",
            "source_bases", "terminology_bases");


    /**
     * Creates the configuration (JNDI-compatible constructor).
     */
    public NeXtProtConfiguration(String service, DataSource connectionPool, DatabaseSchema schema) throws SQLException
    {
        super(service, connectionPool, schema);

        for(String table : dictionaryTables)
            addDictionaryTable(new DatabaseTable("nextprot", table));

        setJoinOptimizer(true);
    }
}
