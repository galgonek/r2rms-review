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
        INDIRECT, DIRECT;
    }


    protected static enum Server
    {
        POSTGRESQL, MARIADB, VIRTUOSO;
    }


    /*
     * Loads the data either into a database, given by its JDBC URL, or into a file of SQL statements that load them
     * into a database. The kind of the database server is given either way.
     */
    public static void main(String[] args) throws IOException, SQLException
    {
        if(args.length != 4)
        {
            System.err.println("usage: NeXtProt <jdbc-url>|<sql-file>[.gz] postgresql|mariadb|virtuoso "
                    + "direct|indirect <data-dir>");
            System.exit(1);
        }

        String output = args[0];
        Server type = Server.valueOf(args[1].toUpperCase());
        Mode mode = Mode.valueOf(args[2].toUpperCase());
        String path = args[3];

        try(Target target = output.startsWith("jdbc:") ? new JdbcTarget(output, type) : SqlTarget.create(output, type))
        {
            Schema.load(path, target, mode);
            Terminology.load(path, target, mode);
            Publication.load(path, target, mode);
            Context.load(path, target, mode);
            Chromosome.load(path, target, mode);
            Expression.load(path, target, mode);

            target.commit();
        }
    }
}
