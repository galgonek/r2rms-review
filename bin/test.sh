#!/bin/bash

set -ue -o pipefail -o xtrace

if [[ "${BASH_SOURCE}" = */* ]]; then
  cd -- "${BASH_SOURCE%/*}/.."
fi

. ./bin/config.sh


SERVER="$1"
MAPPING="$2"
METHOD="$3"
QUERYSET="$4"
COUNT="$5"
CPU="${6-1}"

BASE="${PWD}"
TMPDIR="$(mktemp -d)"
SLEEP="300"

if [ "${METHOD}" = "integer" -o "${METHOD}" = "combined" ]; then
  MODE="indirect"
elif [ "${METHOD}" = "string" ]; then
  MODE="direct"
else
  MODE="rdf"
fi

if [ -d "${DBBASEDIR}/${SERVER}-${MAPPING}-${MODE}" ]; then
  DATADIR="${DBBASEDIR}/${SERVER}-${MAPPING}-${MODE}"
elif [ -d "${DBBASEDIR}/${SERVER}-${MODE}" ]; then
  DATADIR="${DBBASEDIR}/${SERVER}-${MODE}"
elif [ -d "${DBBASEDIR}/${SERVER%-*}-${MODE}" ]; then
  DATADIR="${DBBASEDIR}/${SERVER%-*}-${MODE}"
else
  DATADIR="${DBBASEDIR}/${SERVER}"
fi

if [ -f "${BASE}/data/queries/${QUERYSET}-${SERVER}-${MAPPING}-${METHOD}.sparql" ]; then
  QUERIES="${BASE}/data/queries/${QUERYSET}-${SERVER}-${MAPPING}-${METHOD}.sparql"
elif [ -f "${BASE}/data/queries/${QUERYSET}-${MAPPING}.sparql" ]; then
  QUERIES="${BASE}/data/queries/${QUERYSET}-${MAPPING}.sparql"
elif [ -f "${BASE}/data/queries/${QUERYSET}-${SERVER}.sparql" ]; then
  QUERIES="${BASE}/data/queries/${QUERYSET}-${SERVER}.sparql"
else
  QUERIES="${BASE}/data/queries/${QUERYSET}.sparql"
fi

if [ "${MAPPING}" != "native" ]; then
  OUTDIR="${OUTBASEDIR}/${QUERYSET}/${SERVER}-${MAPPING}-${METHOD}"
else
  OUTDIR="${OUTBASEDIR}/${QUERYSET}/${SERVER}-${METHOD}"
fi

if [ "${CPU}" != "1" ]; then
  OUTDIR="${OUTDIR}-p${CPU}"
fi



#
# start
#

sudo sh -c 'sync; echo 3 > /proc/sys/vm/drop_caches'
sleep "${SLEEP}"

case "${SERVER}" in
  "virtuoso")
    cat "${BASE}/etc/virtuoso/virtuoso.ini" | sed -r "/ThreadsPerQuery/s|1$|${CPU}|" > "${TMPDIR}/virtuoso.ini"
  
    cd "${DATADIR}"
    "${VIRTUOSO_BINDIR}/virtuoso-t" +wait +configfile "${TMPDIR}/virtuoso.ini"
    cd -

    if [ "${METHOD}" != "rdf" ]; then
      SETTINGS="define input:storage virtrdf:NeXtProt${METHOD^}QuadStorage"
    fi

    ENDPOINT="http://localhost:8080/sparql/"
    ;;

  "postgresql")
    "${POSTGRESQL_BINDIR}/pg_ctl" --pgdata="${DATADIR}" -o "--unix_socket_directories='${TMPDIR}'" -o --max_parallel_workers_per_gather=$(("${CPU}" - 1)) start

    ENDPOINT="http://localhost:8080/sparql/"
    ;;

  "mariadb")
    "${MARIADB_BINDIR}/mariadbd-safe" --defaults-file="${BASE}/etc/mariadb/mariadb.cnf" --datadir="${DATADIR}" --socket="${TMPDIR}/mariadb.sock" --pid-file="${TMPDIR}/mariadb.pid" &
    sleep "${SLEEP}"

    ENDPOINT="http://localhost:8080/sparql/"
    ;;

  "graphdb")
    GDB_HEAP_SIZE=42g "${GRAPHDB_BINDIR}/graphdb" -Dgraphdb.home="${DATADIR}" -s &
    PID=$!

    ENDPOINT="http://localhost:7200/repositories/nextprot"
    ;;

  "jena-tdb2")
    cd "${DATADIR}";
    JVM_ARGS=-Xmx42G "${FUSEKI_BINDIR}/fuseki-server" --config="${BASE}/etc/jena/config.ttl" &
    PID=$!
    cd -

    ENDPOINT="http://localhost:3030/nextprot/sparql"
    ;;

  "blazegraph")
    CONFIG="${BASE}/etc/blazegraph/RWStore.properties"
    cd "${DATADIR}";
    java -Djava.awt.headless=true -server -Xmx4g -XX:MaxDirectMemorySize=3000m -XX:+UseG1GC -Djetty.port=9999 -Djetty.resourceBase="${BLAZEGRAPH_HOME}/war/" -Djetty.home="${BLAZEGRAPH_HOME}/war/" -Djetty.overrideWebXml="${BLAZEGRAPH_HOME}/war/WEB-INF/override-web.xml" -DJETTY_XML="${BLAZEGRAPH_HOME}/conf/jetty.xml" -cp "${BLAZEGRAPH_HOME}/lib/*" com.bigdata.rdf.sail.webapp.NanoSparqlServer -jettyXml "${BLAZEGRAPH_HOME}/conf/jetty.xml" 9999 nextprot "${BASE}/etc/blazegraph/RWStore.properties" &
    PID=$!
    cd -

    ENDPOINT="http://localhost:9999/blazegraph/namespace/nextprot/sparql"
    ;;
esac

case "${MAPPING}" in
  "idsm")
    cd "${TMPDIR}"
    java -cp "${BASE}/classes:${BASE}/lib/*" -Dserver.tomcat.basedir="${TMPDIR}" cz.iocb.sparql.nextprot.Server "${METHOD}" &
    PID=$!
    cd -
    ;;
  "ontop")
    cd "${TMPDIR}"
    "${ONTOPDIR}/ontop" endpoint --properties="${BASE}/etc/ontop/${SERVER}.properties" --mapping="${BASE}/etc/ontop/mapping-${METHOD}.obda" &
    PID=$!
    cd -
    ;;
esac

sleep "${SLEEP}"



#
# test
#

java -cp "${BASE}/classes" cz.iocb.nextprot.test.Test "${ENDPOINT}" "${SETTINGS-}" "${QUERIES}" "${OUTDIR}" "${COUNT}"



#
# stop
#

if [ -n "${PID-}" ]; then
  kill "${PID}"
  wait "${PID}" || true
fi

case "${SERVER}" in
  "virtuoso"*)
    "${VIRTUOSO_BINDIR}/isql" 1111 dba dba -K
    ;;

  "postgresql")
    "${POSTGRESQL_BINDIR}/pg_ctl" --pgdata="${DATADIR}" stop
    ;;

  "mariadb")
    "${MARIADB_BINDIR}/mariadb-admin" --defaults-file="${BASE}/etc/mariadb/mariadb.cnf" --socket="${TMPDIR}/mariadb.sock" shutdown
    ;;
esac

rm -r "${TMPDIR}"



#
# notification
#

if [ -n "${EMAIL-}" ]; then
  cat "${OUTDIR}/times.txt" | mail -s "${QUERYSET} by ${SERVER}-${MAPPING}-${METHOD} on ${CPU} CPU is done" ${EMAIL}
fi

sleep "${SLEEP}"
