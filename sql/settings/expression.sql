create index isoform_low_expressions__isoform on QNAME(isoform_low_expressions)(isoform)IDXPARAMS();
create index isoform_low_expressions__annotation on QNAME(isoform_low_expressions)(annotation)IDXPARAMS();
grant select on QNAME(isoform_low_expressions) to SPARQL;

create index isoform_medium_expressions__isoform on QNAME(isoform_medium_expressions)(isoform)IDXPARAMS();
create index isoform_medium_expressions__annotation on QNAME(isoform_medium_expressions)(annotation)IDXPARAMS();
grant select on QNAME(isoform_medium_expressions) to SPARQL;

create index isoform_high_expressions__isoform on QNAME(isoform_high_expressions)(isoform)IDXPARAMS();
create index isoform_high_expressions__annotation on QNAME(isoform_high_expressions)(annotation)IDXPARAMS();
grant select on QNAME(isoform_high_expressions) to SPARQL;

