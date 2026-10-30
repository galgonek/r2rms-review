SOURCEDIR="${HOME}/data/nextprot-2021-11-19.repaired/rdf/ttl"
SQLBASEDIR="${HOME}/sql"
DBBASEDIR="${HOME}/db"
LOGBASEDIR="${HOME}/log/create"
OUTBASEDIR="${HOME}/log/test"

# PostgreSQL, MariaDB and Virtuoso run in containers of this runtime (podman or docker) and publish their ports on
# PUBLISH_ADDRESS; bin/build-images.sh builds the images of PostgreSQL with pgsparql and of Virtuoso
CONTAINER="podman"
PUBLISH_ADDRESS="127.0.0.1"
POSTGRESQL_IMAGE="nextprot-postgresql"
POSTGRESQL_BASE_IMAGE="docker.io/library/postgres:18"
PGSPARQL_REPOSITORY="https://github.com/idsm-src/pgsparql.git"
PGSPARQL_BRANCH="next"
MARIADB_IMAGE="docker.io/library/mariadb:11.8"
VIRTUOSO_IMAGE="nextprot-virtuoso"
VIRTUOSO_BASE_IMAGE="docker.io/openlink/virtuoso-opensource-7:7.2.17"

GRAPHDB_BINDIR="${HOME}/graphdb-10.0.0/bin"
JENA_BINDIR="${HOME}/apache-jena-4.5.0/bin"
FUSEKI_BINDIR="${HOME}/apache-jena-fuseki-4.5.0/"
BLAZEGRAPH_HOME="${HOME}/blazegraph-tgz-2.1.6-SNAPSHOT"
ONTOPDIR="${HOME}/ontop-cli-4.2.1"
QLEVER="${HOME}/qlever"
