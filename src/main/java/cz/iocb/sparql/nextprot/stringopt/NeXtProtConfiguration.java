package cz.iocb.sparql.nextprot.stringopt;

import java.sql.SQLException;
import javax.sql.DataSource;
import cz.iocb.sparql.engine.database.DatabaseSchema;



/**
 * NeXtProt configuration of the string variant (see {@link cz.iocb.sparql.nextprot.string.NeXtProtConfiguration})
 * whose joins the engine chooses itself, from the statistics of the tables, instead of leaving them to the planner of
 * the database (see {@link cz.iocb.sparql.engine.config.SparqlDatabaseConfiguration#setJoinOptimizer}).
 */
public class NeXtProtConfiguration extends cz.iocb.sparql.nextprot.string.NeXtProtConfiguration
{
    /**
     * Creates the configuration (JNDI-compatible constructor).
     */
    public NeXtProtConfiguration(String service, DataSource connectionPool, DatabaseSchema schema) throws SQLException
    {
        super(service, connectionPool, schema);

        setJoinOptimizer(true);
    }
}
