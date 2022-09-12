create schema ontop;
grant usage on schema ontop to sparql;
alter user sparql set search_path to ontop;


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
