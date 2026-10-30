#!/bin/bash

set -ue -o pipefail -o xtrace

if [[ "${BASH_SOURCE}" = */* ]]; then
  cd -- "${BASH_SOURCE%/*}/.."
fi

. ./bin/config.sh
. ./bin/containers.sh


DATADIR="${DBBASEDIR}/qlever"
LOGDIR="${LOGBASEDIR}/qlever"
CPUS="$(nproc)"


test -e "${DATADIR}" && exit 1

mkdir -p "${DATADIR}"
mkdir -p "${LOGDIR}"


# the index builder reads N-Triples from the index directory, the only directory mounted into its container; riot
# labels each blank node randomly, so the blank nodes of different files stay distinct
printf '%s\0' "${SOURCEDIR}"/*.ttl.gz | xargs -0 -P "${CPUS}" -I '{}' bash -c 'set -o pipefail; "$0" --output=nt "$1" | gzip > "$2/$(basename "$1" .ttl.gz).nt.gz"' "${JENA_BINDIR}/riot" '{}' "${DATADIR}" 2>&1 | tee "${LOGDIR}/convert.log"

echo '{ "num-triples-per-batch": 10000000 }' > "${DATADIR}/nextprot.settings.json"


# the index builder may need more open files than the default soft limit allows
"${CONTAINER}" run --rm --name nextprot-qlever "${CONTAINER_USER[@]}" --init --volume "${DATADIR}:/index" --workdir /index --entrypoint bash "${QLEVER_IMAGE}" -c 'ulimit -Sn "$(ulimit -Hn)" && zcat -f *.nt.gz | qlever-index -i nextprot -s nextprot.settings.json --vocabulary-type on-disk-compressed -F ttl -f -' 2>&1 | tee "${LOGDIR}/load.log"

rm "${DATADIR}"/*.nt.gz


echo "done"
