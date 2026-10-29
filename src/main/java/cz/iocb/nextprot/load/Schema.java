package cz.iocb.nextprot.load;

import static cz.iocb.nextprot.load.NeXtProt.nextprot;
import static cz.iocb.nextprot.load.NeXtProt.owl;
import static cz.iocb.nextprot.load.NeXtProt.rdf;
import static cz.iocb.nextprot.load.NeXtProt.rdfs;
import static cz.iocb.nextprot.load.Nodes.getLiteralValue;
import static cz.iocb.nextprot.load.Terminology.terms;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import org.apache.jena.graph.Node;
import cz.iocb.nextprot.load.NeXtProt.Mode;



public class Schema
{
    static Ids sources = new Ids("http://nextprot.org/rdf/source/");
    static Ids databases = new Ids("http://nextprot.org/rdf/db/");
    static Ids entities = new Ids(nextprot);


    static void load(String path, Target target, Mode mode) throws IOException, SQLException
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

        Table classes = dispatcher.table(target, "schema_classes(entity)");
        Table thingSubclasses = dispatcher.table(target, "schema_thing_subclasses(entity)");
        Table restrictions = dispatcher.table(target, "schema_restrictions(entity, notin)");
        Table relatedTerms = dispatcher.table(target, "schema_related_terms(entity, related)");
        Table parentClasses = dispatcher.table(target, "schema_parent_classes(entity, parent)");

        dispatcher.onType(owl + "Class", (entity, _) -> {
            if(entity.isURI() && entity.getURI().startsWith(nextprot) && classes.isNew(entities.getId(entity)))
            {
                classes.setValue(1, entities.getValue(entity, mode));
                classes.add();
            }
        });

        dispatcher.on(rdfs + "subClassOf", (entity, parent) -> {
            if(!entity.isURI() || !entity.getURI().startsWith(nextprot))
                return;

            if(parent.isURI() && parent.getURI().equals(owl + "Thing") && thingSubclasses.isNew(entities.getId(entity)))
            {
                thingSubclasses.setValue(1, entities.getValue(entity, mode));
                thingSubclasses.add();
            }

            if(parent.isURI() && parent.getURI().startsWith(nextprot)
                    && parentClasses.isNew(entities.getId(entity), entities.getId(parent)))
            {
                parentClasses.setValue(1, entities.getValue(entity, mode));
                parentClasses.setValue(2, entities.getValue(parent, mode));
                parentClasses.add();
            }
        });

        dispatcher.on(nextprot + "notIn", (entity, notin) -> {
            if(!restrictions.isNew(entities.getId(entity), entities.getId(notin)))
                return;

            restrictions.setValue(1, entities.getValue(entity, mode));
            restrictions.setValue(2, entities.getValue(notin, mode));
            restrictions.add();
        });

        dispatcher.on(nextprot + "related", (entity, related) -> {
            if(!relatedTerms.isNew(entities.getId(entity), terms.getId(related)))
                return;

            relatedTerms.setValue(1, entities.getValue(entity, mode));
            relatedTerms.setValue(2, terms.getValue(related, mode));
            relatedTerms.add();
        });

        dispatcher.after(() -> {
            try(Table table = target.table("source_bases(id, comment, reference"
                    + (mode == Mode.INDIRECT ? ", iri" : "") + ")"))
            {
                for(Node source : sourceNodes)
                {
                    if(!table.isNew(sources.getId(source)))
                        continue;

                    table.setValue(1, sources.getValue(source, mode));
                    table.setValue(2, getLiteralValue(comments.first(source)));
                    table.setValue(3, getLiteralValue(references.first(source)));

                    if(mode == Mode.INDIRECT)
                        table.setValue(4, sources.getLocalPart(source));

                    table.add();
                }
            }

            try(Table table = target.table("database_bases(id, reference" + (mode == Mode.INDIRECT ? ", iri" : "")
                    + ")"))
            {
                for(Node database : databaseNodes)
                {
                    if(!table.isNew(databases.getId(database)))
                        continue;

                    table.setValue(1, databases.getValue(database, mode));
                    table.setValue(2, getLiteralValue(references.first(database)));

                    if(mode == Mode.INDIRECT)
                        table.setValue(3, databases.getLocalPart(database));

                    table.add();
                }
            }

            try(Table table = target.table("database_comments(db, comment)"))
            {
                for(Node database : databaseNodes)
                {
                    for(Node comment : comments.get(database))
                    {
                        if(!table.isNew(databases.getId(database), getLiteralValue(comment)))
                            continue;

                        table.setValue(1, databases.getValue(database, mode));
                        table.setValue(2, getLiteralValue(comment));
                        table.add();
                    }
                }
            }

            try(Table table = target.table("database_categories(db, category)"))
            {
                for(Node database : databaseNodes)
                {
                    for(Node category : categories.get(database))
                    {
                        if(!table.isNew(databases.getId(database), getLiteralValue(category)))
                            continue;

                        table.setValue(1, databases.getValue(database, mode));
                        table.setValue(2, getLiteralValue(category));
                        table.add();
                    }
                }
            }

            try(Table table = target.table("schema_bases(id, type, label, comment, reference"
                    + (mode == Mode.INDIRECT ? ", iri" : "") + ")"))
            {
                for(Node entity : entityNodes)
                {
                    if(!table.isNew(entities.getId(entity)))
                        continue;

                    Node entityType = types.first(entity, t -> t.isURI() && t.getURI().startsWith(nextprot));

                    table.setValue(1, entities.getValue(entity, mode));
                    table.setValue(2, entities.getValue(entityType, mode));
                    table.setValue(3, getLiteralValue(labels.first(entity)));
                    table.setValue(4, getLiteralValue(comments.first(entity)));
                    table.setValue(5, getLiteralValue(references.first(entity)));

                    if(mode == Mode.INDIRECT)
                        table.setValue(6, entities.getLocalPart(entity));

                    table.add();
                }
            }
        });

        dispatcher.load(path, "schema.ttl.gz", "inferredSubClassOf-a.ttl.gz", "inferredSubClassOf-b.ttl.gz");
    }
}
