-- statistics of the triples per predicate for the join optimizer of the engine (see GroupedStatistics there): for every
-- predicate and for the subjects and the objects of its triples, the number of the triples, the number of distinct
-- values and the effective number of distinct values (the inverse Simpson index of the frequencies of the values, which
-- tells how skewed they are), and the thousand most common values with their counts; the statistics of the database
-- cannot tell these apart, because they describe every column of the table as a whole
set max_parallel_workers_per_gather = 12;
set enable_hashagg = off;

create table nextprot.triples_statistics
(
    key_value         sparql.rdfbox not null,
    column_name       text not null,
    row_count         bigint not null,
    distinct_count    float8 not null,
    effective_count   float8 not null,
    primary key (key_value, column_name)
);

create table nextprot.triples_values
(
    key_value         sparql.rdfbox not null,
    column_name       text not null,
    value             text not null,
    row_count         bigint not null,
    primary key (key_value, column_name, value)
);

create temporary table subject_counts as
    select predicate, subject as value, count(*) as row_count from nextprot.triples group by predicate, subject;

insert into nextprot.triples_statistics (key_value, column_name, row_count, distinct_count, effective_count)
    select predicate, 'subject', sum(row_count), count(*), sum(row_count)^2 / sum(row_count * row_count)
    from subject_counts group by predicate;

insert into nextprot.triples_values (key_value, column_name, value, row_count)
    select predicate, 'subject', value::text, row_count
    from (select predicate, value, row_count, row_number() over (partition by predicate order by row_count desc)
        as position from subject_counts) t
    where position <= 1000 and row_count > 1;

drop table subject_counts;

create temporary table object_counts as
    select predicate, object as value, count(*) as row_count from nextprot.triples group by predicate, object;

insert into nextprot.triples_statistics (key_value, column_name, row_count, distinct_count, effective_count)
    select predicate, 'object', sum(row_count), count(*), sum(row_count)^2 / sum(row_count * row_count)
    from object_counts group by predicate;

insert into nextprot.triples_values (key_value, column_name, value, row_count)
    select predicate, 'object', value::text, row_count
    from (select predicate, value, row_count, row_number() over (partition by predicate order by row_count desc)
        as position from object_counts) t
    where position <= 1000 and row_count > 1;

drop table object_counts;

grant select on nextprot.triples_statistics to sparql;
grant select on nextprot.triples_values to sparql;
