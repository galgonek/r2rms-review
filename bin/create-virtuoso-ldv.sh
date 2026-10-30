#!/bin/bash

set -ue -o pipefail -o xtrace

if [[ "${BASH_SOURCE}" = */* ]]; then
  cd -- "${BASH_SOURCE%/*}/.."
fi

. ./bin/config.sh
. ./bin/containers.sh


MODE="$1"
MAPPING="ldv"
SERVER="virtuoso"

BASE="${PWD}"
SQLFILE="${SQLBASEDIR}/${SERVER}-${MODE}.sql.gz"
DATADIR="${DBBASEDIR}/${SERVER}-${MAPPING}-${MODE}"
LOGDIR="${LOGBASEDIR}/${SERVER}-${MAPPING}-${MODE}"


test -e "${SQLFILE}" || exit 1
test -e "${DATADIR}" && exit 1

mkdir -p "${DATADIR}"
mkdir -p "${LOGDIR}"


start_virtuoso "${DATADIR}" "${BASE}/etc/virtuoso/virtuoso-loader.ini"

cat sql/init/virtuoso-ldv.sql | in_container "${SERVER}" isql 1111 dba dba ECHO=ON PROMPT=OFF | tee "${LOGDIR}/init.log"


cat sql/defs.m4 sql/schema/* | m4 -DSERVER="${SERVER}" -DMAPPING="${MAPPING}" -DMODE="${MODE}" | in_container "${SERVER}" isql 1111 nextprot "password" ECHO=ON PROMPT=OFF 2>&1 | tee "${LOGDIR}/schema.log"

zcat "${SQLFILE}" | in_container "${SERVER}" isql 1111 nextprot "password" VERBOSE=OFF PROMPT=OFF 2>&1 | tee "${LOGDIR}/load.log"
grep -q -F "*** Error" "${LOGDIR}/load.log" && exit 1

echo "checkpoint;" | in_container "${SERVER}" isql 1111 dba dba ECHO=ON PROMPT=OFF

cat sql/defs.m4 sql/settings/* | m4 -DSERVER="${SERVER}" -DMAPPING="${MAPPING}" -DMODE="${MODE}" | in_container "${SERVER}" isql 1111 nextprot "password" ECHO=ON PROMPT=OFF 2>&1 | tee "${LOGDIR}/settings.log"

echo "checkpoint;" | in_container "${SERVER}" isql 1111 dba dba ECHO=ON PROMPT=OFF

cat sql/defs.m4 sql/post/foreignkey.sql | m4 -DSERVER="${SERVER}" -DMAPPING="${MAPPING}" -DMODE="${MODE}" | in_container "${SERVER}" isql 1111 nextprot "password" ECHO=ON PROMPT=OFF 2>&1 | tee "${LOGDIR}/foreignkey.log"

cat "sql/post/${SERVER}-${MAPPING}-${MODE}-"*.sql | in_container "${SERVER}" isql 1111 dba dba ECHO=ON PROMPT=OFF 2>&1 | tee "${LOGDIR}/mapping.log"

echo "checkpoint;" | in_container "${SERVER}" isql 1111 dba dba ECHO=ON PROMPT=OFF

echo "db.dba.sys_db_stat(0);" | in_container "${SERVER}" isql 1111 dba dba ECHO=ON PROMPT=OFF 2>&1 | tee "${LOGDIR}/analyze.log"


stop_container "${SERVER}" "${LOGDIR}/${SERVER}.log"


echo "done"
