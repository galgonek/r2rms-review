#!/bin/bash

set -ue -o pipefail -o xtrace

if [[ "${BASH_SOURCE}" = */* ]]; then
  cd -- "${BASH_SOURCE%/*}/.."
fi

. ./bin/config.sh
. ./bin/containers.sh


SERVER="$1"
MAPPING="$2"
METHOD="$3"
QUERYSET="$4"
COUNT="$5"
CPU="${6-1}"

BASE="${PWD}"
TMPDIR="$(mktemp -d)"
SLEEP="300"

# a variant whose name ends with -opt is the variant of the name without the suffix with the join optimizer of the
# engine on, so it runs over the same database
if [ "${METHOD%-opt}" = "integer" -o "${METHOD%-opt}" = "combined" ]; then
  MODE="indirect"
elif [ "${METHOD%-opt}" = "string" ]; then
  MODE="direct"
elif [ "${METHOD}" = "rdfid" ]; then
  MODE="rdfid"
elif [ "${METHOD}" = "rdfmixed" ]; then
  MODE="rdfmixed"
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

    start_virtuoso "${DATADIR}" "${TMPDIR}/virtuoso.ini"

    if [ "${METHOD}" != "rdf" ]; then
      SETTINGS="define input:storage virtrdf:NeXtProt${METHOD^}QuadStorage"
    fi

    ENDPOINT="http://localhost:8080/sparql/"
    ;;

  "postgresql")
    # work_mem is the memory of one process for a sort or a hash table, so in a parallel run the processes of a query
    # get 12 GB for one together (work_mem times hash_mem_multiplier 2 times the processes); a serial run keeps the
    # work_mem of the configuration
    OPTIONS=()

    if [ "${CPU}" != "1" ]; then
      OPTIONS=(-c work_mem="$((6144 / CPU))MB")
    fi

    start_postgresql "${DATADIR}" -c max_parallel_workers_per_gather=$(("${CPU}" - 1)) "${OPTIONS[@]}"

    ENDPOINT="http://localhost:8080/sparql/"
    ;;

  "mariadb")
    start_mariadb "${DATADIR}"

    ENDPOINT="http://localhost:8080/sparql/"
    ;;

  "graphdb")
    test -e "${GRAPHDB_LICENSE}" || exit 1

    GDB_HEAP_SIZE=42g "${GRAPHDB_BINDIR}/graphdb" -Dgraphdb.home="${DATADIR}" -Dgraphdb.license.file="${GRAPHDB_LICENSE}" -s &
    PID=$!

    ENDPOINT="http://localhost:7200/repositories/nextprot"
    ;;

  "jena-tdb2")
    cd "${DATADIR}";
    # TDB2 maps its files into memory, so they are cached by the system and the heap holds the intermediate results;
    # 31 GB keep the object pointers compressed and leave the rest of the memory to the page cache
    JVM_ARGS=-Xmx31G "${FUSEKI_BINDIR}/fuseki-server" --config="${BASE}/etc/jena/config.ttl" &
    PID=$!
    cd -

    ENDPOINT="http://localhost:3030/nextprot/sparql"
    ;;

  "blazegraph")
    CONFIG="${BASE}/etc/blazegraph/RWStore.properties"
    cd "${DATADIR}";
    # the heap holds the intermediate results (some queries ran out of 4 GB) and the system caches the journal; 31 GB
    # keep the object pointers compressed
    java -Djava.awt.headless=true -server -Xmx31g -XX:MaxDirectMemorySize=3000m -XX:+UseG1GC -Djetty.port=9999 -Djetty.resourceBase="${BLAZEGRAPH_HOME}/war/" -Djetty.home="${BLAZEGRAPH_HOME}/war/" -Djetty.overrideWebXml="${BLAZEGRAPH_HOME}/war/WEB-INF/override-web.xml" -DJETTY_XML="${BLAZEGRAPH_HOME}/conf/jetty.xml" -cp "${BLAZEGRAPH_HOME}/lib/*" com.bigdata.rdf.sail.webapp.NanoSparqlServer -jettyXml "${BLAZEGRAPH_HOME}/conf/jetty.xml" 9999 nextprot "${BASE}/etc/blazegraph/RWStore.properties" &
    PID=$!
    cd -

    # Blazegraph limits the time of a query only when its web application or the request asks for it, so the request
    # asks for the timeout of the other stores (in seconds)
    ENDPOINT="http://localhost:9999/blazegraph/namespace/nextprot/sparql?timeout=1000"
    ;;

  "qlever")
    # one query at a time, no cache, the timeout of the other stores; a query runs on as many threads as the others
    # get processor cores (max-num-threads-per-query bounds all its parallel tasks, sort-num-tasks-in-flight its
    # sorts); 100 GB for the queries leave the rest of the memory to the index and the system, and a sort stays in
    # memory up to 32 GB
    start_qlever "${DATADIR}" -j 1 -m 100G -s 1000s --set-runtime-parameter disable-caching=true --set-runtime-parameter max-num-threads-per-query="${CPU}" --set-runtime-parameter sort-num-tasks-in-flight="${CPU}" --set-runtime-parameter sort-in-memory-threshold=32G

    ENDPOINT="http://localhost:8888"
    ;;
esac

case "${MAPPING}" in
  "idsm")
    cd "${TMPDIR}"
    java -cp "${BASE}/target/classes:${BASE}/target/dependency/*" -Dserver.tomcat.basedir="${TMPDIR}" cz.iocb.sparql.nextprot.Server "${METHOD}" &
    PID=$!
    cd -
    ;;
  "ontop")
    cd "${TMPDIR}"
    # the launcher of Ontop limits the heap to 512 MB unless ONTOP_JAVA_ARGS sets it
    ONTOP_JAVA_ARGS="-Xmx16g" "${ONTOPDIR}/ontop" endpoint --properties="${BASE}/etc/ontop/${SERVER}.properties" --mapping="${BASE}/etc/ontop/mapping-${METHOD}.obda" &
    PID=$!
    cd -
    ;;
esac

sleep "${SLEEP}"



#
# test
#

java -cp "${BASE}/target/classes" cz.iocb.nextprot.test.Test "${ENDPOINT}" "${SETTINGS-}" "${QUERIES}" "${OUTDIR}" "${COUNT}" "${SKIP_AFTER_TIMEOUT}"



#
# stop
#

if [ -n "${PID-}" ]; then
  kill "${PID}"
  wait "${PID}" || true
fi

case "${SERVER}" in
  "virtuoso" | "postgresql" | "mariadb" | "qlever")
    stop_container "${SERVER}" /dev/stdout
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
