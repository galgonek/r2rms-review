#!/bin/bash

set -ue -o pipefail -o xtrace

if [[ "${BASH_SOURCE}" = */* ]]; then
  cd -- "${BASH_SOURCE%/*}/.."
fi

. ./bin/config.sh


BASE="${PWD}"
DATADIR="${DBBASEDIR}/blazegraph"
LOGDIR="${LOGBASEDIR}/blazegraph"


test -e "${DATADIR}" && exit 1

mkdir -p "${DATADIR}"
mkdir -p "${LOGDIR}"


cd "${DATADIR}"
java -cp "${BLAZEGRAPH_HOME}/lib/*" -Dlog4j.configuration="${BLAZEGRAPH_HOME}/conf/log4j.properties" com.bigdata.rdf.store.DataLoader -namespace nextprot "${BASE}/etc/blazegraph/RWStore.properties" "${SOURCEDIR}" 2>&1 | tee "${LOGDIR}/load.log"


echo "done"
