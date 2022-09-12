#!/bin/bash

set -ue -o pipefail -o xtrace

if [[ "${BASH_SOURCE}" = */* ]]; then
  cd -- "${BASH_SOURCE%/*}/.."
fi

. ./bin/config.sh


MODE="$1"
MAPPING="$2"
SERVER="postgresql"

BASE="${PWD}"
DATADIR="${DBBASEDIR}/${SERVER}-${MAPPING}-${MODE}"
LOGDIR="${LOGBASEDIR}/${SERVER}-${MAPPING}-${MODE}"
TMPDIR="$(mktemp -d)"


test -e "${DATADIR}" && exit 1

mkdir -p "${DATADIR}"
mkdir -p "${LOGDIR}"


"${POSTGRESQL_BINDIR}/initdb" --pgdata="${DATADIR}" --encoding=UTF8 --locale=C --username=postgres

"${POSTGRESQL_BINDIR}/pg_ctl" --pgdata="${DATADIR}" --log="${LOGDIR}/${SERVER}.log" -o "--unix_socket_directories='${TMPDIR}'" start

cat sql/init/postgresql.sql | "${POSTGRESQL_BINDIR}/psql" -v ON_ERROR_STOP=1 --host="${TMPDIR}" --echo-all -U postgres 2>&1 | tee "${LOGDIR}/init.log"

cat "sql/init/postgresql-${MAPPING}.sql" | "${POSTGRESQL_BINDIR}/psql" -v ON_ERROR_STOP=1 --host="${TMPDIR}" --echo-all -U postgres -d nextprot 2>&1 | tee "${LOGDIR}/init.log"

"${POSTGRESQL_BINDIR}/pg_ctl" --pgdata="${DATADIR}" --log="${LOGDIR}/${SERVER}.log" -o "--unix_socket_directories='${TMPDIR}'" -o --shared_buffers=1GB -o --effective_cache_size=3GB -o --maintenance_work_mem=512MB -o --work_mem=256MB restart


cat sql/defs.m4 sql/schema/* | m4 -DSERVER="${SERVER}" -DMAPPING="${MAPPING}" -DMODE="${MODE}" | "${POSTGRESQL_BINDIR}/psql" -v ON_ERROR_STOP=1 --host="${TMPDIR}" --echo-all -U nextprot -d nextprot 2>&1 | tee "${LOGDIR}/schema.log"

java -Xmx44G -cp "${BASE}/classes:${BASE}/lib/*" cz.iocb.nextprot.load.NeXtProt "jdbc:postgresql://localhost:5432/nextprot?user=nextprot&password=password" "${SERVER}" "${MODE}" "${SOURCEDIR}" 2>&1 | tee "${LOGDIR}/load.log"

echo "reindex schema nextprot" | "${POSTGRESQL_BINDIR}/psql" -v ON_ERROR_STOP=1 --host="${TMPDIR}" --echo-all -U nextprot -d nextprot 2>&1 | tee "${LOGDIR}/optimize.log"

cat sql/defs.m4 sql/settings/* | m4 -DSERVER="${SERVER}" -DMAPPING="${MAPPING}" -DMODE="${MODE}" | "${POSTGRESQL_BINDIR}/psql" -v ON_ERROR_STOP=1 --host="${TMPDIR}" --echo-all -U nextprot -d nextprot 2>&1 | tee "${LOGDIR}/settings.log"

cat sql/defs.m4 sql/post/foreignkey.sql | m4 -DSERVER="${SERVER}" -DMAPPING="${MAPPING}" -DMODE="${MODE}" | "${POSTGRESQL_BINDIR}/psql" -v ON_ERROR_STOP=1 --host="${TMPDIR}" --echo-all -U nextprot -d nextprot 2>&1 | tee "${LOGDIR}/foreignkey.log"

echo "analyze verbose" | "${POSTGRESQL_BINDIR}/psql" -v ON_ERROR_STOP=1 --host="${TMPDIR}" --echo-all -U nextprot -d nextprot 2>&1 | tee "${LOGDIR}/analyze.log"


"${POSTGRESQL_BINDIR}/pg_ctl" --pgdata="${DATADIR}" stop

rm -r "${TMPDIR}"


echo "done"
