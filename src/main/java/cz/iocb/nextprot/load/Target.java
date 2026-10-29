package cz.iocb.nextprot.load;

import java.io.IOException;
import java.sql.SQLException;



/*
 * Where the loaders store the rows: a database connected by JDBC, or a file of SQL statements.
 */
public interface Target extends AutoCloseable
{
    /*
     * Prepares the insertion into the table given as its name followed by the list of its columns.
     */
    Table table(String table) throws IOException, SQLException;


    /*
     * Makes the stored rows permanent once all of them have been stored.
     */
    void commit() throws IOException, SQLException;


    @Override
    void close() throws IOException, SQLException;
}
