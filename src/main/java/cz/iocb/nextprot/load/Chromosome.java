package cz.iocb.nextprot.load;

import static cz.iocb.nextprot.load.Schema.databases;
import static cz.iocb.nextprot.load.Schema.entities;
import static cz.iocb.nextprot.load.Schema.sources;
import static cz.iocb.nextprot.load.Terminology.terms;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import org.apache.jena.rdf.model.Model;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;
import cz.iocb.nextprot.load.NeXtProt.Mode;
import cz.iocb.nextprot.load.NeXtProt.Server;



public class Chromosome
{
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

    private static HashSet<String> comments = new HashSet<String>();

    private static ObjectIntHashMap<String> names = new ObjectIntHashMap<String>();
    private static ObjectIntHashMap<String> nameLists = new ObjectIntHashMap<String>();

    static ObjectIntHashMap<String> annotations = new ObjectIntHashMap<String>();
    static ObjectIntHashMap<String> evidences = new ObjectIntHashMap<String>();
    static ObjectIntHashMap<String> entries = new ObjectIntHashMap<String>();
    static ObjectIntHashMap<String> isoforms = new ObjectIntHashMap<String>();
    static ObjectIntHashMap<String> proteoforms = new ObjectIntHashMap<String>();
    static ObjectIntHashMap<String> genes = new ObjectIntHashMap<String>();


    private static void loadEvidences(Model model, Connection connection, Server type, Mode mode)
            throws IOException, SQLException
    {
        // @formatter:off
        new DataLoader(model, connection, type,
                "?evidence rdf:type :Evidence "
                        + "optional { ?evidence :expressionScore ?expressionScore }"
                        + "optional { ?evidence :allele-frequency ?alleleFrequency }"
                        + "optional { ?evidence :experimentalContext ?experimentalContext }"
                        + "optional { ?evidence :evidenceCode ?evidenceCode }"
                        + "optional { ?evidence :assignedBy ?assignedBy }"
                        + "optional { ?evidence :expressionLevel ?expressionLevel }"
                        + "optional { ?evidence :fromXref ?fromXref }"
                        + "optional { ?evidence :integrationLevel ?integrationLevel }"
                        + "optional { ?evidence :quality ?quality }"
                        + "optional { ?evidence :interactionDetectionMethod ?detectionMethod }"
                        + "optional { ?evidence :negative ?negative }"
                        + "optional { ?evidence :numberOfExperiments ?numberOfExperiments }"
                        + "optional { ?evidence :homozygote-count ?homozygoteCount }"
                        + "optional { ?evidence :allele-number ?alleleNumber }"
                        + "optional { ?evidence :allele-count ?alleleCount }"
                        + "optional { ?evidence :isoformSpecificity ?isoformSpecificity }"
                        + "optional { ?evidence :antibodiesAcc ?antibodiesAcc }"
                        + "optional { ?evidence :assocType ?assocType }"
                        + "optional { ?evidence :cellLine ?cellLine }"
                        + "optional { ?evidence :goQualifier ?goQualifier }"
                        + "optional { ?evidence :intensity ?intensity }"
                        + "optional { ?evidence :negativeIsoformSpecificity ?negativeIsoformSpecificity }",
                "evidence_bases(id, expression_score, allele_frequency, experimental_context, evidence_code, "
                        + "assigned_by, expression_level, from_xref, integration_level, quality, "
                        + "interaction_detection_method, negative, number_of_experiments, homozygote_count, "
                        + "allele_number, allele_count, isoform_specificity, antibodies_acc, assoc_type, cell_line, "
                        + "go_qualifier, intensity, negative_isoform_specificity" + (mode == Mode.INDIRECT ? ", iri" : "") + ")")
        // @formatter:on
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("evidence", "http://nextprot.org/rdf/evidence/", evidences, mode));
                setValue(2, getDoubleValue("expressionScore"));
                setValue(3, getDoubleValue("alleleFrequency"));
                setValue(4, getIntID("experimentalContext", "http://nextprot.org/rdf/context/"));
                setValue(5, getID("evidenceCode", "http://nextprot.org/rdf/terminology/", terms, mode));
                setValue(6, getID("assignedBy", "http://nextprot.org/rdf/source/", sources, mode));
                setValue(7, getID("expressionLevel", "http://nextprot.org/rdf#", entities, mode));
                setValue(8, getID("fromXref", "http://nextprot.org/rdf/db/", databases, mode));
                setValue(9, getID("integrationLevel", "http://nextprot.org/rdf#", entities, mode));
                setValue(10, getID("quality", "http://nextprot.org/rdf#", entities, mode));
                setValue(11, getID("detectionMethod", "http://nextprot.org/rdf/terminology/", terms, mode));
                setValue(12, getBooleanValue("negative"));
                setValue(13, getIntValue("numberOfExperiments"));
                setValue(14, getIntValue("homozygoteCount"));
                setValue(15, getIntValue("alleleNumber"));
                setValue(16, getIntValue("alleleCount"));
                setValue(17, getLiteralValue("isoformSpecificity"));
                setValue(18, getLiteralValue("antibodiesAcc"));
                setValue(19, getLiteralValue("assocType"));
                setValue(20, getLiteralValue("cellLine"));
                setValue(21, getLiteralValue("goQualifier"));
                setValue(22, getLiteralValue("intensity"));
                setValue(23, getLiteralValue("negativeIsoformSpecificity"));

                if(mode == Mode.INDIRECT)
                    setValue(24, getStringID("evidence", "http://nextprot.org/rdf/evidence/"));
            }
        }.load();


        new DataLoader(model, connection, type,
                "?evidence rdf:type :Evidence; :reference ?reference filter(isIRI(?reference))",
                "evidence_publication_references(evidence, publication)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("evidence", "http://nextprot.org/rdf/evidence/", evidences, mode));
                setValue(2, getIntID("reference", "http://nextprot.org/rdf/publication/"));
            }
        }.load();
    }


    private static void loadAnnotations(Model model, Connection connection, Server type, Mode mode)
            throws SQLException, IOException
    {
        // @formatter:off
        new DataLoader(model, connection, type,
                "{ ?annotation rdf:type ?type. ?annotation :quality ?quality. filter(?type != :Evidence) }"
                        + "union { ?O :impactedObject ?annotation filter not exists { ?annotation rdf:type ?type } }"
                        + "optional { ?annotation :resolution ?resolution }"
                        + "optional { ?annotation :resolution ?resolution }"
                        + "optional { ?annotation :term ?term }"
                        + "optional { ?annotation :impactedObject ?impactedObject }"
                        + "optional { ?annotation :selfInteraction ?selfInteraction }"
                        + "optional { ?annotation :proteotypic ?proteotypic }"
                        + "optional { ?annotation :start ?start }"
                        + "optional { ?annotation :end ?end }"
                        + "optional { ?annotation :entryAnnotationId ?entryAnnotationId }"
                        + "optional { ?annotation :variation ?variation }"
                        + "optional { ?annotation :original ?original }"
                        + "optional { ?annotation :hgvs ?hgvs }"
                        + "optional { ?annotation :method ?method }"
                        + "optional { ?annotation :peptideName ?peptideName }"
                        + "optional { ?annotation :peptideUnicity ?peptideUnicity }"
                        + "optional { ?annotation :antibodyName ?antibodyName }"
                        + "optional { ?annotation :antibodyUnicity ?antibodyUnicity }",
                "annotation_bases(id, resolution, type, quality, term, impacted_object, self_interaction, "
                        + "proteotypic, position_start, position_end, entry_annotation, variation, original, hgvs, "
                        + "method, peptide_name, peptide_unicity, antibody_name, antibody_unicity"
                        + (mode == Mode.INDIRECT ? ", iri" : "") + ")")
        // @formatter:on
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
                setValue(2, getDoubleValue("resolution"));
                setValue(3, getID("type", "http://nextprot.org/rdf#", entities, mode));
                setValue(4, getID("quality", "http://nextprot.org/rdf#", entities, mode));
                setValue(5, getID("term", "http://nextprot.org/rdf/terminology/", terms, mode));
                setValue(6, getID("impactedObject", "http://nextprot.org/rdf/annotation/", annotations, mode));
                setValue(7, getBooleanValue("selfInteraction"));
                setValue(8, getBooleanValue("proteotypic"));
                setValue(9, getIntValue("start"));
                setValue(10, getIntValue("end"));
                setValue(11, getLiteralValue("entryAnnotationId"));
                setValue(12, getLiteralValue("variation"));
                setValue(13, getLiteralValue("original"));
                setValue(14, getLiteralValue("hgvs"));
                setValue(15, getLiteralValue("method"));
                setValue(16, getLiteralValue("peptideName"));
                setValue(17, getLiteralValue("peptideUnicity"));
                setValue(18, getLiteralValue("antibodyName"));
                setValue(19, getLiteralValue("antibodyUnicity"));

                if(mode == Mode.INDIRECT)
                    setValue(20, getStringID("annotation", "http://nextprot.org/rdf/annotation/"));
            }
        }.load();


        new DataLoader(model, connection, type, "?annotation :evidence ?evidence",
                "annotation_evidences(annotation, evidence)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
                setValue(2, getID("evidence", "http://nextprot.org/rdf/evidence/", evidences, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?annotation :negativeEvidence ?evidence",
                "annotation_negative_evidences(annotation, evidence)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
                setValue(2, getID("evidence", "http://nextprot.org/rdf/evidence/", evidences, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?annotation :disease ?disease",
                "annotation_diseases(annotation, disease)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                if(!getIRI("annotation").startsWith("http://nextprot.org/rdf/annotation/"))
                    return;

                setValue(1, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
                setValue(2, getID("disease", "http://nextprot.org/rdf/terminology/", terms, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?annotation rdfs:comment ?comment",
                "annotation_comments(annotation, seq, comment)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                Object annotation = getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode);
                String comment = getLiteralValue("comment");

                String id = annotation + "\0" + comment;

                if(!comments.contains(id))
                {
                    setValue(1, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
                    setValue(2, comments.size());
                    setValue(3, getLiteralValue("comment"));
                    comments.add(id);
                }
            }
        }.load();


        new DataLoader(model, connection, type,
                "?annotation :isoformSpecificity ?specificity "
                        + "filter(strstarts(str(?annotation), 'http://nextprot.org/rdf/annotation/'))",
                "annotation_isoform_specificities(annotation, specificity)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
                setValue(2, getID("specificity", "http://nextprot.org/rdf#", entities, mode));
            }
        }.load();


        new DataLoader(model, connection, type,
                "?annotation :interactant ?interactant "
                        + "filter(strstarts(str(?interactant), 'http://nextprot.org/rdf/entry/'))",
                "annotation_entry_interactants(annotation, interactant)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
                setValue(2, getID("interactant", "http://nextprot.org/rdf/entry/", entries, mode));
            }
        }.load();


        new DataLoader(model, connection, type,
                "?annotation :interactant ?interactant "
                        + "filter(strstarts(str(?interactant), 'http://nextprot.org/rdf/isoform/'))",
                "annotation_isoform_interactants(annotation, interactant)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
                setValue(2, getID("interactant", "http://nextprot.org/rdf/isoform/", isoforms, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?annotation :peptideSet ?set",
                "annotation_peptide_sets(annotation, peptide_set)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
                setValue(2, getLiteralValue("set"));
            }
        }.load();
    }


    private static void loadIsomorfs(Model model, Connection connection, Server type, Mode mode)
            throws SQLException, IOException
    {
        // @formatter:off
        new DataLoader(model, connection, type,
                "?isoform rdf:type :Isoform "
                        + "optional { ?entry :isoform ?isoform }"
                        + "optional { ?isoform :absorptionMax ?absorptionMax }"
                        + "optional { ?isoform :absorptionNote ?absorptionNote }"
                        + "optional { ?isoform :allergen ?allergen }"
                        + "optional { ?isoform :initiatorMethionine ?initiatorMethionine }"
                        + "optional { ?isoform :kineticKM ?kineticKM }"
                        + "optional { ?isoform :kineticNote ?kineticNote }"
                        + "optional { ?isoform :kineticVmax ?kineticVmax }"
                        + "optional { ?isoform :nonConsecutiveResidue ?nonConsecutiveResidue }"
                        + "optional { ?isoform :peroxisomeTransitPeptide ?peroxisomeTransitPeptide }"
                        + "optional { ?isoform :pharmaceutical ?pharmaceutical }"
                        + "optional { ?isoform :phDependence ?phDependence }"
                        + "optional { ?isoform :redoxPotential ?redoxPotential }"
                        + "optional { ?isoform :signalPeptide ?signalPeptide }"
                        + "optional { ?isoform :temperatureDependence ?temperatureDependence }"
                        + "optional { ?isoform :canonicalIsoform ?canonicalIsoform }"
                        + "optional { ?isoform :swissprotDisplayed ?swissprotDisplayed }",
                "isoform_bases (id, entry, absorption_max, absorption_note, allergen, "
                        + "initiator_methionine, kinetic_k_m, kinetic_note, kinetic_vmax, non_consecutive_residue, "
                        + "peroxisome_transit_peptide, pharmaceutical, ph_dependence,  redox_potential, "
                        + "signal_peptide, temperature_dependence, canonical_isoform, swissprot_displayed" + (mode == Mode.INDIRECT ? ", iri" : "")
                        + ")")
        // @formatter:on
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("entry", "http://nextprot.org/rdf/entry/", entries, mode));
                setValue(3, getID("absorptionMax", "http://nextprot.org/rdf/annotation/", annotations, mode));
                setValue(4, getID("absorptionNote", "http://nextprot.org/rdf/annotation/", annotations, mode));
                setValue(5, getID("allergen", "http://nextprot.org/rdf/annotation/", annotations, mode));
                setValue(6, getID("initiatorMethionine", "http://nextprot.org/rdf/annotation/", annotations, mode));
                setValue(7, getID("kineticKM", "http://nextprot.org/rdf/annotation/", annotations, mode));
                setValue(8, getID("kineticNote", "http://nextprot.org/rdf/annotation/", annotations, mode));
                setValue(9, getID("kineticVmax", "http://nextprot.org/rdf/annotation/", annotations, mode));
                setValue(10, getID("nonConsecutiveResidue", "http://nextprot.org/rdf/annotation/", annotations, mode));
                setValue(11,
                        getID("peroxisomeTransitPeptide", "http://nextprot.org/rdf/annotation/", annotations, mode));
                setValue(12, getID("pharmaceutical", "http://nextprot.org/rdf/annotation/", annotations, mode));
                setValue(13, getID("phDependence", "http://nextprot.org/rdf/annotation/", annotations, mode));
                setValue(14, getID("redoxPotential", "http://nextprot.org/rdf/annotation/", annotations, mode));
                setValue(15, getID("signalPeptide", "http://nextprot.org/rdf/annotation/", annotations, mode));
                setValue(16, getID("temperatureDependence", "http://nextprot.org/rdf/annotation/", annotations, mode));
                setValue(17, getBooleanValue("canonicalIsoform"));
                setValue(18, getBooleanValue("swissprotDisplayed"));

                if(mode == Mode.INDIRECT)
                    setValue(19, getStringID("isoform", "http://nextprot.org/rdf/isoform/"));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :proteoform ?proteoform",
                "isoform_proteoforms (isoform, proteoform)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("proteoform", "http://nextprot.org/rdf/proteoform/", proteoforms, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :activeSite ?annotation",
                "isoform_active_sites (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :activityRegulation ?annotation",
                "isoform_activity_regulations (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :antibodyMapping ?annotation",
                "isoform_antibody_mappings (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :betaStrand ?annotation",
                "isoform_beta_strands (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :binaryInteraction ?annotation",
                "isoform_binary_interactions (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :bindingSite ?annotation",
                "isoform_binding_sites (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :biophysicochemicalProperty ?annotation",
                "isoform_biophysicochemical_properties (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :calciumBindingRegion ?annotation",
                "isoform_calcium_binding_regions (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :catalyticActivity ?annotation",
                "isoform_catalytic_activities (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :caution ?annotation",
                "isoform_cautions (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :cellularComponent ?annotation",
                "isoform_cellular_components (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :cleavageSite ?annotation",
                "isoform_cleavage_sites (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :cofactor ?annotation",
                "isoform_cofactors (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :cofactorInfo ?annotation",
                "isoform_cofactor_infos (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :coiledCoilRegion ?annotation",
                "isoform_coiled_coil_regions (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :compositionallyBiasedRegion ?annotation",
                "isoform_compositionally_biased_regions (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :crossLink ?annotation",
                "isoform_cross_links (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :detectedExpression ?annotation",
                "isoform_detected_expressions (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :developmentalStageInfo ?annotation",
                "isoform_developmental_stage_infos (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :disease ?annotation",
                "isoform_diseases (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :disulfideBond ?annotation",
                "isoform_disulfide_bonds (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :dnaBindingRegion ?annotation",
                "isoform_dna_binding_regions (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :domain ?annotation",
                "isoform_domains (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :domainInfo ?annotation",
                "isoform_domain_infos (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type,
                "?isoform rdf:type :Isoform; :electrophysiologicalParameter ?annotation",
                "isoform_electrophysiological_parameters (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :enzymeClassification ?annotation",
                "isoform_enzyme_classifications (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :expression ?annotation",
                "isoform_expressions (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :expressionInfo ?annotation",
                "isoform_expression_infos (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :expressionProfile ?annotation",
                "isoform_expression_profiles (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :function ?annotation",
                "isoform_functions (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :functionInfo ?annotation",
                "isoform_function_infos (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :generalAnnotation ?annotation",
                "isoform_general_annotations (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :glycosylationSite ?annotation",
                "isoform_glycosylation_sites (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :goBiologicalProcess ?annotation",
                "isoform_go_biological_processs (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :goCellularComponent ?annotation",
                "isoform_go_cellular_components (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :goMolecularFunction ?annotation",
                "isoform_go_molecular_functions (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :helix ?annotation",
                "isoform_helixs (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :induction ?annotation",
                "isoform_inductions (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :interactingRegion ?annotation",
                "isoform_interacting_regions (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :interaction ?annotation",
                "isoform_interactions (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :interactionInfo ?annotation",
                "isoform_interaction_infos (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :intramembraneRegion ?annotation",
                "isoform_intramembrane_regions (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :keyword ?annotation",
                "isoform_keywords (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :lipidationSite ?annotation",
                "isoform_lipidation_sites (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :mapping ?annotation",
                "isoform_mappings (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :matureProtein ?annotation",
                "isoform_mature_proteins (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :medical ?annotation",
                "isoform_medicals (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :metalBindingSite ?annotation",
                "isoform_metal_binding_sites (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :miscellaneous ?annotation",
                "isoform_miscellaneouss (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :miscellaneousRegion ?annotation",
                "isoform_miscellaneous_regions (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :miscellaneousSite ?annotation",
                "isoform_miscellaneous_sites (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :mitochondrialTransitPeptide ?annotation",
                "isoform_mitochondrial_transit_peptides (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :modifiedResidue ?annotation",
                "isoform_modified_residues (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :mutagenesis ?annotation",
                "isoform_mutagenesiss (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :nonTerminalResidue ?annotation",
                "isoform_non_terminal_residues (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type,
                "?isoform rdf:type :Isoform; :nucleotidePhosphateBindingRegion ?annotation",
                "isoform_nucleotide_phosphate_binding_regions (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :pathway ?annotation",
                "isoform_pathways (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :pdbMapping ?annotation",
                "isoform_pdb_mappings (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :peptideMapping ?annotation",
                "isoform_peptide_mappings (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :positionalAnnotation ?annotation",
                "isoform_positional_annotations (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :processingProduct ?annotation",
                "isoform_processing_products (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :propeptide ?annotation",
                "isoform_propeptides (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :ptm ?annotation",
                "isoform_ptms (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :ptmInfo ?annotation",
                "isoform_ptm_infos (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :region ?annotation",
                "isoform_regions (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :repeat ?annotation",
                "isoform_repeats (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :secondaryStructure ?annotation",
                "isoform_secondary_structures (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :selenocysteine ?annotation",
                "isoform_selenocysteines (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :sequenceCaution ?annotation",
                "isoform_sequence_cautions (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :sequenceConflict ?annotation",
                "isoform_sequence_conflicts (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :shortSequenceMotif ?annotation",
                "isoform_short_sequence_motifs (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :site ?annotation",
                "isoform_sites (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :smallMoleculeInteraction ?annotation",
                "isoform_small_molecule_interactions (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :srmPeptideMapping ?annotation",
                "isoform_srm_peptide_mappings (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :subcellularLocation ?annotation",
                "isoform_subcellular_locations (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :subcellularLocationNote ?annotation",
                "isoform_subcellular_location_notes (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :topologicalDomain ?annotation",
                "isoform_topological_domains (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :topology ?annotation",
                "isoform_topologies (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :transmembraneRegion ?annotation",
                "isoform_transmembrane_regions (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :transportActivity ?annotation",
                "isoform_transport_activities (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :turn ?annotation",
                "isoform_turns (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :undetectedExpression ?annotation",
                "isoform_undetected_expressions (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :uniprotKeyword ?annotation",
                "isoform_uniprot_keywords (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :variant ?annotation",
                "isoform_variants (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :variantInfo ?annotation",
                "isoform_variant_infos (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :zincFingerRegion ?annotation",
                "isoform_zinc_finger_regions (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?isoform rdf:type :Isoform; :interactionMapping ?annotation",
                "isoform_interaction_mappings (isoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();
    }


    private static void loadEntries(Model model, Connection connection, Server type, Mode mode)
            throws IOException, SQLException
    {
        // @formatter:off
        new DataLoader(model, connection, type,
                "?entry rdf:type :Entry "
                        + "optional { ?entry :existence ?existence }"
                        + "optional { ?entry :isoformCount ?isoformCount }"
                        + "optional { ?entry skos:exactMatch ?exactMatch }"
                        + "optional { ?entry :swissprotPage ?swissprotPage }",
                "entry_bases(id, existence, isoform_count, uniprot" + (mode == Mode.INDIRECT ? ", iri" : "") + ")")
        // @formatter:on
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                String uniprot = getStringID("exactMatch", "http://purl.uniprot.org/uniprot/");
                String page = getStringID("swissprotPage", "http://www.uniprot.org/uniprot/");

                if(!page.equals(uniprot))
                    throw new IOException();

                setValue(1, getID("entry", "http://nextprot.org/rdf/entry/", entries, mode));
                setValue(2, getID("existence", "http://nextprot.org/rdf#", entities, mode));
                setValue(3, getIntValue("isoformCount"));
                setValue(4, uniprot);

                if(mode == Mode.INDIRECT)
                    setValue(5, getStringID("entry", "http://nextprot.org/rdf/entry/"));
            }
        }.load();


        new DataLoader(model, connection, type, "?entry rdf:type :Entry; :classifiedWith ?classifier",
                "entry_classifiers(entry, classifier)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("entry", "http://nextprot.org/rdf/entry/", entries, mode));
                setValue(2, getID("classifier", "http://nextprot.org/rdf/terminology/", terms, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?entry rdf:type :Entry; :gene ?gene", "entry_genes(entry, gene)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("entry", "http://nextprot.org/rdf/entry/", entries, mode));
                setValue(2, getID("gene", "http://nextprot.org/rdf/gene/", genes, mode));
            }
        }.load();


        new DataLoader(model, connection, type,
                "?entry rdf:type :Entry; :reference ?reference filter(isIRI(?reference))",
                "entry_publication_references(entry, publication)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("entry", "http://nextprot.org/rdf/entry/", entries, mode));
                setValue(2, getIntID("reference", "http://nextprot.org/rdf/publication/"));
            }
        }.load();


        new DataLoader(model, connection, type, "?entry rdf:type :Entry; :recommendedName ?name",
                "entry_recommended_names(entry, name)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("entry", "http://nextprot.org/rdf/entry/", entries, mode));
                setValue(2, names.get(getBlankNode("name")));
            }
        }.load();


        new DataLoader(model, connection, type, "?entry rdf:type :Entry; :alternativeName ?name",
                "entry_alternative_names(entry, name)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("entry", "http://nextprot.org/rdf/entry/", entries, mode));
                setValue(2, names.get(getBlankNode("name")));
            }
        }.load();


        new DataLoader(model, connection, type, "?entry :additionalNames ?nameList",
                "entry_additional_names(entry, name_list)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("entry", "http://nextprot.org/rdf/entry/", entries, mode));
                setValue(2, nameLists.get(getBlankNode("nameList")));
            }
        }.load();


        new DataLoader(model, connection, type, "?entry :cleavedRegionNames ?nameList",
                "entry_cleaved_region_names(entry, name_list)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("entry", "http://nextprot.org/rdf/entry/", entries, mode));
                setValue(2, nameLists.get(getBlankNode("nameList")));
            }
        }.load();


        new DataLoader(model, connection, type, "?entry :fonctionalRegionNames ?nameList",
                "entry_functional_region_names(entry, name_list)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("entry", "http://nextprot.org/rdf/entry/", entries, mode));
                setValue(2, nameLists.get(getBlankNode("nameList")));
            }
        }.load();
    }


    private static void loadProteoforms(Model model, Connection connection, Server type, Mode mode)
            throws IOException, SQLException
    {
        // @formatter:off
        new DataLoader(model, connection, type,
                "?proteoform rdf:type :Proteoform optional { ?proteoform rdfs:label ?label }",
                "proteoform_bases(id, label" + (mode == Mode.INDIRECT ? ", iri" : "") + ")")
        // @formatter:on
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("proteoform", "http://nextprot.org/rdf/proteoform/", proteoforms, mode));
                setValue(2, getLiteralValue("label"));

                if(mode == Mode.INDIRECT)
                    setValue(3, getStringID("proteoform", "http://nextprot.org/rdf/proteoform/"));
            }
        }.load();


        new DataLoader(model, connection, type, "?proteoform rdf:type :Proteoform; :generalAnnotation ?annotation",
                "proteoform_general_annotations (proteoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("proteoform", "http://nextprot.org/rdf/proteoform/", proteoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?proteoform rdf:type :Proteoform; :genericPhenotype ?annotation",
                "proteoform_generic_phenotypes (proteoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("proteoform", "http://nextprot.org/rdf/proteoform/", proteoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?proteoform rdf:type :Proteoform; :modification ?annotation",
                "proteoform_modifications (proteoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("proteoform", "http://nextprot.org/rdf/proteoform/", proteoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?proteoform rdf:type :Proteoform; :phenotypicVariation ?annotation",
                "proteoform_phenotypic_variations (proteoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("proteoform", "http://nextprot.org/rdf/proteoform/", proteoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?proteoform rdf:type :Proteoform; :positionalAnnotation ?annotation",
                "proteoform_positional_annotations (proteoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("proteoform", "http://nextprot.org/rdf/proteoform/", proteoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?proteoform rdf:type :Proteoform; :diseaseRelatedVariant ?annotation",
                "proteoform_disease_related_variants (proteoform, annotation)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("proteoform", "http://nextprot.org/rdf/proteoform/", proteoforms, mode));
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
            }
        }.load();
    }


    private static void loadGenes(Model model, Connection connection, Server type, Mode mode)
            throws IOException, SQLException
    {
        // @formatter:off
        new DataLoader(model, connection, type,
                "?gene rdf:type :Gene "
                        + "optional { ?gene :begin ?begin }"
                        + "optional { ?gene :end ?end }"
                        + "optional { ?gene :length ?length }"
                        + "optional { ?gene :band ?band }"
                        + "optional { ?gene :chromosome ?chromosome }"
                        + "optional { ?gene :strand ?strand }",
                "gene_bases(id, gene_begin, gene_end, length, band, chromosome, strand" + (mode == Mode.INDIRECT ? ", iri" : "") + ")")
        // @formatter:on
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("gene", "http://nextprot.org/rdf/gene/", genes, mode));
                setValue(2, getIntValue("begin"));
                setValue(3, getIntValue("end"));
                setValue(4, getIntValue("length"));
                setValue(5, getLiteralValue("band"));
                setValue(6, getLiteralValue("chromosome"));
                setValue(7, getLiteralValue("strand"));

                if(mode == Mode.INDIRECT)
                    setValue(8, getStringID("gene", "http://nextprot.org/rdf/gene/"));
            }
        }.load();


        new DataLoader(model, connection, type, "?gene rdf:type :Gene; :bestGeneMapping ?mapping",
                "gene_best_mappings(gene, mapping)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("gene", "http://nextprot.org/rdf/gene/", genes, mode));
                setValue(2, getID("mapping", "http://nextprot.org/rdf/entry/", entries, mode));
            }
        }.load();


        new DataLoader(model, connection, type, "?gene rdf:type :Gene; :name ?name", "gene_names(gene, name)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, getID("gene", "http://nextprot.org/rdf/gene/", genes, mode));
                setValue(2, getLiteralValue("name"));
            }
        }.load();
    }


    private static void loadProteinSequences(Model model, Connection connection, Server type, Mode mode)
            throws IOException, SQLException
    {
        // @formatter:off
        new DataLoader(model, connection, type,
                "?isoform :sequence ?sequence. ?sequence rdf:type :ProteinSequence. "
                        + "optional { ?sequence :molecularWeight ?molecularWeight }"
                        + "optional { ?sequence :isoelectricPoint ?isoelectricPoint }"
                        + "optional { ?sequence :length ?length }"
                        + "optional { ?sequence :chain ?chain }",
                "protein_sequence_bases(id, molecular_weight, isoelectric_point, isoform, length, chain)")
        // @formatter:on
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, sequenceId++);
                setValue(2, getDoubleValue("molecularWeight"));
                setValue(3, getDoubleValue("isoelectricPoint"));
                setValue(4, getID("isoform", "http://nextprot.org/rdf/isoform/", isoforms, mode));
                setValue(5, getIntValue("length"));
                setValue(6, getLiteralValue("chain"));
            }
        }.load();
    }


    private static void loadProteinFamilyInfos(Model model, Connection connection, Server type, Mode mode)
            throws IOException, SQLException
    {
        // @formatter:off
        new DataLoader(model, connection, type,
                "?entry :family ?family. ?family rdf:type :FamilyInfo. "
                        + "optional { ?family :term ?term }"
                        + "optional { ?family :region ?region }"
                        + "optional { ?family :description ?description }",
                "family_info_bases(id, entry, term, region, description)")
        // @formatter:on
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, familyId++);
                setValue(2, getID("entry", "http://nextprot.org/rdf/entry/", entries, mode));
                setValue(3, getID("term", "http://nextprot.org/rdf/terminology/", terms, mode));
                setValue(4, getLiteralValue("region"));
                setValue(5, getLiteralValue("description"));
            }
        }.load();
    }


    private static void loadHistories(Model model, Connection connection, Server type, Mode mode)
            throws IOException, SQLException
    {
        // @formatter:off
        new DataLoader(model, connection, type,
                "?entry :history ?history. ?history rdf:type :History. "
                        + "optional { ?history :integrated ?integrated }"
                        + "optional { ?history :updated ?updated }"
                        + "optional { ?history :lastSequenceUpdate ?lastSequenceUpdate }"
                        + "optional { ?history :version ?version }"
                        + "optional { ?history :sequenceVersion ?sequenceVersion }"
                        + "optional { ?history :name ?name }",
                "history_bases(id, entry, integrated, updated, last_sequence_update, version, "
                        + "sequence_version, name)")
        // @formatter:on
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, historyId++);
                setValue(2, getID("entry", "http://nextprot.org/rdf/entry/", entries, mode));
                setValue(3, getLiteralValue("integrated"));
                setValue(4, getLiteralValue("updated"));
                setValue(5, getLiteralValue("lastSequenceUpdate"));
                setValue(6, getLiteralValue("version"));
                setValue(7, getLiteralValue("sequenceVersion"));
                setValue(8, getLiteralValue("name"));
            }
        }.load();
    }


    private static void loadIdentifiers(Model model, Connection connection, Server type, Mode mode)
            throws IOException, SQLException
    {
        // @formatter:off
        new DataLoader(model, connection, type,
                "?entry :reference ?identifier. ?identifier rdf:type :Identifier. "
                        + "optional { ?identifier :provenance ?provenance }"
                        + "optional { ?identifier :accession ?accession }",
                "identifier_bases(id, entry, provenance, accession)")
        // @formatter:on
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, identifierId++);
                setValue(2, getID("entry", "http://nextprot.org/rdf/entry/", entries, mode));
                setValue(3, getID("provenance", "http://nextprot.org/rdf/db/", databases, mode));
                setValue(4, getLiteralValue("accession"));
            }
        }.load();
    }


    private static void loadEntryXrefs(Model model, Connection connection, Server type, Mode mode)
            throws IOException, SQLException
    {
        // @formatter:off
        new DataLoader(model, connection, type,
                "?entry rdf:type :Entry; :reference ?ref. ?ref rdf:type :Xref. "
                        + "optional { ?ref :provenance ?provenance }"
                        + "optional { ?ref :accession ?accession }",
                "entry_xref_bases(id, entry, provenance, accession)")
        // @formatter:on
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, entryXrefId++);
                setValue(2, getID("entry", "http://nextprot.org/rdf/entry/", entries, mode));
                setValue(3, getID("provenance", "http://nextprot.org/rdf/db/", databases, mode));
                setValue(4, getLiteralValue("accession"));
            }
        }.load();
    }


    private static void loadEvidenceXrefs(Model model, Connection connection, Server type, Mode mode)
            throws IOException, SQLException
    {
        // @formatter:off
        new DataLoader(model, connection, type,
                "?evidence rdf:type :Evidence; :reference ?ref. ?ref rdf:type :Xref. "
                        + "optional { ?ref :provenance ?provenance }"
                        + "optional { ?ref :accession ?accession }",
                "evidence_xref_bases(id, evidence, provenance, accession)")
        // @formatter:on
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, evidenceXrefId++);
                setValue(2, getID("evidence", "http://nextprot.org/rdf/evidence/", evidences, mode));
                setValue(3, getID("provenance", "http://nextprot.org/rdf/db/", databases, mode));
                setValue(4, getLiteralValue("accession"));
            }
        }.load();
    }


    private static void loadChebiXrefs(Model model, Connection connection, Server type, Mode mode)
            throws IOException, SQLException
    {
        // @formatter:off
        new DataLoader(model, connection, type,
                "?annotation :interactant ?ref. ?ref rdf:type :Xref. ?ref :provenance db:ChEBI. "
                        + "optional { ?ref skos:exactMatch ?chebi }"
                        + "optional { ?ref :accession ?accession }"
                        + "optional { ?ref rdfs:label ?label }",
                "chebi_xref_bases(id, chebi, annotation, accession, label)")
        // @formatter:on
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, chebiXrefId++);
                setValue(2, getIntID("chebi", "http://purl.obolibrary.org/obo/CHEBI_"));
                setValue(3, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
                setValue(4, getLiteralValue("accession"));
                setValue(5, getLiteralValue("label"));
            }
        }.load();
    }


    private static void loadDrugBankXrefs(Model model, Connection connection, Server type, Mode mode)
            throws IOException, SQLException
    {
        // @formatter:off
        new DataLoader(model, connection, type,
                "?annotation :interactant ?ref. ?ref rdf:type :Xref. ?ref :provenance db:DrugBank. "
                        + "optional { ?ref skos:exactMatch ?drugbank }"
                        + "optional { ?ref :accession ?accession }"
                        + "optional { ?ref rdfs:label ?label }",
                "drugbank_xref_bases(id, annotation, drugbank, accession, label)")
        // @formatter:on
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, drugBankXrefId++);
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
                setValue(3, getStringID("drugbank",
                        "http://wifo5-04.informatik.uni-mannheim.de/drugbank/resource/drugs/DB"));
                setValue(4, getLiteralValue("accession"));
                setValue(5, getLiteralValue("label"));
            }
        }.load();
    }


    private static void loadUniProtXrefs(Model model, Connection connection, Server type, Mode mode)
            throws IOException, SQLException
    {
        // @formatter:off
        new DataLoader(model, connection, type,
                "?annotation :interactant ?ref. ?ref rdf:type :Xref. ?ref :provenance db:UniProtKB. "
                        + "optional { ?ref skos:exactMatch ?uniprot }"
                        + "optional { ?ref :accession ?accession }"
                        + "optional { ?ref rdfs:label ?label }",
                "uniprot_xref_bases(id, annotation, uniprot, accession, label)")
        // @formatter:on
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, uniProtXrefId++);
                setValue(2, getID("annotation", "http://nextprot.org/rdf/annotation/", annotations, mode));
                setValue(3, getStringID("uniprot", "http://purl.uniprot.org/uniprot/"));
                setValue(4, getLiteralValue("accession"));
                setValue(5, getLiteralValue("label"));
            }
        }.load();
    }


    private static void loadNames(Model model, Connection connection, Server type, Mode mode)
            throws IOException, SQLException
    {
        // @formatter:off
        new DataLoader(model, connection, type,
                "?name rdf:type :Name "
                        + "optional { ?name :fullName ?fullName }"
                        + "optional { ?name :shortName ?shortName }"
                        + "optional { ?name :fullRegionName ?fullRegionName }"
                        + "optional { ?name :shortRegionName ?shortRegionName }"
                        + "optional { ?name :ecEnzymeName ?ecEnzymeName }"
                        + "optional { ?name :cdAntigenCdAntigen ?cdAntigen }"
                        + "optional { ?name :innInternationalNonproprietaryNames ?innName }"
                        + "optional { ?name :allergenAllergen ?allergen }",
                "name_bases(id, full_name, short_name, full_region_name, short_region_name, "
                        + "ec_enzyme_name, cd_antigen, inn_name, allergen)")
        // @formatter:on
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                names.put(getBlankNode("name"), nameId);

                setValue(1, nameId++);
                setValue(2, getLiteralValue("fullName"));
                setValue(3, getLiteralValue("shortName"));
                setValue(4, getLiteralValue("fullRegionName"));
                setValue(5, getLiteralValue("shortRegionName"));
                setValue(6, getLiteralValue("ecEnzymeName"));
                setValue(7, getLiteralValue("cdAntigen"));
                setValue(8, getLiteralValue("innName"));
                setValue(9, getLiteralValue("allergen"));
            }
        }.load();
    }


    private static void loadNameList(Model model, Connection connection, Server type, Mode mode)
            throws IOException, SQLException
    {
        new DataLoader(model, connection, type, "?list rdf:type :NameList", "name_list_bases(id)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                nameLists.put(getBlankNode("list"), nameListId);

                setValue(1, nameListId++);
            }
        }.load();


        new DataLoader(model, connection, type, "?list rdf:type :NameList; :recommendedName ?name",
                "name_list_recommended_names(list, name)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, nameLists.get(getBlankNode("list")));
                setValue(2, names.get(getBlankNode("name")));
            }
        }.load();


        new DataLoader(model, connection, type, "?list rdf:type :NameList; :alternativeName ?name",
                "name_list_alternative_names(list, name)")
        {
            @Override
            public void insert() throws SQLException, IOException
            {
                setValue(1, nameLists.get(getBlankNode("list")));
                setValue(2, names.get(getBlankNode("name")));
            }
        }.load();
    }


    public static void load(String path, String file, Connection connection, Server type, Mode mode)
            throws IOException, SQLException
    {
        names.clear();
        nameLists.clear();

        Model model = DataLoader.getModel(path, file);

        loadNames(model, connection, type, mode);
        loadNameList(model, connection, type, mode);

        loadIsomorfs(model, connection, type, mode);
        loadAnnotations(model, connection, type, mode);
        loadEvidences(model, connection, type, mode);
        loadEntries(model, connection, type, mode);
        loadProteoforms(model, connection, type, mode);
        loadGenes(model, connection, type, mode);
        loadProteinSequences(model, connection, type, mode);
        loadProteinFamilyInfos(model, connection, type, mode);
        loadHistories(model, connection, type, mode);
        loadIdentifiers(model, connection, type, mode);
        loadEntryXrefs(model, connection, type, mode);
        loadEvidenceXrefs(model, connection, type, mode);
        loadChebiXrefs(model, connection, type, mode);
        loadDrugBankXrefs(model, connection, type, mode);
        loadUniProtXrefs(model, connection, type, mode);

        model.close();
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
