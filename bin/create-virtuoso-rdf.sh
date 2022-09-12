#!/bin/bash

set -ue -o pipefail -o xtrace

if [[ "${BASH_SOURCE}" = */* ]]; then
  cd -- "${BASH_SOURCE%/*}/.."
fi

. ./bin/config.sh


BASE="${PWD}"
DATADIR="${DBBASEDIR}/virtuoso-rdf"
LOGDIR="${LOGBASEDIR}/virtuoso-rdf"


test -e "${DATADIR}" && exit 1

mkdir -p "${DATADIR}"
mkdir -p "${LOGDIR}"


(cd "${DATADIR}"; "${VIRTUOSO_BINDIR}/virtuoso-t" +wait +configfile "${BASE}/etc/virtuoso/virtuoso.ini")

cat sql/init/virtuoso-rdf.sql | "${VIRTUOSO_BINDIR}/isql" 1111 dba dba ECHO=ON PROMPT=OFF | tee "${LOGDIR}/init.log"


(echo "ld_dir('${SOURCEDIR}' ,'*.ttl.gz', 'http://nextprot.org/rdf');"; echo "rdf_loader_run();") | "${VIRTUOSO_BINDIR}/isql" 1111 dba dba ECHO=ON PROMPT=OFF 2>&1 | tee "${LOGDIR}/load.log"

echo "checkpoint;" | "${VIRTUOSO_BINDIR}/isql" 1111 dba dba ECHO=ON PROMPT=OFF

echo "db.dba.sys_db_stat(0);" | "${VIRTUOSO_BINDIR}/isql" 1111 dba dba ECHO=ON PROMPT=OFF 2>&1 | tee "${LOGDIR}/analyze.log"


"${VIRTUOSO_BINDIR}/isql" 1111 dba dba -K


echo "done"
