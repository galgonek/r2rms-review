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

        // a variant whose name ends with -adv is the variant of the name without the suffix whose joins are kept by
        // plan advice
        String variant = args[0].endsWith("-adv") ? args[0].substring(0, args[0].length() - 4) : args[0];

        ContextResource config = new ContextResource();
        config.setName("config");
        config.setAuth("Container");
        // the package of a variant is its name without the hyphens (string-opt in stringopt)
        config.setType("cz.iocb.sparql.nextprot." + variant.replace("-", "") + ".NeXtProtConfiguration");
        config.setSingleton(true);
        config.setProperty("factory", "cz.iocb.sparql.endpoint.SparqlDatabaseConfigurationFactory");
        config.setProperty("datasource", "jdbc");

        if(!variant.equals(args[0]))
            config.setProperty("join-optimizer", "advice");

        context.getNamingResources().addResource(config);

        Wrapper servlet = context.createWrapper();
        servlet.setName("default");
        servlet.addInitParameter("resource", "config");
        servlet.addInitParameter("fetch-size", "0");
        // the timeout of the other stores (in seconds), stated so that a change of the default does not change it
        servlet.addInitParameter("timeout", "1000");
        servlet.setServletClass("cz.iocb.sparql.endpoint.EndpointServlet");
        servlet.setLoadOnStartup(1);
        servlet.setOverridable(true);

        context.addChild(servlet);
        context.addServletMapping("/sparql/", "default");

        tomcat.start();
        tomcat.getServer().await();
    }
}
