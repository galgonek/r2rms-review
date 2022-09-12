create table QNAME(isoform_low_expressions)
(
    isoform         KEYTYPE(15) not null,
    annotation      KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_medium_expressions)
(
    isoform         KEYTYPE(15) not null,
    annotation      KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_high_expressions)
(
    isoform         KEYTYPE(15) not null,
    annotation      KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

