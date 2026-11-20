package cz.iocb.nextprot.test;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import cz.iocb.nextprot.test.QueryCheck.Difference;
import cz.iocb.nextprot.test.QueryCheck.Finding;
import cz.iocb.nextprot.test.QueryCheck.Row;
import cz.iocb.nextprot.test.QueryCheck.Source;



/*
 * Checks the results of the engines on a query set against the reference results, and prints the results that differ
 * from them.
 *
 * The results of the query set lie in a directory with a subdirectory for each engine (named by bin/test.sh after the
 * server, the mapping, the method and the number of CPUs), which has a subdirectory run-<i> for each run, holding
 * <query>.xml for a query that the engine answered in time; a query that failed or timed out, or that was not run after
 * it had timed out in an earlier run, has <query>.txt instead, and is ignored, as is an older <query>.xml left there by
 * an earlier test. Only the results of the last run of each
 * engine are checked. The reference results lie in another
 * directory as <query>.xml. Each result is compared with the reference of its query as QueryCheck describes, using the
 * queries of the query set if they are given.
 *
 * The results that differ in the same way are printed together, by the query, with some of the solutions they miss
 * (-) and of those the reference does not have (+); then a summary by the engines follows, which counts the results
 * that differ apart from those that differ by the datatypes of the same values. The exit status is 0 if all results
 * agree with their references, 1 if some differ (by datatypes too), and 2 if the check cannot be done, or not for all
 * queries (no result is compared, or a reference cannot be read).
 */
public class Check
{
    private static final String usage = """
            usage: Check [--queries <file>] [--examples <count>] [--threads <count>] <results> <references> [<engine>...]
              <results>      the directory of the results of the query set, with a directory for each engine
              <references>   the directory of the reference results of the query set
              <engine>       the name of the directory of an engine to check, may be a glob pattern; all by default
              --queries      the file of the queries of the query set, which tells what differences are allowed
              --examples     how many of the solutions that differ are shown, 5 by default
              --threads      how many results are read in parallel, the number of the processors by default
            """;

    private static final int width = 120;


    public static void main(String[] args)
    {
        int status;

        try
        {
            status = check(args);
        }
        catch(Exception | Error e)
        {
            e.printStackTrace();
            status = 2;
        }

        System.exit(status);
    }


    /*
     * Checks the results as the arguments ask; returns the exit status.
     */
    private static int check(String[] args) throws IOException, InterruptedException, ExecutionException
    {
        Path queries = null;
        int examples = 5;
        int threads = Runtime.getRuntime().availableProcessors();
        List<String> arguments = new ArrayList<>();

        try
        {
            for(int i = 0; i < args.length; i++)
            {
                switch(args[i])
                {
                    case "--queries" -> queries = Path.of(args[++i]);
                    case "--examples" -> examples = Integer.parseInt(args[++i]);
                    case "--threads" -> threads = Integer.parseInt(args[++i]);
                    default -> arguments.add(args[i]);
                }
            }
        }
        catch(IndexOutOfBoundsException | NumberFormatException e)
        {
            arguments.clear();
        }

        if(arguments.size() < 2 || arguments.stream().anyMatch(argument -> argument.startsWith("--")) || examples < 0
                || threads < 1)
        {
            System.err.print(usage);
            return 2;
        }

        Map<String, PathMatcher> patterns = new LinkedHashMap<>();

        for(String pattern : arguments.subList(2, arguments.size()))
        {
            try
            {
                patterns.put(pattern, FileSystems.getDefault().getPathMatcher("glob:" + pattern));
            }
            catch(IllegalArgumentException e)
            {
                System.err.println("error: invalid engine pattern " + pattern + ": " + e.getMessage());
                return 2;
            }
        }

        Path results = Path.of(arguments.get(0));
        Path references = Path.of(arguments.get(1));

        if(!Files.isDirectory(results) || !Files.isDirectory(references))
        {
            System.err.println("error: " + (Files.isDirectory(results) ? references : results) + " is not a directory");
            return 2;
        }

        Map<String, QueryShape> shapes = Map.of();

        if(queries != null && Files.isRegularFile(queries))
            shapes = QueryShape.read(queries, System.err);
        else if(queries != null)
            System.err.println("warning: " + queries + " does not exist, the results are compared as they are");

        // the results by the queries, and the numbers of the checked results, of the results that differ, and of those
        // that differ by datatypes, by the engines
        SortedMap<String, List<Source>> sources = new TreeMap<>();
        Map<String, int[]> counts = new LinkedHashMap<>();
        Set<String> matched = new HashSet<>();

        for(Path engine : directories(results, "*"))
        {
            List<String> matching = patterns.entrySet().stream()
                    .filter(pattern -> pattern.getValue().matches(engine.getFileName())).map(Entry::getKey).toList();

            if(!patterns.isEmpty() && matching.isEmpty())
                continue;

            List<Path> runs = directories(engine, "run-*");

            if(runs.isEmpty())
                continue;

            matched.addAll(matching);
            counts.put(engine.getFileName().toString(), new int[3]);

            // only the last run, as ordered by the numbers of the runs
            Path run = runs.getLast();

            for(Path file : files(run))
                sources.computeIfAbsent(id(file), _ -> new ArrayList<>())
                        .add(new Source(engine.getFileName().toString(), run.getFileName().toString(), file));
        }

        for(String pattern : patterns.keySet())
        {
            if(!matched.contains(pattern))
            {
                System.err.println("error: no results of an engine " + pattern + " in " + results);
                return 2;
            }
        }

        if(counts.isEmpty())
        {
            System.err.println("error: no results of any engine in " + results);
            return 2;
        }

        List<String> unchecked = new ArrayList<>();
        List<String> unreadable = new ArrayList<>();
        Map<String, List<String>> notes = new LinkedHashMap<>();
        boolean differs = false;

        ExecutorService pool = Executors.newFixedThreadPool(threads, Thread.ofPlatform().daemon().factory());

        for(Entry<String, List<Source>> query : sources.entrySet())
        {
            String id = query.getKey();
            Path reference = references.resolve(id + ".xml");

            if(!Files.isRegularFile(reference))
            {
                unchecked.add(id);
                continue;
            }

            System.err.println("> " + id);

            QueryCheck check = new QueryCheck(reference, shapes.getOrDefault(id, QueryShape.unknown), query.getValue(),
                    pool, examples);

            String error = check.run();

            if(error != null)
            {
                System.out.println(id + ": unreadable reference: " + error);
                System.out.println();
                unreadable.add(id);
                continue;
            }

            for(Source source : query.getValue())
                counts.get(source.engine())[0]++;

            for(Finding finding : check.findings())
            {
                if(finding.difference().differs)
                    counts.get(finding.source().engine())[1]++;

                if(finding.difference().datatypes > 0)
                    counts.get(finding.source().engine())[2]++;
            }

            if(!check.notes().isEmpty())
                notes.put(id, check.notes());

            if(!check.findings().isEmpty())
            {
                if(!differs)
                    System.out.println(
                            "the results that differ from the references (- a missing solution of the reference, + "
                                    + "an unexpected solution):\n");

                print(System.out, id, check);
                differs = true;
            }
        }

        pool.shutdown();

        summary(System.out, counts, notes, unchecked, unreadable);

        if(!unreadable.isEmpty() || counts.values().stream().allMatch(count -> count[0] == 0))
            return 2;

        return differs ? 1 : 0;
    }


    /*
     * Prints the results of the query that differ from the reference, those that differ in the same way together.
     */
    private static void print(PrintStream out, String id, QueryCheck check)
    {
        Map<Difference, List<Source>> groups = new LinkedHashMap<>();

        for(Finding finding : check.findings())
            groups.computeIfAbsent(finding.difference(), _ -> new ArrayList<>()).add(finding.source());

        out.println(id + " (the reference has "
                + (check.count() < 0 ? "a boolean" : QueryCheck.solutions(check.count())) + ")");

        for(String note : check.notes())
            out.println("  note: " + note);

        for(Entry<Difference, List<Source>> group : groups.entrySet())
        {
            Difference difference = group.getKey();

            List<String> engines = new ArrayList<>();

            for(Source source : group.getValue())
                engines.add(source.engine());

            String line = "  ";

            for(int i = 0; i < engines.size(); i++)
            {
                String engine = engines.get(i) + (i < engines.size() - 1 ? "," : ":");

                if(line.length() > 2 && line.length() + 1 + engine.length() > width)
                {
                    out.println(line);
                    line = "  ";
                }

                line += (line.length() > 2 ? " " : "") + engine;
            }

            out.println(line);

            for(String problem : difference.problems)
                out.println("    " + problem);

            print(out, "-", difference.missingRows, difference.missing, check.variables());
            print(out, "+", difference.unexpectedRows, difference.unexpected, check.variables());
        }

        out.println();
    }


    /*
     * Prints the solutions shown, and how many solutions are not.
     */
    private static void print(PrintStream out, String sign, Map<Row, Integer> rows, long count, List<String> variables)
    {
        long shown = 0;

        for(Entry<Row, Integer> row : rows.entrySet())
        {
            StringBuilder text = new StringBuilder("    " + sign);

            List<String> names = new ArrayList<>(variables);

            for(String variable : new TreeSet<>(row.getKey().solution().keySet()))
                if(!names.contains(variable))
                    names.add(variable);

            for(String variable : names)
            {
                Term term = row.getKey().solution().get(variable);
                text.append(" ?").append(variable).append('=').append(term == null ? "UNDEF" : Terms.format(term));
            }

            if(row.getValue() > 1)
                text.append(" (").append(row.getValue()).append(" times)");

            out.println(text);
            shown += row.getValue();
        }

        if(count > shown)
            out.println("    " + sign + " ... " + (shown > 0 ? "and " : "") + QueryCheck.solutions(count - shown)
                    + (shown > 0 ? " more" : " not shown"));
    }


    /*
     * Prints the numbers of the checked results, of the results that differ and of those that differ by the datatypes
     * of the same values, by the engines, how the comparison of the
     * results of the queries was limited, which queries have results but no reference, and which references cannot be
     * read.
     */
    private static void summary(PrintStream out, Map<String, int[]> counts, Map<String, List<String>> notes,
            List<String> unchecked, List<String> unreadable)
    {
        int length = Math.max("engine".length(), counts.keySet().stream().mapToInt(String::length).max().orElse(0));
        String format = "%-" + length + "s %9s %9s %9s%n";
        int[] total = new int[3];

        out.println("summary (the numbers of the results of the last runs, one result for a query):");
        out.printf(format, "engine", "compared", "different", "datatypes");

        for(Entry<String, int[]> engine : counts.entrySet())
        {
            out.printf(format, engine.getKey(), engine.getValue()[0], engine.getValue()[1], engine.getValue()[2]);

            for(int i = 0; i < total.length; i++)
                total[i] += engine.getValue()[i];
        }

        out.printf(format, "total", total[0], total[1], total[2]);

        if(!notes.isEmpty())
        {
            out.println();
            out.println("queries whose results are compared with the allowed differences:");

            for(Entry<String, List<String>> query : notes.entrySet())
                for(String note : query.getValue())
                    out.println("  " + query.getKey() + ": " + note);
        }

        if(!unchecked.isEmpty())
        {
            out.println();
            out.println("queries with results but no reference: " + String.join(" ", unchecked));
        }

        if(!unreadable.isEmpty())
        {
            out.println();
            out.println("queries whose references cannot be read: " + String.join(" ", unreadable));
        }
    }


    private static List<Path> directories(Path directory, String glob) throws IOException
    {
        List<Path> directories = new ArrayList<>();

        try(DirectoryStream<Path> stream = Files.newDirectoryStream(directory, glob))
        {
            for(Path path : stream)
                if(Files.isDirectory(path))
                    directories.add(path);
        }

        directories.sort(Comparator.comparing((Path path) -> path.getFileName().toString(), Check::compareNames)
                .thenComparing(path -> path.getFileName().toString()));

        return directories;
    }


    /*
     * Compares two names in the order of their characters, the numbers in them by their values (run-2 before run-10).
     */
    private static int compareNames(String first, String second)
    {
        int i = 0;
        int j = 0;

        while(i < first.length() && j < second.length())
        {
            if(isDigit(first.charAt(i)) && isDigit(second.charAt(j)))
            {
                int start = i;

                while(i < first.length() && isDigit(first.charAt(i)))
                    i++;

                String x = first.substring(start, i).replaceFirst("^0+(?=.)", "");

                start = j;

                while(j < second.length() && isDigit(second.charAt(j)))
                    j++;

                String y = second.substring(start, j).replaceFirst("^0+(?=.)", "");
                int result = x.length() != y.length() ? Integer.compare(x.length(), y.length()) : x.compareTo(y);

                if(result != 0)
                    return result;
            }
            else if(first.charAt(i) != second.charAt(j))
            {
                return Character.compare(first.charAt(i), second.charAt(j));
            }
            else
            {
                i++;
                j++;
            }
        }

        return Integer.compare(first.length() - i, second.length() - j);
    }


    private static boolean isDigit(char c)
    {
        return c >= '0' && c <= '9';
    }


    /*
     * Returns the results in the directory of a run, leaving out a result older than the <query>.txt of a later test,
     * which returned no result.
     */
    private static List<Path> files(Path directory) throws IOException
    {
        List<Path> files = new ArrayList<>();

        try(DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.xml"))
        {
            for(Path path : stream)
            {
                if(!Files.isRegularFile(path))
                    continue;

                Path failure = path.resolveSibling(id(path) + ".txt");

                if(Files.isRegularFile(failure)
                        && Files.getLastModifiedTime(failure).compareTo(Files.getLastModifiedTime(path)) > 0)
                {
                    System.err.println("warning: " + path + " is older than " + failure.getFileName()
                            + ", the result of the last test, which is ignored");
                    continue;
                }

                files.add(path);
            }
        }

        return files;
    }


    private static String id(Path file)
    {
        String name = file.getFileName().toString();
        return name.substring(0, name.length() - ".xml".length());
    }
}
