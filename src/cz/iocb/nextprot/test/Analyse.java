package cz.iocb.nextprot.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map.Entry;



public class Analyse
{
    record Predicate(String from, String to, String pred)
    {
    }


    record Chain(String from, String middle, String to, String pred1, String pred2)
    {
    }


    public static void main(String[] args)
    {
        HashMap<String, Integer> classes = new HashMap<String, Integer>();
        HashMap<Predicate, Integer> preds = new HashMap<Predicate, Integer>();
        HashMap<Chain, Integer> chains = new HashMap<Chain, Integer>();


        try(BufferedReader br = Files.newBufferedReader(Paths.get("/home/galgonek/stats/CLASSES.tsv")))
        {
            String line = br.readLine();
            while((line = br.readLine()) != null)
            {
                String[] values = line.split("\t");
                classes.put(values[0], Integer.parseInt(values[1].replaceFirst("\"(.*)\".*", "$1")));
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }


        try(BufferedReader br = Files.newBufferedReader(Paths.get("/home/galgonek/stats/PREDS.tsv")))
        {
            String line = br.readLine();
            while((line = br.readLine()) != null)
            {
                String[] values = line.split("\t");
                Predicate key = new Predicate(values[0], values[1], values[2]);
                preds.put(key, Integer.parseInt(values[3].replaceFirst("\"(.*)\".*", "$1")));
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        try(BufferedReader br = Files.newBufferedReader(Paths.get("/home/galgonek/stats/JOINS.tsv")))
        {
            String line = br.readLine();
            while((line = br.readLine()) != null)
            {
                String[] values = line.split("\t");
                Chain key = new Chain(values[0], values[1], values[2], values[3], values[4]);
                chains.put(key, Integer.parseInt(values[5].replaceFirst("\"(.*)\".*", "$1")));
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }


        try(PrintWriter out = new PrintWriter(Files.newBufferedWriter(Path.of("/home/galgonek/stats/stats.txt"),
                StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)))
        {
            for(Entry<Chain, Integer> c : chains.entrySet())
            {
                Integer dom = classes.get(c.getKey().middle);

                Integer n1 = preds.get(new Predicate(c.getKey().from, c.getKey().middle, c.getKey().pred1));
                Integer n2 = preds.get(new Predicate(c.getKey().middle, c.getKey().to, c.getKey().pred2));

                int count = c.getValue();
                double est = (double) n1 * (double) n2 / (double) dom;

                double ratio = est / count;

                if(count > 1000 && (ratio < 0.01 || ratio > 100))
                    System.err.println(c.getKey() + ": " + ((long) est) + " " + count + " " + ratio);

                if(count > 100)
                    out.println(((long) est) + " " + count + " " + ratio);
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

}
