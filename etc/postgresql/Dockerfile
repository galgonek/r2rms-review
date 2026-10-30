# PostgreSQL with pgsparql, built by bin/build-images.sh

ARG BASE_IMAGE=docker.io/library/postgres:18


FROM ${BASE_IMAGE} AS build

ARG PGSPARQL_REPOSITORY=https://github.com/idsm-src/pgsparql.git
ARG PGSPARQL_COMMIT=next

RUN apt-get update && apt-get install --yes --no-install-recommends ca-certificates git build-essential autoconf automake libtool pkgconf libpcre2-dev "postgresql-server-dev-${PG_MAJOR}"
RUN git init /pgsparql && git -C /pgsparql fetch --depth 1 "${PGSPARQL_REPOSITORY}" "${PGSPARQL_COMMIT}" && git -C /pgsparql checkout FETCH_HEAD
RUN cd /pgsparql && autoreconf -fi && ./configure && make && make install DESTDIR=/install


FROM ${BASE_IMAGE}

COPY --from=build /install/ /
COPY pg_hba.conf /etc/postgresql/pg_hba.conf
