create table QNAME(publication_bases)
(
    id          integer not null,
    large       XSDBOOLEAN not null,
    title       VARCHAR(497),
    journal     VARCHAR(239),
    year        VARCHAR(4),
    volume      VARCHAR(28),
    issue       VARCHAR(16),
    pub_type    VARCHAR(23),
    first_page  VARCHAR(35),
    last_page   VARCHAR(10),
    publisher   VARCHAR(40),
    city        VARCHAR(20),
    primary key(id)IDXPARAMS()
);

create table QNAME(publication_links)
(
    publication integer not null,
    link        VARCHAR(126) not null,
    primary key(publication, link)IDXPARAMS()
);

create table QNAME(publication_authors)
(
    id          integer not null,
    publication integer not null,
    person      XSDBOOLEAN not null,
    name        VARCHAR(428) not null,
    suffix      VARCHAR(17),
    primary key(id)IDXPARAMS()
);

create table QNAME(publication_editors)
(
    id          integer not null,
    publication integer not null,
    name        VARCHAR(24) not null,
    primary key(id)IDXPARAMS()
);

