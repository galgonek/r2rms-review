create table QNAME(source_bases)
(
    id          KEYTYPE(68) not null,
if(KEYTYPE, `integer', `    iri         KEYCHAR(68) UNIQUE()not null,')
    comment     VARCHAR(143),
    reference   VARCHAR(130),
    primary key(id)IDXPARAMS()
);


create table QNAME(database_bases)
(
    id          KEYTYPE(27) not null,
if(KEYTYPE, `integer', `    iri         KEYCHAR(27) UNIQUE()not null,')
    reference   VARCHAR(126),
    primary key(id)IDXPARAMS()
);


create table QNAME(database_comments)
(
    db          KEYTYPE(27) not null,
    comment     VARCHAR(146) not null,
    primary key(db, comment)IDXPARAMS()
);


create table QNAME(database_categories)
(
    db          KEYTYPE(27) not null,
    category    VARCHAR(37) not null,
    primary key(db, category)IDXPARAMS()
);


create table QNAME(schema_bases)
(
    id          KEYTYPE(37) not null,
    type        KEYTYPE(37),
if(KEYTYPE, `integer', `    iri         KEYCHAR(37) UNIQUE()not null,')
    label       VARCHAR(41),
    comment     VARCHAR(292),
    reference   VARCHAR(62),
    primary key(id)IDXPARAMS()
);


create table QNAME(schema_classes)
(
    entity      KEYTYPE(37) not null,
    primary key(entity)IDXPARAMS()
);


create table QNAME(schema_thing_subclasses)
(
    entity      KEYTYPE(37) not null,
    primary key(entity)IDXPARAMS()
);


create table QNAME(schema_restrictions)
(
    entity      KEYTYPE(37) not null,
    notin       KEYTYPE(37) not null,
    primary key(entity, notin)IDXPARAMS()
);

create table QNAME(schema_related_terms)
(
    entity      KEYTYPE(37) not null,
    related     KEYTYPE(85) not null,
    primary key(entity, related)IDXPARAMS()
);

create table QNAME(schema_parent_classes)
(
    entity      KEYTYPE(37) not null,
    parent      KEYTYPE(37) not null,
    primary key(entity, parent)IDXPARAMS()
);

