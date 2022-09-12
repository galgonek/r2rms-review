package cz.iocb.sparql.nextprot;


import java.io.File;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;



public class Server
{
    public static void main(String[] args) throws LifecycleException
    {
        Tomcat tomcat = new Tomcat();
        tomcat.enableNaming();
        tomcat.setPort(8080);
        tomcat.getConnector();

        Context context = tomcat.addContext("", (new File(System.getProperty("java.io.tmpdir"))).getAbsolutePath());

        ContextResource datasource = new ContextResource();
        datasource.setName("jdbc");
        datasource.setAuth("Container");
        datasource.setType("javax.sql.DataSource");
        datasource.setScope("Sharable");
        datasource.setCloseMethod("close");
        datasource.setProperty("driverClassName", "org.postgresql.Driver");
        datasource.setProperty("url", "jdbc:postgresql://localhost:5432/nextprot");
        datasource.setProperty("username", "sparql");
        datasource.setProperty("password", "password");
        datasource.setProperty("factory", "org.apache.tomcat.jdbc.pool.DataSourceFactory");
        datasource.setProperty("initialSize", "1");
        datasource.setProperty("maxActive", "1");
        datasource.setProperty("maxIdle", "1");
        datasource.setProperty("minIdle", "1");
        datasource.setProperty("maxWait", "-1");
        datasource.setProperty("testOnBorrow", "true");
        datasource.setProperty("validationQuery", "SELECT 1");
        datasource.setProperty("testWhileIdle", "false");
        datasource.setProperty("removeAbandoned", "false");
        datasource.setProperty("timeBetweenEvictionRunsMillis", "0");
        context.getNamingResources().addResource(datasource);

        ContextResource config = new ContextResource();
        config.setName("config");
        config.setAuth("Container");
        config.setType("cz.iocb.sparql.nextprot." + args[0] + ".NeXtProtConfiguration");
        config.setSingleton(true);
        config.setProperty("factory", "cz.iocb.chemweb.server.sparql.config.SparqlDatabaseConfigurationFactory");
        config.setProperty("datasource", "jdbc");
        context.getNamingResources().addResource(config);

        Wrapper servlet = context.createWrapper();
        servlet.setName("default");
        servlet.addInitParameter("resource", "config");
        servlet.addInitParameter("fetch-size", "0");
        servlet.setServletClass("cz.iocb.chemweb.server.servlets.endpoint.EndpointServlet");
        servlet.setLoadOnStartup(1);
        servlet.setOverridable(true);

        context.addChild(servlet);
        context.addServletMappingDecoded("/sparql/", "default");

        tomcat.start();
        tomcat.getServer().await();
    }
}
