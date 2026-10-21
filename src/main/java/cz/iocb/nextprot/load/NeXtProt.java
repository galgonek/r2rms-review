package cz.iocb.nextprot.load;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



public class NeXtProt
{
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
