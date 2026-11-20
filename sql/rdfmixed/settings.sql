-- the resources are found by their numbers through the primary key and by themselves through a hash index, which is
-- enough, as a resource is looked up only for equality; the index cannot say that the resources are unique, so the
-- configuration of the engine declares it. The triples of resources are found by the predicate with the subject and
-- with the object through two unique indexes holding whole triples, so that a triple pattern with a constant predicate
-- is answered from an index alone, and by the subject or the object alone when the predicate is a variable. The other
-- triples are found by the predicate with the subject, by the subject alone and by the predicate with the object; the
-- objects of rdfs:comment and :chain can be too long for a btree index (comments, protein sequences), so the index of
-- the objects leaves these two predicates out, by their numbers, and a triple pattern with any other constant predicate
-- can use it. The foreign keys let the engine leave the table of the resources out wherever a query needs only the
-- number of a resource.
set max_parallel_maintenance_workers = 12;

alter table nextprot.resources add primary key (id) with(fillfactor=100);
create index resources__term on nextprot.resources using hash(term);

alter table nextprot.resource_triples add primary key (predicate, subject, object) with(fillfactor=100);
create unique index resource_triples__predicate_object_subject on nextprot.resource_triples(predicate, object, subject) with(fillfactor=100);
create index resource_triples__subject on nextprot.resource_triples(subject) with(fillfactor=100);
create index resource_triples__object on nextprot.resource_triples(object) with(fillfactor=100);

create index value_triples__predicate_subject on nextprot.value_triples(predicate, subject) with(fillfactor=100);
create index value_triples__subject on nextprot.value_triples(subject) with(fillfactor=100);

do $$
declare
    excluded text;
begin
    select coalesce(string_agg(format('predicate <> %s', id), ' and '), 'true') into excluded
    from nextprot.resources
    where term operator(sparql.@=) sparql.rdfbox_create_from_iri('http://nextprot.org/rdf#chain')
        or term operator(sparql.@=) sparql.rdfbox_create_from_iri('http://www.w3.org/2000/01/rdf-schema#comment');

    execute 'create index value_triples__predicate_object on nextprot.value_triples(predicate, object) '
        || 'with(fillfactor=100) where ' || excluded;
end
$$;

alter table nextprot.resource_triples
    add foreign key (subject) references nextprot.resources(id),
    add foreign key (predicate) references nextprot.resources(id),
    add foreign key (object) references nextprot.resources(id);

alter table nextprot.value_triples
    add foreign key (subject) references nextprot.resources(id),
    add foreign key (predicate) references nextprot.resources(id);

grant select on nextprot.resources to sparql;
grant select on nextprot.resource_triples to sparql;
grant select on nextprot.value_triples to sparql;
