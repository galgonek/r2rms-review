# Prints a benchmark that the Sparqloscope generator wrote in YAML as a query set of the tests: every query labelled
# SCQ_<number> as the nextProt queries are labelled NXQ_<number>, its description and its name in Sparqloscope as
# comments, the IRIs abbreviated by the prefixes that cz.iocb.nextprot.test.Test declares
#
# usage: queryset.py <query templates> <benchmark>

import re
import sys
import yaml


# the prefixes of the tests, to which the generator cannot abbreviate since it does not accept the empty prefix
PREFIXES = {
    "rdf": "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
    "rdfs": "http://www.w3.org/2000/01/rdf-schema#",
    "xsd": "http://www.w3.org/2001/XMLSchema#",
    "": "http://nextprot.org/rdf#",
    "cv": "http://nextprot.org/rdf/terminology/",
    "db": "http://nextprot.org/rdf/db/",
    "entry": "http://nextprot.org/rdf/entry/",
    "source": "http://nextprot.org/rdf/source/",
    "chebi": "http://purl.obolibrary.org/obo/",
    "skos": "http://www.w3.org/2004/02/skos/core#",
}

# an IRI of one of the namespaces whose local part is a local name without escapes, starting with a letter as SPARQL
# 1.0 requires
IRI = re.compile(r"<(" + "|".join(map(re.escape, PREFIXES.values())) +
                 r")([A-Za-z_](?:[A-Za-z0-9_.-]*[A-Za-z0-9_-])?)>")

NAMES = {namespace: prefix for prefix, namespace in PREFIXES.items()}

LABEL = "SCQ"


def abbreviate(match: re.Match) -> str:
    return f"{NAMES[match.group(1)]}:{match.group(2)}"


with open(sys.argv[1]) as file:
    templates = yaml.safe_load(file)

with open(sys.argv[2]) as file:
    benchmark = yaml.safe_load(file)

# a query is numbered by the position of its template, so it keeps its label when the generator skips another one
NUMBERS = {template["name"]: number for number, template in enumerate(templates["queries"], 1)}

for i, query in enumerate(benchmark["queries"]):
    if i > 0:
        print("\n")

    print(f"### {LABEL}_{NUMBERS[query['name']]:05d} ###")

    for line in (query.get("description") or "").splitlines():
        print(f"# {line}".rstrip())

    print(f"# Sparqloscope query {query['name']}")

    # the tests skip empty lines, the generator leaves them in the expanded repetitions
    for line in query["query"].splitlines():
        line = IRI.sub(abbreviate, line).rstrip()

        if line.strip():
            print(line)
