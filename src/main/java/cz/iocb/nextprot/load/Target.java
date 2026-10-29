package cz.iocb.nextprot.load;

import java.io.IOException;
import java.sql.SQLException;



/*
 * Where the rows are stored: a database connected by JDBC, or a file of SQL statements.
 */
public interface Target extends AutoCloseable
{
    /*
     * Receives the rows of a table, each given by the values of its columns. An array of values passed to add() is not
     * changed afterwards, so that a sink may keep it.
     */
    interface Sink extends AutoCloseable
    {
        void add(Object[] values) throws IOException, SQLException;


        /*
         * Stores the rows that have been added and not stored yet and releases the resources of the sink.
         */
        @Override
        void close() throws IOException, SQLException;
    }


    /*
     * Prepares the insertion into the table given as its name followed by the list of its columns.
     */
    Sink sink(String table) throws IOException, SQLException;


    /*
     * Makes the stored rows permanent once all of them have been stored.
     */
    void commit() throws IOException, SQLException;


    @Override
    void close() throws IOException, SQLException;
}
