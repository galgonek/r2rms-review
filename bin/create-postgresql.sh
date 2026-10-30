#!/bin/bash

set -ue -o pipefail -o xtrace

if [[ "${BASH_SOURCE}" = */* ]]; then
  cd -- "${BASH_SOURCE%/*}/.."
fi

. ./bin/config.sh
. ./bin/containers.sh


MODE="$1"
MAPPING="$2"
SERVER="postgresql"

SQLFILE="${SQLBASEDIR}/${SERVER}-${MODE}.sql.gz"
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

start_postgresql "${DATADIR}" -c shared_buffers=1GB -c effective_cache_size=3GB -c maintenance_work_mem=512MB -c work_mem=256MB


cat sql/defs.m4 sql/schema/* | m4 -DSERVER="${SERVER}" -DMAPPING="${MAPPING}" -DMODE="${MODE}" | in_container "${SERVER}" psql -v ON_ERROR_STOP=1 --echo-all -U nextprot -d nextprot 2>&1 | tee "${LOGDIR}/schema.log"

zcat "${SQLFILE}" | in_container "${SERVER}" psql -v ON_ERROR_STOP=1 -U nextprot -d nextprot 2>&1 | tee "${LOGDIR}/load.log"

echo "reindex schema nextprot" | in_container "${SERVER}" psql -v ON_ERROR_STOP=1 --echo-all -U nextprot -d nextprot 2>&1 | tee "${LOGDIR}/optimize.log"

cat sql/defs.m4 sql/settings/* | m4 -DSERVER="${SERVER}" -DMAPPING="${MAPPING}" -DMODE="${MODE}" | in_container "${SERVER}" psql -v ON_ERROR_STOP=1 --echo-all -U nextprot -d nextprot 2>&1 | tee "${LOGDIR}/settings.log"

cat sql/defs.m4 sql/post/foreignkey.sql | m4 -DSERVER="${SERVER}" -DMAPPING="${MAPPING}" -DMODE="${MODE}" | in_container "${SERVER}" psql -v ON_ERROR_STOP=1 --echo-all -U nextprot -d nextprot 2>&1 | tee "${LOGDIR}/foreignkey.log"

echo "analyze verbose" | in_container "${SERVER}" psql -v ON_ERROR_STOP=1 --echo-all -U nextprot -d nextprot 2>&1 | tee "${LOGDIR}/analyze.log"


stop_container "${SERVER}" "${LOGDIR}/${SERVER}.log"


echo "done"
