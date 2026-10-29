package cz.iocb.nextprot.load;

import java.io.IOException;
import java.sql.SQLException;



public class NeXtProt
{
    static final String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    static final String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
    static final String owl = "http://www.w3.org/2002/07/owl#";
    static final String skos = "http://www.w3.org/2004/02/skos/core#";
    static final String nextprot = "http://nextprot.org/rdf#";
    static final String db = "http://nextprot.org/rdf/db/";


    protected static enum Mode
    {
        INDIRECT, DIRECT, RDF;
    }


    protected static enum Server
    {
        POSTGRESQL, MARIADB, VIRTUOSO;
    }


    /*
     * Loads the data into one or more targets at once, each of them given by three arguments: a database, given by its
     * JDBC URL, or a file of SQL statements that load the data into a database, the kind of the database server, and
     * the mode of the schema. The triples of the rdf mode are written into a file for PostgreSQL only, while the
     * loaders of the other modes read the data.
     */
    public static void main(String[] args) throws IOException, SQLException
    {
        if(args.length < 4 || args.length % 3 != 1)
        {
            System.err.println("usage: NeXtProt (<jdbc-url>|<sql-file>[.gz] postgresql|mariadb|virtuoso "
                    + "direct|indirect|rdf)... <data-dir>");
            System.exit(1);
        }

        String path = args[args.length - 1];

        try(Targets targets = new Targets())
        {
            String triples = null;

            for(int i = 0; i < args.length - 1; i += 3)
            {
                String output = args[i];
                Server type = Server.valueOf(args[i + 1].toUpperCase());
                Mode mode = Mode.valueOf(args[i + 2].toUpperCase());

                if(mode == Mode.RDF)
                {
                    if(triples != null || output.startsWith("jdbc:") || type != Server.POSTGRESQL)
                        throw new IllegalArgumentException("the rdf mode needs one SQL file for PostgreSQL");

                    triples = output;
                    continue;
                }

                Target target = output.startsWith("jdbc:") ? new JdbcTarget(output, type) :
                        SqlTarget.create(output, type);

                targets.add(target, mode);
            }

            try(TripleFile _ = triples != null ? new TripleFile(triples, path) : null)
            {
                if(!targets.isEmpty())
                {
                    Schema.load(path, targets);
                    Terminology.load(path, targets);
                    Publication.load(path, targets);
                    Context.load(path, targets);
                    Chromosome.load(path, targets);
                    Expression.load(path, targets);

                    targets.commit();
                }
            }
        }
    }
}
