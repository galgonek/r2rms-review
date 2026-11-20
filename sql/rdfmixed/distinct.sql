-- numbers the IRIs and the blank nodes of the loaded triples and stores each triple once, the triples whose object is
-- such a resource as the numbers of their terms and the other triples, whose object is a literal, as the numbers of
-- the subject and the predicate with the object as it is; both tables are ordered by predicate and subject, so that the
-- triples of a predicate and those of its subjects lie together. The resources are made distinct by their text forms,
-- which are unique, in a sort done by parallel workers, and numbered in the order of the forms, so that the blank nodes
-- and the IRIs of one namespace get neighbouring numbers; the triples are joined with the numbers of their resources
-- and made distinct by them, the literals by their text forms. The text form of a blank node begins with an
-- underscore and that of an IRI with an angle bracket, which is doubled only for a triple term
set max_parallel_workers_per_gather = 12;
set enable_hashagg = off;

create unlogged table nextprot.loaded_resources as
    select row_number() over (order by term) as id, term
    from (select distinct term
        from (select subject as term from nextprot.loaded_triples
            union all select predicate from nextprot.loaded_triples
            union all select object from nextprot.loaded_triples
                where left(object, 1) = '_' or left(object, 1) = '<' and left(object, 2) <> '<<') t) t;

analyze nextprot.loaded_resources;

-- the predicates are few, so their numbers are joined from a table of their own instead of the table of all the
-- resources
set enable_hashagg = on;

create temporary table loaded_predicates as
    select id, term from nextprot.loaded_resources
    where term in (select predicate from nextprot.loaded_triples group by predicate);

analyze loaded_predicates;

set enable_hashagg = off;

create table nextprot.resource_triples as
    select distinct p.id as predicate, s.id as subject, o.id as object
    from nextprot.loaded_triples t
        join nextprot.loaded_resources s on s.term = t.subject
        join loaded_predicates p on p.term = t.predicate
        join nextprot.loaded_resources o on o.term = t.object
    where left(t.object, 1) = '_' or left(t.object, 1) = '<' and left(t.object, 2) <> '<<'
    order by 1, 2, 3;

alter table nextprot.resource_triples
    alter column predicate set not null,
    alter column subject set not null,
    alter column object set not null;

-- the objects are turned into rdfboxes by the input function of rdfbox, which also checks their syntax, so that an
-- invalid literal stops the load
create table nextprot.value_triples as
    select predicate, subject, object::sparql.rdfbox as object
    from (select distinct p.id as predicate, s.id as subject, t.object
        from nextprot.loaded_triples t
            join nextprot.loaded_resources s on s.term = t.subject
            join loaded_predicates p on p.term = t.predicate
        where not (left(t.object, 1) = '_' or left(t.object, 1) = '<' and left(t.object, 2) <> '<<')
        order by 1, 2, 3) t;

alter table nextprot.value_triples
    alter column predicate set not null,
    alter column subject set not null,
    alter column object set not null;

drop table nextprot.loaded_triples;

-- so are the resources, which checks the syntax of the IRIs
create table nextprot.resources as
    select id, term::sparql.rdfbox as term from nextprot.loaded_resources order by id;

alter table nextprot.resources
    alter column id set not null,
    alter column term set not null;

drop table nextprot.loaded_resources;
