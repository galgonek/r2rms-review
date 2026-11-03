# The Sparqloscope benchmark generator on the QLever image, built by bin/build-images.sh: the entry point is a Python
# with the packages the generator needs, the generator lies in /sparqloscope

ARG BASE_IMAGE=docker.io/adfreiburg/qlever:commit-16a682506e


FROM ${BASE_IMAGE} AS fetch

ARG SPARQLOSCOPE_REPOSITORY=https://github.com/ad-freiburg/sparqloscope.git
ARG SPARQLOSCOPE_COMMIT=main

USER root
RUN apt-get update && apt-get install --yes --no-install-recommends ca-certificates git
RUN git init /sparqloscope && git -C /sparqloscope fetch --depth 1 "${SPARQLOSCOPE_REPOSITORY}" "${SPARQLOSCOPE_COMMIT}" && git -C /sparqloscope checkout FETCH_HEAD

# the offsets of the percentiles must be integers, QLever returns the floor of a decimal as a decimal
COPY query-templates.patch /
RUN git -C /sparqloscope apply /query-templates.patch


FROM ${BASE_IMAGE}

USER root
RUN python3 -m venv /sparqloscope/venv && /sparqloscope/venv/bin/pip install --no-cache-dir argcomplete termcolor pyyaml
COPY --from=fetch /sparqloscope/generate-benchmark.py /sparqloscope/query-templates.yaml /sparqloscope/
USER qlever

ENTRYPOINT ["/sparqloscope/venv/bin/python"]
