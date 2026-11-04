package cz.iocb.nextprot.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;



public class Test
{
    // @formatter:off
    static String prefixes = """
        prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
        prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>
        prefix xsd: <http://www.w3.org/2001/XMLSchema#>
        prefix : <http://nextprot.org/rdf#>
        prefix cv: <http://nextprot.org/rdf/terminology/>
        prefix db: <http://nextprot.org/rdf/db/>
        prefix entry: <http://nextprot.org/rdf/entry/>
        prefix source: <http://nextprot.org/rdf/source/>
        prefix chebi: <http://purl.obolibrary.org/obo/>
        prefix skos: <http://www.w3.org/2004/02/skos/core#>
        """;
    // @formatter:on


    public static void main(String[] args) throws Exception
    {
        String endpoint = args[0];
        String settings = args[1];
        String queries = args[2];
        String path = args[3];
        int count = Integer.parseInt(args[4]);

        for(int i = 1; i <= count; i++)
            (new File(path + "/run-" + i)).mkdirs();

        try(PrintStream out = new PrintStream(path + "/times.txt"))
        {
            exec(endpoint, settings, queries, path, out, count);
        }
    }


    public static void exec(String endpoint, String settings, String queries, String path, PrintStream out, int limit)
            throws Exception
    {
        try(BufferedReader reader = new BufferedReader(new FileReader(queries)))
        {
            String line = null;

            String id = null;
            StringBuffer query = null;

            while((line = reader.readLine()) != null)
            {
                if(line.startsWith("### "))
                {
                    if(id != null)
                        evaluate(endpoint, id, settings, query.toString(), path, out, limit);

                    id = line.substring(4, line.length() - 4);
                    query = new StringBuffer();
                }
                else if(!line.isEmpty())
                {
                    query.append(line);
                    query.append('\n');
                }
            }

            if(id != null)
                evaluate(endpoint, id, settings, query.toString(), path, out, limit);
        }
    }


    private static void evaluate(String endpoint, String id, String settings, String query, String path,
            PrintStream out, int count) throws Exception
    {
        System.err.println("> " + id);

        out.print(id);

        for(int i = 1; i <= count; i++)
        {
            Thread.sleep(2500);

            String rndQuery = query + "\n#RND" + Math.random() + " " + Math.random();
            String url = endpoint + (endpoint.contains("?") ? "&" : "?") + "query="
                    + URLEncoder.encode(settings + "\n" + prefixes + rndQuery, "UTF-8");

            long time = System.nanoTime();

            URL request = (new URI(url)).toURL();
            HttpURLConnection con = (HttpURLConnection) request.openConnection();
            con.setRequestProperty("accept", "application/sparql-results+xml");
            con.setRequestMethod("GET");

            Path result = Path.of(path, "run-" + i, id + ".tmp");

            try(InputStream in = con.getInputStream())
            {
                Files.copy(in, result, StandardCopyOption.REPLACE_EXISTING);

                time = System.nanoTime() - time;

                if(time <= 1000 * 1000000000l)
                {
                    out.printf(" %5d.%09d", time / 1000000000, time % 1000000000);
                    Files.move(result, result.resolveSibling(id + ".xml"), StandardCopyOption.REPLACE_EXISTING);
                }
                else
                {
                    out.print("              NA");
                    Files.move(result, result.resolveSibling(id + ".txt"), StandardCopyOption.REPLACE_EXISTING);
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();

                out.print("              NA");

                try(InputStream in = con.getErrorStream())
                {
                    if(in != null)
                        Files.copy(in, result, StandardCopyOption.REPLACE_EXISTING);
                    else
                        Files.write(result, new byte[0]);
                }

                Files.move(result, result.resolveSibling(id + ".txt"), StandardCopyOption.REPLACE_EXISTING);
            }
        }

        out.println();
    }
}
