package cz.iocb.nextprot.load;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
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


    public static void main(String[] args) throws IOException, SQLException
    {
        String url = args[0];
        Server type = Server.valueOf(args[1].toUpperCase());
        Mode mode = Mode.valueOf(args[2].toUpperCase());
        String path = args[3];

        try(Connection connection = DriverManager.getConnection(url))
        {
            if(type != Server.VIRTUOSO)
                connection.setAutoCommit(false);

            Schema.load(path, connection, type, mode);
            Terminology.load(path, connection, type, mode);
            Publication.load(path, connection, type, mode);
            Context.load(path, connection, type, mode);
            Chromosome.load(path, connection, type, mode);
            Expression.load(path, connection, type, mode);

            if(type != Server.VIRTUOSO)
                connection.commit();
        }
    }
}
