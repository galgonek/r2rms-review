alter system set max_connections = '10';
alter system set autovacuum = 'off';
alter system set track_counts = 'off';
alter system set track_activities = 'off';

alter system set checkpoint_completion_target = '0.9';
alter system set default_statistics_target = '1000';
alter system set random_page_cost = '1.1';
alter system set effective_io_concurrency = '200';

alter system set shared_buffers = '16GB';
alter system set effective_cache_size = '48GB';
alter system set maintenance_work_mem = '2GB';
alter system set work_mem = '1GB';

alter system set wal_buffers = '16MB';
alter system set min_wal_size = '4GB';
alter system set max_wal_size = '16GB';

alter system set max_worker_processes = '12';
alter system set max_parallel_workers = '12';
alter system set max_parallel_workers_per_gather = '0';
alter system set max_parallel_maintenance_workers = '0';

alter system set geqo = 'off';
alter system set constraint_exclusion = 'on';
alter system set from_collapse_limit = '128'; 
alter system set join_collapse_limit = '128';

create user nextprot with login password 'password';
create user sparql with login password 'password';

create database nextprot owner nextprot;
\connect nextprot

create schema nextprot;
alter schema nextprot owner to nextprot;
grant usage on schema nextprot to sparql;
