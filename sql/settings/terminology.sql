if(SERVER-MODE, `virtuoso-indirect', `create index terminology_bases__iri on QNAME(terminology_bases)(iri)IDXPARAMS();')
create index terminology_bases__type on QNAME(terminology_bases)(type)IDXPARAMS()WHERE(type is not null);
grant select on QNAME(terminology_bases) to SPARQL;

create index terminology_labels__term on QNAME(terminology_labels)(term)IDXPARAMS();
create index terminology_labels__label on QNAME(terminology_labels)(label)IDXPARAMS();
grant select on QNAME(terminology_labels) to SPARQL;

create index terminology_parents__term on QNAME(terminology_parents)(term)IDXPARAMS();
create index terminology_parents__parent on QNAME(terminology_parents)(parent)IDXPARAMS();
grant select on QNAME(terminology_parents) to SPARQL;

create index terminology_related_terms__term on QNAME(terminology_related_terms)(term)IDXPARAMS();
create index terminology_related_terms__related on QNAME(terminology_related_terms)(related)IDXPARAMS();
grant select on QNAME(terminology_related_terms) to SPARQL;

