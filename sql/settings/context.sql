create index context_bases__metadata on QNAME(context_bases)(metadata)IDXPARAMS();
create index context_bases__method on QNAME(context_bases)(method)IDXPARAMS();
create index context_bases__disease on QNAME(context_bases)(disease)IDXPARAMS()WHERE(disease is not null);
create index context_bases__tissue on QNAME(context_bases)(tissue)IDXPARAMS()WHERE(tissue is not null);
create index context_bases__line on QNAME(context_bases)(line)IDXPARAMS()WHERE(line is not null);
create index context_bases__stage on QNAME(context_bases)(stage)IDXPARAMS()WHERE(stage is not null);
grant select on QNAME(context_bases) to SPARQL;

