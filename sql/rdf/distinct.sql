-- stores the loaded triples once each into the table of the triples, ordered by predicate and subject, so that the
-- triples of a predicate and those of its subjects lie together: the triples are made distinct by the text forms of
-- their terms, which are unique, in a sort done by parallel workers, and then turned into rdfboxes by the input
-- function of rdfbox, which also checks the syntax of the IRIs and of the language tags, so that an invalid term stops
-- the load
set max_parallel_workers_per_gather = 12;
set enable_hashagg = off;

create table nextprot.triples as
    select subject::sparql.rdfbox as subject, predicate::sparql.rdfbox as predicate, object::sparql.rdfbox as object
    from (select distinct subject, predicate, object from nextprot.loaded_triples
        order by predicate, subject, object) t;

alter table nextprot.triples
    alter column subject set not null,
    alter column predicate set not null,
    alter column object set not null;

drop table nextprot.loaded_triples;
