package cz.iocb.nextprot.load;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import cz.iocb.nextprot.load.NeXtProt.Mode;



/*
 * The targets that the loaders store the rows into, all of them at once, so that the data are read only once. Each
 * target holds the schema of its mode.
 */
public class Targets implements AutoCloseable
{
    private final List<Target> direct = new ArrayList<>();
    private final List<Target> indirect = new ArrayList<>();


    public void add(Target target, Mode mode)
    {
        (mode == Mode.DIRECT ? direct : indirect).add(target);
    }


    /*
     * Prepares the insertion into the table given as its name followed by the list of its columns in the indirect
     * mode.
     */
    public Table table(String table) throws IOException, SQLException
    {
        return new Table(table, direct, indirect);
    }


    public void commit() throws IOException, SQLException
    {
        for(Target target : direct)
            target.commit();

        for(Target target : indirect)
            target.commit();
    }


    /*
     * Closes all the targets, also when some of them fail.
     */
    @Override
    public void close() throws IOException, SQLException
    {
        Exception failure = null;

        for(Target target : direct)
            failure = close(target, failure);

        for(Target target : indirect)
            failure = close(target, failure);

        if(failure instanceof IOException exception)
            throw exception;

        if(failure instanceof SQLException exception)
            throw exception;
    }


    private static Exception close(Target target, Exception failure)
    {
        try
        {
            target.close();
        }
        catch(IOException | SQLException exception)
        {
            if(failure == null)
                return exception;

            failure.addSuppressed(exception);
        }

        return failure;
    }
}
