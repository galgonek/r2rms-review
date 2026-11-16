#!/bin/bash

set -ue -o pipefail

if [ $# -lt 1 ]; then
  echo "usage: bin/check.sh <queryset> [--examples <count>] [--threads <count>] [<engine>...]" >&2
  exit 2
fi

set -o xtrace

if [[ "${BASH_SOURCE}" = */* ]]; then
  cd -- "${BASH_SOURCE%/*}/.."
fi

. ./bin/config.sh


# the name of the query set, then the options of the check and the engines to check as the names of the directories of
# their results (glob patterns allowed, all engines by default); prints the results that differ from the references
QUERYSET="$1"
shift

BASE="${PWD}"
QUERIES="${BASE}/data/queries/${QUERYSET}.sparql"


# no warning that no logger is bound (Jena parses the queries)
java -Dslf4j.internal.verbosity=ERROR -cp "${BASE}/target/classes:${BASE}/target/dependency/*" cz.iocb.nextprot.test.Check --queries "${QUERIES}" "${OUTBASEDIR}/${QUERYSET}" "${REFBASEDIR}/${QUERYSET}" "$@"
