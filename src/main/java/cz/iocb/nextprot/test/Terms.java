package cz.iocb.nextprot.test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;



/*
 * The comparison of the terms of query results by their values, and their text in the report.
 *
 * The key of a term stands for its value, so that two terms written in different lexical forms of one value get the
 * same key: an IRI stands for its text, a language-tagged string for its text, its tag in lower case and its base
 * direction, a simple literal is an xsd:string, and the literals of the XSD datatypes whose values have several lexical
 * forms stand for their values: all the integer types and xsd:decimal share one value space (xsd:int 1 is xsd:integer 1
 * and xsd:decimal 1.0), so do xsd:float and xsd:double, then xsd:boolean, xsd:dateTime (an instant when it has a time
 * zone), xsd:date, xsd:time and the durations. Any other literal, and one whose lexical form is not valid, stands for
 * its datatype and lexical form. Line breaks are compared as LF, because the XML of a result normalizes the line breaks
 * it does not escape. A blank node stands for the order of its first occurrence in the solution, which the caller
 * numbers, as the labels of blank nodes are local to a result.
 *
 * The numbers of xsd:decimal, xsd:float and xsd:double need not be exact, as an engine may compute a division or a sum
 * of floating point numbers to another precision than the reference, so they are also compared approximately: two of
 * them of the same value space are equal when they differ at most by the precision of the less precise of them,
 * provided it is written with at least 10 significant digits (6 for xsd:float) or with at least 10 fractional digits
 * while the other one has more, and two floating point numbers also when they differ relatively at most by 1e-12
 * (1e-6 for xsd:float). A number written with fewer digits is taken as exact, and so are the integers.
 *
 * The lexical forms of xsd:decimal may have an exponent, as QLever writes them so, and those of the integer types may
 * be out of the range of their types, as QLever writes counts as xsd:int.
 */
final class Terms
{
    private enum Kind
    {
        STRING, INTEGER, DECIMAL, FLOAT, DOUBLE, BOOLEAN, DATETIME, DATE, TIME, DURATION, OTHER
    }


    private static final String xsd = "http://www.w3.org/2001/XMLSchema#";
    private static final String xsdString = xsd + "string";
    private static final Pattern floatingPattern = Pattern.compile("[+-]?([0-9]+(\\.[0-9]*)?|\\.[0-9]+)([eE][+-]?[0-9]+)?");
    private static final Pattern integerPattern = Pattern.compile("[+-]?[0-9]+");
    private static final Pattern booleanPattern = Pattern.compile("true|false");
    private static final Pattern decimalPattern = Pattern.compile("[+-]?[0-9]*\\.[0-9]+");
    private static final Pattern doublePattern = Pattern.compile("[+-]?([0-9]+(\\.[0-9]*)?|\\.[0-9]+)[eE][+-]?[0-9]+");
    private static final Pattern localPattern = Pattern.compile("([A-Za-z0-9_]([A-Za-z0-9_.-]*[A-Za-z0-9_-])?)?");
    private static final Pattern prefixPattern = Pattern.compile("prefix\\s+([^\\s:]*):\\s*<([^>]*)>",
            Pattern.CASE_INSENSITIVE);
    private static final ThreadLocal<DatatypeFactory> factories = ThreadLocal.withInitial(Terms::factory);
    private static final List<String[]> prefixes = prefixes(Test.prefixes);
    private static final int maxLength = 200;


    private Terms()
    {
    }


    /*
     * Returns the key of the term; the blank nodes of the solution are numbered in the map given, and all blank nodes
     * get the same key if it is null.
     */
    static String key(Term term, Map<String, Integer> labels)
    {
        return switch(term)
        {
            case Term.Iri iri -> "I" + iri.value();
            case Term.BlankNode node -> "_" + (labels == null ? 0 : labels.computeIfAbsent(node.label(),
                    _ -> labels.size()));
            case Term.Literal literal -> key(literal);
            case Term.Triple triple ->
            {
                StringBuilder key = new StringBuilder("<");
                append(key, key(triple.subject(), labels));
                append(key, key(triple.predicate(), labels));
                append(key, key(triple.object(), labels));
                yield key.toString();
            }
        };
    }


    /*
     * Returns the key of a literal that GROUP_CONCAT made by joining values with the separator in an order that the
     * query does not determine: the key stands for the multiset of the parts between the separators, which does not
     * depend on the order of the values, as no occurrence of the separator can overlap another one; for a separator
     * that may overlap itself (one that begins with its own end, like --), and for an empty one, the key stands for the
     * multiset of the characters.
     */
    static String concatenationKey(Term.Literal literal, String separator)
    {
        String text = lineBreaks(literal.lexical());
        List<String> parts = new ArrayList<>();
        separator = lineBreaks(separator);

        if(separator.isEmpty() || bordered(separator))
        {
            text.codePoints().forEach(c -> parts.add(Character.toString(c)));
        }
        else
        {
            int start = 0;

            for(int end; (end = text.indexOf(separator, start)) >= 0; start = end + separator.length())
                parts.add(text.substring(start, end));

            parts.add(text.substring(start));
        }

        Collections.sort(parts);

        StringBuilder key = new StringBuilder("G");

        if(literal.language() != null)
            key.append('@').append(literal.language().toLowerCase(Locale.ROOT));
        else if(literal.datatype() != null && !literal.datatype().equals(xsdString))
            key.append(literal.datatype());

        key.append('\u0000');

        for(String part : parts)
            append(key, part);

        return key.toString();
    }


    /*
     * Returns whether the text begins with its own end, so that two of its occurrences may overlap.
     */
    private static boolean bordered(String text)
    {
        for(int length = 1; length < text.length(); length++)
            if(text.startsWith(text.substring(text.length() - length)))
                return true;

        return false;
    }


    /*
     * Appends the value to the key so that the keys of different sequences of values differ.
     */
    static void append(StringBuilder key, String value)
    {
        key.append(value.length()).append(':').append(value);
    }


    private static String key(Term.Literal literal)
    {
        String lexical = lineBreaks(literal.lexical());

        if(literal.language() != null)
            return "L" + literal.language().toLowerCase(Locale.ROOT) + '\u0000'
                    + (literal.direction() == null ? "" : literal.direction().toLowerCase(Locale.ROOT)) + '\u0000'
                    + lexical;

        String datatype = literal.datatype() == null ? xsdString : literal.datatype();
        String value = value(kind(datatype), lexical);

        return value != null ? value : "O" + datatype + '\u0000' + lexical;
    }


    /*
     * Returns the key of the value of the lexical form in the datatype of the given kind, or null if the datatype is
     * compared by its lexical forms or the lexical form is not valid.
     */
    private static String value(Kind kind, String lexical)
    {
        String text = lexical.trim();

        switch(kind)
        {
            case STRING:
                return "S" + lexical;

            case INTEGER:
            case DECIMAL:
            {
                BigDecimal value = decimal(text, kind == Kind.INTEGER);
                return value == null ? null : "D" + canonical(value);
            }

            case FLOAT:
            case DOUBLE:
            {
                Double value = floating(text, kind == Kind.FLOAT);

                if(value == null)
                    return null;
                else if(value.isNaN())
                    return "FNaN";
                else if(value.isInfinite())
                    return value > 0 ? "FINF" : "F-INF";
                else
                    return "F" + value;
            }

            case BOOLEAN:
                return switch(text)
                {
                    case "true", "1" -> "Btrue";
                    case "false", "0" -> "Bfalse";
                    default -> null;
                };

            case DATETIME:
                return dateTime(text);

            case DATE:
                return date(text);

            case TIME:
                return time(text);

            case DURATION:
                return duration(text);

            default:
                return null;
        }
    }


    private static Kind kind(String datatype)
    {
        return switch(datatype)
        {
            case xsdString -> Kind.STRING;
            case xsd + "decimal" -> Kind.DECIMAL;
            case xsd + "integer", xsd + "nonPositiveInteger", xsd + "negativeInteger", xsd + "long", xsd + "int",
                    xsd + "short", xsd + "byte", xsd + "nonNegativeInteger", xsd + "unsignedLong",
                    xsd + "unsignedInt", xsd + "unsignedShort", xsd + "unsignedByte", xsd + "positiveInteger" ->
                Kind.INTEGER;
            case xsd + "float" -> Kind.FLOAT;
            case xsd + "double" -> Kind.DOUBLE;
            case xsd + "boolean" -> Kind.BOOLEAN;
            case xsd + "dateTime", xsd + "dateTimeStamp" -> Kind.DATETIME;
            case xsd + "date" -> Kind.DATE;
            case xsd + "time" -> Kind.TIME;
            case xsd + "duration", xsd + "dayTimeDuration", xsd + "yearMonthDuration" -> Kind.DURATION;
            default -> Kind.OTHER;
        };
    }


    /*
     * Returns the value of a lexical form of an integer type or of xsd:decimal, or null if it is not valid.
     */
    private static BigDecimal decimal(String text, boolean integer)
    {
        if(!(integer ? integerPattern : floatingPattern).matcher(text).matches())
            return null;

        return new BigDecimal(text);
    }


    private static String canonical(BigDecimal value)
    {
        return value.signum() == 0 ? "0" : value.stripTrailingZeros().toString();
    }


    /*
     * Returns the value of a lexical form of xsd:float or xsd:double, a negative zero as zero, or null if the lexical
     * form is not valid.
     */
    private static Double floating(String text, boolean single)
    {
        switch(text.toLowerCase(Locale.ROOT))
        {
            case "inf", "+inf", "infinity", "+infinity":
                return Double.POSITIVE_INFINITY;

            case "-inf", "-infinity":
                return Double.NEGATIVE_INFINITY;

            case "nan":
                return Double.NaN;
        }

        if(!floatingPattern.matcher(text).matches())
            return null;

        double value = single ? Float.parseFloat(text) : Double.parseDouble(text);

        return value == 0 ? 0.0 : value;
    }


    private static XMLGregorianCalendar calendar(String text, QName type)
    {
        try
        {
            XMLGregorianCalendar calendar = factories.get().newXMLGregorianCalendar(text);
            return type.equals(calendar.getXMLSchemaType()) ? calendar : null;
        }
        catch(IllegalArgumentException | IllegalStateException e)
        {
            return null;
        }
    }


    private static String dateTime(String text)
    {
        XMLGregorianCalendar calendar = calendar(text, DatatypeConstants.DATETIME);

        if(calendar == null)
            return null;

        boolean zoned = calendar.getTimezone() != DatatypeConstants.FIELD_UNDEFINED;

        if(zoned)
            calendar = calendar.normalize();

        return "T" + calendar.getEonAndYear() + '-' + calendar.getMonth() + '-' + calendar.getDay() + 'T'
                + calendar.getHour() + ':' + calendar.getMinute() + ':' + canonical(seconds(calendar))
                + (zoned ? "Z" : "");
    }


    private static String date(String text)
    {
        XMLGregorianCalendar calendar = calendar(text, DatatypeConstants.DATE);

        if(calendar == null)
            return null;

        return "d" + calendar.getEonAndYear() + '-' + calendar.getMonth() + '-' + calendar.getDay()
                + (calendar.getTimezone() != DatatypeConstants.FIELD_UNDEFINED ? "@" + calendar.getTimezone() : "");
    }


    /*
     * Returns the key of a time as the seconds since midnight, of UTC when the time has a time zone.
     */
    private static String time(String text)
    {
        XMLGregorianCalendar calendar = calendar(text, DatatypeConstants.TIME);

        if(calendar == null)
            return null;

        BigDecimal day = BigDecimal.valueOf(86400);
        BigDecimal seconds = seconds(calendar).add(BigDecimal.valueOf(calendar.getHour() * 3600L
                + calendar.getMinute() * 60L));
        boolean zoned = calendar.getTimezone() != DatatypeConstants.FIELD_UNDEFINED;

        if(zoned)
            seconds = seconds.subtract(BigDecimal.valueOf(calendar.getTimezone() * 60L)).remainder(day);

        if(seconds.signum() < 0)
            seconds = seconds.add(day);

        return "t" + canonical(seconds) + (zoned ? "Z" : "");
    }


    private static BigDecimal seconds(XMLGregorianCalendar calendar)
    {
        BigDecimal seconds = BigDecimal.valueOf(calendar.getSecond());

        if(calendar.getFractionalSecond() != null)
            seconds = seconds.add(calendar.getFractionalSecond());

        return seconds;
    }


    /*
     * Returns the key of a duration as its months and seconds, which are its value.
     */
    private static String duration(String text)
    {
        try
        {
            Duration duration = factories.get().newDuration(text);

            BigInteger months = integer(duration, DatatypeConstants.YEARS).multiply(BigInteger.valueOf(12))
                    .add(integer(duration, DatatypeConstants.MONTHS));

            BigInteger minutes = integer(duration, DatatypeConstants.DAYS).multiply(BigInteger.valueOf(24))
                    .add(integer(duration, DatatypeConstants.HOURS)).multiply(BigInteger.valueOf(60))
                    .add(integer(duration, DatatypeConstants.MINUTES));

            BigDecimal seconds = new BigDecimal(minutes.multiply(BigInteger.valueOf(60)));

            if(duration.getField(DatatypeConstants.SECONDS) instanceof BigDecimal value)
                seconds = seconds.add(value);

            if(duration.getSign() < 0)
            {
                months = months.negate();
                seconds = seconds.negate();
            }

            return "P" + months + ',' + canonical(seconds);
        }
        catch(IllegalArgumentException | UnsupportedOperationException e)
        {
            return null;
        }
    }


    private static BigInteger integer(Duration duration, DatatypeConstants.Field field)
    {
        return duration.getField(field) instanceof BigInteger value ? value : BigInteger.ZERO;
    }


    /*
     * Returns the value space of the term if it is a number compared approximately: D for decimals, F for floating
     * point numbers; null for any other term.
     */
    static String approximateFamily(Term term)
    {
        if(term instanceof Term.Literal literal && literal.language() == null && literal.datatype() != null)
        {
            String text = literal.lexical().trim();

            switch(kind(literal.datatype()))
            {
                case DECIMAL:
                    return decimal(text, false) != null ? "D" : null;

                case FLOAT:
                case DOUBLE:
                {
                    Double value = floating(text, kind(literal.datatype()) == Kind.FLOAT);
                    return value != null && Double.isFinite(value) ? "F" : null;
                }

                default:
                    return null;
            }
        }

        return null;
    }


    /*
     * Returns the value of a number compared approximately, as a double.
     */
    static double approximateValue(Term.Literal literal)
    {
        Kind kind = kind(literal.datatype());
        String text = literal.lexical().trim();

        return kind == Kind.DECIMAL ? decimal(text, false).doubleValue() : floating(text, kind == Kind.FLOAT);
    }


    /*
     * Returns whether two numbers of the same value space, as approximateFamily() gives it, are equal approximately.
     */
    static boolean approximatelyEqual(Term.Literal first, Term.Literal second)
    {
        BigDecimal x = new BigDecimal(first.lexical().trim());
        BigDecimal y = new BigDecimal(second.lexical().trim());

        Kind firstKind = kind(first.datatype());
        Kind secondKind = kind(second.datatype());
        boolean floating = firstKind == Kind.FLOAT || firstKind == Kind.DOUBLE;
        boolean single = firstKind == Kind.FLOAT || secondKind == Kind.FLOAT;

        if(floating)
        {
            double a = floating(first.lexical().trim(), firstKind == Kind.FLOAT);
            double b = floating(second.lexical().trim(), secondKind == Kind.FLOAT);

            if(Math.abs(a - b) <= (single ? 1e-6 : 1e-12) * Math.max(Math.abs(a), Math.abs(b)))
                return true;
        }

        BigDecimal coarse = x.scale() <= y.scale() ? x : y;
        BigDecimal fine = coarse == x ? y : x;

        // a number written to fewer digits is exact
        if(coarse.precision() < (single ? 6 : 10) && (coarse.scale() < 10 || fine.scale() <= coarse.scale()))
            return false;

        BigDecimal tolerance = BigDecimal.ONE.scaleByPowerOfTen(-coarse.scale())
                .add(BigDecimal.ONE.scaleByPowerOfTen(-fine.scale()));

        return x.subtract(y).abs().compareTo(tolerance) <= 0;
    }


    /*
     * Returns the text of the term in the report, in the syntax of Turtle with the prefixes of the queries; long
     * literals are cut.
     */
    static String format(Term term)
    {
        return switch(term)
        {
            case Term.Iri iri -> iri(iri.value());
            case Term.BlankNode node -> "_:" + node.label();
            case Term.Literal literal -> literal(literal);
            case Term.Triple triple -> "<<( " + format(triple.subject()) + " " + format(triple.predicate()) + " "
                    + format(triple.object()) + " )>>";
        };
    }


    private static String literal(Term.Literal literal)
    {
        String lexical = literal.lexical();

        if(literal.language() == null && literal.datatype() != null)
        {
            Pattern shorthand = switch(literal.datatype())
            {
                case xsd + "integer" -> integerPattern;
                case xsd + "decimal" -> decimalPattern;
                case xsd + "double" -> doublePattern;
                case xsd + "boolean" -> booleanPattern;
                default -> null;
            };

            if(shorthand != null && shorthand.matcher(lexical).matches())
                return lexical;
        }

        StringBuilder text = new StringBuilder("\"");

        lexical.codePoints().limit(maxLength).forEach(c -> {
            switch(c)
            {
                case '\\' -> text.append("\\\\");
                case '"' -> text.append("\\\"");
                case '\n' -> text.append("\\n");
                case '\r' -> text.append("\\r");
                case '\t' -> text.append("\\t");
                default -> text.append(c < 0x20 || c == 0x7F ? String.format(Locale.ROOT, "\\u%04X", c)
                        : Character.toString(c));
            }
        });

        int length = lexical.codePointCount(0, lexical.length());

        if(length > maxLength)
            text.append("...");

        text.append('"');

        if(literal.language() != null)
            text.append('@').append(literal.language())
                    .append(literal.direction() != null ? "--" + literal.direction() : "");
        else if(literal.datatype() != null)
            text.append("^^").append(iri(literal.datatype()));

        // a cut literal shows its length and a hash of its text, so that two cut literals that differ look different
        if(length > maxLength)
            text.append(String.format(Locale.ROOT, " [%d characters, #%08x]", length, lexical.hashCode()));

        return text.toString();
    }


    private static String iri(String value)
    {
        for(String[] prefix : prefixes)
        {
            if(value.startsWith(prefix[1]))
            {
                String local = value.substring(prefix[1].length());

                if(localPattern.matcher(local).matches())
                    return prefix[0] + ":" + local;
            }
        }

        return "<" + value + ">";
    }


    /*
     * Returns the prefixes declared by the text as pairs of the name and the IRI, the longest IRI first.
     */
    private static List<String[]> prefixes(String declarations)
    {
        List<String[]> prefixes = new ArrayList<>();
        Matcher matcher = prefixPattern.matcher(declarations);

        while(matcher.find())
            prefixes.add(new String[] { matcher.group(1), matcher.group(2) });

        prefixes.sort(Comparator.comparingInt((String[] prefix) -> prefix[1].length()).reversed());

        return prefixes;
    }


    private static String lineBreaks(String text)
    {
        return text.indexOf('\r') < 0 ? text : text.replace("\r\n", "\n").replace('\r', '\n');
    }


    private static DatatypeFactory factory()
    {
        try
        {
            return DatatypeFactory.newInstance();
        }
        catch(DatatypeConfigurationException e)
        {
            throw new IllegalStateException(e);
        }
    }
}
