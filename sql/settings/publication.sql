create index publication_bases__large on QNAME(publication_bases)(large)IDXPARAMS()WHERE(large = true);
create index publication_bases__title on QNAME(publication_bases)(title)IDXPARAMS()WHERE(title is not null);
create index publication_bases__journal on QNAME(publication_bases)(journal)IDXPARAMS()WHERE(journal is not null);
create index publication_bases__year on QNAME(publication_bases)(year)IDXPARAMS()WHERE(year is not null);
create index publication_bases__volume on QNAME(publication_bases)(volume)IDXPARAMS()WHERE(volume is not null);
create index publication_bases__issue on QNAME(publication_bases)(issue)IDXPARAMS()WHERE(issue is not null);
create index publication_bases__pub_type on QNAME(publication_bases)(pub_type)IDXPARAMS()WHERE(pub_type is not null);
create index publication_bases__first_page on QNAME(publication_bases)(first_page)IDXPARAMS()WHERE(first_page is not null);
create index publication_bases__last_page on QNAME(publication_bases)(last_page)IDXPARAMS()WHERE(last_page is not null);
create index publication_bases__publisher on QNAME(publication_bases)(publisher)IDXPARAMS()WHERE(publisher is not null);
create index publication_bases__city on QNAME(publication_bases)(city)IDXPARAMS()WHERE(city is not null);
grant select on QNAME(publication_bases) to SPARQL;

create index publication_links__publication on QNAME(publication_links)(publication)IDXPARAMS();
create index publication_links__link on QNAME(publication_links)(link)IDXPARAMS();
grant select on QNAME(publication_links) to SPARQL;

create index publication_authors__publication on QNAME(publication_authors)(publication)IDXPARAMS();
create index publication_authors__name on QNAME(publication_authors)(name)IDXPARAMS();
create index publication_authors__person on QNAME(publication_authors)(person)IDXPARAMS();
create index publication_authors__suffix on QNAME(publication_authors)(suffix)IDXPARAMS();
grant select on QNAME(publication_authors) to SPARQL;

create index publication_editors__publication on QNAME(publication_editors)(publication)IDXPARAMS();
create index publication_editors__name on QNAME(publication_editors)(name)IDXPARAMS();
grant select on QNAME(publication_editors) to SPARQL;

