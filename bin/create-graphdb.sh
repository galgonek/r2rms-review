#!/bin/bash

set -ue -o pipefail -o xtrace

if [[ "${BASH_SOURCE}" = */* ]]; then
  cd -- "${BASH_SOURCE%/*}/.."
fi

. ./bin/config.sh


BASE="${PWD}"
DATADIR="${DBBASEDIR}/graphdb"
LOGDIR="${LOGBASEDIR}/graphdb"


test -e "${DATADIR}" && exit 1

mkdir -p "${DATADIR}"
mkdir -p "${LOGDIR}"


"${GRAPHDB_BINDIR}/importrdf" preload -Xms42g -Xmx42g -Dgraphdb.home="${DATADIR}" -c "${BASE}/etc/graphdb/nextprot.ttl" "${SOURCEDIR}"/* 2>&1 | tee "${LOGDIR}/load.log"


echo "done"
