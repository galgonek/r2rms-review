create table QNAME(context_bases)
(
    id          integer not null,
    metadata    integer not null,
    method      KEYTYPE(85),
    disease     KEYTYPE(85),
    tissue      KEYTYPE(85),
    line        KEYTYPE(85),
    stage       KEYTYPE(85),
    primary key(id)IDXPARAMS()
);

