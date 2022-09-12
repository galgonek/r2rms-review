#!/bin/bash

set -ue -o pipefail -o xtrace

if [[ "${BASH_SOURCE}" = */* ]]; then
  cd -- "${BASH_SOURCE%/*}/.."
fi

. ./bin/config.sh


MODE="$1"
MAPPING="ontop"
SERVER="mariadb"

BASE="${PWD}"
DATADIR="${DBBASEDIR}/${SERVER}-${MAPPING}-${MODE}"
LOGDIR="${LOGBASEDIR}/${SERVER}-${MAPPING}-${MODE}"
TMPDIR="$(mktemp -d)"

CONFIG="${BASE}/etc/mariadb/mariadb.cnf"
SOCKET="${TMPDIR}/mariadb.sock"
PIDFILE="${TMPDIR}/mariadb.pid"
SLEEP="120"


test -e "${DATADIR}" && exit 1

mkdir -p "${DATADIR}"
mkdir -p "${LOGDIR}"


"${MARIADB_BINDIR}/mariadb-install-db" --defaults-file="${CONFIG}" --datadir="${DATADIR}"

"${MARIADB_BINDIR}/mariadbd-safe" --defaults-file="${CONFIG}" --datadir="${DATADIR}" --log-error="${LOGDIR}/${SERVER}.log" --socket="${SOCKET}" --pid-file="${PIDFILE}" --innodb_buffer_pool_size=4G &
sleep "${SLEEP}"

cat sql/init/mariadb.sql | "${MARIADB_BINDIR}/mariadb" -v --socket="${SOCKET}" 2>&1 | tee "${LOGDIR}/init.log"


cat sql/defs.m4 sql/schema/* | m4 -DSERVER="${SERVER}" -DMAPPING="${MAPPING}" -DMODE="${MODE}" | "${MARIADB_BINDIR}/mariadb" -v --socket="${SOCKET}" --user=nextprot --password="password" 2>&1 | tee "${LOGDIR}/schema.log"

java -Xmx44G -cp "${BASE}/classes:${BASE}/lib/*" cz.iocb.nextprot.load.NeXtProt "jdbc:mariadb://localhost:3306/nextprot?rewriteBatchedStatements=true&user=nextprot&password=password" "${SERVER}" "${MODE}" "${SOURCEDIR}" 2>&1 | tee "${LOGDIR}/load.log"

"${MARIADB_BINDIR}/mariadb-check" --defaults-file="${CONFIG}" --optimize --socket="${SOCKET}" --all-databases 2>&1 | tee "${LOGDIR}/optimize.log"

cat sql/defs.m4 sql/settings/* | m4 -DSERVER="${SERVER}" -DMAPPING="${MAPPING}" -DMODE="${MODE}" | "${MARIADB_BINDIR}/mariadb" -v --socket="${SOCKET}" --user=nextprot --password="password" 2>&1 | tee "${LOGDIR}/settings.log"

cat sql/defs.m4 sql/post/foreignkey.sql | m4 -DSERVER="${SERVER}" -DMAPPING="${MAPPING}" -DMODE="${MODE}" | "${MARIADB_BINDIR}/mariadb" -v --socket="${SOCKET}" --user=nextprot --password="password" 2>&1 | tee "${LOGDIR}/foreignkey.log"

"${MARIADB_BINDIR}/mariadb-check" --defaults-file="${CONFIG}" --analyze --socket="${SOCKET}" --persistent --all-databases 2>&1 | tee "${LOGDIR}/analyze.log"


"${MARIADB_BINDIR}/mariadb-admin" --defaults-file="${CONFIG}" --socket="${SOCKET}" shutdown

rm -r "${TMPDIR}"


echo "done"
