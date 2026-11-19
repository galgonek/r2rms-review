create schema ontop;
grant usage on schema ontop to sparql;

-- Ontop computes the hash functions of SPARQL (SHA1, SHA256, ...) by digest() of pgcrypto
create schema pgcrypto authorization nextprot;
grant usage on schema pgcrypto to sparql;
create extension pgcrypto with schema pgcrypto;

alter user sparql set search_path to ontop, pgcrypto;


create function ontop.substr(string in varchar, start in bigint, count in bigint) returns varchar language sql as
$$
  select substr(string, start::int, count::int);
$$
immutable parallel safe;

create function ontop.substr(string in varchar, start in bigint, count in integer) returns varchar language sql as
$$
  select substr(string, start::int, count);
$$
immutable parallel safe;

create function ontop.substr(string in varchar, start in integer, count in bigint) returns varchar language sql as
$$
  select substr(string, start, count::int);
$$
immutable parallel safe;
