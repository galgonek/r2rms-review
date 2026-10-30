#!/bin/bash

set -ue -o pipefail -o xtrace

if [[ "${BASH_SOURCE}" = */* ]]; then
  cd -- "${BASH_SOURCE%/*}/.."
fi

. ./bin/config.sh
. ./bin/containers.sh


MODE="$1"
MAPPING="ontop"
SERVER="mariadb"

SQLFILE="${SQLBASEDIR}/${SERVER}-${MODE}.sql.gz"
DATADIR="${DBBASEDIR}/${SERVER}-${MAPPING}-${MODE}"
LOGDIR="${LOGBASEDIR}/${SERVER}-${MAPPING}-${MODE}"


test -e "${SQLFILE}" || exit 1
test -e "${DATADIR}" && exit 1

mkdir -p "${DATADIR}"
mkdir -p "${LOGDIR}"


start_mariadb "${DATADIR}" --innodb_buffer_pool_size=4G

cat sql/init/mariadb.sql | in_container "${SERVER}" mariadb -v --user=root 2>&1 | tee "${LOGDIR}/init.log"


cat sql/defs.m4 sql/schema/* | m4 -DSERVER="${SERVER}" -DMAPPING="${MAPPING}" -DMODE="${MODE}" | in_container "${SERVER}" mariadb -v --user=nextprot --password="password" 2>&1 | tee "${LOGDIR}/schema.log"

zcat "${SQLFILE}" | in_container "${SERVER}" mariadb --user=nextprot --password="password" 2>&1 | tee "${LOGDIR}/load.log"

in_container "${SERVER}" mariadb-check --user=root --optimize --all-databases 2>&1 | tee "${LOGDIR}/optimize.log"

cat sql/defs.m4 sql/settings/* | m4 -DSERVER="${SERVER}" -DMAPPING="${MAPPING}" -DMODE="${MODE}" | in_container "${SERVER}" mariadb -v --user=nextprot --password="password" 2>&1 | tee "${LOGDIR}/settings.log"

cat sql/defs.m4 sql/post/foreignkey.sql | m4 -DSERVER="${SERVER}" -DMAPPING="${MAPPING}" -DMODE="${MODE}" | in_container "${SERVER}" mariadb -v --user=nextprot --password="password" 2>&1 | tee "${LOGDIR}/foreignkey.log"

in_container "${SERVER}" mariadb-check --user=root --analyze --persistent --all-databases 2>&1 | tee "${LOGDIR}/analyze.log"


stop_container "${SERVER}" "${LOGDIR}/${SERVER}.log"


echo "done"
