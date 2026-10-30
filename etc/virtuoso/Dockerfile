# Virtuoso runnable as any user, built by bin/build-images.sh: the base image closes its home to other users and its
# entry point creates a database with a generated password

ARG BASE_IMAGE=docker.io/openlink/virtuoso-opensource-7:7.2.17

FROM ${BASE_IMAGE}

RUN chmod o+x /opt/virtuoso-opensource

WORKDIR /database
ENTRYPOINT ["virtuoso-t", "+foreground"]

# the server shuts down normally, with a checkpoint
STOPSIGNAL SIGINT
