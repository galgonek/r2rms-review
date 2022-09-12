create table QNAME(terminology_bases)
(
    id      KEYTYPE(85) not null,
    type    KEYTYPE(37),
if(KEYTYPE, `integer', `    iri     KEYCHAR(85) UNIQUE()not null,')
    primary key(id)IDXPARAMS()
);

create table QNAME(terminology_labels)
(
    term    KEYTYPE(85) not null,
    label   VARCHAR(215) not null,
    primary key(term, label)IDXPARAMS()
);

create table QNAME(terminology_parents)
(
    term    KEYTYPE(85) not null,
    parent  KEYTYPE(85) not null,
    primary key(term, parent)IDXPARAMS()
);

create table QNAME(terminology_related_terms)
(
    term    KEYTYPE(85) not null,
    related KEYTYPE(85) not null,
    primary key(term, related)IDXPARAMS()
);

