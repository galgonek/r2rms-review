package cz.iocb.nextprot.test;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;



/*
 * The check of the results of one query against its reference result.
 *
 * A result is compared with the reference as a multiset of solutions, whatever their order: each solution gets a key
 * made of the keys of its terms, which stand for their values (see Terms), and the multisets of the hashes of the keys
 * are compared, so that a result of any size takes little memory. The solutions that differ are then read again, to
 * pair the missing solutions with the unexpected ones that differ only by approximately equal numbers, and to show
 * them.
 *
 * The query tells which differences are allowed. A variable computed by SAMPLE (directly, or by an expression, BIND or
 * a subquery) is compared only by being bound, and one projected as GROUP_CONCAT as the multiset of the parts between
 * its separators. The results of a REDUCED query are compared as
 * sets. A result cut by LIMIT (when the reference has as many solutions as the limit allows) or by OFFSET may hold any
 * of the solutions that its query orders equally at the cut: when the query orders its solutions by projected
 * variables, the solutions ordered equally (with approximately equal numbers) with the first solution of the reference
 * (if the query has an offset) and with the last one (if the limit cuts) are compared only by their numbers and the
 * others as multisets; an unordered or REDUCED result that is cut is compared only by its number of solutions. The
 * solutions of a query whose subquery cuts its solutions by LIMIT or OFFSET without ordering them, or in which a value
 * computed by SAMPLE decides other values or solutions, are not compared at all, as even their number is not
 * determined.
 */
final class QueryCheck
{
    enum Mode
    {
        // the solutions are compared as multisets
        FULL,

        // the solutions ordered equally with those at the cuts are compared by their numbers, the others as multisets
        BOUNDARY,

        // only the numbers of the solutions are compared
        COUNT,

        // the solutions are not compared
        NONE
    }


    /*
     * A result of an engine: its file in the directory of a run.
     */
    record Source(String engine, String run, Path file)
    {
    }


    /*
     * A solution read again: its terms and the keys of its terms.
     */
    record Row(SortedMap<String, Term> solution, Map<String, String> keys)
    {
    }


    /*
     * How a result differs from the reference: the problems as texts, the numbers of the missing and of the unexpected
     * solutions with a hash of their multisets, and some of the missing and of the unexpected solutions with the
     * numbers of their occurrences. Two results with the same problems and the same solutions missing and unexpected
     * differ equally.
     */
    static final class Difference
    {
        final List<String> problems;
        final long missing;
        final long unexpected;
        final long signature;
        final Map<Row, Integer> missingRows;
        final Map<Row, Integer> unexpectedRows;


        Difference(List<String> problems, long missing, long unexpected, long signature,
                Map<Row, Integer> missingRows, Map<Row, Integer> unexpectedRows)
        {
            this.problems = problems;
            this.missing = missing;
            this.unexpected = unexpected;
            this.signature = signature;
            this.missingRows = missingRows;
            this.unexpectedRows = unexpectedRows;
        }


        @Override
        public boolean equals(Object object)
        {
            return object instanceof Difference other && problems.equals(other.problems) && missing == other.missing
                    && unexpected == other.unexpected && signature == other.signature;
        }


        @Override
        public int hashCode()
        {
            return Objects.hash(problems, missing, unexpected, signature);
        }
    }


    record Finding(Source source, Difference difference)
    {
    }


    /*
     * What the first reading of a result found out: its head, the number of its solutions, the hashes of the keys of
     * the solutions and of their order keys in the order of the file, the occurrences of its blank nodes by their
     * labels, and its first and last solution; or the error that made the file unreadable.
     */
    private static final class Digest
    {
        List<String> variables;
        Boolean value;
        long count;
        LongList rows = new LongList();
        LongList orders = new LongList();
        final Map<String, int[]> blankNodes = new HashMap<>();
        SortedMap<String, Term> first;
        SortedMap<String, Term> last;
        String error;
    }


    /*
     * A result that differs from the reference before the approximate numbers are compared: the hashes of its missing
     * and unexpected solutions (sorted; only those to be shown if the result is large), the numbers of its solutions
     * ordered equally with the solutions of the reference at the cuts, and whether its blank nodes are shared
     * differently.
     */
    private static final class Pending
    {
        final Source source;
        final long count;
        final boolean unreadable;
        final List<String> problems;
        final boolean blankNodes;
        final long[] tied;
        final long[] expectedTied;
        final boolean large;
        long missingCount;
        long unexpectedCount;
        long signature;
        long[] missing;
        long[] unexpected;
        Map<Long, Row> missingRows = Map.of();
        Map<Long, Row> unexpectedRows = Map.of();


        Pending(Source source, long count, boolean unreadable, List<String> problems, boolean blankNodes, long[] tied,
                long[] expectedTied, long[] missing, long[] unexpected, int examples)
        {
            this.source = source;
            this.count = count;
            this.unreadable = unreadable;
            this.problems = problems;
            this.blankNodes = blankNodes;
            this.tied = tied;
            this.expectedTied = expectedTied;
            this.large = missing.length > rowLimit || unexpected.length > rowLimit;
            this.missingCount = missing.length;
            this.unexpectedCount = unexpected.length;

            if(large)
            {
                // too many solutions differ to pair them, only some of them are shown
                this.signature = signature(missing, unexpected);
                this.missing = distinct(missing, examples);
                this.unexpected = distinct(unexpected, examples);
            }
            else
            {
                this.missing = missing;
                this.unexpected = unexpected;
            }
        }


        /*
         * Whether all the solutions that differ were read again, so that they can be paired approximately.
         */
        boolean isComplete()
        {
            return !large && Arrays.stream(missing).allMatch(missingRows::containsKey)
                    && Arrays.stream(unexpected).allMatch(unexpectedRows::containsKey);
        }
    }


    /*
     * A growing array of longs.
     */
    private static final class LongList
    {
        private long[] values = new long[16];
        private int size;


        void add(long value)
        {
            if(size == values.length)
                values = Arrays.copyOf(values, 2 * size);

            values[size++] = value;
        }


        long get(int index)
        {
            return values[index];
        }


        int size()
        {
            return size;
        }


        long[] toArray()
        {
            return Arrays.copyOf(values, size);
        }
    }


    // the most solutions of each side of a difference that are paired approximately
    private static final int rowLimit = 100_000;

    // the most comparisons of solutions spent by the approximate pairing of one result
    private static final long comparisonLimit = 20_000_000;

    private static final long[] none = new long[0];
    private static final VarHandle longs = MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);
    private static final ThreadLocal<MessageDigest> digests = ThreadLocal.withInitial(QueryCheck::md5);

    private final Path reference;
    private final QueryShape shape;
    private final List<Source> sources;
    private final ExecutorService pool;
    private final int examples;

    private Digest expected;
    private Mode mode;
    private long[] solutions;
    private long distinctSolutions;
    private long[] boundary = none;
    private final List<SortedMap<String, Term>> boundarySolutions = new ArrayList<>();
    private long[] expectedTied = none;
    private final List<Finding> findings = new ArrayList<>();


    /*
     * Prepares the check of the results of the query against the reference, which reads the results in parallel in the
     * pool and keeps the given number of the solutions that differ to show them.
     */
    QueryCheck(Path reference, QueryShape shape, List<Source> sources, ExecutorService pool, int examples)
    {
        this.reference = reference;
        this.shape = shape;
        this.sources = sources;
        this.pool = pool;
        this.examples = examples;
    }


    /*
     * Compares the results with the reference; returns the error that makes the reference unreadable, or null.
     */
    String run() throws InterruptedException, ExecutionException
    {
        expected = digest(reference, true, shape.order() != null && (shape.limit() >= 0 || shape.offset() > 0), true);

        if(expected.error != null)
            return expected.error;

        long[] all = expected.rows.toArray();
        Arrays.sort(all);
        distinctSolutions = distinct(all, Integer.MAX_VALUE).length;

        if(expected.value == null && shape.undetermined() != null)
            mode = Mode.NONE;
        else if(expected.value != null || !shape.isCut(expected.count))
            mode = Mode.FULL;
        else if(shape.order() != null && !shape.reduced())
            mode = Mode.BOUNDARY;
        else
            mode = Mode.COUNT;

        if(mode == Mode.BOUNDARY && expected.count > 0)
        {
            if(shape.offset() > 0)
                addBoundary(expected.orders.get(0), expected.first);

            if(shape.limit() >= 0 && expected.count >= shape.limit())
                addBoundary(expected.orders.get(expected.orders.size() - 1), expected.last);

            expectedTied = tied(expected);
        }

        solutions = solutions(expected);
        expected.rows = null;
        expected.orders = null;

        List<Future<Pending>> comparisons = new ArrayList<>();

        for(Source source : sources)
            comparisons.add(pool.submit(() -> compare(source, digest(source.file(), solutions != null,
                    mode == Mode.BOUNDARY, mode == Mode.FULL))));

        List<Pending> pendings = new ArrayList<>();

        for(Future<Pending> comparison : comparisons)
            if(comparison.get() != null)
                pendings.add(comparison.get());

        read(pendings);

        List<Future<Difference>> differences = new ArrayList<>();

        for(Pending pending : pendings)
            differences.add(pool.submit(() -> {
                pair(pending);
                untie(pending);
                return difference(pending);
            }));

        for(int i = 0; i < pendings.size(); i++)
            if(differences.get(i).get() != null)
                findings.add(new Finding(pendings.get(i).source, differences.get(i).get()));

        return null;
    }


    private void addBoundary(long key, SortedMap<String, Term> solution)
    {
        if(Arrays.stream(boundary).noneMatch(k -> k == key))
        {
            boundary = Arrays.copyOf(boundary, boundary.length + 1);
            boundary[boundary.length - 1] = key;
            boundarySolutions.add(solution);
        }
    }


    /*
     * Returns the results that differ from the reference, in the order of the sources.
     */
    List<Finding> findings()
    {
        return findings;
    }


    /*
     * Returns the number of the solutions of the reference, or -1 if it is the result of an ASK query.
     */
    long count()
    {
        return expected.value != null ? -1 : expected.count;
    }


    /*
     * Returns the variables of the reference.
     */
    List<String> variables()
    {
        return expected.variables;
    }


    /*
     * Returns how the comparison of the results is limited by the query, as texts.
     */
    List<String> notes()
    {
        List<String> notes = new ArrayList<>();

        if(mode == Mode.COUNT)
            notes.add("cut by " + cuts() + (shape.reduced() ? " and REDUCED" : shape.ordered()
                    ? " and not ordered by variables" : " and not ordered") + ", only the numbers of the solutions "
                    + "are compared");

        if(mode == Mode.BOUNDARY)
            notes.add("cut by " + cuts() + ", the solutions ordered equally with those at the cut are compared only "
                    + "by their numbers");

        if(mode == Mode.NONE)
        {
            notes.add(shape.undetermined() + ", the solutions are not compared");
            return notes;
        }

        for(String variable : new TreeSet<>(shape.samples()))
            notes.add("?" + variable + " is computed by SAMPLE, compared only as bound");

        for(String variable : new TreeSet<>(shape.separators().keySet()))
            notes.add("?" + variable + " is a GROUP_CONCAT, compared as the multiset of its parts");

        if(shape.reduced() && mode == Mode.FULL)
            notes.add("the query is REDUCED, the solutions are compared as sets");

        return notes;
    }


    private String cuts()
    {
        return shape.offset() > 0 && shape.limit() >= 0 && expected.count >= shape.limit() ? "LIMIT and OFFSET" :
                shape.offset() > 0 ? "OFFSET" : "LIMIT";
    }


    /*
     * Reads a result for the first time: counts its solutions and keeps the hashes of their keys, of their order keys,
     * and the occurrences of the blank nodes, as asked.
     */
    private Digest digest(Path file, boolean rows, boolean orders, boolean blankNodes)
    {
        Digest digest = new Digest();
        MessageDigest md5 = digests.get();

        try
        {
            ResultFile.Head head = ResultFile.read(file, solution -> {
                if(digest.count++ == 0)
                    digest.first = solution;

                digest.last = solution;

                if(rows)
                    digest.rows.add(hash(md5, key(solution)));

                if(orders)
                    digest.orders.add(hash(md5, orderKey(solution)));

                if(blankNodes)
                    for(Entry<String, Term> binding : solution.entrySet())
                        if(!shape.samples().contains(binding.getKey()))
                            countBlankNodes(binding.getValue(), digest.blankNodes);
            });

            digest.variables = head.variables();
            digest.value = head.value();
        }
        catch(IOException e)
        {
            digest.error = e.getClass().getSimpleName() + ": " + e.getMessage();
        }
        catch(ResultFile.InvalidResultException e)
        {
            digest.error = e.getMessage();
        }
        catch(RuntimeException e)
        {
            // what the XML parser throws on some malformed files
            digest.error = e.toString();
        }

        return digest;
    }


    /*
     * Compares the first reading of a result with the reference; returns the result if it may differ, or null.
     */
    private Pending compare(Source source, Digest result)
    {
        List<String> problems = new ArrayList<>();

        if(result.error != null)
        {
            problems.add("unreadable result: " + result.error);
            return new Pending(source, result.count, true, problems, false, none, none, none, none, examples);
        }

        if(expected.value != null || result.value != null)
        {
            if(!Objects.equals(expected.value, result.value))
                problems.add((result.value == null ? "solutions" : "the boolean " + result.value) + " instead of "
                        + (expected.value == null ? "solutions" : "the boolean " + expected.value));

            return problems.isEmpty() ? null :
                    new Pending(source, result.count, false, problems, false, none, none, none, none, examples);
        }

        if(!new HashSet<>(result.variables).equals(new HashSet<>(expected.variables)))
            problems.add("variables " + variables(result.variables) + " instead of " + variables(expected.variables));

        long[] missing = none;
        long[] unexpected = none;

        if(solutions != null)
        {
            long[] actual = solutions(result);
            LongList onlyExpected = new LongList();
            LongList onlyActual = new LongList();

            int i = 0;
            int j = 0;

            while(i < solutions.length && j < actual.length)
            {
                if(solutions[i] < actual[j])
                {
                    onlyExpected.add(solutions[i++]);
                }
                else if(solutions[i] > actual[j])
                {
                    onlyActual.add(actual[j++]);
                }
                else
                {
                    i++;
                    j++;
                }
            }

            while(i < solutions.length)
                onlyExpected.add(solutions[i++]);

            while(j < actual.length)
                onlyActual.add(actual[j++]);

            missing = onlyExpected.toArray();
            unexpected = onlyActual.toArray();
        }

        boolean blankNodes = mode == Mode.FULL && !shape.reduced()
                && !Arrays.equals(occurrences(expected), occurrences(result));

        long[] tied = mode == Mode.BOUNDARY ? tied(result) : none;

        if(problems.isEmpty() && missing.length == 0 && unexpected.length == 0 && !blankNodes
                && Arrays.equals(tied, expectedTied) && countProblem(result.count) == null)
            return null;

        return new Pending(source, result.count, false, problems, blankNodes, tied, expectedTied.clone(), missing,
                unexpected, examples);
    }


    /*
     * Returns the hashes of the solutions of a result that are compared as a multiset (as a set for a REDUCED query),
     * that is, all but those ordered equally with the solutions of the reference at the cuts, sorted; or null if the
     * solutions are not compared.
     */
    private long[] solutions(Digest digest)
    {
        if(mode == Mode.COUNT || mode == Mode.NONE)
            return null;

        long[] solutions;

        if(boundary.length == 0)
        {
            solutions = digest.rows.toArray();
        }
        else
        {
            LongList list = new LongList();

            for(int i = 0; i < digest.rows.size(); i++)
                if(boundary(digest.orders.get(i)) < 0)
                    list.add(digest.rows.get(i));

            solutions = list.toArray();
        }

        Arrays.sort(solutions);

        return shape.reduced() ? distinct(solutions, Integer.MAX_VALUE) : solutions;
    }


    /*
     * Returns the numbers of the solutions of a result ordered equally with each solution of the reference at a cut.
     */
    private long[] tied(Digest digest)
    {
        long[] tied = new long[boundary.length];

        for(int i = 0; i < digest.orders.size(); i++)
        {
            int index = boundary(digest.orders.get(i));

            if(index >= 0)
                tied[index]++;
        }

        return tied;
    }


    private int boundary(long key)
    {
        for(int i = 0; i < boundary.length; i++)
            if(boundary[i] == key)
                return i;

        return -1;
    }


    /*
     * Returns how the number of the solutions of a result differs from what the reference allows, or null.
     */
    private String countProblem(long count)
    {
        if(expected.value != null || mode == Mode.NONE)
            return null;

        if(!shape.reduced())
            return count == expected.count ? null : solutions(count) + " instead of " + expected.count;

        // a REDUCED query may keep some duplicates, but no more solutions than the limit, and if it is not cut by an
        // offset, at least those distinct among the solutions of the reference
        if(mode == Mode.COUNT && shape.limit() >= 0 && count > shape.limit())
            return solutions(count) + ", more than the limit " + shape.limit();

        if(shape.offset() == 0 && count < distinctSolutions)
            return solutions(count) + ", fewer than the " + distinctSolutions
                    + " distinct solutions of the reference";

        return null;
    }


    /*
     * Reads again the solutions of the reference that the results miss, and those of the results that the reference
     * does not have.
     */
    private void read(List<Pending> pendings) throws InterruptedException, ExecutionException
    {
        TreeSet<Long> wanted = new TreeSet<>();

        for(Pending pending : pendings)
            for(long hash : distinct(pending.missing, Integer.MAX_VALUE))
                wanted.add(hash);

        long[] hashes = wanted.stream().mapToLong(Long::longValue).toArray();
        Future<Map<Long, Row>> missing = hashes.length > 0 ? pool.submit(() -> rows(reference, hashes)) : null;

        List<Future<Map<Long, Row>>> unexpected = new ArrayList<>();

        for(Pending pending : pendings)
        {
            long[] own = distinct(pending.unexpected, Integer.MAX_VALUE);
            unexpected.add(own.length > 0 ? pool.submit(() -> rows(pending.source.file(), own)) : null);
        }

        for(int i = 0; i < pendings.size(); i++)
        {
            if(missing != null)
                pendings.get(i).missingRows = missing.get();

            if(unexpected.get(i) != null)
                pendings.get(i).unexpectedRows = unexpected.get(i).get();
        }
    }


    /*
     * Reads the solutions of the given hashes (sorted) from a result.
     */
    private Map<Long, Row> rows(Path file, long[] hashes)
    {
        Map<Long, Row> rows = new HashMap<>();
        MessageDigest md5 = digests.get();

        try
        {
            ResultFile.read(file, solution -> {
                long hash = hash(md5, key(solution));

                if(Arrays.binarySearch(hashes, hash) >= 0 && !rows.containsKey(hash))
                    rows.put(hash, row(solution));
            });
        }
        catch(IOException | ResultFile.InvalidResultException | RuntimeException e)
        {
            // the file was read already, its solutions are not shown if it cannot be read again
        }

        return rows;
    }


    /*
     * Pairs the missing solutions of a result with the unexpected solutions that differ from them only by
     * approximately equal numbers, and removes the pairs from the difference.
     */
    private void pair(Pending pending)
    {
        if(pending.missing.length == 0 || pending.unexpected.length == 0 || !pending.isComplete())
            return;

        Map<String, List<Integer>> missingGroups = groups(pending.missing, pending.missingRows);
        Map<String, List<Integer>> unexpectedGroups = groups(pending.unexpected, pending.unexpectedRows);

        boolean[] missingPaired = new boolean[pending.missing.length];
        boolean[] unexpectedPaired = new boolean[pending.unexpected.length];
        long comparisons = 0;

        for(Entry<String, List<Integer>> group : missingGroups.entrySet())
        {
            List<Integer> unexpected = unexpectedGroups.get(group.getKey());

            if(unexpected == null)
                continue;

            List<Integer> missing = group.getValue();
            String variable = firstApproximate(pending.missingRows.get(pending.missing[missing.get(0)]));

            // the candidates of a missing solution are the unexpected solutions whose first approximate number lies
            // within a window that holds every number approximately equal to its own
            double[] values = new double[unexpected.size()];
            Integer[] order = new Integer[unexpected.size()];

            for(int j = 0; j < unexpected.size(); j++)
            {
                order[j] = j;
                values[j] = value(pending.unexpectedRows.get(pending.unexpected[unexpected.get(j)]), variable);
            }

            Arrays.sort(order, Comparator.comparingDouble((Integer j) -> values[j]));
            double[] sorted = new double[order.length];

            for(int k = 0; k < order.length; k++)
                sorted[k] = values[order[k]];

            int[][] edges = new int[missing.size()][];

            for(int i = 0; i < missing.size() && comparisons <= comparisonLimit; i++)
            {
                Row row = pending.missingRows.get(pending.missing[missing.get(i)]);
                double value = value(row, variable);
                double window = 2e-6 * Math.abs(value) + 1e-9;
                List<Integer> list = new ArrayList<>();

                for(int k = lowerBound(sorted, value - window); k < sorted.length && !(sorted[k] > value + window);
                        k++)
                {
                    int j = order[k];
                    comparisons++;

                    if(approximatelyEqual(row, pending.unexpectedRows.get(pending.unexpected[unexpected.get(j)])))
                        list.add(j);
                }

                edges[i] = list.stream().mapToInt(Integer::intValue).toArray();
            }

            int[] pairs = new int[unexpected.size()];
            Arrays.fill(pairs, -1);

            for(int i = 0; i < missing.size(); i++)
                if(edges[i] != null)
                    augment(i, edges, pairs, new boolean[unexpected.size()]);

            for(int j = 0; j < pairs.length; j++)
            {
                if(pairs[j] >= 0)
                {
                    unexpectedPaired[unexpected.get(j)] = true;
                    missingPaired[missing.get(pairs[j])] = true;
                }
            }
        }

        pending.missing = unpaired(pending.missing, missingPaired);
        pending.unexpected = unpaired(pending.unexpected, unexpectedPaired);
        pending.missingCount = pending.missing.length;
        pending.unexpectedCount = pending.unexpected.length;
    }


    /*
     * Groups the solutions given by their hashes so that only the solutions of the same group may be equal
     * approximately: by their keys, the approximate numbers replaced by their value spaces. Solutions without
     * approximate numbers are left out.
     */
    private Map<String, List<Integer>> groups(long[] hashes, Map<Long, Row> rows)
    {
        Map<String, List<Integer>> groups = new LinkedHashMap<>();

        for(int i = 0; i < hashes.length; i++)
        {
            Row row = rows.get(hashes[i]);
            StringBuilder key = new StringBuilder();
            boolean approximate = false;

            for(Entry<String, Term> binding : row.solution().entrySet())
            {
                String family = approximateFamily(binding.getKey(), binding.getValue());
                approximate |= family != null;

                Terms.append(key, binding.getKey());
                Terms.append(key, family != null ? "~" + family : row.keys().get(binding.getKey()));
            }

            if(approximate)
                groups.computeIfAbsent(key.toString(), _ -> new ArrayList<>()).add(i);
        }

        return groups;
    }


    private String approximateFamily(String variable, Term term)
    {
        if(shape.samples().contains(variable) || shape.separators().containsKey(variable))
            return null;

        return Terms.approximateFamily(term);
    }


    private String firstApproximate(Row row)
    {
        for(Entry<String, Term> binding : row.solution().entrySet())
            if(approximateFamily(binding.getKey(), binding.getValue()) != null)
                return binding.getKey();

        throw new IllegalStateException();
    }


    private static double value(Row row, String variable)
    {
        return Terms.approximateValue((Term.Literal) row.solution().get(variable));
    }


    private static int lowerBound(double[] sorted, double value)
    {
        int low = 0;
        int high = sorted.length;

        while(low < high)
        {
            int middle = (low + high) >>> 1;

            if(sorted[middle] < value)
                low = middle + 1;
            else
                high = middle;
        }

        return low;
    }


    /*
     * Returns whether two solutions of the same group are equal approximately.
     */
    private boolean approximatelyEqual(Row first, Row second)
    {
        for(Entry<String, Term> binding : first.solution().entrySet())
        {
            String variable = binding.getKey();

            if(first.keys().get(variable).equals(second.keys().get(variable)))
                continue;

            if(!approximatelyEqual(variable, binding.getValue(), second.solution().get(variable)))
                return false;
        }

        return true;
    }


    /*
     * Returns whether two terms bound to the variable are approximately equal numbers.
     */
    private boolean approximatelyEqual(String variable, Term first, Term second)
    {
        String family = approximateFamily(variable, first);

        return family != null && family.equals(approximateFamily(variable, second))
                && Terms.approximatelyEqual((Term.Literal) first, (Term.Literal) second);
    }


    /*
     * Finds an augmenting path from the missing solution given (Kuhn's algorithm of a maximum bipartite matching),
     * without recursion, so that long paths cannot overflow the stack.
     */
    private static boolean augment(int root, int[][] edges, int[] pairs, boolean[] visited)
    {
        int[] vertices = new int[16];
        int[] positions = new int[16];
        int[] chosen = new int[16];
        int depth = 0;

        vertices[0] = root;

        while(depth >= 0)
        {
            int vertex = vertices[depth];

            if(edges[vertex] == null || positions[depth] == edges[vertex].length)
            {
                depth--;
                continue;
            }

            int j = edges[vertex][positions[depth]++];

            if(visited[j])
                continue;

            visited[j] = true;
            chosen[depth] = j;

            if(pairs[j] < 0)
            {
                for(int level = 0; level <= depth; level++)
                    pairs[chosen[level]] = vertices[level];

                return true;
            }

            if(++depth == vertices.length)
            {
                vertices = Arrays.copyOf(vertices, 2 * depth);
                positions = Arrays.copyOf(positions, 2 * depth);
                chosen = Arrays.copyOf(chosen, 2 * depth);
            }

            vertices[depth] = pairs[j];
            positions[depth] = 0;
        }

        return false;
    }


    private static long[] unpaired(long[] hashes, boolean[] paired)
    {
        LongList list = new LongList();

        for(int i = 0; i < hashes.length; i++)
            if(!paired[i])
                list.add(hashes[i]);

        return list.toArray();
    }


    /*
     * Leaves out of the difference of a cut result the solutions that are ordered approximately equally with a
     * solution of the reference at a cut, and counts them as tied with it.
     */
    private void untie(Pending pending)
    {
        if(mode != Mode.BOUNDARY || !pending.isComplete())
            return;

        pending.missing = untie(pending.missing, pending.missingRows, pending.expectedTied);
        pending.unexpected = untie(pending.unexpected, pending.unexpectedRows, pending.tied);
        pending.missingCount = pending.missing.length;
        pending.unexpectedCount = pending.unexpected.length;
    }


    private long[] untie(long[] hashes, Map<Long, Row> rows, long[] tied)
    {
        LongList list = new LongList();

        for(long hash : hashes)
        {
            int index = tie(rows.get(hash).solution());

            if(index >= 0)
                tied[index]++;
            else
                list.add(hash);
        }

        return list.toArray();
    }


    /*
     * Returns the solution of the reference at a cut that the solution is ordered approximately equally with, or -1.
     */
    private int tie(SortedMap<String, Term> solution)
    {
        search:
        for(int i = 0; i < boundarySolutions.size(); i++)
        {
            SortedMap<String, Term> cut = boundarySolutions.get(i);

            for(String variable : shape.order())
            {
                if(shape.samples().contains(variable) || shape.separators().containsKey(variable))
                    break;

                Term first = solution.get(variable);
                Term second = cut.get(variable);

                if(first == null || second == null)
                {
                    if(first != second)
                        continue search;
                }
                else if(!key(variable, first, null).equals(key(variable, second, null))
                        && !approximatelyEqual(variable, first, second))
                {
                    continue search;
                }
            }

            return i;
        }

        return -1;
    }


    /*
     * Returns how the result differs from the reference, after the approximate numbers are paired, or null if it does
     * not differ.
     */
    private Difference difference(Pending pending)
    {
        List<String> problems = new ArrayList<>(pending.problems);

        if(pending.missingCount > 0 && pending.unexpectedCount > 0)
            problems.add(solutions(pending.missingCount) + " missing, " + pending.unexpectedCount + " unexpected");
        else if(pending.missingCount > 0)
            problems.add(solutions(pending.missingCount) + " missing");
        else if(pending.unexpectedCount > 0)
            problems.add(solutions(pending.unexpectedCount) + " unexpected");

        if(!pending.unreadable && pending.missingCount == 0 && pending.unexpectedCount == 0 && pending.blankNodes)
            problems.add("blank nodes shared by other solutions than in the reference");

        if(!pending.unreadable && pending.tied.length > 1 && !Arrays.equals(pending.tied, pending.expectedTied))
            problems.add(pending.tied[0] + " and " + pending.tied[1] + " solutions ordered equally with the first and "
                    + "the last solution of the reference instead of " + pending.expectedTied[0] + " and "
                    + pending.expectedTied[1]);

        String count = pending.unreadable ? null : countProblem(pending.count);

        if(count != null)
            problems.add(count);

        if(problems.isEmpty())
            return null;

        long signature = pending.large ? pending.signature : signature(pending.missing, pending.unexpected);

        return new Difference(problems, pending.missingCount, pending.unexpectedCount, signature,
                examples(pending.missing, pending.missingRows), examples(pending.unexpected, pending.unexpectedRows));
    }


    /*
     * Returns the solutions to be shown, with the numbers of their occurrences, in the order of their texts.
     */
    private Map<Row, Integer> examples(long[] hashes, Map<Long, Row> rows)
    {
        List<Long> shown = new ArrayList<>();

        for(long hash : rows.keySet())
            if(Arrays.binarySearch(hashes, hash) >= 0)
                shown.add(hash);

        shown.sort(Comparator.comparing((Long hash) -> text(rows.get(hash))));

        Map<Row, Integer> examples = new LinkedHashMap<>();

        for(long hash : shown.subList(0, Math.min(this.examples, shown.size())))
        {
            int index = Arrays.binarySearch(hashes, hash);
            int first = index;
            int last = index;

            while(first > 0 && hashes[first - 1] == hash)
                first--;

            while(last < hashes.length - 1 && hashes[last + 1] == hash)
                last++;

            examples.put(rows.get(hash), last - first + 1);
        }

        return examples;
    }


    private static String text(Row row)
    {
        StringBuilder text = new StringBuilder();

        for(Entry<String, Term> binding : row.solution().entrySet())
            text.append(binding.getKey()).append('\u0000').append(Terms.format(binding.getValue())).append('\u0000');

        return text.toString();
    }


    /*
     * Returns the key of a solution, made of the names of its bound variables and the keys of their terms.
     */
    private String key(SortedMap<String, Term> solution)
    {
        StringBuilder key = new StringBuilder();
        Map<String, Integer> labels = new HashMap<>();

        for(Entry<String, Term> binding : solution.entrySet())
        {
            Terms.append(key, binding.getKey());
            Terms.append(key, key(binding.getKey(), binding.getValue(), labels));
        }

        return key.toString();
    }


    /*
     * Returns the order key of a solution, made of the keys of the terms of the variables that order the solutions;
     * blank nodes, whose order no query determines, are all equal, and the order ends with a SAMPLE or GROUP_CONCAT
     * variable, whose values are not determined.
     */
    private String orderKey(SortedMap<String, Term> solution)
    {
        StringBuilder key = new StringBuilder();

        for(String variable : shape.order())
        {
            if(shape.samples().contains(variable) || shape.separators().containsKey(variable))
                break;

            Term term = solution.get(variable);
            Terms.append(key, term == null ? "" : key(variable, term, null));
        }

        return key.toString();
    }


    /*
     * Returns the key of the term bound to the variable in a solution, as the query compares it.
     */
    private String key(String variable, Term term, Map<String, Integer> labels)
    {
        if(shape.samples().contains(variable))
            return "*";

        String separator = shape.separators().get(variable);

        if(separator != null && term instanceof Term.Literal literal)
            return Terms.concatenationKey(literal, separator);

        return Terms.key(term, labels);
    }


    private Row row(SortedMap<String, Term> solution)
    {
        Map<String, String> keys = new HashMap<>();
        Map<String, Integer> labels = new HashMap<>();

        for(Entry<String, Term> binding : solution.entrySet())
            keys.put(binding.getKey(), key(binding.getKey(), binding.getValue(), labels));

        return new Row(solution, keys);
    }


    private static void countBlankNodes(Term term, Map<String, int[]> blankNodes)
    {
        if(term instanceof Term.BlankNode node)
        {
            blankNodes.computeIfAbsent(node.label(), _ -> new int[1])[0]++;
        }
        else if(term instanceof Term.Triple triple)
        {
            countBlankNodes(triple.subject(), blankNodes);
            countBlankNodes(triple.predicate(), blankNodes);
            countBlankNodes(triple.object(), blankNodes);
        }
    }


    /*
     * Returns the numbers of the occurrences of the blank nodes of a result, sorted; two results whose blank nodes are
     * the same up to their labels have the same numbers.
     */
    private static int[] occurrences(Digest digest)
    {
        return digest.blankNodes.values().stream().mapToInt(count -> count[0]).sorted().toArray();
    }


    /*
     * Returns at most the given number of the distinct values of a sorted array.
     */
    private static long[] distinct(long[] sorted, int limit)
    {
        LongList list = new LongList();

        for(int i = 0; i < sorted.length && list.size() < limit; i++)
            if(i == 0 || sorted[i] != sorted[i - 1])
                list.add(sorted[i]);

        return list.toArray();
    }


    /*
     * Returns a hash of the multisets of the missing and of the unexpected solutions given by their sorted hashes.
     */
    private static long signature(long[] missing, long[] unexpected)
    {
        long signature = missing.length;

        for(long hash : missing)
            signature = Long.rotateLeft(signature ^ hash, 27) * 0x94D049BB133111EBL;

        signature = Long.rotateLeft(signature ^ unexpected.length, 31) * 0xBF58476D1CE4E5B9L;

        for(long hash : unexpected)
            signature = Long.rotateLeft(signature ^ hash, 27) * 0x94D049BB133111EBL;

        return signature;
    }


    private static String variables(List<String> variables)
    {
        return variables.isEmpty() ? "none" : "?" + String.join(" ?", variables);
    }


    static String solutions(long count)
    {
        return count + (count == 1 ? " solution" : " solutions");
    }


    private static long hash(MessageDigest md5, String key)
    {
        return (long) longs.get(md5.digest(key.getBytes(UTF_8)), 0);
    }


    private static MessageDigest md5()
    {
        try
        {
            return MessageDigest.getInstance("MD5");
        }
        catch(NoSuchAlgorithmException e)
        {
            throw new IllegalStateException(e);
        }
    }
}
