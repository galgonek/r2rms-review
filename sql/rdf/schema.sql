-- the triples as the export loads them: every occurrence of a triple in the data files, each term in the text form of
-- an rdfbox; distinct.sql turns them into the table of the triples, so this one needs no logging
create unlogged table nextprot.loaded_triples
(
    subject     text not null,
    predicate   text not null,
    object      text not null
);
