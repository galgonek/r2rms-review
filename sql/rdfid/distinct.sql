-- numbers the terms of the loaded triples and stores each triple once as the numbers of its terms, ordered by predicate
-- and subject, so that the triples of a predicate and those of its subjects lie together: the terms are made distinct
-- by their text forms, which are unique, in a sort done by parallel workers, and numbered in the order of the forms, so
-- that the terms of one kind and the IRIs of one namespace get neighbouring numbers; the triples are joined with the
-- numbers of their terms and made distinct by the numbers, which sorts three numbers a triple instead of three texts
set max_parallel_workers_per_gather = 12;
set enable_hashagg = off;

create unlogged table nextprot.loaded_terms as
    select row_number() over (order by term) as id, term
    from (select distinct term
        from (select subject as term from nextprot.loaded_triples
            union all select predicate from nextprot.loaded_triples
            union all select object from nextprot.loaded_triples) t) t;

analyze nextprot.loaded_terms;

-- the predicates are few, so their numbers are joined from a table of their own instead of the table of all the terms
set enable_hashagg = on;

create temporary table loaded_predicates as
    select id, term from nextprot.loaded_terms
    where term in (select predicate from nextprot.loaded_triples group by predicate);

analyze loaded_predicates;

set enable_hashagg = off;

create table nextprot.triples as
    select distinct p.id as predicate, s.id as subject, o.id as object
    from nextprot.loaded_triples t
        join nextprot.loaded_terms s on s.term = t.subject
        join loaded_predicates p on p.term = t.predicate
        join nextprot.loaded_terms o on o.term = t.object
    order by 1, 2, 3;

alter table nextprot.triples
    alter column predicate set not null,
    alter column subject set not null,
    alter column object set not null;

drop table nextprot.loaded_triples;

-- the terms are turned into rdfboxes by the input function of rdfbox, which also checks the syntax of the IRIs and of
-- the language tags, so that an invalid term stops the load
create table nextprot.terms as
    select id, term::sparql.rdfbox as term from nextprot.loaded_terms order by id;

alter table nextprot.terms
    alter column id set not null,
    alter column term set not null;

drop table nextprot.loaded_terms;
