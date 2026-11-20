#!/bin/bash

set -ue -o pipefail -o xtrace

if [[ "${BASH_SOURCE}" = */* ]]; then
  cd -- "${BASH_SOURCE%/*}/.."
fi

. ./bin/config.sh
. ./bin/containers.sh


# how the triples are stored: rdf keeps the terms in boxes in the table of the triples, rdfid numbers the terms in a
# table of their own and keeps the triples as their numbers, rdfmixed numbers only the IRIs and the blank nodes and
# keeps the triples in two tables, by whether the object is numbered or a literal kept in a box; all are made of the
# triples the rdf mode of the export loads
MODE="${1-rdf}"
MAPPING="idsm"
SERVER="postgresql"

SQLFILE="${SQLBASEDIR}/${SERVER}-rdf.sql.gz"
DATADIR="${DBBASEDIR}/${SERVER}-${MAPPING}-${MODE}"
LOGDIR="${LOGBASEDIR}/${SERVER}-${MAPPING}-${MODE}"


test -e "${SQLFILE}" || exit 1
test -e "${DATADIR}" && exit 1

mkdir -p "${DATADIR}"
mkdir -p "${LOGDIR}"


start_postgresql "${DATADIR}"

cat sql/init/postgresql.sql | in_container "${SERVER}" psql -v ON_ERROR_STOP=1 --echo-all -U postgres 2>&1 | tee "${LOGDIR}/init.log"

cat "sql/init/postgresql-${MAPPING}.sql" | in_container "${SERVER}" psql -v ON_ERROR_STOP=1 --echo-all -U postgres -d nextprot 2>&1 | tee "${LOGDIR}/init.log"

stop_container "${SERVER}" "${LOGDIR}/${SERVER}.log"

# the load runs with the memory of the configuration and more for the index builds, which get parallel workers, and
# logs as little as it can: a table or an index filled in the transaction creating it is not logged (wal_level
# minimal), and no page is logged whole (full_page_writes), as a load that fails is started anew anyway
start_postgresql "${DATADIR}" -c maintenance_work_mem=16GB -c max_parallel_maintenance_workers=11 -c wal_level=minimal -c max_wal_senders=0 -c full_page_writes=off


cat sql/rdf/schema.sql | in_container "${SERVER}" psql -v ON_ERROR_STOP=1 --echo-all -U nextprot -d nextprot 2>&1 | tee "${LOGDIR}/schema.log"

zcat "${SQLFILE}" | in_container "${SERVER}" psql -v ON_ERROR_STOP=1 -U nextprot -d nextprot 2>&1 | tee "${LOGDIR}/load.log"

cat "sql/${MODE}/distinct.sql" | in_container "${SERVER}" psql -v ON_ERROR_STOP=1 --echo-all -U nextprot -d nextprot 2>&1 | tee "${LOGDIR}/distinct.log"

cat "sql/${MODE}/settings.sql" | in_container "${SERVER}" psql -v ON_ERROR_STOP=1 --echo-all -U nextprot -d nextprot 2>&1 | tee "${LOGDIR}/settings.log"

cat "sql/${MODE}/statistics.sql" | in_container "${SERVER}" psql -v ON_ERROR_STOP=1 --echo-all -U nextprot -d nextprot 2>&1 | tee "${LOGDIR}/statistics.log"

# autovacuum is off, so vacuum sets the hint bits and the visibility map now; the queries would write every page they
# read first otherwise, and an index-only scan would read the table for every row
echo "vacuum (freeze, analyze, verbose)" | in_container "${SERVER}" psql -v ON_ERROR_STOP=1 --echo-all -U nextprot -d nextprot 2>&1 | tee "${LOGDIR}/vacuum.log"


stop_container "${SERVER}" "${LOGDIR}/${SERVER}.log"


echo "done"
