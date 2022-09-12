#!/bin/bash

set -ue -o pipefail -o xtrace

if [[ $BASH_SOURCE = */* ]]; then
  cd -- "${BASH_SOURCE%/*}/.."
fi


time ./bin/create-postgresql.sh direct idsm
time ./bin/create-postgresql.sh indirect idsm
time ./bin/create-postgresql.sh direct ontop
time ./bin/create-postgresql.sh indirect ontop
time ./bin/create-mariadb.sh direct
time ./bin/create-mariadb.sh indirect
time ./bin/create-virtuoso-ldv.sh direct
time ./bin/create-virtuoso-ldv.sh indirect

time ./bin/create-virtuoso-rdf.sh
time ./bin/create-jena-tdb2.sh
time ./bin/create-graphdb.sh
time ./bin/create-blazegraph.sh
