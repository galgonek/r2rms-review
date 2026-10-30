# the directory of the data, the databases, the logs and the tools, unless BASE is set in the environment
BASE="${BASE:-${HOME}/review}"

SOURCEDIR="${BASE}/data/nextprot-2021-11-19.repaired/rdf/ttl"
SQLBASEDIR="${BASE}/sql"
DBBASEDIR="${BASE}/db"
LOGBASEDIR="${BASE}/log/create"
OUTBASEDIR="${BASE}/log/test"

# PostgreSQL, MariaDB, Virtuoso and QLever run in containers of this runtime (podman or docker) and publish their
# ports on PUBLISH_ADDRESS; bin/build-images.sh builds the images of PostgreSQL with pgsparql and of Virtuoso
CONTAINER="podman"
PUBLISH_ADDRESS="127.0.0.1"
POSTGRESQL_IMAGE="nextprot-postgresql"
POSTGRESQL_BASE_IMAGE="docker.io/library/postgres:18.6"
PGSPARQL_REPOSITORY="https://github.com/idsm-src/pgsparql.git"
PGSPARQL_BRANCH="next"
MARIADB_IMAGE="docker.io/library/mariadb:13.0.2"
VIRTUOSO_IMAGE="nextprot-virtuoso"
VIRTUOSO_BASE_IMAGE="docker.io/openlink/virtuoso-opensource-7:7.2.17"
QLEVER_IMAGE="docker.io/adfreiburg/qlever:commit-16a682506e"

GRAPHDB_BINDIR="${BASE}/graphdb-11.5.2/bin"
GRAPHDB_LICENSE="${BASE}/graphdb.license"
JENA_BINDIR="${BASE}/apache-jena-6.2.0/bin"
FUSEKI_BINDIR="${BASE}/apache-jena-fuseki-6.2.0/"
BLAZEGRAPH_HOME="${BASE}/blazegraph-tgz-2.1.6-SNAPSHOT"
ONTOPDIR="${BASE}/ontop-cli-5.5.0"
