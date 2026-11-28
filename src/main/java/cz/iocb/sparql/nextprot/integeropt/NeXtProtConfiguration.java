package cz.iocb.sparql.nextprot.integeropt;

import java.sql.SQLException;
import javax.sql.DataSource;
import cz.iocb.sparql.engine.config.EngineSettings.JoinOptimizerMode;
import cz.iocb.sparql.engine.database.DatabaseSchema;



/**
 * NeXtProt configuration of the integer variant (see {@link cz.iocb.sparql.nextprot.integer.NeXtProtConfiguration})
 * whose joins the engine chooses itself, from the statistics of the tables, instead of leaving them to the planner of
 * the database (see {@link cz.iocb.sparql.engine.config.EngineSettings#joinOptimizer()}).
 */
public class NeXtProtConfiguration extends cz.iocb.sparql.nextprot.integer.NeXtProtConfiguration
{
    /**
     * Creates the configuration (JNDI-compatible constructor).
     */
    public NeXtProtConfiguration(String service, DataSource connectionPool, DatabaseSchema schema) throws SQLException
    {
        super(service, connectionPool, schema);

        setEngineSettings(getEngineSettings().withJoinOptimizer(JoinOptimizerMode.SETTINGS));
    }
}
