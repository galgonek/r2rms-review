# The database servers in containers: the server <server> runs in the container nextprot-<server> as the user running
# the scripts, keeps its data in a directory of the host and publishes its ports on PUBLISH_ADDRESS; its clients run in
# the same container. The scripts source this file after config.sh in the base directory of the repository.


# rootless podman maps the user to the same identifiers in the container, so the files of the databases belong to the
# user in both cases
if [ "${CONTAINER}" = "podman" ]; then
  CONTAINER_USER=(--userns=keep-id)
else
  CONTAINER_USER=(--user "$(id -u):$(id -g)")
fi

# how long a server may take to shut down before the runtime kills it
STOP_TIMEOUT="3600"


# wait_for_server <server> <command>...
#   waits until the command succeeds in the container of the server, fails if the server stops instead
wait_for_server()
{
  until "${CONTAINER}" exec "nextprot-$1" "${@:2}" > /dev/null 2>&1; do
    test "$("${CONTAINER}" inspect --format '{{.State.Running}}' "nextprot-$1")" = "true" || return 1
    sleep 5
  done
}


# start_postgresql <data directory> [<server option>...]
#   initializes an empty data directory as initdb --encoding=UTF8 --locale=C --username=postgres would; the server
#   listens on all addresses and takes pg_hba.conf from the image, so a data directory of a server running outside a
#   container works as well; the parallel queries share the memory of the host, the 64 MB of a container are too little
start_postgresql()
{
  "${CONTAINER}" run --detach --name nextprot-postgresql "${CONTAINER_USER[@]}" --ipc=host --publish "${PUBLISH_ADDRESS}:5432:5432" --volume "$1:/var/lib/postgresql/data" --env PGDATA=/var/lib/postgresql/data --env POSTGRES_PASSWORD=password --env POSTGRES_INITDB_ARGS="--encoding=UTF8 --locale=C" "${POSTGRESQL_IMAGE}" -c listen_addresses='*' -c hba_file=/etc/postgresql/pg_hba.conf "${@:2}"

  # the temporary server initializing the data directory does not listen on TCP
  wait_for_server postgresql pg_isready --host=127.0.0.1
}


# start_mariadb <data directory> [<server option>...]
#   initializes an empty data directory with the user root@localhost without a password
start_mariadb()
{
  "${CONTAINER}" run --detach --name nextprot-mariadb "${CONTAINER_USER[@]}" --publish "${PUBLISH_ADDRESS}:3306:3306" --volume "$1:/var/lib/mysql" --volume "${PWD}/etc/mariadb/mariadb.cnf:/etc/mysql/conf.d/nextprot.cnf:ro" --env MARIADB_ALLOW_EMPTY_ROOT_PASSWORD=1 --env MARIADB_ROOT_HOST=localhost "${MARIADB_IMAGE}" "${@:2}"

  # the temporary server initializing the data directory does not listen on TCP; ping succeeds without a user
  wait_for_server mariadb mariadb-admin --protocol=tcp ping
}


# start_virtuoso <data directory> <configuration file> [<container option>...]
#   publishes the SQL port 1111 and the HTTP port of the configuration file
start_virtuoso()
{
  local PORT="$(sed -n -r 's/^ServerPort *= *([0-9]+).*/\1/p' "$2")"

  "${CONTAINER}" run --detach --name nextprot-virtuoso "${CONTAINER_USER[@]}" --publish "${PUBLISH_ADDRESS}:1111:1111" --publish "${PUBLISH_ADDRESS}:${PORT}:${PORT}" --volume "$1:/database" --volume "$2:/etc/virtuoso.ini:ro" "${@:3}" "${VIRTUOSO_IMAGE}" +configfile /etc/virtuoso.ini

  wait_for_server virtuoso isql 1111 dba dba "EXEC=status();"
}


# start_qlever <index directory> [<server option>...]
#   serves the index nextprot on the port 8888; the server ends on the stop signal only under an init process
start_qlever()
{
  "${CONTAINER}" run --detach --name nextprot-qlever "${CONTAINER_USER[@]}" --init --publish "${PUBLISH_ADDRESS}:8888:8888" --volume "$1:/index" --workdir /index --entrypoint qlever-server "${QLEVER_IMAGE}" -i nextprot -p 8888 "${@:2}"

  wait_for_server qlever curl --silent --fail http://localhost:8888/ping
}


# in_container <server> <command>...
#   runs the command in the container of the server, with the standard input of the caller
in_container()
{
  "${CONTAINER}" exec --interactive "nextprot-$1" "${@:2}"
}


# stop_container <server> <log file>
#   stops the server unless it has stopped already, appends its output to the log file and removes the container
stop_container()
{
  "${CONTAINER}" stop -t "${STOP_TIMEOUT}" "nextprot-$1"
  "${CONTAINER}" logs "nextprot-$1" >> "$2" 2>&1
  "${CONTAINER}" rm --volumes "nextprot-$1"
}
