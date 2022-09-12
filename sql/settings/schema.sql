if(SERVER-MODE, `virtuoso-indirect', `create index source_bases__iri on QNAME(source_bases)(iri)IDXPARAMS();')
create index source_bases__comment on QNAME(source_bases)(comment)IDXPARAMS()WHERE(comment is not null);
create index source_bases__reference on QNAME(source_bases)(reference)IDXPARAMS()WHERE(reference is not null);
grant select on QNAME(source_bases) to SPARQL;

if(SERVER-MODE, `virtuoso-indirect', `create index database_bases__iri on QNAME(database_bases)(iri)IDXPARAMS();')
create index database_bases__reference on QNAME(database_bases)(reference)IDXPARAMS()WHERE(reference is not null);
grant select on QNAME(database_bases) to SPARQL;

create index database_comments__db on QNAME(database_comments)(db)IDXPARAMS();
create index database_comments__comment on QNAME(database_comments)(comment)IDXPARAMS();
grant select on QNAME(database_comments) to SPARQL;

create index database_categories__db on QNAME(database_categories)(db)IDXPARAMS();
create index database_categories__category on QNAME(database_categories)(category)IDXPARAMS();
grant select on QNAME(database_categories) to SPARQL;

if(SERVER-MODE, `virtuoso-indirect', `create index schema_bases__iri on QNAME(schema_bases)(iri)IDXPARAMS();')
create index schema_bases__type on QNAME(schema_bases)(type)IDXPARAMS()WHERE(type is not null);
create index schema_bases__label on QNAME(schema_bases)(label)IDXPARAMS()WHERE(label is not null);
create index schema_bases__comment on QNAME(schema_bases)(comment)IDXPARAMS()WHERE(comment is not null);
create index schema_bases__reference on QNAME(schema_bases)(reference)IDXPARAMS()WHERE(reference is not null);
grant select on QNAME(schema_bases) to SPARQL;

grant select on QNAME(schema_classes) to SPARQL;

grant select on QNAME(schema_thing_subclasses) to SPARQL;

create index schema_restrictions__entity on QNAME(schema_restrictions)(entity)IDXPARAMS();
create index schema_restrictions__notin on QNAME(schema_restrictions)(notin)IDXPARAMS();
grant select on QNAME(schema_restrictions) to SPARQL;

create index schema_related_terms__entity on QNAME(schema_related_terms)(entity)IDXPARAMS();
create index schema_related_terms__related on QNAME(schema_related_terms)(related)IDXPARAMS();
grant select on QNAME(schema_related_terms) to SPARQL;

create index schema_parent_classes__entity on QNAME(schema_parent_classes)(entity)IDXPARAMS();
create index schema_parent_classes__parent on QNAME(schema_parent_classes)(parent)IDXPARAMS();
grant select on QNAME(schema_parent_classes) to SPARQL;

