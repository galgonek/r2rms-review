package cz.iocb.nextprot.load;

import static cz.iocb.nextprot.load.NeXtProt.nextprot;
import static cz.iocb.nextprot.load.NeXtProt.owl;
import static cz.iocb.nextprot.load.NeXtProt.rdf;
import static cz.iocb.nextprot.load.NeXtProt.rdfs;
import static cz.iocb.nextprot.load.Nodes.getID;
import static cz.iocb.nextprot.load.Nodes.getLiteralValue;
import static cz.iocb.nextprot.load.Nodes.getStringID;
import static cz.iocb.nextprot.load.Terminology.terms;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import org.apache.jena.graph.Node;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;
import cz.iocb.nextprot.load.NeXtProt.Mode;
import cz.iocb.nextprot.load.NeXtProt.Server;



public class Schema
{
    static ObjectIntHashMap<String> sources = new ObjectIntHashMap<>();
    static ObjectIntHashMap<String> databases = new ObjectIntHashMap<>();
    static ObjectIntHashMap<String> entities = new ObjectIntHashMap<>();


    static void load(String path, Connection connection, Server type, Mode mode) throws IOException, SQLException
    {
        TripleDispatcher dispatcher = new TripleDispatcher();

        LinkedHashSet<Node> sourceNodes = dispatcher.instances(nextprot + "Source");
        LinkedHashSet<Node> databaseNodes = dispatcher.instances(nextprot + "Database");
        LinkedHashSet<Node> entityNodes = new LinkedHashSet<>();

        Values types = dispatcher.values(rdf + "type");
        Values labels = dispatcher.values(rdfs + "label");
        Values comments = dispatcher.values(rdfs + "comment");
        Values references = dispatcher.values(rdfs + "seeAlso");
        Values categories = dispatcher.values(nextprot + "category");

        dispatcher.onEvery((subject, _, object) -> {
            if(subject.isURI() && subject.getURI().startsWith(nextprot))
                entityNodes.add(subject);

            if(object.isURI() && object.getURI().startsWith(nextprot))
                entityNodes.add(object);
        });

        Table classes = dispatcher.table(connection, type, "schema_classes(entity)");
        Table thingSubclasses = dispatcher.table(connection, type, "schema_thing_subclasses(entity)");
        Table restrictions = dispatcher.table(connection, type, "schema_restrictions(entity, notin)");
        Table relatedTerms = dispatcher.table(connection, type, "schema_related_terms(entity, related)");
        Table parentClasses = dispatcher.table(connection, type, "schema_parent_classes(entity, parent)");

        dispatcher.onType(owl + "Class", (entity, _) -> {
            if(entity.isURI() && entity.getURI().startsWith(nextprot))
            {
                classes.setValue(1, getID(entity, nextprot, entities, mode));
                classes.add();
            }
        });

        dispatcher.on(rdfs + "subClassOf", (entity, parent) -> {
            if(!entity.isURI() || !entity.getURI().startsWith(nextprot))
                return;

            if(parent.isURI() && parent.getURI().equals(owl + "Thing"))
            {
                thingSubclasses.setValue(1, getID(entity, nextprot, entities, mode));
                thingSubclasses.add();
            }

            if(parent.isURI() && parent.getURI().startsWith(nextprot))
            {
                parentClasses.setValue(1, getID(entity, nextprot, entities, mode));
                parentClasses.setValue(2, getID(parent, nextprot, entities, mode));
                parentClasses.add();
            }
        });

        dispatcher.on(nextprot + "notIn", (entity, notin) -> {
            restrictions.setValue(1, getID(entity, nextprot, entities, mode));
            restrictions.setValue(2, getID(notin, nextprot, entities, mode));
            restrictions.add();
        });

        dispatcher.on(nextprot + "related", (entity, related) -> {
            relatedTerms.setValue(1, getID(entity, nextprot, entities, mode));
            relatedTerms.setValue(2, getID(related, "http://nextprot.org/rdf/terminology/", terms, mode));
            relatedTerms.add();
        });

        dispatcher.after(() -> {
            try(Table table = new Table(connection, type,
                    "source_bases(id, comment, reference" + (mode == Mode.INDIRECT ? ", iri" : "") + ")"))
            {
                for(Node source : sourceNodes)
                {
                    table.setValue(1, getID(source, "http://nextprot.org/rdf/source/", sources, mode));
                    table.setValue(2, getLiteralValue(comments.first(source)));
                    table.setValue(3, getLiteralValue(references.first(source)));

                    if(mode == Mode.INDIRECT)
                        table.setValue(4, getStringID(source, "http://nextprot.org/rdf/source/"));

                    table.add();
                }
            }

            try(Table table = new Table(connection, type,
                    "database_bases(id, reference" + (mode == Mode.INDIRECT ? ", iri" : "") + ")"))
            {
                for(Node database : databaseNodes)
                {
                    table.setValue(1, getID(database, "http://nextprot.org/rdf/db/", databases, mode));
                    table.setValue(2, getLiteralValue(references.first(database)));

                    if(mode == Mode.INDIRECT)
                        table.setValue(3, getStringID(database, "http://nextprot.org/rdf/db/"));

                    table.add();
                }
            }

            try(Table table = new Table(connection, type, "database_comments(db, comment)"))
            {
                for(Node database : databaseNodes)
                {
                    for(Node comment : comments.get(database))
                    {
                        table.setValue(1, getID(database, "http://nextprot.org/rdf/db/", databases, mode));
                        table.setValue(2, getLiteralValue(comment));
                        table.add();
                    }
                }
            }

            try(Table table = new Table(connection, type, "database_categories(db, category)"))
            {
                for(Node database : databaseNodes)
                {
                    for(Node category : categories.get(database))
                    {
                        table.setValue(1, getID(database, "http://nextprot.org/rdf/db/", databases, mode));
                        table.setValue(2, getLiteralValue(category));
                        table.add();
                    }
                }
            }

            try(Table table = new Table(connection, type,
                    "schema_bases(id, type, label, comment, reference" + (mode == Mode.INDIRECT ? ", iri" : "") + ")"))
            {
                for(Node entity : entityNodes)
                {
                    Node entityType = types.first(entity, t -> t.isURI() && t.getURI().startsWith(nextprot));

                    table.setValue(1, getID(entity, nextprot, entities, mode));
                    table.setValue(2, getID(entityType, nextprot, entities, mode));
                    table.setValue(3, getLiteralValue(labels.first(entity)));
                    table.setValue(4, getLiteralValue(comments.first(entity)));
                    table.setValue(5, getLiteralValue(references.first(entity)));

                    if(mode == Mode.INDIRECT)
                        table.setValue(6, getStringID(entity, nextprot));

                    table.add();
                }
            }
        });

        dispatcher.load(path, "schema.ttl.gz", "inferredSubClassOf-a.ttl.gz", "inferredSubClassOf-b.ttl.gz");
    }
}
