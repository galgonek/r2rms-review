#!/bin/bash

set -ue -o pipefail -o xtrace

if [[ "${BASH_SOURCE}" = */* ]]; then
  cd -- "${BASH_SOURCE%/*}/.."
fi

. ./bin/config.sh


# the commit of the branch, so that a new commit invalidates the cached layers of the build
PGSPARQL_COMMIT="$(git ls-remote --exit-code "${PGSPARQL_REPOSITORY}" "refs/heads/${PGSPARQL_BRANCH}" | cut -f 1)"


"${CONTAINER}" build --tag "${POSTGRESQL_IMAGE}" --build-arg BASE_IMAGE="${POSTGRESQL_BASE_IMAGE}" --build-arg PGSPARQL_REPOSITORY="${PGSPARQL_REPOSITORY}" --build-arg PGSPARQL_COMMIT="${PGSPARQL_COMMIT}" etc/postgresql

"${CONTAINER}" build --tag "${VIRTUOSO_IMAGE}" --build-arg BASE_IMAGE="${VIRTUOSO_BASE_IMAGE}" etc/virtuoso


echo "done"
