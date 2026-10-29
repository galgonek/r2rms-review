#!/bin/bash

set -ue -o pipefail -o xtrace

if [[ "${BASH_SOURCE}" = */* ]]; then
  cd -- "${BASH_SOURCE%/*}/.."
fi

. ./bin/config.sh


# the variants as <server>-<mode>, all of them by default; the data are read once for all of them
VARIANTS="${*:-postgresql-direct postgresql-indirect mariadb-direct mariadb-indirect virtuoso-direct virtuoso-indirect}"

BASE="${PWD}"
LOGDIR="${LOGBASEDIR}/sql"


TARGETS=()

for VARIANT in ${VARIANTS}; do
  SERVER="${VARIANT%-*}"
  MODE="${VARIANT##*-}"
  FILE="${SQLBASEDIR}/${SERVER}-${MODE}.sql.gz"

  test -e "${FILE}" && exit 1

  TARGETS+=("${FILE}" "${SERVER}" "${MODE}")
done

mkdir -p "${SQLBASEDIR}"
mkdir -p "${LOGDIR}"


java -Xmx44G -cp "${BASE}/target/classes:${BASE}/target/dependency/*" cz.iocb.nextprot.load.NeXtProt "${TARGETS[@]}" "${SOURCEDIR}" 2>&1 | tee "${LOGDIR}/generate.log"


echo "done"
