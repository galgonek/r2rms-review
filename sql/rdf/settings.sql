-- the objects of rdfs:comment and :chain can be too long for a btree index (comments, protein sequences), so the index
-- of the objects leaves these two predicates out, and a triple pattern with any other constant predicate can use it
set max_parallel_maintenance_workers = 12;

create index triples__predicate_subject on nextprot.triples(predicate, subject) with(fillfactor=100);
create index triples__subject on nextprot.triples(subject) with(fillfactor=100);
create index triples__predicate_object on nextprot.triples(predicate, object) with(fillfactor=100)
    where predicate operator(sparql.@<>) sparql.rdfbox_create_from_iri('http://nextprot.org/rdf#chain')
    and predicate operator(sparql.@<>) sparql.rdfbox_create_from_iri('http://www.w3.org/2000/01/rdf-schema#comment');
grant select on nextprot.triples to sparql;
