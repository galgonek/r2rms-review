#!/bin/bash

set -ue -o pipefail -o xtrace

if [[ "${BASH_SOURCE}" = */* ]]; then
  cd -- "${BASH_SOURCE%/*}/.."
fi

. ./bin/config.sh


BASE="${PWD}"
DATADIR="${DBBASEDIR}/jena-tdb2"
LOGDIR="${LOGBASEDIR}/jena-tdb2"


test -e "${DATADIR}" && exit 1

mkdir -p "${DATADIR}"
mkdir -p "${LOGDIR}"


"${JENA_BINDIR}/tdb2.tdbloader" --loc "${DATADIR}" "${SOURCEDIR}"/* 2>&1 | tee "${LOGDIR}/load.log"

"${JENA_BINDIR}/tdb2.tdbcompact" --loc "${DATADIR}" --deleteOld 2>&1 | tee "${LOGDIR}/optimize.log"


echo "done"
