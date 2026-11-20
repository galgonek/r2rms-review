# the directory of the data, the databases, the logs and the tools, unless BASE is set in the environment
BASE="${BASE:-${HOME}/review}"

SOURCEDIR="${BASE}/data/nextprot-2021-11-19.repaired/rdf/ttl"
SQLBASEDIR="${BASE}/sql"
DBBASEDIR="${BASE}/db"
LOGBASEDIR="${BASE}/log/create"
OUTBASEDIR="${BASE}/log/test"

# the reference results of the query sets, which bin/check.sh compares the results in OUTBASEDIR with: a directory for
# each query set, holding <query>.xml for each query
REFBASEDIR="${BASE}/references"

# the caches of the Sparqloscope benchmark generator, reused by the next run; remove them when the QLever index changes
SPARQLOSCOPEDIR="${BASE}/sparqloscope"

# PostgreSQL, MariaDB, Virtuoso and QLever run in containers of this runtime (podman or docker) and publish their
# ports on PUBLISH_ADDRESS; bin/build-images.sh builds the images of PostgreSQL with pgsparql, of Virtuoso and of the
# Sparqloscope benchmark generator
CONTAINER="podman"
PUBLISH_ADDRESS="127.0.0.1"
POSTGRESQL_IMAGE="nextprot-postgresql"
POSTGRESQL_BASE_IMAGE="docker.io/library/postgres:18.7"
PGSPARQL_REPOSITORY="https://github.com/idsm-src/pgsparql.git"
PGSPARQL_BRANCH="next"
MARIADB_IMAGE="docker.io/library/mariadb:13.0.2"
VIRTUOSO_IMAGE="nextprot-virtuoso"
VIRTUOSO_COMMIT="d166f17809b0af0138690adfe9c6db241500a366"
QLEVER_IMAGE="docker.io/adfreiburg/qlever:commit-0ab2234a0d"
SPARQLOSCOPE_IMAGE="nextprot-sparqloscope"
SPARQLOSCOPE_REPOSITORY="https://github.com/ad-freiburg/sparqloscope.git"
SPARQLOSCOPE_COMMIT="61ad92f7ae3de202580ab6112aa4f08001990f56"

GRAPHDB_BINDIR="${BASE}/graphdb-11.5.3/bin"
GRAPHDB_LICENSE="${BASE}/graphdb.license"
JENA_BINDIR="${BASE}/apache-jena-6.2.0/bin"
FUSEKI_BINDIR="${BASE}/apache-jena-fuseki-6.2.0/"
BLAZEGRAPH_HOME="${BASE}/blazegraph-tgz-2.1.6-SNAPSHOT"
ONTOPDIR="${BASE}/ontop-cli-5.5.0"
