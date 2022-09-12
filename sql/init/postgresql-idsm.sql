create schema uuid authorization nextprot;
create schema pgcrypto authorization nextprot;
create schema sparql authorization nextprot;

grant usage on schema uuid to sparql;
grant usage on schema pgcrypto to sparql;
grant usage on schema sparql to sparql;

create extension "uuid-ossp" with schema uuid;
create extension pgcrypto with schema pgcrypto;
create extension pgsparql;

alter user sparql set search_path to uuid, pgcrypto;
