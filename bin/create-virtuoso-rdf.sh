#!/bin/bash

set -ue -o pipefail -o xtrace

if [[ "${BASH_SOURCE}" = */* ]]; then
  cd -- "${BASH_SOURCE%/*}/.."
fi

. ./bin/config.sh
. ./bin/containers.sh


SERVER="virtuoso"

BASE="${PWD}"
DATADIR="${DBBASEDIR}/virtuoso-rdf"
LOGDIR="${LOGBASEDIR}/virtuoso-rdf"


test -e "${DATADIR}" && exit 1

mkdir -p "${DATADIR}"
mkdir -p "${LOGDIR}"


# the source directory has the same path in the container, which must be in DirsAllowed of the configuration
start_virtuoso "${DATADIR}" "${BASE}/etc/virtuoso/virtuoso.ini" --volume "${SOURCEDIR}:${SOURCEDIR}:ro"

cat sql/init/virtuoso-rdf.sql | in_container "${SERVER}" isql 1111 dba dba ECHO=ON PROMPT=OFF | tee "${LOGDIR}/init.log"


(echo "ld_dir('${SOURCEDIR}' ,'*.ttl.gz', 'http://nextprot.org/rdf');"; echo "rdf_loader_run();") | in_container "${SERVER}" isql 1111 dba dba ECHO=ON PROMPT=OFF 2>&1 | tee "${LOGDIR}/load.log"

echo "checkpoint;" | in_container "${SERVER}" isql 1111 dba dba ECHO=ON PROMPT=OFF

echo "db.dba.sys_db_stat(0);" | in_container "${SERVER}" isql 1111 dba dba ECHO=ON PROMPT=OFF 2>&1 | tee "${LOGDIR}/analyze.log"


stop_container "${SERVER}" "${LOGDIR}/${SERVER}.log"


echo "done"
