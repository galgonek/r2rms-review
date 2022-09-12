#!/bin/bash

set -ue -o pipefail -o xtrace

if [[ "${BASH_SOURCE}" = */* ]]; then
  cd -- "${BASH_SOURCE%/*}/.."
fi

. ./bin/config.sh


MODE="$1"
MAPPING="ldv"
SERVER="virtuoso"

BASE="${PWD}"
DATADIR="${DBBASEDIR}/${SERVER}-${MAPPING}-${MODE}"
LOGDIR="${LOGBASEDIR}/${SERVER}-${MAPPING}-${MODE}"


test -e "${DATADIR}" && exit 1

mkdir -p "${DATADIR}"
mkdir -p "${LOGDIR}"


(cd "${DATADIR}"; "${VIRTUOSO_BINDIR}/virtuoso-t" +wait +configfile "${BASE}/etc/virtuoso/virtuoso-loader.ini")

cat sql/init/virtuoso-ldv.sql | "${VIRTUOSO_BINDIR}/isql" 1111 dba dba ECHO=ON PROMPT=OFF | tee "${LOGDIR}/init.log"


cat sql/defs.m4 sql/schema/* | m4 -DSERVER="${SERVER}" -DMAPPING="${MAPPING}" -DMODE="${MODE}" | "${VIRTUOSO_BINDIR}/isql" 1111 nextprot "password" ECHO=ON PROMPT=OFF 2>&1 | tee "${LOGDIR}/schema.log"

java -Xmx44G -cp "${BASE}/classes:${BASE}/lib/*" cz.iocb.nextprot.load.NeXtProt "jdbc:virtuoso://localhost:1111/DATABASE=nextprot/UID=nextprot/PWD=password" "${SERVER}" "${MODE}" "${SOURCEDIR}" 2>&1 | tee "${LOGDIR}/load.log"

echo "checkpoint;" | "${VIRTUOSO_BINDIR}/isql" 1111 dba dba ECHO=ON PROMPT=OFF

cat sql/defs.m4 sql/settings/* | m4 -DSERVER="${SERVER}" -DMAPPING="${MAPPING}" -DMODE="${MODE}" | "${VIRTUOSO_BINDIR}/isql" 1111 nextprot "password" ECHO=ON PROMPT=OFF 2>&1 | tee "${LOGDIR}/settings.log"

echo "checkpoint;" | "${VIRTUOSO_BINDIR}/isql" 1111 dba dba ECHO=ON PROMPT=OFF

cat sql/defs.m4 sql/post/foreignkey.sql | m4 -DSERVER="${SERVER}" -DMAPPING="${MAPPING}" -DMODE="${MODE}" | "${VIRTUOSO_BINDIR}/isql" 1111 nextprot "password" ECHO=ON PROMPT=OFF 2>&1 | tee "${LOGDIR}/foreignkey.log"

cat "sql/post/${SERVER}-${MAPPING}-${MODE}-"*.sql | "${VIRTUOSO_BINDIR}/isql" 1111 dba dba ECHO=ON PROMPT=OFF 2>&1 | tee "${LOGDIR}/mapping.log"

echo "checkpoint;" | "${VIRTUOSO_BINDIR}/isql" 1111 dba dba ECHO=ON PROMPT=OFF

echo "db.dba.sys_db_stat(0);" | "${VIRTUOSO_BINDIR}/isql" 1111 dba dba ECHO=ON PROMPT=OFF 2>&1 | tee "${LOGDIR}/analyze.log"


"${VIRTUOSO_BINDIR}/isql" 1111 dba dba -K


echo "done"
