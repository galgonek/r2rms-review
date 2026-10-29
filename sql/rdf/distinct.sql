-- stores the loaded triples once each into the table of the triples, ordered by predicate and subject, so that the
-- triples of a predicate and those of its subjects lie together: the triples are made distinct by the text forms of
-- their terms, which are unique, in a sort done by parallel workers, and then turned into rdfboxes, the IRIs by
-- rdfbox_create_from_iri() as the engine boxes them, since the input function of rdfbox checks each IRI at a high cost
set max_parallel_workers_per_gather = 12;
set enable_hashagg = off;

create table nextprot.triples as
    select
        case
            when starts_with(subject, '<') then sparql.rdfbox_create_from_iri(substr(subject, 2, length(subject) - 2))
            else subject::sparql.rdfbox
        end as subject,
        sparql.rdfbox_create_from_iri(substr(predicate, 2, length(predicate) - 2)) as predicate,
        case
            when starts_with(object, '<') then sparql.rdfbox_create_from_iri(substr(object, 2, length(object) - 2))
            else object::sparql.rdfbox
        end as object
    from (select distinct subject, predicate, object from nextprot.loaded_triples
        order by predicate, subject, object) t;

alter table nextprot.triples
    alter column subject set not null,
    alter column predicate set not null,
    alter column object set not null;

drop table nextprot.loaded_triples;
