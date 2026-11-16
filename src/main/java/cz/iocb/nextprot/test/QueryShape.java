package cz.iocb.nextprot.test;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryException;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.SortCondition;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpVars;
import org.apache.jena.sparql.algebra.op.Op1;
import org.apache.jena.sparql.algebra.op.Op2;
import org.apache.jena.sparql.algebra.op.OpDistinctReduced;
import org.apache.jena.sparql.algebra.op.OpExtendAssign;
import org.apache.jena.sparql.algebra.op.OpFilter;
import org.apache.jena.sparql.algebra.op.OpGraph;
import org.apache.jena.sparql.algebra.op.OpGroup;
import org.apache.jena.sparql.algebra.op.OpLeftJoin;
import org.apache.jena.sparql.algebra.op.OpMinus;
import org.apache.jena.sparql.algebra.op.OpN;
import org.apache.jena.sparql.algebra.op.OpOrder;
import org.apache.jena.sparql.algebra.op.OpProject;
import org.apache.jena.sparql.algebra.op.OpSlice;
import org.apache.jena.sparql.algebra.op.OpUnion;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprAggregator;
import org.apache.jena.sparql.expr.aggregate.AggGroupConcat;
import org.apache.jena.sparql.expr.aggregate.AggGroupConcatDistinct;
import org.apache.jena.sparql.expr.aggregate.AggSample;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementSubQuery;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.syntax.ElementWalker;



/*
 * What a query says about the comparison of its results: the limit and the offset that may cut the solutions, the
 * variables that order them (null if the query has no ORDER BY, or orders them by something else than projected
 * variables), the projected variables computed by SAMPLE, directly or by expressions, BIND or subqueries, whose values
 * the query does not determine, the variables projected as GROUP_CONCAT with their separators, whose values are joined
 * in an order that the query does not determine, whether the query is REDUCED, so that its results may hold some
 * duplicates, and why the solutions of the query are not determined at all (null if they are): a subquery cuts its
 * solutions by LIMIT or OFFSET without ordering them, or a value computed by SAMPLE decides other values or solutions.
 */
record QueryShape(long limit, long offset, boolean ordered, List<String> order, Set<String> samples,
        Map<String, String> separators, boolean reduced, String undetermined)
{
    /*
     * The shape of a query that is not known: its results are compared as they are.
     */
    static final QueryShape unknown = new QueryShape(-1, 0, false, null, Set.of(), Map.of(), false, null);


    /*
     * Finds the variables whose values a query does not determine because SAMPLE computes them, directly or by
     * expressions, BIND or subqueries, and whether such a value decides anything else, which leaves the solutions
     * themselves undetermined: a join, a filter, a group, another aggregate, DISTINCT (unless the solutions are distinct
     * by their groups), or the order of a cut subquery. The order of the cut query itself is left to the comparison.
     */
    private static final class Samples
    {
        private final Map<Op, Boolean> orders = new IdentityHashMap<>();
        private boolean decisive;


        /*
         * Returns the variables of the operator whose values SAMPLE computes; the top operators are those of the query
         * itself, not of its subqueries.
         */
        Set<Var> visit(Op op, boolean top)
        {
            switch(op)
            {
                case OpExtendAssign extend:
                {
                    Set<Var> found = visit(extend.getSubOp(), top);

                    extend.getVarExprList().forEachVarExpr((variable, expression) -> {
                        if(mentions(expression.getVarsMentioned(), found))
                            found.add(variable);
                    });

                    return found;
                }

                case OpGroup group:
                {
                    Set<Var> found = visit(group.getSubOp(), false);

                    group.getGroupVars().forEachVarExpr((variable, expression) -> {
                        if(expression == null ? found.contains(variable) :
                                mentions(expression.getVarsMentioned(), found))
                            decisive = true;
                    });

                    Set<Var> samples = new HashSet<>();

                    for(ExprAggregator aggregator : group.getAggregators())
                    {
                        if(aggregator.getAggregator() instanceof AggSample)
                            samples.add(aggregator.getVar());
                        else if(aggregator.getAggregator().getExprList() != null
                                && mentions(aggregator.getAggregator().getExprList().getVarsMentioned(), found))
                            decisive = true;
                    }

                    return samples;
                }

                case OpProject project:
                {
                    // the cut of the query lies above its projection, below it only those of subqueries
                    Set<Var> found = visit(project.getSubOp(), false);
                    found.retainAll(project.getVars());
                    return found;
                }

                case OpDistinctReduced distinct:
                {
                    Set<Var> found = visit(distinct.getSubOp(), top);

                    if(!found.isEmpty() && !isGrouped(distinct.getSubOp()))
                        decisive = true;

                    return found;
                }

                case OpOrder order:
                {
                    Set<Var> found = visit(order.getSubOp(), top);

                    for(SortCondition condition : order.getConditions())
                        if(mentions(condition.getExpression().getVarsMentioned(), found))
                            orders.put(order, true);

                    return found;
                }

                case OpSlice slice:
                {
                    Set<Var> found = visit(slice.getSubOp(), top);

                    if(!top && orders.containsKey(order(slice.getSubOp())))
                        decisive = true;

                    return found;
                }

                case OpFilter filter:
                {
                    Set<Var> found = visit(filter.getSubOp(), top);

                    if(mentions(filter.getExprs().getVarsMentioned(), found))
                        decisive = true;

                    return found;
                }

                case OpGraph graph:
                {
                    Set<Var> found = visit(graph.getSubOp(), false);

                    if(graph.getNode().isVariable() && found.contains(Var.alloc(graph.getNode())))
                        decisive = true;

                    return found;
                }

                case OpUnion union:
                {
                    Set<Var> found = visit(union.getLeft(), false);
                    found.addAll(visit(union.getRight(), false));
                    return found;
                }

                case Op2 join:
                {
                    // a join, a left join, a minus: the shared variables decide which solutions are joined
                    Set<Var> left = visit(join.getLeft(), false);
                    Set<Var> right = visit(join.getRight(), false);
                    Set<Var> shared = new HashSet<>(OpVars.visibleVars(join.getLeft()));
                    shared.retainAll(OpVars.visibleVars(join.getRight()));

                    if(mentions(shared, left) || mentions(shared, right))
                        decisive = true;

                    if(join instanceof OpLeftJoin leftJoin && leftJoin.getExprs() != null
                            && (mentions(leftJoin.getExprs().getVarsMentioned(), left)
                                    || mentions(leftJoin.getExprs().getVarsMentioned(), right)))
                        decisive = true;

                    if(!(join instanceof OpMinus))
                        left.addAll(right);

                    return left;
                }

                case OpN sequence:
                {
                    Set<Var> found = new HashSet<>();
                    Set<Var> visible = new HashSet<>();

                    for(Op element : sequence.getElements())
                    {
                        Set<Var> own = visit(element, false);
                        Set<Var> shared = new HashSet<>(OpVars.visibleVars(element));
                        shared.retainAll(visible);

                        if(mentions(shared, found) || mentions(shared, own))
                            decisive = true;

                        found.addAll(own);
                        visible.addAll(OpVars.visibleVars(element));
                    }

                    return found;
                }

                case Op1 other:
                    return visit(other.getSubOp(), top);

                default:
                    return new HashSet<>();
            }
        }


        /*
         * Returns whether the solutions of the operator are distinct by the groups of a GROUP BY whose keys are all
         * projected.
         */
        private static boolean isGrouped(Op op)
        {
            if(!(op instanceof OpProject project))
                return false;

            Op sub = project.getSubOp();

            while(sub instanceof OpExtendAssign || sub instanceof OpFilter || sub instanceof OpOrder)
                sub = ((Op1) sub).getSubOp();

            return sub instanceof OpGroup group && project.getVars().containsAll(group.getGroupVars().getVars());
        }


        /*
         * Returns the ORDER BY of a subquery whose operator is given, or null.
         */
        private static Op order(Op op)
        {
            while(op instanceof OpProject || op instanceof OpDistinctReduced || op instanceof OpExtendAssign)
                op = ((Op1) op).getSubOp();

            return op instanceof OpOrder ? op : null;
        }


        private static boolean mentions(Set<Var> variables, Set<Var> found)
        {
            return !Collections.disjoint(variables, found);
        }
    }


    /*
     * Reads the shapes of the queries of a query set by their identifiers; the file has the format that Test reads, and
     * the queries get the prefixes that Test declares. A query that cannot be parsed is reported to the warnings and
     * gets the unknown shape.
     */
    static Map<String, QueryShape> read(Path file, PrintStream warnings) throws IOException
    {
        Map<String, QueryShape> shapes = new LinkedHashMap<>();

        String id = null;
        StringBuilder query = null;

        for(String line : Files.readAllLines(file))
        {
            if(line.startsWith("### "))
            {
                if(id != null)
                    shapes.put(id, parse(id, query.toString(), warnings));

                id = line.substring(4, line.length() - 4);
                query = new StringBuilder();
            }
            else if(id != null && !line.isEmpty())
            {
                query.append(line);
                query.append('\n');
            }
        }

        if(id != null)
            shapes.put(id, parse(id, query.toString(), warnings));

        return shapes;
    }


    private static QueryShape parse(String id, String text, PrintStream warnings)
    {
        Query query;

        try
        {
            query = QueryFactory.create(Test.prefixes + text, Syntax.syntaxARQ);
        }
        catch(QueryException e)
        {
            warnings.println("warning: " + id + " cannot be parsed, its results are compared as they are: "
                    + e.getMessage().replaceAll("\\s+", " ").trim());
            return unknown;
        }

        List<String> order = null;

        if(query.hasOrderBy())
        {
            order = new ArrayList<>();

            for(SortCondition condition : query.getOrderBy())
            {
                Expr expression = condition.getExpression();

                if(!expression.isVariable() || !query.getResultVars().contains(expression.getVarName()))
                {
                    order = null;
                    break;
                }

                order.add(expression.getVarName());
            }
        }

        Set<String> samples = new HashSet<>();
        Map<String, String> separators = new HashMap<>();
        Samples analysis = new Samples();

        for(Var variable : analysis.visit(Algebra.compile(query), true))
            samples.add(variable.getVarName());

        String undetermined = null;

        if(query.getQueryPattern() != null && hasUnorderedCut(query.getQueryPattern()))
            undetermined = "a subquery is cut by LIMIT or OFFSET and not ordered";
        else if(analysis.decisive)
            undetermined = "a value computed by SAMPLE decides other values or solutions";

        if(query.isSelectType())
        {
            for(Var variable : query.getProject().getVars())
            {
                if(!(query.getProject().getExpr(variable) instanceof ExprAggregator aggregator))
                    continue;

                switch(aggregator.getAggregator())
                {
                    case AggGroupConcat concat -> separators.put(variable.getVarName(),
                            separator(concat.getSeparator()));
                    case AggGroupConcatDistinct concat -> separators.put(variable.getVarName(),
                            separator(concat.getSeparator()));
                    default ->
                            {
                            }
                }
            }
        }

        return new QueryShape(query.hasLimit() ? query.getLimit() : -1, query.hasOffset() ? query.getOffset() : 0,
                query.hasOrderBy(), order, samples, separators, query.isReduced(), undetermined);
    }


    /*
     * Returns whether a subquery in the pattern, or in a subquery of it, cuts its solutions by LIMIT or OFFSET without
     * ordering them.
     */
    private static boolean hasUnorderedCut(Element pattern)
    {
        boolean[] found = new boolean[1];

        // the walker does not enter the subqueries
        ElementWalker.walk(pattern, new ElementVisitorBase()
        {
            @Override
            public void visit(ElementSubQuery element)
            {
                Query subquery = element.getQuery();

                if((subquery.hasLimit() || subquery.hasOffset()) && !subquery.hasOrderBy())
                    found[0] = true;
                else if(subquery.getQueryPattern() != null && hasUnorderedCut(subquery.getQueryPattern()))
                    found[0] = true;
            }
        });

        return found[0];
    }


    private static String separator(String separator)
    {
        return separator == null ? " " : separator;
    }


    /*
     * Returns whether the results of the query may be cut by LIMIT or OFFSET, given the number of the solutions of the
     * reference result: a result is cut when the query skips some solutions, or when the reference has as many
     * solutions as the limit allows.
     */
    boolean isCut(long count)
    {
        return offset > 0 || limit >= 0 && count >= limit;
    }
}
