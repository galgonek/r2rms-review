-- the terms are found by their numbers through the primary key and by themselves through a hash index, which holds
-- terms of any length (comments and protein sequences are too long for a btree index) and which is enough, as a term
-- is looked up only for equality; the index cannot say that the terms are unique, so the configuration of the engine
-- declares it. The triples are found by the predicate with the subject and with the object through two unique indexes
-- holding whole triples, so that a triple pattern with a constant predicate is answered from an index alone, and by the
-- subject or the object alone when the predicate is a variable. The foreign keys let the engine leave the table of the
-- terms out wherever a query needs only the number of a term.
set max_parallel_maintenance_workers = 12;

alter table nextprot.terms add primary key (id) with(fillfactor=100);
create index terms__term on nextprot.terms using hash(term);

alter table nextprot.triples add primary key (predicate, subject, object) with(fillfactor=100);
create unique index triples__predicate_object_subject on nextprot.triples(predicate, object, subject) with(fillfactor=100);
create index triples__subject on nextprot.triples(subject) with(fillfactor=100);
create index triples__object on nextprot.triples(object) with(fillfactor=100);

alter table nextprot.triples
    add foreign key (subject) references nextprot.terms(id),
    add foreign key (predicate) references nextprot.terms(id),
    add foreign key (object) references nextprot.terms(id);

grant select on nextprot.terms to sparql;
grant select on nextprot.triples to sparql;
