package cz.iocb.nextprot.load;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.zip.GZIPInputStream;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;
import cz.iocb.nextprot.load.NeXtProt.Mode;
import cz.iocb.nextprot.load.NeXtProt.Server;



public abstract class DataLoader
{
    // @formatter:off
    static protected String prefixes = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
            + "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
            + "prefix owl: <http://www.w3.org/2002/07/owl#> "
            + "prefix : <http://nextprot.org/rdf#> "
            + "prefix db: <http://nextprot.org/rdf/db/> "
            + "prefix skos: <http://www.w3.org/2004/02/skos/core#> ";
    // @formatter:on

    static final protected int batchSize = 10000;

    final protected Connection connection;
    protected PreparedStatement statement = null;
    protected boolean set;

    protected QuerySolution solution = null;
    final protected Model model;
    final protected String sparql;
    final protected String sql;


    public DataLoader(Model model, Connection connection, Server type, String sparql, String sql)
    {
        System.out.println("   store " + sql.replaceFirst("\\(.*", ""));

        StringBuilder values = new StringBuilder(" values(?");

        for(int i = 0; i < sql.length(); i++)
            if(sql.charAt(i) == ',')
                values.append(",?");

        values.append(")");

        if(type == Server.POSTGRESQL)
            sql = "insert into nextprot." + sql + values + " on conflict do nothing";
        else if(type == Server.VIRTUOSO)
            sql = "insert soft nextprot.nextprot." + sql + values;
        else if(type == Server.MARIADB)
            sql = "insert ignore into nextprot." + sql + values;
        else
            sql = null;

        this.connection = connection;
        this.model = model;
        this.sparql = sparql;
        this.sql = sql;
    }


    public static Model getModel(String path, String file) throws IOException
    {
        System.out.println("load " + file);

        Model model = ModelFactory.createDefaultModel();

        try(InputStream in = new BufferedInputStream(
                new GZIPInputStream(new FileInputStream(new File(path, file)), 65536)))
        {
            return model.read(in, null, "TTL");
        }
    }


    public void load() throws SQLException, IOException
    {
        try(PreparedStatement insertStatement = connection.prepareStatement(sql))
        {
            statement = insertStatement;
            int count = 0;

            Query query = QueryFactory.create(prefixes + "select * where { " + sparql + " }");

            try(QueryExecution qexec = QueryExecutionFactory.create(query, model))
            {
                ResultSet results = qexec.execSelect();
                while(results.hasNext())
                {
                    solution = results.nextSolution();
                    set = false;

                    insert();

                    if(!set)
                        continue;

                    insertStatement.addBatch();

                    if(++count % batchSize == 0)
                        insertStatement.executeBatch();
                }
            }

            if(count % batchSize != 0)
                insertStatement.executeBatch();
        }
    }


    public abstract void insert() throws SQLException, IOException;


    protected String getIRI(String name)
    {
        Resource resource = solution.getResource(name);

        if(resource == null)
            return null;

        return resource.getURI();
    }


    protected String getBlankNode(String name)
    {
        Resource resource = solution.getResource(name);

        if(resource == null)
            return null;

        return resource.getId().getLabelString();
    }


    protected Integer getIntID(String name, String prefix) throws IOException
    {
        Resource resource = solution.getResource(name);

        if(resource == null)
            return null;

        String value = resource.getURI();

        if(!value.startsWith(prefix))
            throw new IOException();

        return Integer.parseInt(value.substring(prefix.length()));
    }


    protected String getStringID(String name, String prefix) throws IOException
    {
        Resource resource = solution.getResource(name);

        if(resource == null)
            return null;

        String value = resource.getURI();

        if(!value.startsWith(prefix))
            throw new IOException("wrong value: " + value);

        return value.substring(prefix.length());
    }


    protected Object getID(String name, String prefix, ObjectIntHashMap<String> map, Mode mode) throws IOException
    {
        Resource resource = solution.getResource(name);

        if(resource == null)
            return null;

        String iri = resource.getURI();

        if(!iri.startsWith(prefix))
            throw new IOException("wrong value: " + iri);

        String value = iri.substring(prefix.length());

        if(mode == Mode.DIRECT)
            return value;

        int id = map.getIfAbsent(value, Integer.MIN_VALUE);

        if(id == Integer.MIN_VALUE)
        {
            id = map.size();
            map.put(value, id);
        }

        return id;
    }


    protected String getLiteralValue(String name)
    {
        Literal literal = solution.getLiteral(name);

        if(literal == null)
            return null;

        return literal.getString();
    }


    protected Integer getIntValue(String name)
    {
        Literal literal = solution.getLiteral(name);

        if(literal == null)
            return null;

        return literal.getInt();
    }


    protected Double getDoubleValue(String name)
    {
        Literal literal = solution.getLiteral(name);

        if(literal == null)
            return null;

        return literal.getDouble();
    }


    protected Boolean getBooleanValue(String name)
    {
        Literal literal = solution.getLiteral(name);

        if(literal == null)
            return null;

        return literal.getBoolean();
    }


    protected void setNull(int idx, int sqlType) throws SQLException
    {
        set = true;
        statement.setNull(idx, sqlType);
    }


    protected void setValue(int idx, Object value) throws SQLException
    {
        set = true;
        statement.setObject(idx, value);
    }


    protected void setValue(int idx, int value) throws SQLException
    {
        set = true;
        statement.setInt(idx, value);
    }


    protected void setValue(int idx, short value) throws SQLException
    {
        set = true;
        statement.setInt(idx, value);
    }


    protected void setValue(int idx, float value) throws SQLException
    {
        set = true;
        statement.setFloat(idx, value);
    }


    protected void setValue(int idx, Long value) throws SQLException
    {
        set = true;

        if(value != null)
            statement.setLong(idx, value);
        else
            statement.setNull(idx, Types.BIGINT);
    }


    protected void setValue(int idx, Integer value) throws SQLException
    {
        set = true;

        if(value != null)
            statement.setInt(idx, value);
        else
            statement.setNull(idx, Types.INTEGER);
    }


    protected void setValue(int idx, Short value) throws SQLException
    {
        set = true;

        if(value != null)
            statement.setInt(idx, value);
        else
            statement.setNull(idx, Types.SMALLINT);
    }


    protected void setValue(int idx, Float value) throws SQLException
    {
        set = true;

        if(value != null)
            statement.setFloat(idx, value);
        else
            statement.setNull(idx, Types.FLOAT);
    }


    protected void setValue(int idx, Double value) throws SQLException
    {
        set = true;

        if(value != null)
            statement.setDouble(idx, value);
        else
            statement.setNull(idx, Types.DOUBLE);
    }


    protected void setValue(int idx, String value) throws SQLException
    {
        set = true;

        if(value != null)
            statement.setString(idx, value);
        else
            statement.setNull(idx, Types.VARCHAR);
    }
}
