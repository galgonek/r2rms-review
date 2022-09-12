#!/bin/bash

set -ue -o pipefail -o xtrace

if [[ "${BASH_SOURCE}" = */* ]]; then
  cd -- "${BASH_SOURCE%/*}/.."
fi

QUERYSET="${1-queryset}"
COUNT="${2-2}"
CPUS="${3-12}"


./bin/test.sh virtuoso ldv integer "${QUERYSET}" "${COUNT}"
./bin/test.sh virtuoso ldv string "${QUERYSET}" "${COUNT}"
./bin/test.sh virtuoso ldv combined "${QUERYSET}" "${COUNT}"
./bin/test.sh virtuoso native rdf "${QUERYSET}" "${COUNT}"

./bin/test.sh postgresql idsm integer "${QUERYSET}" "${COUNT}"
./bin/test.sh postgresql idsm string "${QUERYSET}" "${COUNT}"
./bin/test.sh postgresql idsm combined "${QUERYSET}" "${COUNT}"

./bin/test.sh postgresql ontop string "${QUERYSET}" "${COUNT}"
./bin/test.sh postgresql ontop combined "${QUERYSET}" "${COUNT}"

./bin/test.sh mariadb ontop string "${QUERYSET}" "${COUNT}"
./bin/test.sh mariadb ontop combined "${QUERYSET}" "${COUNT}"

./bin/test.sh jena-tdb2 native rdf "${QUERYSET}" "${COUNT}"
./bin/test.sh graphdb native rdf "${QUERYSET}" "${COUNT}"
./bin/test.sh blazegraph native rdf "${QUERYSET}" "${COUNT}"


./bin/test.sh virtuoso ldv integer "${QUERYSET}" "${COUNT}" "${CPUS}"
./bin/test.sh virtuoso ldv string "${QUERYSET}" "${COUNT}" "${CPUS}"
./bin/test.sh virtuoso ldv combined "${QUERYSET}" "${COUNT}" "${CPUS}"
./bin/test.sh virtuoso native rdf "${QUERYSET}" "${COUNT}" "${CPUS}"

./bin/test.sh postgresql idsm integer "${QUERYSET}" "${COUNT}" "${CPUS}"
./bin/test.sh postgresql idsm string "${QUERYSET}" "${COUNT}" "${CPUS}"
./bin/test.sh postgresql idsm combined "${QUERYSET}" "${COUNT}" "${CPUS}"

./bin/test.sh postgresql ontop string "${QUERYSET}" "${COUNT}" "${CPUS}"
./bin/test.sh postgresql ontop combined "${QUERYSET}" "${COUNT}" "${CPUS}"
