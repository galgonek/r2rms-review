package cz.iocb.nextprot.load;

import static cz.iocb.nextprot.load.Chromosome.annotations;
import static cz.iocb.nextprot.load.Chromosome.isoforms;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.jena.rdf.model.Model;
import cz.iocb.nextprot.load.NeXtProt.Mode;
import cz.iocb.nextprot.load.NeXtProt.Server;



public class Expression
{
    public static void loadExpressionLow(String path, String file, Connection connection, Server type, Mode mode)
            throws IOException, SQLException
    {
        Model model = DataLoader.getModel(path, file);

        new DataLoader(model, connection, type, "?isoform :lowExpression ?annotation",
                "isoform_low_expressions(isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();

        model.close();
    }


    public static void loadExpressionMedium(String path, String file, Connection connection, Server type, Mode mode)
            throws IOException, SQLException
    {
        Model model = DataLoader.getModel(path, file);

        new DataLoader(model, connection, type, "?isoform :mediumExpression ?annotation",
                "isoform_medium_expressions(isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();

        model.close();
    }


    public static void loadExpressionHigh(String path, String file, Connection connection, Server type, Mode mode)
            throws IOException, SQLException
    {
        Model model = DataLoader.getModel(path, file);

        new DataLoader(model, connection, type, "?isoform :highExpression ?annotation",
                "isoform_high_expressions(isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();

        model.close();
    }


    public static void load(String path, Connection connection, Server type, Mode mode) throws IOException, SQLException
    {
        loadExpressionLow(path, "expression_low.ttl.gz", connection, type, mode);
        loadExpressionMedium(path, "expression_medium.ttl.gz", connection, type, mode);
        loadExpressionHigh(path, "expression_high.ttl.gz", connection, type, mode);
    }
}
