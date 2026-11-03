#!/bin/bash

set -ue -o pipefail -o xtrace

if [[ "${BASH_SOURCE}" = */* ]]; then
  cd -- "${BASH_SOURCE%/*}/.."
fi

. ./bin/config.sh
. ./bin/containers.sh


# the name of the query set, data/queries/sparqloscope.sparql by default
QUERYSET="${1-sparqloscope}"

BASE="${PWD}"
DATADIR="${DBBASEDIR}/qlever"
LOGDIR="${LOGBASEDIR}/sparqloscope"
QUERIES="${BASE}/data/queries/${QUERYSET}.sparql"
TOKEN="_F7YYBYaoSzs9"


test -e "${QUERIES}" && exit 1

mkdir -p "${SPARQLOSCOPEDIR}"
mkdir -p "${LOGDIR}"


# the settings that Sparqloscope asks for: no timeout of the long precomputations, all rows passed to SERVICE, the
# results of SERVICE cached and GROUP BY by hash maps
start_qlever "${DATADIR}" -m 128G -s 86400s -S 0 --cache-service-results true -a "${TOKEN}"
in_container qlever curl --silent --fail http://localhost:8888 --data-urlencode group-by-hash-map-enabled=true --data-urlencode "access-token=${TOKEN}" > /dev/null


# QLever reads the precomputed results from the generator by SERVICE, so the generator shares the network of the
# container of QLever and serves them on the port 8000 there
"${CONTAINER}" run --rm --name nextprot-sparqloscope "${CONTAINER_USER[@]}" --network container:nextprot-qlever --volume "${SPARQLOSCOPEDIR}:/work" --workdir /work "${SPARQLOSCOPE_IMAGE}" /sparqloscope/generate-benchmark.py --query-templates /sparqloscope/query-templates.yaml --sparql-endpoint http://localhost:8888 --external-url http://localhost:8000 --port 8000 --kg-name nextprot --output-format yaml 2>&1 | tee "${LOGDIR}/generate.log"

stop_container qlever "${LOGDIR}/qlever.log"


"${CONTAINER}" run --rm "${CONTAINER_USER[@]}" --volume "${SPARQLOSCOPEDIR}:/work:ro" --volume "${BASE}/etc/sparqloscope/queryset.py:/sparqloscope/queryset.py:ro" "${SPARQLOSCOPE_IMAGE}" /sparqloscope/queryset.py /sparqloscope/query-templates.yaml /work/nextprot.benchmark.yaml > "${QUERIES}"


echo "done"
