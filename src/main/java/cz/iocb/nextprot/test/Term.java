package cz.iocb.nextprot.test;



/*
 * An RDF term of a query result, as the result file writes it.
 */
sealed interface Term
{
    record Iri(String value) implements Term
    {
    }


    record BlankNode(String label) implements Term
    {
    }


    /*
     * A literal; the datatype is null for a simple literal and for a language-tagged string, the language and the base
     * direction are null if the literal has none.
     */
    record Literal(String lexical, String datatype, String language, String direction) implements Term
    {
    }


    record Triple(Term subject, Term predicate, Term object) implements Term
    {
    }
}
