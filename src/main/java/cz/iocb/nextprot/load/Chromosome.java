package cz.iocb.nextprot.load;

import static cz.iocb.nextprot.load.NeXtProt.db;
import static cz.iocb.nextprot.load.NeXtProt.nextprot;
import static cz.iocb.nextprot.load.NeXtProt.rdf;
import static cz.iocb.nextprot.load.NeXtProt.rdfs;
import static cz.iocb.nextprot.load.NeXtProt.skos;
import static cz.iocb.nextprot.load.Nodes.getBooleanValue;
import static cz.iocb.nextprot.load.Nodes.getDoubleValue;
import static cz.iocb.nextprot.load.Nodes.getID;
import static cz.iocb.nextprot.load.Nodes.getIntID;
import static cz.iocb.nextprot.load.Nodes.getIntValue;
import static cz.iocb.nextprot.load.Nodes.getLiteralValue;
import static cz.iocb.nextprot.load.Nodes.getMappedID;
import static cz.iocb.nextprot.load.Nodes.getStringID;
import static cz.iocb.nextprot.load.Schema.databases;
import static cz.iocb.nextprot.load.Schema.entities;
import static cz.iocb.nextprot.load.Schema.sources;
import static cz.iocb.nextprot.load.Terminology.terms;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;
import cz.iocb.nextprot.load.NeXtProt.Mode;
import cz.iocb.nextprot.load.NeXtProt.Server;



public class Chromosome
{
    /*
     * Sets the columns of a cross-reference that differ between the databases: the id, the annotation and the
     * identifier of the referenced entity.
     */
    @FunctionalInterface
    private static interface XrefColumns
    {
        void set(Table table, Node annotation, Node exactMatch) throws IOException, SQLException;
    }


    private static int sequenceId = 0;
    private static int familyId = 0;
    private static int historyId = 0;
    private static int identifierId = 0;
    private static int entryXrefId = 0;
    private static int evidenceXrefId = 0;
    private static int chebiXrefId = 0;
    private static int drugBankXrefId = 0;
    private static int uniProtXrefId = 0;
    private static int nameId = 0;
    private static int nameListId = 0;

    private static HashSet<String> comments = new HashSet<>();

    private static ObjectIntHashMap<Node> names = new ObjectIntHashMap<>();
    private static ObjectIntHashMap<Node> nameLists = new ObjectIntHashMap<>();

    static ObjectIntHashMap<String> annotations = new ObjectIntHashMap<>();
    static ObjectIntHashMap<String> evidences = new ObjectIntHashMap<>();
    static ObjectIntHashMap<String> entries = new ObjectIntHashMap<>();
    static ObjectIntHashMap<String> isoforms = new ObjectIntHashMap<>();
    static ObjectIntHashMap<String> proteoforms = new ObjectIntHashMap<>();
    static ObjectIntHashMap<String> genes = new ObjectIntHashMap<>();

    private static final Node evidenceType = NodeFactory.createURI(nextprot + "Evidence");
    private static final Node proteinSequenceType = NodeFactory.createURI(nextprot + "ProteinSequence");
    private static final Node familyInfoType = NodeFactory.createURI(nextprot + "FamilyInfo");
    private static final Node historyType = NodeFactory.createURI(nextprot + "History");
    private static final Node identifierType = NodeFactory.createURI(nextprot + "Identifier");
    private static final Node xrefType = NodeFactory.createURI(nextprot + "Xref");

    // @formatter:off
    private static final String[][] isoformAnnotations = {
            { "activeSite", "isoform_active_sites" },
            { "activityRegulation", "isoform_activity_regulations" },
            { "antibodyMapping", "isoform_antibody_mappings" },
            { "betaStrand", "isoform_beta_strands" },
            { "binaryInteraction", "isoform_binary_interactions" },
            { "bindingSite", "isoform_binding_sites" },
            { "biophysicochemicalProperty", "isoform_biophysicochemical_properties" },
            { "calciumBindingRegion", "isoform_calcium_binding_regions" },
            { "catalyticActivity", "isoform_catalytic_activities" },
            { "caution", "isoform_cautions" },
            { "cellularComponent", "isoform_cellular_components" },
            { "cleavageSite", "isoform_cleavage_sites" },
            { "cofactor", "isoform_cofactors" },
            { "cofactorInfo", "isoform_cofactor_infos" },
            { "coiledCoilRegion", "isoform_coiled_coil_regions" },
            { "compositionallyBiasedRegion", "isoform_compositionally_biased_regions" },
            { "crossLink", "isoform_cross_links" },
            { "detectedExpression", "isoform_detected_expressions" },
            { "developmentalStageInfo", "isoform_developmental_stage_infos" },
            { "disease", "isoform_diseases" },
            { "disulfideBond", "isoform_disulfide_bonds" },
            { "dnaBindingRegion", "isoform_dna_binding_regions" },
            { "domain", "isoform_domains" },
            { "domainInfo", "isoform_domain_infos" },
            { "electrophysiologicalParameter", "isoform_electrophysiological_parameters" },
            { "enzymeClassification", "isoform_enzyme_classifications" },
            { "expression", "isoform_expressions" },
            { "expressionInfo", "isoform_expression_infos" },
            { "expressionProfile", "isoform_expression_profiles" },
            { "function", "isoform_functions" },
            { "functionInfo", "isoform_function_infos" },
            { "generalAnnotation", "isoform_general_annotations" },
            { "glycosylationSite", "isoform_glycosylation_sites" },
            { "goBiologicalProcess", "isoform_go_biological_processs" },
            { "goCellularComponent", "isoform_go_cellular_components" },
            { "goMolecularFunction", "isoform_go_molecular_functions" },
            { "helix", "isoform_helixs" },
            { "induction", "isoform_inductions" },
            { "interactingRegion", "isoform_interacting_regions" },
            { "interaction", "isoform_interactions" },
            { "interactionInfo", "isoform_interaction_infos" },
            { "intramembraneRegion", "isoform_intramembrane_regions" },
            { "keyword", "isoform_keywords" },
            { "lipidationSite", "isoform_lipidation_sites" },
            { "mapping", "isoform_mappings" },
            { "matureProtein", "isoform_mature_proteins" },
            { "medical", "isoform_medicals" },
            { "metalBindingSite", "isoform_metal_binding_sites" },
            { "miscellaneous", "isoform_miscellaneouss" },
            { "miscellaneousRegion", "isoform_miscellaneous_regions" },
            { "miscellaneousSite", "isoform_miscellaneous_sites" },
            { "mitochondrialTransitPeptide", "isoform_mitochondrial_transit_peptides" },
            { "modifiedResidue", "isoform_modified_residues" },
            { "mutagenesis", "isoform_mutagenesiss" },
            { "nonTerminalResidue", "isoform_non_terminal_residues" },
            { "nucleotidePhosphateBindingRegion", "isoform_nucleotide_phosphate_binding_regions" },
            { "pathway", "isoform_pathways" },
            { "pdbMapping", "isoform_pdb_mappings" },
            { "peptideMapping", "isoform_peptide_mappings" },
            { "positionalAnnotation", "isoform_positional_annotations" },
            { "processingProduct", "isoform_processing_products" },
            { "propeptide", "isoform_propeptides" },
            { "ptm", "isoform_ptms" },
            { "ptmInfo", "isoform_ptm_infos" },
            { "region", "isoform_regions" },
            { "repeat", "isoform_repeats" },
            { "secondaryStructure", "isoform_secondary_structures" },
            { "selenocysteine", "isoform_selenocysteines" },
            { "sequenceCaution", "isoform_sequence_cautions" },
            { "sequenceConflict", "isoform_sequence_conflicts" },
            { "shortSequenceMotif", "isoform_short_sequence_motifs" },
            { "site", "isoform_sites" },
            { "smallMoleculeInteraction", "isoform_small_molecule_interactions" },
            { "srmPeptideMapping", "isoform_srm_peptide_mappings" },
            { "subcellularLocation", "isoform_subcellular_locations" },
            { "subcellularLocationNote", "isoform_subcellular_location_notes" },
            { "topologicalDomain", "isoform_topological_domains" },
            { "topology", "isoform_topologies" },
            { "transmembraneRegion", "isoform_transmembrane_regions" },
            { "transportActivity", "isoform_transport_activities" },
            { "turn", "isoform_turns" },
            { "undetectedExpression", "isoform_undetected_expressions" },
            { "uniprotKeyword", "isoform_uniprot_keywords" },
            { "variant", "isoform_variants" },
            { "variantInfo", "isoform_variant_infos" },
            { "zincFingerRegion", "isoform_zinc_finger_regions" },
            { "interactionMapping", "isoform_interaction_mappings" },
    };

    private static final String[][] proteoformAnnotations = {
            { "generalAnnotation", "proteoform_general_annotations" },
            { "genericPhenotype", "proteoform_generic_phenotypes" },
            { "modification", "proteoform_modifications" },
            { "phenotypicVariation", "proteoform_phenotypic_variations" },
            { "positionalAnnotation", "proteoform_positional_annotations" },
            { "diseaseRelatedVariant", "proteoform_disease_related_variants" },
    };
    // @formatter:on


    private static void loadEvidences(TripleDispatcher dispatcher, Connection connection, Server type, Mode mode)
    {
        LinkedHashSet<Node> evidenceNodes = dispatcher.instances(nextprot + "Evidence");
        Values expressionScores = dispatcher.values(nextprot + "expressionScore");
        Values alleleFrequencies = dispatcher.values(nextprot + "allele-frequency");
        Values experimentalContexts = dispatcher.values(nextprot + "experimentalContext");
        Values evidenceCodes = dispatcher.values(nextprot + "evidenceCode");
        Values assignedBy = dispatcher.values(nextprot + "assignedBy");
        Values expressionLevels = dispatcher.values(nextprot + "expressionLevel");
        Values fromXrefs = dispatcher.values(nextprot + "fromXref");
        Values integrationLevels = dispatcher.values(nextprot + "integrationLevel");
        Values qualities = dispatcher.values(nextprot + "quality");
        Values detectionMethods = dispatcher.values(nextprot + "interactionDetectionMethod");
        Values negatives = dispatcher.values(nextprot + "negative");
        Values numbersOfExperiments = dispatcher.values(nextprot + "numberOfExperiments");
        Values homozygoteCounts = dispatcher.values(nextprot + "homozygote-count");
        Values alleleNumbers = dispatcher.values(nextprot + "allele-number");
        Values alleleCounts = dispatcher.values(nextprot + "allele-count");
        Values isoformSpecificities = dispatcher.values(nextprot + "isoformSpecificity");
        Values antibodiesAccs = dispatcher.values(nextprot + "antibodiesAcc");
        Values assocTypes = dispatcher.values(nextprot + "assocType");
        Values cellLines = dispatcher.values(nextprot + "cellLine");
        Values goQualifiers = dispatcher.values(nextprot + "goQualifier");
        Values intensities = dispatcher.values(nextprot + "intensity");
        Values negativeIsoformSpecificities = dispatcher.values(nextprot + "negativeIsoformSpecificity");
        Values references = dispatcher.values(nextprot + "reference");

        dispatcher.after(() -> {
            try(Table table = new Table(connection, type, "evidence_bases(id, expression_score, allele_frequency, "
                    + "experimental_context, evidence_code, assigned_by, expression_level, from_xref, "
                    + "integration_level, quality, interaction_detection_method, negative, number_of_experiments, "
                    + "homozygote_count, allele_number, allele_count, isoform_specificity, antibodies_acc, "
                    + "assoc_type, cell_line, go_qualifier, intensity, negative_isoform_specificity"
                    + (mode == Mode.INDIRECT ? ", iri" : "") + ")"))
            {
                for(Node evidence : evidenceNodes)
                {
                    table.setValue(1, getID(evidence, "http://nextprot.org/rdf/evidence/", evidences, mode));
                    table.setValue(2, getDoubleValue(expressionScores.first(evidence)));
                    table.setValue(3, getDoubleValue(alleleFrequencies.first(evidence)));
                    table.setValue(4,
                            getIntID(experimentalContexts.first(evidence), "http://nextprot.org/rdf/context/"));
                    table.setValue(5,
                            getID(evidenceCodes.first(evidence), "http://nextprot.org/rdf/terminology/", terms, mode));
                    table.setValue(6,
                            getID(assignedBy.first(evidence), "http://nextprot.org/rdf/source/", sources, mode));
                    table.setValue(7, getID(expressionLevels.first(evidence), nextprot, entities, mode));
                    table.setValue(8, getID(fromXrefs.first(evidence), "http://nextprot.org/rdf/db/", databases, mode));
                    table.setValue(9, getID(integrationLevels.first(evidence), nextprot, entities, mode));
                    table.setValue(10, getID(qualities.first(evidence), nextprot, entities, mode));
                    table.setValue(11, getID(detectionMethods.first(evidence), "http://nextprot.org/rdf/terminology/",
                            terms, mode));
                    table.setValue(12, getBooleanValue(negatives.first(evidence)));
                    table.setValue(13, getIntValue(numbersOfExperiments.first(evidence)));
                    table.setValue(14, getIntValue(homozygoteCounts.first(evidence)));
                    table.setValue(15, getIntValue(alleleNumbers.first(evidence)));
                    table.setValue(16, getIntValue(alleleCounts.first(evidence)));
                    table.setValue(17, getLiteralValue(isoformSpecificities.first(evidence)));
                    table.setValue(18, getLiteralValue(antibodiesAccs.first(evidence)));
                    table.setValue(19, getLiteralValue(assocTypes.first(evidence)));
                    table.setValue(20, getLiteralValue(cellLines.first(evidence)));
                    table.setValue(21, getLiteralValue(goQualifiers.first(evidence)));
                    table.setValue(22, getLiteralValue(intensities.first(evidence)));
                    table.setValue(23, getLiteralValue(negativeIsoformSpecificities.first(evidence)));

                    if(mode == Mode.INDIRECT)
                        table.setValue(24, getStringID(evidence, "http://nextprot.org/rdf/evidence/"));

                    table.add();
                }
            }

            try(Table table = new Table(connection, type, "evidence_publication_references(evidence, publication)"))
            {
                for(Node evidence : evidenceNodes)
                {
                    for(Node reference : references.get(evidence))
                    {
                        if(!reference.isURI())
                            continue;

                        table.setValue(1, getID(evidence, "http://nextprot.org/rdf/evidence/", evidences, mode));
                        table.setValue(2, getIntID(reference, "http://nextprot.org/rdf/publication/"));
                        table.add();
                    }
                }
            }
        });
    }


    private static void loadAnnotations(TripleDispatcher dispatcher, Connection connection, Server type, Mode mode)
            throws SQLException
    {
        Values types = dispatcher.values(rdf + "type");
        Values qualities = dispatcher.values(nextprot + "quality");
        Values impactingAnnotations = dispatcher.inverse(nextprot + "impactedObject");
        Values resolutions = dispatcher.values(nextprot + "resolution");
        Values annotationTerms = dispatcher.values(nextprot + "term");
        Values impactedObjects = dispatcher.values(nextprot + "impactedObject");
        Values selfInteractions = dispatcher.values(nextprot + "selfInteraction");
        Values proteotypics = dispatcher.values(nextprot + "proteotypic");
        Values starts = dispatcher.values(nextprot + "start");
        Values ends = dispatcher.values(nextprot + "end");
        Values entryAnnotationIds = dispatcher.values(nextprot + "entryAnnotationId");
        Values variations = dispatcher.values(nextprot + "variation");
        Values originals = dispatcher.values(nextprot + "original");
        Values hgvses = dispatcher.values(nextprot + "hgvs");
        Values methods = dispatcher.values(nextprot + "method");
        Values peptideNames = dispatcher.values(nextprot + "peptideName");
        Values peptideUnicities = dispatcher.values(nextprot + "peptideUnicity");
        Values antibodyNames = dispatcher.values(nextprot + "antibodyName");
        Values antibodyUnicities = dispatcher.values(nextprot + "antibodyUnicity");

        Table annotationEvidences = dispatcher.table(connection, type, "annotation_evidences(annotation, evidence)");
        Table negativeEvidences = dispatcher.table(connection, type,
                "annotation_negative_evidences(annotation, evidence)");
        Table diseases = dispatcher.table(connection, type, "annotation_diseases(annotation, disease)");
        Table annotationComments = dispatcher.table(connection, type, "annotation_comments(annotation, seq, comment)");
        Table specificities = dispatcher.table(connection, type,
                "annotation_isoform_specificities(annotation, specificity)");
        Table entryInteractants = dispatcher.table(connection, type,
                "annotation_entry_interactants(annotation, interactant)");
        Table isoformInteractants = dispatcher.table(connection, type,
                "annotation_isoform_interactants(annotation, interactant)");
        Table peptideSets = dispatcher.table(connection, type, "annotation_peptide_sets(annotation, peptide_set)");

        dispatcher.on(nextprot + "evidence", (annotation, evidence) -> {
            annotationEvidences.setValue(1,
                    getID(annotation, "http://nextprot.org/rdf/annotation/", annotations, mode));
            annotationEvidences.setValue(2, getID(evidence, "http://nextprot.org/rdf/evidence/", evidences, mode));
            annotationEvidences.add();
        });

        dispatcher.on(nextprot + "negativeEvidence", (annotation, evidence) -> {
            negativeEvidences.setValue(1, getID(annotation, "http://nextprot.org/rdf/annotation/", annotations, mode));
            negativeEvidences.setValue(2, getID(evidence, "http://nextprot.org/rdf/evidence/", evidences, mode));
            negativeEvidences.add();
        });

        dispatcher.on(nextprot + "disease", (annotation, disease) -> {
            if(!annotation.isURI() || !annotation.getURI().startsWith("http://nextprot.org/rdf/annotation/"))
                return;

            diseases.setValue(1, getID(annotation, "http://nextprot.org/rdf/annotation/", annotations, mode));
            diseases.setValue(2, getID(disease, "http://nextprot.org/rdf/terminology/", terms, mode));
            diseases.add();
        });

        dispatcher.on(rdfs + "comment", (annotation, comment) -> {
            Object id = getID(annotation, "http://nextprot.org/rdf/annotation/", annotations, mode);
            String key = id + "\0" + getLiteralValue(comment);

            if(!comments.contains(key))
            {
                annotationComments.setValue(1, id);
                annotationComments.setValue(2, comments.size());
                annotationComments.setValue(3, getLiteralValue(comment));
                annotationComments.add();
                comments.add(key);
            }
        });

        dispatcher.on(nextprot + "isoformSpecificity", (annotation, specificity) -> {
            if(!annotation.isURI() || !annotation.getURI().startsWith("http://nextprot.org/rdf/annotation/"))
                return;

            specificities.setValue(1, getID(annotation, "http://nextprot.org/rdf/annotation/", annotations, mode));
            specificities.setValue(2, getID(specificity, nextprot, entities, mode));
            specificities.add();
        });

        dispatcher.on(nextprot + "interactant", (annotation, interactant) -> {
            if(interactant.isURI() && interactant.getURI().startsWith("http://nextprot.org/rdf/entry/"))
            {
                entryInteractants.setValue(1,
                        getID(annotation, "http://nextprot.org/rdf/annotation/", annotations, mode));
                entryInteractants.setValue(2, getID(interactant, "http://nextprot.org/rdf/entry/", entries, mode));
                entryInteractants.add();
            }

            if(interactant.isURI() && interactant.getURI().startsWith("http://nextprot.org/rdf/isoform/"))
            {
                isoformInteractants.setValue(1,
                        getID(annotation, "http://nextprot.org/rdf/annotation/", annotations, mode));
                isoformInteractants.setValue(2, getID(interactant, "http://nextprot.org/rdf/isoform/", isoforms, mode));
                isoformInteractants.add();
            }
        });

        dispatcher.on(nextprot + "peptideSet", (annotation, set) -> {
            peptideSets.setValue(1, getID(annotation, "http://nextprot.org/rdf/annotation/", annotations, mode));
            peptideSets.setValue(2, getLiteralValue(set));
            peptideSets.add();
        });

        dispatcher.after(() -> {
            try(Table table = new Table(connection, type,
                    "annotation_bases(id, resolution, type, quality, term, "
                            + "impacted_object, self_interaction, proteotypic, position_start, position_end, "
                            + "entry_annotation, variation, original, hgvs, method, peptide_name, peptide_unicity, "
                            + "antibody_name, antibody_unicity" + (mode == Mode.INDIRECT ? ", iri" : "") + ")"))
            {
                /* the typed annotations with a quality, and the untyped impacted objects */
                LinkedHashSet<Node> annotationNodes = new LinkedHashSet<Node>();

                for(Node annotation : qualities.nodes())
                    if(types.get(annotation).stream().anyMatch(t -> !t.equals(evidenceType)))
                        annotationNodes.add(annotation);

                for(Node annotation : impactingAnnotations.nodes())
                    if(types.get(annotation).isEmpty())
                        annotationNodes.add(annotation);

                for(Node annotation : annotationNodes)
                {
                    Node annotationType = types.first(annotation, t -> !t.equals(evidenceType));
                    Node quality = annotationType != null ? qualities.first(annotation) : null;

                    table.setValue(1, getID(annotation, "http://nextprot.org/rdf/annotation/", annotations, mode));
                    table.setValue(2, getDoubleValue(resolutions.first(annotation)));
                    table.setValue(3, getID(annotationType, nextprot, entities, mode));
                    table.setValue(4, getID(quality, nextprot, entities, mode));
                    table.setValue(5, getID(annotationTerms.first(annotation), "http://nextprot.org/rdf/terminology/",
                            terms, mode));
                    table.setValue(6, getID(impactedObjects.first(annotation), "http://nextprot.org/rdf/annotation/",
                            annotations, mode));
                    table.setValue(7, getBooleanValue(selfInteractions.first(annotation)));
                    table.setValue(8, getBooleanValue(proteotypics.first(annotation)));
                    table.setValue(9, getIntValue(starts.first(annotation)));
                    table.setValue(10, getIntValue(ends.first(annotation)));
                    table.setValue(11, getLiteralValue(entryAnnotationIds.first(annotation)));
                    table.setValue(12, getLiteralValue(variations.first(annotation)));
                    table.setValue(13, getLiteralValue(originals.first(annotation)));
                    table.setValue(14, getLiteralValue(hgvses.first(annotation)));
                    table.setValue(15, getLiteralValue(methods.first(annotation)));
                    table.setValue(16, getLiteralValue(peptideNames.first(annotation)));
                    table.setValue(17, getLiteralValue(peptideUnicities.first(annotation)));
                    table.setValue(18, getLiteralValue(antibodyNames.first(annotation)));
                    table.setValue(19, getLiteralValue(antibodyUnicities.first(annotation)));

                    if(mode == Mode.INDIRECT)
                        table.setValue(20, getStringID(annotation, "http://nextprot.org/rdf/annotation/"));

                    table.add();
                }
            }
        });
    }


    private static void loadIsoforms(TripleDispatcher dispatcher, Connection connection, Server type, Mode mode)
    {
        LinkedHashSet<Node> isoformNodes = dispatcher.instances(nextprot + "Isoform");
        Values isoformEntries = dispatcher.inverse(nextprot + "isoform");
        Values absorptionMaxes = dispatcher.values(nextprot + "absorptionMax");
        Values absorptionNotes = dispatcher.values(nextprot + "absorptionNote");
        Values allergens = dispatcher.values(nextprot + "allergen");
        Values initiatorMethionines = dispatcher.values(nextprot + "initiatorMethionine");
        Values kineticKMs = dispatcher.values(nextprot + "kineticKM");
        Values kineticNotes = dispatcher.values(nextprot + "kineticNote");
        Values kineticVmaxes = dispatcher.values(nextprot + "kineticVmax");
        Values nonConsecutiveResidues = dispatcher.values(nextprot + "nonConsecutiveResidue");
        Values peroxisomeTransitPeptides = dispatcher.values(nextprot + "peroxisomeTransitPeptide");
        Values pharmaceuticals = dispatcher.values(nextprot + "pharmaceutical");
        Values phDependences = dispatcher.values(nextprot + "phDependence");
        Values redoxPotentials = dispatcher.values(nextprot + "redoxPotential");
        Values signalPeptides = dispatcher.values(nextprot + "signalPeptide");
        Values temperatureDependences = dispatcher.values(nextprot + "temperatureDependence");
        Values canonicalIsoforms = dispatcher.values(nextprot + "canonicalIsoform");
        Values swissprotDisplayed = dispatcher.values(nextprot + "swissprotDisplayed");
        Values isoformProteoforms = dispatcher.values(nextprot + "proteoform");

        Values[] links = new Values[isoformAnnotations.length];

        for(int i = 0; i < isoformAnnotations.length; i++)
            links[i] = dispatcher.values(nextprot + isoformAnnotations[i][0]);

        dispatcher.after(() -> {
            try(Table table = new Table(connection, type, "isoform_bases (id, entry, absorption_max, "
                    + "absorption_note, allergen, initiator_methionine, kinetic_k_m, kinetic_note, kinetic_vmax, "
                    + "non_consecutive_residue, peroxisome_transit_peptide, pharmaceutical, ph_dependence,  "
                    + "redox_potential, signal_peptide, temperature_dependence, canonical_isoform, swissprot_displayed"
                    + (mode == Mode.INDIRECT ? ", iri" : "") + ")"))
            {
                for(Node isoform : isoformNodes)
                {
                    table.setValue(1, getID(isoform, "http://nextprot.org/rdf/isoform/", isoforms, mode));
                    table.setValue(2,
                            getID(isoformEntries.first(isoform), "http://nextprot.org/rdf/entry/", entries, mode));
                    table.setValue(3, getAnnotationID(absorptionMaxes.first(isoform), mode));
                    table.setValue(4, getAnnotationID(absorptionNotes.first(isoform), mode));
                    table.setValue(5, getAnnotationID(allergens.first(isoform), mode));
                    table.setValue(6, getAnnotationID(initiatorMethionines.first(isoform), mode));
                    table.setValue(7, getAnnotationID(kineticKMs.first(isoform), mode));
                    table.setValue(8, getAnnotationID(kineticNotes.first(isoform), mode));
                    table.setValue(9, getAnnotationID(kineticVmaxes.first(isoform), mode));
                    table.setValue(10, getAnnotationID(nonConsecutiveResidues.first(isoform), mode));
                    table.setValue(11, getAnnotationID(peroxisomeTransitPeptides.first(isoform), mode));
                    table.setValue(12, getAnnotationID(pharmaceuticals.first(isoform), mode));
                    table.setValue(13, getAnnotationID(phDependences.first(isoform), mode));
                    table.setValue(14, getAnnotationID(redoxPotentials.first(isoform), mode));
                    table.setValue(15, getAnnotationID(signalPeptides.first(isoform), mode));
                    table.setValue(16, getAnnotationID(temperatureDependences.first(isoform), mode));
                    table.setValue(17, getBooleanValue(canonicalIsoforms.first(isoform)));
                    table.setValue(18, getBooleanValue(swissprotDisplayed.first(isoform)));

                    if(mode == Mode.INDIRECT)
                        table.setValue(19, getStringID(isoform, "http://nextprot.org/rdf/isoform/"));

                    table.add();
                }
            }

            try(Table table = new Table(connection, type, "isoform_proteoforms (isoform, proteoform)"))
            {
                for(Node isoform : isoformNodes)
                {
                    for(Node proteoform : isoformProteoforms.get(isoform))
                    {
                        table.setValue(1, getID(isoform, "http://nextprot.org/rdf/isoform/", isoforms, mode));
                        table.setValue(2, getID(proteoform, "http://nextprot.org/rdf/proteoform/", proteoforms, mode));
                        table.add();
                    }
                }
            }

            for(int i = 0; i < isoformAnnotations.length; i++)
            {
                try(Table table = new Table(connection, type, isoformAnnotations[i][1] + " (isoform, annotation)"))
                {
                    for(Node isoform : isoformNodes)
                    {
                        for(Node annotation : links[i].get(isoform))
                        {
                            table.setValue(1, getID(isoform, "http://nextprot.org/rdf/isoform/", isoforms, mode));
                            table.setValue(2, getAnnotationID(annotation, mode));
                            table.add();
                        }
                    }
                }
            }
        });
    }


    private static void loadEntries(TripleDispatcher dispatcher, Connection connection, Server type, Mode mode)
    {
        LinkedHashSet<Node> entryNodes = dispatcher.instances(nextprot + "Entry");
        Values existences = dispatcher.values(nextprot + "existence");
        Values isoformCounts = dispatcher.values(nextprot + "isoformCount");
        Values exactMatches = dispatcher.values(skos + "exactMatch");
        Values swissprotPages = dispatcher.values(nextprot + "swissprotPage");
        Values classifiers = dispatcher.values(nextprot + "classifiedWith");
        Values entryGenes = dispatcher.values(nextprot + "gene");
        Values references = dispatcher.values(nextprot + "reference");
        Values recommendedNames = dispatcher.values(nextprot + "recommendedName");
        Values alternativeNames = dispatcher.values(nextprot + "alternativeName");
        Values additionalNames = dispatcher.values(nextprot + "additionalNames");
        Values cleavedRegionNames = dispatcher.values(nextprot + "cleavedRegionNames");
        Values functionalRegionNames = dispatcher.values(nextprot + "fonctionalRegionNames");

        dispatcher.after(() -> {
            try(Table table = new Table(connection, type,
                    "entry_bases(id, existence, isoform_count, uniprot" + (mode == Mode.INDIRECT ? ", iri" : "") + ")"))
            {
                for(Node entry : entryNodes)
                {
                    String uniprot = getStringID(exactMatches.first(entry), "http://purl.uniprot.org/uniprot/");
                    String page = getStringID(swissprotPages.first(entry), "http://www.uniprot.org/uniprot/");

                    if(page == null || !page.equals(uniprot))
                        throw new IOException("wrong swissprot page: " + entry);

                    table.setValue(1, getID(entry, "http://nextprot.org/rdf/entry/", entries, mode));
                    table.setValue(2, getID(existences.first(entry), nextprot, entities, mode));
                    table.setValue(3, getIntValue(isoformCounts.first(entry)));
                    table.setValue(4, uniprot);

                    if(mode == Mode.INDIRECT)
                        table.setValue(5, getStringID(entry, "http://nextprot.org/rdf/entry/"));

                    table.add();
                }
            }

            try(Table table = new Table(connection, type, "entry_classifiers(entry, classifier)"))
            {
                for(Node entry : entryNodes)
                {
                    for(Node classifier : classifiers.get(entry))
                    {
                        table.setValue(1, getID(entry, "http://nextprot.org/rdf/entry/", entries, mode));
                        table.setValue(2, getID(classifier, "http://nextprot.org/rdf/terminology/", terms, mode));
                        table.add();
                    }
                }
            }

            try(Table table = new Table(connection, type, "entry_genes(entry, gene)"))
            {
                for(Node entry : entryNodes)
                {
                    for(Node gene : entryGenes.get(entry))
                    {
                        table.setValue(1, getID(entry, "http://nextprot.org/rdf/entry/", entries, mode));
                        table.setValue(2, getID(gene, "http://nextprot.org/rdf/gene/", genes, mode));
                        table.add();
                    }
                }
            }

            try(Table table = new Table(connection, type, "entry_publication_references(entry, publication)"))
            {
                for(Node entry : entryNodes)
                {
                    for(Node reference : references.get(entry))
                    {
                        if(!reference.isURI())
                            continue;

                        table.setValue(1, getID(entry, "http://nextprot.org/rdf/entry/", entries, mode));
                        table.setValue(2, getIntID(reference, "http://nextprot.org/rdf/publication/"));
                        table.add();
                    }
                }
            }

            try(Table table = new Table(connection, type, "entry_recommended_names(entry, name)"))
            {
                for(Node entry : entryNodes)
                {
                    for(Node name : recommendedNames.get(entry))
                    {
                        table.setValue(1, getID(entry, "http://nextprot.org/rdf/entry/", entries, mode));
                        table.setValue(2, getMappedID(name, names));
                        table.add();
                    }
                }
            }

            try(Table table = new Table(connection, type, "entry_alternative_names(entry, name)"))
            {
                for(Node entry : entryNodes)
                {
                    for(Node name : alternativeNames.get(entry))
                    {
                        table.setValue(1, getID(entry, "http://nextprot.org/rdf/entry/", entries, mode));
                        table.setValue(2, getMappedID(name, names));
                        table.add();
                    }
                }
            }

            try(Table table = new Table(connection, type, "entry_additional_names(entry, name_list)"))
            {
                additionalNames.forEach((entry, list) -> {
                    table.setValue(1, getID(entry, "http://nextprot.org/rdf/entry/", entries, mode));
                    table.setValue(2, getMappedID(list, nameLists));
                    table.add();
                });
            }

            try(Table table = new Table(connection, type, "entry_cleaved_region_names(entry, name_list)"))
            {
                cleavedRegionNames.forEach((entry, list) -> {
                    table.setValue(1, getID(entry, "http://nextprot.org/rdf/entry/", entries, mode));
                    table.setValue(2, getMappedID(list, nameLists));
                    table.add();
                });
            }

            try(Table table = new Table(connection, type, "entry_functional_region_names(entry, name_list)"))
            {
                functionalRegionNames.forEach((entry, list) -> {
                    table.setValue(1, getID(entry, "http://nextprot.org/rdf/entry/", entries, mode));
                    table.setValue(2, getMappedID(list, nameLists));
                    table.add();
                });
            }
        });
    }


    private static void loadProteoforms(TripleDispatcher dispatcher, Connection connection, Server type, Mode mode)
    {
        LinkedHashSet<Node> proteoformNodes = dispatcher.instances(nextprot + "Proteoform");
        Values labels = dispatcher.values(rdfs + "label");

        Values[] links = new Values[proteoformAnnotations.length];

        for(int i = 0; i < proteoformAnnotations.length; i++)
            links[i] = dispatcher.values(nextprot + proteoformAnnotations[i][0]);

        dispatcher.after(() -> {
            try(Table table = new Table(connection, type,
                    "proteoform_bases(id, label" + (mode == Mode.INDIRECT ? ", iri" : "") + ")"))
            {
                for(Node proteoform : proteoformNodes)
                {
                    table.setValue(1, getID(proteoform, "http://nextprot.org/rdf/proteoform/", proteoforms, mode));
                    table.setValue(2, getLiteralValue(labels.first(proteoform)));

                    if(mode == Mode.INDIRECT)
                        table.setValue(3, getStringID(proteoform, "http://nextprot.org/rdf/proteoform/"));

                    table.add();
                }
            }

            for(int i = 0; i < proteoformAnnotations.length; i++)
            {
                try(Table table = new Table(connection, type,
                        proteoformAnnotations[i][1] + " (proteoform, annotation)"))
                {
                    for(Node proteoform : proteoformNodes)
                    {
                        for(Node annotation : links[i].get(proteoform))
                        {
                            table.setValue(1,
                                    getID(proteoform, "http://nextprot.org/rdf/proteoform/", proteoforms, mode));
                            table.setValue(2, getAnnotationID(annotation, mode));
                            table.add();
                        }
                    }
                }
            }
        });
    }


    private static void loadGenes(TripleDispatcher dispatcher, Connection connection, Server type, Mode mode)
    {
        LinkedHashSet<Node> geneNodes = dispatcher.instances(nextprot + "Gene");
        Values begins = dispatcher.values(nextprot + "begin");
        Values ends = dispatcher.values(nextprot + "end");
        Values lengths = dispatcher.values(nextprot + "length");
        Values bands = dispatcher.values(nextprot + "band");
        Values chromosomes = dispatcher.values(nextprot + "chromosome");
        Values strands = dispatcher.values(nextprot + "strand");
        Values bestGeneMappings = dispatcher.values(nextprot + "bestGeneMapping");
        Values geneNames = dispatcher.values(nextprot + "name");

        dispatcher.after(() -> {
            try(Table table = new Table(connection, type, "gene_bases(id, gene_begin, gene_end, length, band, "
                    + "chromosome, strand" + (mode == Mode.INDIRECT ? ", iri" : "") + ")"))
            {
                for(Node gene : geneNodes)
                {
                    table.setValue(1, getID(gene, "http://nextprot.org/rdf/gene/", genes, mode));
                    table.setValue(2, getIntValue(begins.first(gene)));
                    table.setValue(3, getIntValue(ends.first(gene)));
                    table.setValue(4, getIntValue(lengths.first(gene)));
                    table.setValue(5, getLiteralValue(bands.first(gene)));
                    table.setValue(6, getLiteralValue(chromosomes.first(gene)));
                    table.setValue(7, getLiteralValue(strands.first(gene)));

                    if(mode == Mode.INDIRECT)
                        table.setValue(8, getStringID(gene, "http://nextprot.org/rdf/gene/"));

                    table.add();
                }
            }

            try(Table table = new Table(connection, type, "gene_best_mappings(gene, mapping)"))
            {
                for(Node gene : geneNodes)
                {
                    for(Node mapping : bestGeneMappings.get(gene))
                    {
                        table.setValue(1, getID(gene, "http://nextprot.org/rdf/gene/", genes, mode));
                        table.setValue(2, getID(mapping, "http://nextprot.org/rdf/entry/", entries, mode));
                        table.add();
                    }
                }
            }

            try(Table table = new Table(connection, type, "gene_names(gene, name)"))
            {
                for(Node gene : geneNodes)
                {
                    for(Node name : geneNames.get(gene))
                    {
                        table.setValue(1, getID(gene, "http://nextprot.org/rdf/gene/", genes, mode));
                        table.setValue(2, getLiteralValue(name));
                        table.add();
                    }
                }
            }
        });
    }


    private static void loadProteinSequences(TripleDispatcher dispatcher, Connection connection, Server type, Mode mode)
    {
        Values types = dispatcher.values(rdf + "type");
        Values sequences = dispatcher.values(nextprot + "sequence");
        Values molecularWeights = dispatcher.values(nextprot + "molecularWeight");
        Values isoelectricPoints = dispatcher.values(nextprot + "isoelectricPoint");
        Values lengths = dispatcher.values(nextprot + "length");
        Values chains = dispatcher.values(nextprot + "chain");

        dispatcher.after(() -> {
            try(Table table = new Table(connection, type,
                    "protein_sequence_bases(id, molecular_weight, isoelectric_point, isoform, length, chain)"))
            {
                for(Node isoform : sequences.nodes())
                {
                    for(Node sequence : sequences.get(isoform))
                    {
                        if(!types.contains(sequence, proteinSequenceType))
                            continue;

                        table.setValue(1, sequenceId++);
                        table.setValue(2, getDoubleValue(molecularWeights.first(sequence)));
                        table.setValue(3, getDoubleValue(isoelectricPoints.first(sequence)));
                        table.setValue(4, getID(isoform, "http://nextprot.org/rdf/isoform/", isoforms, mode));
                        table.setValue(5, getIntValue(lengths.first(sequence)));
                        table.setValue(6, getLiteralValue(chains.first(sequence)));
                        table.add();
                    }
                }
            }
        });
    }


    private static void loadProteinFamilyInfos(TripleDispatcher dispatcher, Connection connection, Server type,
            Mode mode)
    {
        Values types = dispatcher.values(rdf + "type");
        Values families = dispatcher.values(nextprot + "family");
        Values familyTerms = dispatcher.values(nextprot + "term");
        Values regions = dispatcher.values(nextprot + "region");
        Values descriptions = dispatcher.values(nextprot + "description");

        dispatcher.after(() -> {
            try(Table table = new Table(connection, type, "family_info_bases(id, entry, term, region, description)"))
            {
                for(Node entry : families.nodes())
                {
                    for(Node family : families.get(entry))
                    {
                        if(!types.contains(family, familyInfoType))
                            continue;

                        table.setValue(1, familyId++);
                        table.setValue(2, getID(entry, "http://nextprot.org/rdf/entry/", entries, mode));
                        table.setValue(3,
                                getID(familyTerms.first(family), "http://nextprot.org/rdf/terminology/", terms, mode));
                        table.setValue(4, getLiteralValue(regions.first(family)));
                        table.setValue(5, getLiteralValue(descriptions.first(family)));
                        table.add();
                    }
                }
            }
        });
    }


    private static void loadHistories(TripleDispatcher dispatcher, Connection connection, Server type, Mode mode)
    {
        Values types = dispatcher.values(rdf + "type");
        Values histories = dispatcher.values(nextprot + "history");
        Values integrated = dispatcher.values(nextprot + "integrated");
        Values updated = dispatcher.values(nextprot + "updated");
        Values lastSequenceUpdates = dispatcher.values(nextprot + "lastSequenceUpdate");
        Values versions = dispatcher.values(nextprot + "version");
        Values sequenceVersions = dispatcher.values(nextprot + "sequenceVersion");
        Values historyNames = dispatcher.values(nextprot + "name");

        dispatcher.after(() -> {
            try(Table table = new Table(connection, type, "history_bases(id, entry, integrated, updated, "
                    + "last_sequence_update, version, sequence_version, name)"))
            {
                for(Node entry : histories.nodes())
                {
                    for(Node history : histories.get(entry))
                    {
                        if(!types.contains(history, historyType))
                            continue;

                        table.setValue(1, historyId++);
                        table.setValue(2, getID(entry, "http://nextprot.org/rdf/entry/", entries, mode));
                        table.setValue(3, getLiteralValue(integrated.first(history)));
                        table.setValue(4, getLiteralValue(updated.first(history)));
                        table.setValue(5, getLiteralValue(lastSequenceUpdates.first(history)));
                        table.setValue(6, getLiteralValue(versions.first(history)));
                        table.setValue(7, getLiteralValue(sequenceVersions.first(history)));
                        table.setValue(8, getLiteralValue(historyNames.first(history)));
                        table.add();
                    }
                }
            }
        });
    }


    private static void loadIdentifiers(TripleDispatcher dispatcher, Connection connection, Server type, Mode mode)
    {
        Values types = dispatcher.values(rdf + "type");
        Values references = dispatcher.values(nextprot + "reference");
        Values provenances = dispatcher.values(nextprot + "provenance");
        Values accessions = dispatcher.values(nextprot + "accession");

        dispatcher.after(() -> {
            try(Table table = new Table(connection, type, "identifier_bases(id, entry, provenance, accession)"))
            {
                for(Node entry : references.nodes())
                {
                    for(Node identifier : references.get(entry))
                    {
                        if(!types.contains(identifier, identifierType))
                            continue;

                        table.setValue(1, identifierId++);
                        table.setValue(2, getID(entry, "http://nextprot.org/rdf/entry/", entries, mode));
                        table.setValue(3,
                                getID(provenances.first(identifier), "http://nextprot.org/rdf/db/", databases, mode));
                        table.setValue(4, getLiteralValue(accessions.first(identifier)));
                        table.add();
                    }
                }
            }
        });
    }


    private static void loadEntryXrefs(TripleDispatcher dispatcher, Connection connection, Server type, Mode mode)
    {
        LinkedHashSet<Node> entryNodes = dispatcher.instances(nextprot + "Entry");
        Values types = dispatcher.values(rdf + "type");
        Values references = dispatcher.values(nextprot + "reference");
        Values provenances = dispatcher.values(nextprot + "provenance");
        Values accessions = dispatcher.values(nextprot + "accession");

        dispatcher.after(() -> {
            try(Table table = new Table(connection, type, "entry_xref_bases(id, entry, provenance, accession)"))
            {
                for(Node entry : entryNodes)
                {
                    for(Node xref : references.get(entry))
                    {
                        if(!types.contains(xref, xrefType))
                            continue;

                        table.setValue(1, entryXrefId++);
                        table.setValue(2, getID(entry, "http://nextprot.org/rdf/entry/", entries, mode));
                        table.setValue(3,
                                getID(provenances.first(xref), "http://nextprot.org/rdf/db/", databases, mode));
                        table.setValue(4, getLiteralValue(accessions.first(xref)));
                        table.add();
                    }
                }
            }
        });
    }


    private static void loadEvidenceXrefs(TripleDispatcher dispatcher, Connection connection, Server type, Mode mode)
    {
        LinkedHashSet<Node> evidenceNodes = dispatcher.instances(nextprot + "Evidence");
        Values types = dispatcher.values(rdf + "type");
        Values references = dispatcher.values(nextprot + "reference");
        Values provenances = dispatcher.values(nextprot + "provenance");
        Values accessions = dispatcher.values(nextprot + "accession");

        dispatcher.after(() -> {
            try(Table table = new Table(connection, type, "evidence_xref_bases(id, evidence, provenance, accession)"))
            {
                for(Node evidence : evidenceNodes)
                {
                    for(Node xref : references.get(evidence))
                    {
                        if(!types.contains(xref, xrefType))
                            continue;

                        table.setValue(1, evidenceXrefId++);
                        table.setValue(2, getID(evidence, "http://nextprot.org/rdf/evidence/", evidences, mode));
                        table.setValue(3,
                                getID(provenances.first(xref), "http://nextprot.org/rdf/db/", databases, mode));
                        table.setValue(4, getLiteralValue(accessions.first(xref)));
                        table.add();
                    }
                }
            }
        });
    }


    /*
     * Registers the loading of the cross-references to the database that annotations have as interactants.
     */
    private static void loadInteractantXrefs(TripleDispatcher dispatcher, Connection connection, Server type, Mode mode,
            String database, String table, XrefColumns columns)
    {
        Node provenance = NodeFactory.createURI(db + database);
        Values types = dispatcher.values(rdf + "type");
        Values interactants = dispatcher.values(nextprot + "interactant");
        Values provenances = dispatcher.values(nextprot + "provenance");
        Values exactMatches = dispatcher.values(skos + "exactMatch");
        Values accessions = dispatcher.values(nextprot + "accession");
        Values labels = dispatcher.values(rdfs + "label");

        dispatcher.after(() -> {
            try(Table xrefs = new Table(connection, type, table))
            {
                for(Node annotation : interactants.nodes())
                {
                    for(Node xref : interactants.get(annotation))
                    {
                        if(!types.contains(xref, xrefType) || !provenances.contains(xref, provenance))
                            continue;

                        columns.set(xrefs, annotation, exactMatches.first(xref));
                        xrefs.setValue(4, getLiteralValue(accessions.first(xref)));
                        xrefs.setValue(5, getLiteralValue(labels.first(xref)));
                        xrefs.add();
                    }
                }
            }
        });
    }


    private static void loadNames(TripleDispatcher dispatcher, Connection connection, Server type)
    {
        LinkedHashSet<Node> nameNodes = dispatcher.instances(nextprot + "Name");
        Values fullNames = dispatcher.values(nextprot + "fullName");
        Values shortNames = dispatcher.values(nextprot + "shortName");
        Values fullRegionNames = dispatcher.values(nextprot + "fullRegionName");
        Values shortRegionNames = dispatcher.values(nextprot + "shortRegionName");
        Values ecEnzymeNames = dispatcher.values(nextprot + "ecEnzymeName");
        Values cdAntigens = dispatcher.values(nextprot + "cdAntigenCdAntigen");
        Values innNames = dispatcher.values(nextprot + "innInternationalNonproprietaryNames");
        Values allergens = dispatcher.values(nextprot + "allergenAllergen");

        dispatcher.after(() -> {
            try(Table table = new Table(connection, type, "name_bases(id, full_name, short_name, full_region_name, "
                    + "short_region_name, ec_enzyme_name, cd_antigen, inn_name, allergen)"))
            {
                for(Node name : nameNodes)
                {
                    names.put(name, nameId);

                    table.setValue(1, nameId++);
                    table.setValue(2, getLiteralValue(fullNames.first(name)));
                    table.setValue(3, getLiteralValue(shortNames.first(name)));
                    table.setValue(4, getLiteralValue(fullRegionNames.first(name)));
                    table.setValue(5, getLiteralValue(shortRegionNames.first(name)));
                    table.setValue(6, getLiteralValue(ecEnzymeNames.first(name)));
                    table.setValue(7, getLiteralValue(cdAntigens.first(name)));
                    table.setValue(8, getLiteralValue(innNames.first(name)));
                    table.setValue(9, getLiteralValue(allergens.first(name)));
                    table.add();
                }
            }
        });
    }


    private static void loadNameLists(TripleDispatcher dispatcher, Connection connection, Server type)
    {
        LinkedHashSet<Node> listNodes = dispatcher.instances(nextprot + "NameList");
        Values recommendedNames = dispatcher.values(nextprot + "recommendedName");
        Values alternativeNames = dispatcher.values(nextprot + "alternativeName");

        dispatcher.after(() -> {
            try(Table table = new Table(connection, type, "name_list_bases(id)"))
            {
                for(Node list : listNodes)
                {
                    nameLists.put(list, nameListId);

                    table.setValue(1, nameListId++);
                    table.add();
                }
            }

            try(Table table = new Table(connection, type, "name_list_recommended_names(list, name)"))
            {
                for(Node list : listNodes)
                {
                    for(Node name : recommendedNames.get(list))
                    {
                        table.setValue(1, getMappedID(list, nameLists));
                        table.setValue(2, getMappedID(name, names));
                        table.add();
                    }
                }
            }

            try(Table table = new Table(connection, type, "name_list_alternative_names(list, name)"))
            {
                for(Node list : listNodes)
                {
                    for(Node name : alternativeNames.get(list))
                    {
                        table.setValue(1, getMappedID(list, nameLists));
                        table.setValue(2, getMappedID(name, names));
                        table.add();
                    }
                }
            }
        });
    }


    private static Object getAnnotationID(Node node, Mode mode) throws IOException
    {
        return getID(node, "http://nextprot.org/rdf/annotation/", annotations, mode);
    }


    public static void load(String path, String file, Connection connection, Server type, Mode mode)
            throws IOException, SQLException
    {
        names.clear();
        nameLists.clear();

        TripleDispatcher dispatcher = new TripleDispatcher();

        loadNames(dispatcher, connection, type);
        loadNameLists(dispatcher, connection, type);

        loadIsoforms(dispatcher, connection, type, mode);
        loadAnnotations(dispatcher, connection, type, mode);
        loadEvidences(dispatcher, connection, type, mode);
        loadEntries(dispatcher, connection, type, mode);
        loadProteoforms(dispatcher, connection, type, mode);
        loadGenes(dispatcher, connection, type, mode);
        loadProteinSequences(dispatcher, connection, type, mode);
        loadProteinFamilyInfos(dispatcher, connection, type, mode);
        loadHistories(dispatcher, connection, type, mode);
        loadIdentifiers(dispatcher, connection, type, mode);
        loadEntryXrefs(dispatcher, connection, type, mode);
        loadEvidenceXrefs(dispatcher, connection, type, mode);

        loadInteractantXrefs(dispatcher, connection, type, mode, "ChEBI",
                "chebi_xref_bases(id, chebi, annotation, accession, label)", (table, annotation, chebi) -> {
                    table.setValue(1, chebiXrefId++);
                    table.setValue(2, getIntID(chebi, "http://purl.obolibrary.org/obo/CHEBI_"));
                    table.setValue(3, getAnnotationID(annotation, mode));
                });

        loadInteractantXrefs(dispatcher, connection, type, mode, "DrugBank",
                "drugbank_xref_bases(id, annotation, drugbank, accession, label)", (table, annotation, drugbank) -> {
                    table.setValue(1, drugBankXrefId++);
                    table.setValue(2, getAnnotationID(annotation, mode));
                    table.setValue(3, getStringID(drugbank,
                            "http://wifo5-04.informatik.uni-mannheim.de/drugbank/resource/drugs/DB"));
                });

        loadInteractantXrefs(dispatcher, connection, type, mode, "UniProtKB",
                "uniprot_xref_bases(id, annotation, uniprot, accession, label)", (table, annotation, uniprot) -> {
                    table.setValue(1, uniProtXrefId++);
                    table.setValue(2, getAnnotationID(annotation, mode));
                    table.setValue(3, getStringID(uniprot, "http://purl.uniprot.org/uniprot/"));
                });

        dispatcher.load(path, file);
    }


    public static void load(String path, Connection connection, Server type, Mode mode) throws IOException, SQLException
    {
        load(path, "nextprot_chromosome_MT.ttl.gz", connection, type, mode);
        load(path, "nextprot_chromosome_unknown.ttl.gz", connection, type, mode);
        load(path, "nextprot_chromosome_X.ttl.gz", connection, type, mode);
        load(path, "nextprot_chromosome_Y.ttl.gz", connection, type, mode);
        load(path, "nextprot_chromosome_01.ttl.gz", connection, type, mode);
        load(path, "nextprot_chromosome_02.ttl.gz", connection, type, mode);
        load(path, "nextprot_chromosome_03.ttl.gz", connection, type, mode);
        load(path, "nextprot_chromosome_04.ttl.gz", connection, type, mode);
        load(path, "nextprot_chromosome_05.ttl.gz", connection, type, mode);
        load(path, "nextprot_chromosome_06.ttl.gz", connection, type, mode);
        load(path, "nextprot_chromosome_07.ttl.gz", connection, type, mode);
        load(path, "nextprot_chromosome_08.ttl.gz", connection, type, mode);
        load(path, "nextprot_chromosome_09.ttl.gz", connection, type, mode);
        load(path, "nextprot_chromosome_10.ttl.gz", connection, type, mode);
        load(path, "nextprot_chromosome_11.ttl.gz", connection, type, mode);
        load(path, "nextprot_chromosome_12.ttl.gz", connection, type, mode);
        load(path, "nextprot_chromosome_13.ttl.gz", connection, type, mode);
        load(path, "nextprot_chromosome_14.ttl.gz", connection, type, mode);
        load(path, "nextprot_chromosome_15.ttl.gz", connection, type, mode);
        load(path, "nextprot_chromosome_16.ttl.gz", connection, type, mode);
        load(path, "nextprot_chromosome_17.ttl.gz", connection, type, mode);
        load(path, "nextprot_chromosome_18.ttl.gz", connection, type, mode);
        load(path, "nextprot_chromosome_19.ttl.gz", connection, type, mode);
        load(path, "nextprot_chromosome_20.ttl.gz", connection, type, mode);
        load(path, "nextprot_chromosome_21.ttl.gz", connection, type, mode);
        load(path, "nextprot_chromosome_22.ttl.gz", connection, type, mode);
    }
}
