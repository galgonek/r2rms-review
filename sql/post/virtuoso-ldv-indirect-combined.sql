sparql
prefix : <http://nextprot.org/rdf#>
prefix evidence: <http://nextprot.org/rdf/evidence/>
prefix chebi: <http://purl.obolibrary.org/obo/>
prefix owl: <http://www.w3.org/2002/07/owl#>
prefix xsd: <http://www.w3.org/2001/XMLSchema#>
prefix skos: <http://www.w3.org/2004/02/skos/core#>
prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>
prefix source: <http://nextprot.org/rdf/source/>
prefix unipage: <http://www.uniprot.org/uniprot/>
prefix publication: <http://nextprot.org/rdf/publication/>
prefix context: <http://nextprot.org/rdf/context/>
prefix up: <http://purl.uniprot.org/core/>
prefix annotation: <http://nextprot.org/rdf/annotation/>
prefix proteoform: <http://nextprot.org/rdf/proteoform/>
prefix identifier: <http://nextprot.org/rdf/identifier/>
prefix xref: <http://nextprot.org/rdf/xref/>
prefix gene: <http://nextprot.org/rdf/gene/>
prefix drugbankdrugs: <http://wifo5-04.informatik.uni-mannheim.de/drugbank/resource/drugs/>
prefix entry: <http://nextprot.org/rdf/entry/>
prefix cv: <http://nextprot.org/rdf/terminology/>
prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
prefix interaction: <http://nextprot.org/rdf/interaction/>
prefix uniprot: <http://purl.uniprot.org/uniprot/>
prefix isoform: <http://nextprot.org/rdf/isoform/>
prefix db: <http://nextprot.org/rdf/db/>
prefix dc: <http://purl.org/dc/elements/1.1/>
prefix iri: <http://www.openlinksw.com/schemas/virtrdf/combined#>

create iri class iri:uniprot "http://purl.uniprot.org/uniprot/%U"(in id varchar not null) option (bijection).
create iri class iri:uniprotpage "http://www.uniprot.org/uniprot/%U"(in id varchar not null) option (bijection).
create iri class iri:chebi "http://purl.obolibrary.org/obo/CHEBI_%d"(in id integer not null) option (bijection).
create iri class iri:drugbank "http://wifo5-04.informatik.uni-mannheim.de/drugbank/resource/drugs/DB%U"(in id varchar not null) option (bijection).
create iri class iri:isoform "http://nextprot.org/rdf/isoform/%U"(in id varchar not null) option (bijection).
create iri class iri:annotation "http://nextprot.org/rdf/annotation/%U"(in id varchar not null) option (bijection).
create iri class iri:evidence "http://nextprot.org/rdf/evidence/%U"(in id varchar not null) option (bijection).
create iri class iri:entry "http://nextprot.org/rdf/entry/%U"(in id varchar not null) option (bijection).
create iri class iri:proteoform "http://nextprot.org/rdf/proteoform/%U"(in id varchar not null) option (bijection).
create iri class iri:gene "http://nextprot.org/rdf/gene/%U"(in id varchar not null) option (bijection).
create iri class iri:context "http://nextprot.org/rdf/context/%d"(in id integer not null) option (bijection).
create iri class iri:publication "http://nextprot.org/rdf/publication/%d"(in id integer not null) option (bijection).
create iri class iri:source "http://nextprot.org/rdf/source/%U"(in id varchar not null) option (bijection).
create iri class iri:database "http://nextprot.org/rdf/db/%U"(in id varchar not null) option (bijection).
create iri class iri:schema "http://nextprot.org/rdf#%U"(in id varchar not null) option (bijection).
create iri class iri:terminology "http://nextprot.org/rdf/terminology/%U"(in id varchar not null) option (bijection).
create iri class iri:blanknode1 "_:blanknode1#%d"(in id integer not null) option (bijection).
create iri class iri:blanknode2 "_:blanknode2#%d"(in id integer not null) option (bijection).
create iri class iri:blanknode3 "_:blanknode3#%d"(in id integer not null) option (bijection).
create iri class iri:blanknode4 "_:blanknode4#%d"(in id integer not null) option (bijection).
create iri class iri:blanknode5 "_:blanknode5#%d"(in id integer not null) option (bijection).
create iri class iri:blanknode6 "_:blanknode6#%d"(in id integer not null) option (bijection).
create iri class iri:blanknode7 "_:blanknode7#%d"(in id integer not null) option (bijection).
create iri class iri:blanknode8 "_:blanknode8#%d"(in id integer not null) option (bijection).
create iri class iri:blanknode9 "_:blanknode9#%d"(in id integer not null) option (bijection).
create iri class iri:blanknode10 "_:blanknode10#%d"(in id integer not null) option (bijection).
create iri class iri:blanknode11 "_:blanknode11#%d"(in id integer not null) option (bijection).
create iri class iri:blanknode12 "_:blanknode12#%d"(in id integer not null) option (bijection).
create iri class iri:blanknode13 "_:blanknode13#%d"(in id integer not null) option (bijection).

create quad storage virtrdf:NeXtProtCombinedQuadStorage
  from nextprot.nextprot.isoform_calcium_binding_regions as isoform_calcium_binding_regions
  from nextprot.nextprot.isoform_compositionally_biased_regions as isoform_compositionally_biased_regions
  from nextprot.nextprot.isoform_turns as isoform_turns
  from nextprot.nextprot.source_bases as source_bases
  from nextprot.nextprot.isoform_coiled_coil_regions as isoform_coiled_coil_regions
  from nextprot.nextprot.isoform_short_sequence_motifs as isoform_short_sequence_motifs
  from nextprot.nextprot.isoform_small_molecule_interactions as isoform_small_molecule_interactions
  from nextprot.nextprot.isoform_disulfide_bonds as isoform_disulfide_bonds
  from nextprot.nextprot.isoform_binding_sites as isoform_binding_sites
  from nextprot.nextprot.isoform_regions as isoform_regions
  from nextprot.nextprot.name_list_recommended_names as name_list_recommended_names
  from nextprot.nextprot.proteoform_disease_related_variants as proteoform_disease_related_variants
  from nextprot.nextprot.isoform_go_biological_processs as isoform_go_biological_processs
  from nextprot.nextprot.uniprot_xref_bases as uniprot_xref_bases
  from nextprot.nextprot.isoform_processing_products as isoform_processing_products
  from nextprot.nextprot.isoform_interactions as isoform_interactions
  from nextprot.nextprot.isoform_keywords as isoform_keywords
  from nextprot.nextprot.annotation_entry_interactants as annotation_entry_interactants
  from nextprot.nextprot.isoform_uniprot_keywords as isoform_uniprot_keywords
  from nextprot.nextprot.annotation_diseases as annotation_diseases
  from nextprot.nextprot.isoform_expressions as isoform_expressions
  from nextprot.nextprot.entry_xref_bases as entry_xref_bases
  from nextprot.nextprot.schema_restrictions as schema_restrictions
  from nextprot.nextprot.isoform_beta_strands as isoform_beta_strands
  from nextprot.nextprot.isoform_medicals as isoform_medicals
  from nextprot.nextprot.entry_alternative_names as entry_alternative_names
  from nextprot.nextprot.isoform_general_annotations as isoform_general_annotations
  from nextprot.nextprot.entry_bases as entry_bases
  from nextprot.nextprot.annotation_peptide_sets as annotation_peptide_sets
  from nextprot.nextprot.publication_links as publication_links
  from nextprot.nextprot.isoform_nucleotide_phosphate_binding_regions as isoform_nucleotide_phosphate_binding_regions
  from nextprot.nextprot.isoform_selenocysteines as isoform_selenocysteines
  from nextprot.nextprot.entry_genes as entry_genes
  from nextprot.nextprot.entry_functional_region_names as entry_functional_region_names
  from nextprot.nextprot.isoform_mitochondrial_transit_peptides as isoform_mitochondrial_transit_peptides
  from nextprot.nextprot.isoform_ptms as isoform_ptms
  from nextprot.nextprot.terminology_related_terms as terminology_related_terms
  from nextprot.nextprot.isoform_undetected_expressions as isoform_undetected_expressions
  from nextprot.nextprot.isoform_interaction_mappings as isoform_interaction_mappings
  from nextprot.nextprot.isoform_topologies as isoform_topologies
  from nextprot.nextprot.isoform_miscellaneous_regions as isoform_miscellaneous_regions
  from nextprot.nextprot.gene_best_mappings as gene_best_mappings
  from nextprot.nextprot.entry_additional_names as entry_additional_names
  from nextprot.nextprot.isoform_binary_interactions as isoform_binary_interactions
  from nextprot.nextprot.isoform_helixs as isoform_helixs
  from nextprot.nextprot.isoform_propeptides as isoform_propeptides
  from nextprot.nextprot.evidence_xref_bases as evidence_xref_bases
  from nextprot.nextprot.isoform_modified_residues as isoform_modified_residues
  from nextprot.nextprot.isoform_pdb_mappings as isoform_pdb_mappings
  from nextprot.nextprot.isoform_antibody_mappings as isoform_antibody_mappings
  from nextprot.nextprot.annotation_isoform_specificities as annotation_isoform_specificities
  from nextprot.nextprot.annotation_negative_evidences as annotation_negative_evidences
  from nextprot.nextprot.isoform_pathways as isoform_pathways
  from nextprot.nextprot.name_list_bases as name_list_bases
  from nextprot.nextprot.isoform_high_expressions as isoform_high_expressions
  from nextprot.nextprot.database_categories as database_categories
  from nextprot.nextprot.publication_editors as publication_editors
  from nextprot.nextprot.isoform_proteoforms as isoform_proteoforms
  from nextprot.nextprot.terminology_labels as terminology_labels
  from nextprot.nextprot.isoform_medium_expressions as isoform_medium_expressions
  from nextprot.nextprot.isoform_electrophysiological_parameters as isoform_electrophysiological_parameters
  from nextprot.nextprot.annotation_comments as annotation_comments
  from nextprot.nextprot.proteoform_modifications as proteoform_modifications
  from nextprot.nextprot.name_list_alternative_names as name_list_alternative_names
  from nextprot.nextprot.isoform_cofactors as isoform_cofactors
  from nextprot.nextprot.isoform_miscellaneous_sites as isoform_miscellaneous_sites
  from nextprot.nextprot.annotation_evidences as annotation_evidences
  from nextprot.nextprot.isoform_transport_activities as isoform_transport_activities
  from nextprot.nextprot.isoform_bases as isoform_bases
  from nextprot.nextprot.isoform_active_sites as isoform_active_sites
  from nextprot.nextprot.isoform_low_expressions as isoform_low_expressions
  from nextprot.nextprot.isoform_secondary_structures as isoform_secondary_structures
  from nextprot.nextprot.isoform_miscellaneouss as isoform_miscellaneouss
  from nextprot.nextprot.isoform_subcellular_location_notes as isoform_subcellular_location_notes
  from nextprot.nextprot.isoform_functions as isoform_functions
  from nextprot.nextprot.schema_bases as schema_bases
  from nextprot.nextprot.proteoform_phenotypic_variations as proteoform_phenotypic_variations
  from nextprot.nextprot.isoform_cleavage_sites as isoform_cleavage_sites
  from nextprot.nextprot.isoform_go_cellular_components as isoform_go_cellular_components
  from nextprot.nextprot.isoform_go_molecular_functions as isoform_go_molecular_functions
  from nextprot.nextprot.isoform_intramembrane_regions as isoform_intramembrane_regions
  from nextprot.nextprot.entry_classifiers as entry_classifiers
  from nextprot.nextprot.entry_publication_references as entry_publication_references
  from nextprot.nextprot.isoform_sites as isoform_sites
  from nextprot.nextprot.isoform_inductions as isoform_inductions
  from nextprot.nextprot.isoform_diseases as isoform_diseases
  from nextprot.nextprot.isoform_function_infos as isoform_function_infos
  from nextprot.nextprot.isoform_expression_infos as isoform_expression_infos
  from nextprot.nextprot.entry_cleaved_region_names as entry_cleaved_region_names
  from nextprot.nextprot.evidence_publication_references as evidence_publication_references
  from nextprot.nextprot.proteoform_positional_annotations as proteoform_positional_annotations
  from nextprot.nextprot.isoform_cofactor_infos as isoform_cofactor_infos
  from nextprot.nextprot.entry_recommended_names as entry_recommended_names
  from nextprot.nextprot.protein_sequence_bases as protein_sequence_bases
  from nextprot.nextprot.terminology_bases as terminology_bases
  from nextprot.nextprot.annotation_isoform_interactants as annotation_isoform_interactants
  from nextprot.nextprot.isoform_zinc_finger_regions as isoform_zinc_finger_regions
  from nextprot.nextprot.drugbank_xref_bases as drugbank_xref_bases
  from nextprot.nextprot.isoform_variants as isoform_variants
  from nextprot.nextprot.family_info_bases as family_info_bases
  from nextprot.nextprot.isoform_interaction_infos as isoform_interaction_infos
  from nextprot.nextprot.terminology_parents as terminology_parents
  from nextprot.nextprot.proteoform_bases as proteoform_bases
  from nextprot.nextprot.identifier_bases as identifier_bases
  from nextprot.nextprot.isoform_activity_regulations as isoform_activity_regulations
  from nextprot.nextprot.isoform_biophysicochemical_properties as isoform_biophysicochemical_properties
  from nextprot.nextprot.isoform_mutagenesiss as isoform_mutagenesiss
  from nextprot.nextprot.isoform_topological_domains as isoform_topological_domains
  from nextprot.nextprot.isoform_cellular_components as isoform_cellular_components
  from nextprot.nextprot.isoform_peptide_mappings as isoform_peptide_mappings
  from nextprot.nextprot.isoform_glycosylation_sites as isoform_glycosylation_sites
  from nextprot.nextprot.isoform_srm_peptide_mappings as isoform_srm_peptide_mappings
  from nextprot.nextprot.isoform_lipidation_sites as isoform_lipidation_sites
  from nextprot.nextprot.isoform_positional_annotations as isoform_positional_annotations
  from nextprot.nextprot.schema_thing_subclasses as schema_thing_subclasses
  from nextprot.nextprot.proteoform_generic_phenotypes as proteoform_generic_phenotypes
  from nextprot.nextprot.isoform_repeats as isoform_repeats
  from nextprot.nextprot.isoform_cautions as isoform_cautions
  from nextprot.nextprot.evidence_bases as evidence_bases
  from nextprot.nextprot.gene_bases as gene_bases
  from nextprot.nextprot.isoform_metal_binding_sites as isoform_metal_binding_sites
  from nextprot.nextprot.isoform_enzyme_classifications as isoform_enzyme_classifications
  from nextprot.nextprot.publication_bases as publication_bases
  from nextprot.nextprot.annotation_bases as annotation_bases
  from nextprot.nextprot.isoform_catalytic_activities as isoform_catalytic_activities
  from nextprot.nextprot.isoform_detected_expressions as isoform_detected_expressions
  from nextprot.nextprot.isoform_variant_infos as isoform_variant_infos
  from nextprot.nextprot.history_bases as history_bases
  from nextprot.nextprot.database_bases as database_bases
  from nextprot.nextprot.isoform_transmembrane_regions as isoform_transmembrane_regions
  from nextprot.nextprot.schema_classes as schema_classes
  from nextprot.nextprot.isoform_non_terminal_residues as isoform_non_terminal_residues
  from nextprot.nextprot.isoform_ptm_infos as isoform_ptm_infos
  from nextprot.nextprot.isoform_expression_profiles as isoform_expression_profiles
  from nextprot.nextprot.isoform_sequence_cautions as isoform_sequence_cautions
  from nextprot.nextprot.isoform_dna_binding_regions as isoform_dna_binding_regions
  from nextprot.nextprot.gene_names as gene_names
  from nextprot.nextprot.isoform_interacting_regions as isoform_interacting_regions
  from nextprot.nextprot.proteoform_general_annotations as proteoform_general_annotations
  from nextprot.nextprot.chebi_xref_bases as chebi_xref_bases
  from nextprot.nextprot.name_bases as name_bases
  from nextprot.nextprot.schema_parent_classes as schema_parent_classes
  from nextprot.nextprot.isoform_sequence_conflicts as isoform_sequence_conflicts
  from nextprot.nextprot.schema_related_terms as schema_related_terms
  from nextprot.nextprot.isoform_cross_links as isoform_cross_links
  from nextprot.nextprot.isoform_domains as isoform_domains
  from nextprot.nextprot.publication_authors as publication_authors
  from nextprot.nextprot.isoform_mature_proteins as isoform_mature_proteins
  from nextprot.nextprot.isoform_mappings as isoform_mappings
  from nextprot.nextprot.database_comments as database_comments
  from nextprot.nextprot.isoform_subcellular_locations as isoform_subcellular_locations
  from nextprot.nextprot.isoform_developmental_stage_infos as isoform_developmental_stage_infos
  from nextprot.nextprot.isoform_domain_infos as isoform_domain_infos
  from nextprot.nextprot.context_bases as context_bases
  from nextprot.nextprot.terminology_bases as terminology_bases2
  from nextprot.nextprot.annotation_bases as annotation_bases2
  from nextprot.nextprot.schema_bases as schema_bases2
{
  create virtrdf:NeXtProtCombined as graph iri ("http://nextprot.org/rdf")
  {
    iri:isoform(isoform_bases.iri) rdf:type :Isoform .
    iri:isoform(isoform_bases.iri) :canonicalIsoform isoform_bases.canonical_isoform .
    iri:isoform(isoform_bases.iri) :swissprotDisplayed isoform_bases.swissprot_displayed .
    iri:isoform(isoform_bases.iri) :absorptionMax iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.absorption_max = ^{annotation_bases.}^.id) .
    iri:isoform(isoform_bases.iri) :absorptionNote iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.absorption_note = ^{annotation_bases.}^.id) .
    iri:isoform(isoform_bases.iri) :allergen iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.allergen = ^{annotation_bases.}^.id) .
    iri:isoform(isoform_bases.iri) :initiatorMethionine iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.initiator_methionine = ^{annotation_bases.}^.id) .
    iri:isoform(isoform_bases.iri) :kineticKM iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.kinetic_k_m = ^{annotation_bases.}^.id) .
    iri:isoform(isoform_bases.iri) :kineticNote iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.kinetic_note = ^{annotation_bases.}^.id) .
    iri:isoform(isoform_bases.iri) :kineticVmax iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.kinetic_vmax = ^{annotation_bases.}^.id) .
    iri:isoform(isoform_bases.iri) :nonConsecutiveResidue iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.non_consecutive_residue = ^{annotation_bases.}^.id) .
    iri:isoform(isoform_bases.iri) :peroxisomeTransitPeptide iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.peroxisome_transit_peptide = ^{annotation_bases.}^.id) .
    iri:isoform(isoform_bases.iri) :pharmaceutical iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.pharmaceutical = ^{annotation_bases.}^.id) .
    iri:isoform(isoform_bases.iri) :phDependence iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.ph_dependence = ^{annotation_bases.}^.id) .
    iri:isoform(isoform_bases.iri) :redoxPotential iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.redox_potential = ^{annotation_bases.}^.id) .
    iri:isoform(isoform_bases.iri) :signalPeptide iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.signal_peptide = ^{annotation_bases.}^.id) .
    iri:isoform(isoform_bases.iri) :temperatureDependence iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.temperature_dependence = ^{annotation_bases.}^.id) .
    iri:isoform(isoform_bases.iri) :proteoform iri:proteoform(proteoform_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_proteoforms.}^.isoform and ^{isoform_proteoforms.}^.proteoform = ^{proteoform_bases.}^.id) option (using isoform_proteoforms) .
    iri:isoform(isoform_bases.iri) :activeSite iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_active_sites.}^.isoform and ^{isoform_active_sites.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_active_sites) .
    iri:isoform(isoform_bases.iri) :activityRegulation iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_activity_regulations.}^.isoform and ^{isoform_activity_regulations.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_activity_regulations) .
    iri:isoform(isoform_bases.iri) :antibodyMapping iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_antibody_mappings.}^.isoform and ^{isoform_antibody_mappings.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_antibody_mappings) .
    iri:isoform(isoform_bases.iri) :betaStrand iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_beta_strands.}^.isoform and ^{isoform_beta_strands.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_beta_strands) .
    iri:isoform(isoform_bases.iri) :binaryInteraction iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_binary_interactions.}^.isoform and ^{isoform_binary_interactions.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_binary_interactions) .
    iri:isoform(isoform_bases.iri) :bindingSite iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_binding_sites.}^.isoform and ^{isoform_binding_sites.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_binding_sites) .
    iri:isoform(isoform_bases.iri) :biophysicochemicalProperty iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_biophysicochemical_properties.}^.isoform and ^{isoform_biophysicochemical_properties.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_biophysicochemical_properties) .
    iri:isoform(isoform_bases.iri) :calciumBindingRegion iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_calcium_binding_regions.}^.isoform and ^{isoform_calcium_binding_regions.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_calcium_binding_regions) .
    iri:isoform(isoform_bases.iri) :catalyticActivity iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_catalytic_activities.}^.isoform and ^{isoform_catalytic_activities.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_catalytic_activities) .
    iri:isoform(isoform_bases.iri) :caution iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_cautions.}^.isoform and ^{isoform_cautions.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_cautions) .
    iri:isoform(isoform_bases.iri) :cellularComponent iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_cellular_components.}^.isoform and ^{isoform_cellular_components.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_cellular_components) .
    iri:isoform(isoform_bases.iri) :cleavageSite iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_cleavage_sites.}^.isoform and ^{isoform_cleavage_sites.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_cleavage_sites) .
    iri:isoform(isoform_bases.iri) :cofactor iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_cofactors.}^.isoform and ^{isoform_cofactors.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_cofactors) .
    iri:isoform(isoform_bases.iri) :cofactorInfo iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_cofactor_infos.}^.isoform and ^{isoform_cofactor_infos.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_cofactor_infos) .
    iri:isoform(isoform_bases.iri) :coiledCoilRegion iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_coiled_coil_regions.}^.isoform and ^{isoform_coiled_coil_regions.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_coiled_coil_regions) .
    iri:isoform(isoform_bases.iri) :compositionallyBiasedRegion iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_compositionally_biased_regions.}^.isoform and ^{isoform_compositionally_biased_regions.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_compositionally_biased_regions) .
    iri:isoform(isoform_bases.iri) :crossLink iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_cross_links.}^.isoform and ^{isoform_cross_links.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_cross_links) .
    iri:isoform(isoform_bases.iri) :detectedExpression iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_detected_expressions.}^.isoform and ^{isoform_detected_expressions.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_detected_expressions) .
    iri:isoform(isoform_bases.iri) :developmentalStageInfo iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_developmental_stage_infos.}^.isoform and ^{isoform_developmental_stage_infos.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_developmental_stage_infos) .
    iri:isoform(isoform_bases.iri) :disease iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_diseases.}^.isoform and ^{isoform_diseases.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_diseases) .
    iri:isoform(isoform_bases.iri) :disulfideBond iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_disulfide_bonds.}^.isoform and ^{isoform_disulfide_bonds.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_disulfide_bonds) .
    iri:isoform(isoform_bases.iri) :dnaBindingRegion iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_dna_binding_regions.}^.isoform and ^{isoform_dna_binding_regions.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_dna_binding_regions) .
    iri:isoform(isoform_bases.iri) :domain iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_domains.}^.isoform and ^{isoform_domains.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_domains) .
    iri:isoform(isoform_bases.iri) :domainInfo iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_domain_infos.}^.isoform and ^{isoform_domain_infos.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_domain_infos) .
    iri:isoform(isoform_bases.iri) :electrophysiologicalParameter iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_electrophysiological_parameters.}^.isoform and ^{isoform_electrophysiological_parameters.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_electrophysiological_parameters) .
    iri:isoform(isoform_bases.iri) :enzymeClassification iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_enzyme_classifications.}^.isoform and ^{isoform_enzyme_classifications.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_enzyme_classifications) .
    iri:isoform(isoform_bases.iri) :expression iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_expressions.}^.isoform and ^{isoform_expressions.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_expressions) .
    iri:isoform(isoform_bases.iri) :expressionInfo iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_expression_infos.}^.isoform and ^{isoform_expression_infos.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_expression_infos) .
    iri:isoform(isoform_bases.iri) :expressionProfile iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_expression_profiles.}^.isoform and ^{isoform_expression_profiles.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_expression_profiles) .
    iri:isoform(isoform_bases.iri) :function iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_functions.}^.isoform and ^{isoform_functions.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_functions) .
    iri:isoform(isoform_bases.iri) :functionInfo iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_function_infos.}^.isoform and ^{isoform_function_infos.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_function_infos) .
    iri:isoform(isoform_bases.iri) :generalAnnotation iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_general_annotations.}^.isoform and ^{isoform_general_annotations.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_general_annotations) .
    iri:isoform(isoform_bases.iri) :glycosylationSite iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_glycosylation_sites.}^.isoform and ^{isoform_glycosylation_sites.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_glycosylation_sites) .
    iri:isoform(isoform_bases.iri) :goBiologicalProcess iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_go_biological_processs.}^.isoform and ^{isoform_go_biological_processs.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_go_biological_processs) .
    iri:isoform(isoform_bases.iri) :goCellularComponent iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_go_cellular_components.}^.isoform and ^{isoform_go_cellular_components.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_go_cellular_components) .
    iri:isoform(isoform_bases.iri) :goMolecularFunction iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_go_molecular_functions.}^.isoform and ^{isoform_go_molecular_functions.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_go_molecular_functions) .
    iri:isoform(isoform_bases.iri) :helix iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_helixs.}^.isoform and ^{isoform_helixs.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_helixs) .
    iri:isoform(isoform_bases.iri) :induction iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_inductions.}^.isoform and ^{isoform_inductions.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_inductions) .
    iri:isoform(isoform_bases.iri) :interactingRegion iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_interacting_regions.}^.isoform and ^{isoform_interacting_regions.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_interacting_regions) .
    iri:isoform(isoform_bases.iri) :interaction iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_interactions.}^.isoform and ^{isoform_interactions.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_interactions) .
    iri:isoform(isoform_bases.iri) :interactionInfo iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_interaction_infos.}^.isoform and ^{isoform_interaction_infos.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_interaction_infos) .
    iri:isoform(isoform_bases.iri) :intramembraneRegion iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_intramembrane_regions.}^.isoform and ^{isoform_intramembrane_regions.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_intramembrane_regions) .
    iri:isoform(isoform_bases.iri) :keyword iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_keywords.}^.isoform and ^{isoform_keywords.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_keywords) .
    iri:isoform(isoform_bases.iri) :lipidationSite iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_lipidation_sites.}^.isoform and ^{isoform_lipidation_sites.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_lipidation_sites) .
    iri:isoform(isoform_bases.iri) :mapping iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_mappings.}^.isoform and ^{isoform_mappings.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_mappings) .
    iri:isoform(isoform_bases.iri) :matureProtein iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_mature_proteins.}^.isoform and ^{isoform_mature_proteins.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_mature_proteins) .
    iri:isoform(isoform_bases.iri) :medical iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_medicals.}^.isoform and ^{isoform_medicals.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_medicals) .
    iri:isoform(isoform_bases.iri) :metalBindingSite iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_metal_binding_sites.}^.isoform and ^{isoform_metal_binding_sites.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_metal_binding_sites) .
    iri:isoform(isoform_bases.iri) :miscellaneous iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_miscellaneouss.}^.isoform and ^{isoform_miscellaneouss.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_miscellaneouss) .
    iri:isoform(isoform_bases.iri) :miscellaneousRegion iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_miscellaneous_regions.}^.isoform and ^{isoform_miscellaneous_regions.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_miscellaneous_regions) .
    iri:isoform(isoform_bases.iri) :miscellaneousSite iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_miscellaneous_sites.}^.isoform and ^{isoform_miscellaneous_sites.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_miscellaneous_sites) .
    iri:isoform(isoform_bases.iri) :mitochondrialTransitPeptide iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_mitochondrial_transit_peptides.}^.isoform and ^{isoform_mitochondrial_transit_peptides.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_mitochondrial_transit_peptides) .
    iri:isoform(isoform_bases.iri) :modifiedResidue iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_modified_residues.}^.isoform and ^{isoform_modified_residues.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_modified_residues) .
    iri:isoform(isoform_bases.iri) :mutagenesis iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_mutagenesiss.}^.isoform and ^{isoform_mutagenesiss.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_mutagenesiss) .
    iri:isoform(isoform_bases.iri) :nonTerminalResidue iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_non_terminal_residues.}^.isoform and ^{isoform_non_terminal_residues.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_non_terminal_residues) .
    iri:isoform(isoform_bases.iri) :nucleotidePhosphateBindingRegion iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_nucleotide_phosphate_binding_regions.}^.isoform and ^{isoform_nucleotide_phosphate_binding_regions.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_nucleotide_phosphate_binding_regions) .
    iri:isoform(isoform_bases.iri) :pathway iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_pathways.}^.isoform and ^{isoform_pathways.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_pathways) .
    iri:isoform(isoform_bases.iri) :pdbMapping iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_pdb_mappings.}^.isoform and ^{isoform_pdb_mappings.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_pdb_mappings) .
    iri:isoform(isoform_bases.iri) :peptideMapping iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_peptide_mappings.}^.isoform and ^{isoform_peptide_mappings.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_peptide_mappings) .
    iri:isoform(isoform_bases.iri) :positionalAnnotation iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_positional_annotations.}^.isoform and ^{isoform_positional_annotations.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_positional_annotations) .
    iri:isoform(isoform_bases.iri) :processingProduct iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_processing_products.}^.isoform and ^{isoform_processing_products.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_processing_products) .
    iri:isoform(isoform_bases.iri) :propeptide iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_propeptides.}^.isoform and ^{isoform_propeptides.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_propeptides) .
    iri:isoform(isoform_bases.iri) :ptm iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_ptms.}^.isoform and ^{isoform_ptms.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_ptms) .
    iri:isoform(isoform_bases.iri) :ptmInfo iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_ptm_infos.}^.isoform and ^{isoform_ptm_infos.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_ptm_infos) .
    iri:isoform(isoform_bases.iri) :region iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_regions.}^.isoform and ^{isoform_regions.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_regions) .
    iri:isoform(isoform_bases.iri) :repeat iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_repeats.}^.isoform and ^{isoform_repeats.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_repeats) .
    iri:isoform(isoform_bases.iri) :secondaryStructure iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_secondary_structures.}^.isoform and ^{isoform_secondary_structures.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_secondary_structures) .
    iri:isoform(isoform_bases.iri) :selenocysteine iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_selenocysteines.}^.isoform and ^{isoform_selenocysteines.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_selenocysteines) .
    iri:isoform(isoform_bases.iri) :sequenceCaution iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_sequence_cautions.}^.isoform and ^{isoform_sequence_cautions.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_sequence_cautions) .
    iri:isoform(isoform_bases.iri) :sequenceConflict iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_sequence_conflicts.}^.isoform and ^{isoform_sequence_conflicts.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_sequence_conflicts) .
    iri:isoform(isoform_bases.iri) :shortSequenceMotif iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_short_sequence_motifs.}^.isoform and ^{isoform_short_sequence_motifs.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_short_sequence_motifs) .
    iri:isoform(isoform_bases.iri) :site iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_sites.}^.isoform and ^{isoform_sites.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_sites) .
    iri:isoform(isoform_bases.iri) :smallMoleculeInteraction iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_small_molecule_interactions.}^.isoform and ^{isoform_small_molecule_interactions.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_small_molecule_interactions) .
    iri:isoform(isoform_bases.iri) :srmPeptideMapping iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_srm_peptide_mappings.}^.isoform and ^{isoform_srm_peptide_mappings.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_srm_peptide_mappings) .
    iri:isoform(isoform_bases.iri) :subcellularLocation iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_subcellular_locations.}^.isoform and ^{isoform_subcellular_locations.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_subcellular_locations) .
    iri:isoform(isoform_bases.iri) :subcellularLocationNote iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_subcellular_location_notes.}^.isoform and ^{isoform_subcellular_location_notes.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_subcellular_location_notes) .
    iri:isoform(isoform_bases.iri) :topologicalDomain iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_topological_domains.}^.isoform and ^{isoform_topological_domains.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_topological_domains) .
    iri:isoform(isoform_bases.iri) :topology iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_topologies.}^.isoform and ^{isoform_topologies.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_topologies) .
    iri:isoform(isoform_bases.iri) :transmembraneRegion iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_transmembrane_regions.}^.isoform and ^{isoform_transmembrane_regions.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_transmembrane_regions) .
    iri:isoform(isoform_bases.iri) :transportActivity iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_transport_activities.}^.isoform and ^{isoform_transport_activities.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_transport_activities) .
    iri:isoform(isoform_bases.iri) :turn iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_turns.}^.isoform and ^{isoform_turns.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_turns) .
    iri:isoform(isoform_bases.iri) :undetectedExpression iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_undetected_expressions.}^.isoform and ^{isoform_undetected_expressions.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_undetected_expressions) .
    iri:isoform(isoform_bases.iri) :uniprotKeyword iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_uniprot_keywords.}^.isoform and ^{isoform_uniprot_keywords.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_uniprot_keywords) .
    iri:isoform(isoform_bases.iri) :variant iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_variants.}^.isoform and ^{isoform_variants.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_variants) .
    iri:isoform(isoform_bases.iri) :variantInfo iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_variant_infos.}^.isoform and ^{isoform_variant_infos.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_variant_infos) .
    iri:isoform(isoform_bases.iri) :zincFingerRegion iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_zinc_finger_regions.}^.isoform and ^{isoform_zinc_finger_regions.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_zinc_finger_regions) .
    iri:isoform(isoform_bases.iri) :interactionMapping iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_interaction_mappings.}^.isoform and ^{isoform_interaction_mappings.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_interaction_mappings) .
    iri:annotation(annotation_bases.iri) rdf:type iri:schema(schema_bases.iri) where(^{annotation_bases.}^.type = ^{schema_bases.}^.id) .
    iri:annotation(annotation_bases.iri) :quality iri:schema(schema_bases.iri) where(^{annotation_bases.}^.quality = ^{schema_bases.}^.id) .
    iri:annotation(annotation_bases.iri) :term iri:terminology(terminology_bases.iri) where(^{annotation_bases.}^.term = ^{terminology_bases.}^.id) .
    iri:annotation(annotation_bases.iri) :impactedObject iri:annotation(annotation_bases2.iri) where(^{annotation_bases.}^.impacted_object = ^{annotation_bases2.}^.id) .
    iri:annotation(annotation_bases.iri) :entryAnnotationId annotation_bases.entry_annotation where(^{annotation_bases.}^.entry_annotation is not null) .
    iri:annotation(annotation_bases.iri) :variation annotation_bases.variation where(^{annotation_bases.}^.variation is not null) .
    iri:annotation(annotation_bases.iri) :original annotation_bases.original where(^{annotation_bases.}^.original is not null) .
    iri:annotation(annotation_bases.iri) :hgvs annotation_bases.hgvs .
    iri:annotation(annotation_bases.iri) :method annotation_bases.method where(^{annotation_bases.}^.method is not null) .
    iri:annotation(annotation_bases.iri) :peptideUnicity annotation_bases.peptide_unicity where(^{annotation_bases.}^.peptide_unicity is not null) .
    iri:annotation(annotation_bases.iri) :peptideName annotation_bases.peptide_name where(^{annotation_bases.}^.peptide_name is not null) .
    iri:annotation(annotation_bases.iri) :antibodyUnicity annotation_bases.antibody_unicity where(^{annotation_bases.}^.antibody_unicity is not null) .
    iri:annotation(annotation_bases.iri) :antibodyName annotation_bases.antibody_name where(^{annotation_bases.}^.antibody_name is not null) .
    iri:annotation(annotation_bases.iri) :start annotation_bases.position_start where(^{annotation_bases.}^.position_start is not null) .
    iri:annotation(annotation_bases.iri) :end annotation_bases.position_end where(^{annotation_bases.}^.position_end is not null) .
    iri:annotation(annotation_bases.iri) :resolution annotation_bases.resolution where(^{annotation_bases.}^.resolution is not null) .
    iri:annotation(annotation_bases.iri) :proteotypic annotation_bases.proteotypic where(^{annotation_bases.}^.proteotypic is not null) .
    iri:annotation(annotation_bases.iri) :selfInteraction annotation_bases.self_interaction where(^{annotation_bases.}^.self_interaction is not null) .
    iri:annotation(annotation_bases.iri) :evidence iri:evidence(evidence_bases.iri) where(^{annotation_bases.}^.id = ^{annotation_evidences.}^.annotation and ^{annotation_evidences.}^.evidence = ^{evidence_bases.}^.id) option (using annotation_evidences) .
    iri:annotation(annotation_bases.iri) :negativeEvidence iri:evidence(evidence_bases.iri) where(^{annotation_bases.}^.id = ^{annotation_negative_evidences.}^.annotation and ^{annotation_negative_evidences.}^.evidence = ^{evidence_bases.}^.id) option (using annotation_negative_evidences) .
    iri:annotation(annotation_bases.iri) :disease iri:terminology(terminology_bases.iri) where(^{annotation_bases.}^.id = ^{annotation_diseases.}^.annotation and ^{annotation_diseases.}^.disease = ^{terminology_bases.}^.id) option (using annotation_diseases) .
    iri:annotation(annotation_bases.iri) rdfs:comment annotation_comments.comment where(^{annotation_bases.}^.id = ^{annotation_comments.}^.annotation) .
    iri:annotation(annotation_bases.iri) :isoformSpecificity iri:schema(schema_bases.iri) where(^{annotation_bases.}^.id = ^{annotation_isoform_specificities.}^.annotation and ^{annotation_isoform_specificities.}^.specificity = ^{schema_bases.}^.id) option (using annotation_isoform_specificities) .
    iri:annotation(annotation_bases.iri) :interactant iri:entry(entry_bases.iri) where(^{annotation_bases.}^.id = ^{annotation_entry_interactants.}^.annotation and ^{annotation_entry_interactants.}^.interactant = ^{entry_bases.}^.id) option (using annotation_entry_interactants) .
    iri:annotation(annotation_bases.iri) :interactant iri:isoform(isoform_bases.iri) where(^{annotation_bases.}^.id = ^{annotation_isoform_interactants.}^.annotation and ^{annotation_isoform_interactants.}^.interactant = ^{isoform_bases.}^.id) option (using annotation_isoform_interactants) .
    iri:annotation(annotation_bases.iri) :peptideSet annotation_peptide_sets.peptide_set where(^{annotation_bases.}^.id = ^{annotation_peptide_sets.}^.annotation) .
    iri:evidence(evidence_bases.iri) rdf:type :Evidence .
    iri:evidence(evidence_bases.iri) :negative evidence_bases.negative .
    iri:evidence(evidence_bases.iri) :numberOfExperiments evidence_bases.number_of_experiments where(^{evidence_bases.}^.number_of_experiments is not null) .
    iri:evidence(evidence_bases.iri) :evidenceCode iri:terminology(terminology_bases.iri) where(^{evidence_bases.}^.evidence_code = ^{terminology_bases.}^.id) .
    iri:evidence(evidence_bases.iri) :experimentalContext iri:context(evidence_bases.experimental_context) where(^{evidence_bases.}^.experimental_context is not null) .
    iri:evidence(evidence_bases.iri) :assignedBy iri:source(source_bases.iri) where(^{evidence_bases.}^.assigned_by = ^{source_bases.}^.id) .
    iri:evidence(evidence_bases.iri) :expressionLevel iri:schema(schema_bases.iri) where(^{evidence_bases.}^.expression_level = ^{schema_bases.}^.id) .
    iri:evidence(evidence_bases.iri) :fromXref iri:database(database_bases.iri) where(^{evidence_bases.}^.from_xref = ^{database_bases.}^.id) .
    iri:evidence(evidence_bases.iri) :integrationLevel iri:schema(schema_bases.iri) where(^{evidence_bases.}^.integration_level = ^{schema_bases.}^.id) .
    iri:evidence(evidence_bases.iri) :quality iri:schema(schema_bases.iri) where(^{evidence_bases.}^.quality = ^{schema_bases.}^.id) .
    iri:evidence(evidence_bases.iri) :isoformSpecificity evidence_bases.isoform_specificity where(^{evidence_bases.}^.isoform_specificity is not null) .
    iri:evidence(evidence_bases.iri) :antibodiesAcc evidence_bases.antibodies_acc where(^{evidence_bases.}^.antibodies_acc is not null) .
    iri:evidence(evidence_bases.iri) :assocType evidence_bases.assoc_type .
    iri:evidence(evidence_bases.iri) :cellLine evidence_bases.cell_line where(^{evidence_bases.}^.cell_line is not null) .
    iri:evidence(evidence_bases.iri) :goQualifier evidence_bases.go_qualifier where(^{evidence_bases.}^.go_qualifier is not null) .
    iri:evidence(evidence_bases.iri) :intensity evidence_bases.intensity where(^{evidence_bases.}^.intensity is not null) .
    iri:evidence(evidence_bases.iri) :negativeIsoformSpecificity evidence_bases.negative_isoform_specificity where(^{evidence_bases.}^.negative_isoform_specificity is not null) .
    iri:evidence(evidence_bases.iri) :interactionDetectionMethod iri:terminology(terminology_bases.iri) where(^{evidence_bases.}^.interaction_detection_method = ^{terminology_bases.}^.id) .
    iri:evidence(evidence_bases.iri) :expressionScore evidence_bases.expression_score where(^{evidence_bases.}^.expression_score is not null) .
    iri:evidence(evidence_bases.iri) :homozygote-count evidence_bases.homozygote_count where(^{evidence_bases.}^.homozygote_count is not null) .
    iri:evidence(evidence_bases.iri) :allele-number evidence_bases.allele_number where(^{evidence_bases.}^.allele_number is not null) .
    iri:evidence(evidence_bases.iri) :allele-count evidence_bases.allele_count where(^{evidence_bases.}^.allele_count is not null) .
    iri:evidence(evidence_bases.iri) :allele-frequency evidence_bases.allele_frequency where(^{evidence_bases.}^.allele_frequency is not null) .
    iri:evidence(evidence_bases.iri) :reference iri:publication(evidence_publication_references.publication) where(^{evidence_bases.}^.id = ^{evidence_publication_references.}^.evidence) .
    iri:entry(entry_bases.iri) rdf:type :Entry .
    iri:entry(entry_bases.iri) :isoformCount entry_bases.isoform_count .
    iri:entry(entry_bases.iri) :existence iri:schema(schema_bases.iri) where(^{entry_bases.}^.existence = ^{schema_bases.}^.id) .
    iri:entry(entry_bases.iri) skos:exactMatch iri:uniprot(entry_bases.uniprot) .
    iri:entry(entry_bases.iri) :swissprotPage iri:uniprotpage(entry_bases.uniprot) .
    iri:entry(entry_bases.iri) :classifiedWith iri:terminology(terminology_bases.iri) where(^{entry_bases.}^.id = ^{entry_classifiers.}^.entry and ^{entry_classifiers.}^.classifier = ^{terminology_bases.}^.id) option (using entry_classifiers) .
    iri:entry(entry_bases.iri) :gene iri:gene(gene_bases.iri) where(^{entry_bases.}^.id = ^{entry_genes.}^.entry and ^{entry_genes.}^.gene = ^{gene_bases.}^.id) option (using entry_genes) .
    iri:entry(entry_bases.iri) :isoform iri:isoform(isoform_bases.iri) where(^{entry_bases.}^.id = ^{isoform_bases.}^.entry) .
    iri:entry(entry_bases.iri) :reference iri:publication(entry_publication_references.publication) where(^{entry_bases.}^.id = ^{entry_publication_references.}^.entry) .
    iri:entry(entry_bases.iri) :recommendedName iri:blanknode1(entry_recommended_names.name) where(^{entry_bases.}^.id = ^{entry_recommended_names.}^.entry) .
    iri:entry(entry_bases.iri) :alternativeName iri:blanknode1(entry_alternative_names.name) where(^{entry_bases.}^.id = ^{entry_alternative_names.}^.entry) .
    iri:entry(entry_bases.iri) :additionalNames iri:blanknode2(entry_additional_names.name_list) where(^{entry_bases.}^.id = ^{entry_additional_names.}^.entry) .
    iri:entry(entry_bases.iri) :cleavedRegionNames iri:blanknode2(entry_cleaved_region_names.name_list) where(^{entry_bases.}^.id = ^{entry_cleaved_region_names.}^.entry) .
    iri:entry(entry_bases.iri) :fonctionalRegionNames iri:blanknode2(entry_functional_region_names.name_list) where(^{entry_bases.}^.id = ^{entry_functional_region_names.}^.entry) .
    iri:proteoform(proteoform_bases.iri) rdf:type :Proteoform .
    iri:proteoform(proteoform_bases.iri) rdfs:label proteoform_bases.label .
    iri:proteoform(proteoform_bases.iri) :generalAnnotation iri:annotation(annotation_bases.iri) where(^{proteoform_bases.}^.id = ^{proteoform_general_annotations.}^.proteoform and ^{proteoform_general_annotations.}^.annotation = ^{annotation_bases.}^.id) option (using proteoform_general_annotations) .
    iri:proteoform(proteoform_bases.iri) :genericPhenotype iri:annotation(annotation_bases.iri) where(^{proteoform_bases.}^.id = ^{proteoform_generic_phenotypes.}^.proteoform and ^{proteoform_generic_phenotypes.}^.annotation = ^{annotation_bases.}^.id) option (using proteoform_generic_phenotypes) .
    iri:proteoform(proteoform_bases.iri) :modification iri:annotation(annotation_bases.iri) where(^{proteoform_bases.}^.id = ^{proteoform_modifications.}^.proteoform and ^{proteoform_modifications.}^.annotation = ^{annotation_bases.}^.id) option (using proteoform_modifications) .
    iri:proteoform(proteoform_bases.iri) :phenotypicVariation iri:annotation(annotation_bases.iri) where(^{proteoform_bases.}^.id = ^{proteoform_phenotypic_variations.}^.proteoform and ^{proteoform_phenotypic_variations.}^.annotation = ^{annotation_bases.}^.id) option (using proteoform_phenotypic_variations) .
    iri:proteoform(proteoform_bases.iri) :positionalAnnotation iri:annotation(annotation_bases.iri) where(^{proteoform_bases.}^.id = ^{proteoform_positional_annotations.}^.proteoform and ^{proteoform_positional_annotations.}^.annotation = ^{annotation_bases.}^.id) option (using proteoform_positional_annotations) .
    iri:proteoform(proteoform_bases.iri) :diseaseRelatedVariant iri:annotation(annotation_bases.iri) where(^{proteoform_bases.}^.id = ^{proteoform_disease_related_variants.}^.proteoform and ^{proteoform_disease_related_variants.}^.annotation = ^{annotation_bases.}^.id) option (using proteoform_disease_related_variants) .
    iri:gene(gene_bases.iri) rdf:type :Gene .
    iri:gene(gene_bases.iri) :begin gene_bases.gene_begin .
    iri:gene(gene_bases.iri) :end gene_bases.gene_end .
    iri:gene(gene_bases.iri) :length gene_bases.length .
    iri:gene(gene_bases.iri) :band gene_bases.band .
    iri:gene(gene_bases.iri) :chromosome gene_bases.chromosome .
    iri:gene(gene_bases.iri) :strand gene_bases.strand .
    iri:gene(gene_bases.iri) :bestGeneMapping iri:entry(entry_bases.iri) where(^{gene_bases.}^.id = ^{gene_best_mappings.}^.gene and ^{gene_best_mappings.}^.mapping = ^{entry_bases.}^.id) option (using gene_best_mappings) .
    iri:gene(gene_bases.iri) :name gene_names.name where(^{gene_bases.}^.id = ^{gene_names.}^.gene) .
    iri:blanknode3(protein_sequence_bases.id) rdf:type :ProteinSequence .
    iri:blanknode3(protein_sequence_bases.id) :length protein_sequence_bases.length .
    iri:blanknode3(protein_sequence_bases.id) :molecularWeight protein_sequence_bases.molecular_weight .
    iri:blanknode3(protein_sequence_bases.id) :isoelectricPoint protein_sequence_bases.isoelectric_point .
    iri:blanknode3(protein_sequence_bases.id) :chain protein_sequence_bases.chain .
    iri:isoform(isoform_bases.iri) :sequence iri:blanknode3(protein_sequence_bases.id) where(^{isoform_bases.}^.id = ^{protein_sequence_bases.}^.isoform) .
    iri:blanknode4(family_info_bases.id) rdf:type :FamilyInfo .
    iri:blanknode4(family_info_bases.id) :term iri:terminology(terminology_bases.iri) where(^{family_info_bases.}^.term = ^{terminology_bases.}^.id) .
    iri:blanknode4(family_info_bases.id) :region family_info_bases.region where(^{family_info_bases.}^.region is not null) .
    iri:blanknode4(family_info_bases.id) :description family_info_bases.description .
    iri:entry(entry_bases.iri) :family iri:blanknode4(family_info_bases.id) where(^{entry_bases.}^.id = ^{family_info_bases.}^.entry) .
    iri:blanknode5(history_bases.id) rdf:type :History .
    iri:blanknode5(history_bases.id) :integrated history_bases.integrated .
    iri:blanknode5(history_bases.id) :updated history_bases.updated .
    iri:blanknode5(history_bases.id) :lastSequenceUpdate history_bases.last_sequence_update where(^{history_bases.}^.last_sequence_update is not null) .
    iri:blanknode5(history_bases.id) :version history_bases.version where(^{history_bases.}^.version is not null) .
    iri:blanknode5(history_bases.id) :sequenceVersion history_bases.sequence_version where(^{history_bases.}^.sequence_version is not null) .
    iri:blanknode5(history_bases.id) :name history_bases.name .
    iri:entry(entry_bases.iri) :history iri:blanknode5(history_bases.id) where(^{entry_bases.}^.id = ^{history_bases.}^.entry) .
    iri:blanknode6(identifier_bases.id) rdf:type :Identifier .
    iri:blanknode6(identifier_bases.id) :provenance iri:database(database_bases.iri) where(^{identifier_bases.}^.provenance = ^{database_bases.}^.id) .
    iri:blanknode6(identifier_bases.id) :accession identifier_bases.accession .
    iri:entry(entry_bases.iri) :reference iri:blanknode6(identifier_bases.id) where(^{entry_bases.}^.id = ^{identifier_bases.}^.entry) .
    iri:blanknode7(entry_xref_bases.id) rdf:type :Xref .
    iri:blanknode7(entry_xref_bases.id) :provenance iri:database(database_bases.iri) where(^{entry_xref_bases.}^.provenance = ^{database_bases.}^.id) .
    iri:blanknode7(entry_xref_bases.id) :accession entry_xref_bases.accession .
    iri:entry(entry_bases.iri) :reference iri:blanknode7(entry_xref_bases.id) where(^{entry_bases.}^.id = ^{entry_xref_bases.}^.entry) .
    iri:blanknode8(evidence_xref_bases.id) rdf:type :Xref .
    iri:blanknode8(evidence_xref_bases.id) :provenance iri:database(database_bases.iri) where(^{evidence_xref_bases.}^.provenance = ^{database_bases.}^.id) .
    iri:blanknode8(evidence_xref_bases.id) :accession evidence_xref_bases.accession .
    iri:evidence(evidence_bases.iri) :reference iri:blanknode8(evidence_xref_bases.id) where(^{evidence_bases.}^.id = ^{evidence_xref_bases.}^.evidence) .
    iri:blanknode9(chebi_xref_bases.id) rdf:type :Xref .
    iri:blanknode9(chebi_xref_bases.id) :provenance db:ChEBI .
    iri:blanknode9(chebi_xref_bases.id) skos:exactMatch iri:chebi(chebi_xref_bases.chebi) .
    iri:blanknode9(chebi_xref_bases.id) :accession chebi_xref_bases.accession .
    iri:blanknode9(chebi_xref_bases.id) rdfs:label chebi_xref_bases.label .
    iri:annotation(annotation_bases.iri) :interactant iri:blanknode9(chebi_xref_bases.id) where(^{annotation_bases.}^.id = ^{chebi_xref_bases.}^.annotation) .
    iri:blanknode10(drugbank_xref_bases.id) rdf:type :Xref .
    iri:blanknode10(drugbank_xref_bases.id) :provenance db:DrugBank .
    iri:blanknode10(drugbank_xref_bases.id) skos:exactMatch iri:drugbank(drugbank_xref_bases.drugbank) .
    iri:blanknode10(drugbank_xref_bases.id) :accession drugbank_xref_bases.accession .
    iri:blanknode10(drugbank_xref_bases.id) rdfs:label drugbank_xref_bases.label .
    iri:annotation(annotation_bases.iri) :interactant iri:blanknode10(drugbank_xref_bases.id) where(^{annotation_bases.}^.id = ^{drugbank_xref_bases.}^.annotation) .
    iri:blanknode11(uniprot_xref_bases.id) rdf:type :Xref .
    iri:blanknode11(uniprot_xref_bases.id) :provenance db:UniProtKB .
    iri:blanknode11(uniprot_xref_bases.id) skos:exactMatch iri:uniprot(uniprot_xref_bases.uniprot) .
    iri:blanknode11(uniprot_xref_bases.id) :accession uniprot_xref_bases.accession .
    iri:blanknode11(uniprot_xref_bases.id) rdfs:label uniprot_xref_bases.label .
    iri:annotation(annotation_bases.iri) :interactant iri:blanknode11(uniprot_xref_bases.id) where(^{annotation_bases.}^.id = ^{uniprot_xref_bases.}^.annotation) .
    iri:blanknode1(name_bases.id) rdf:type :Name .
    iri:blanknode1(name_bases.id) :fullName name_bases.full_name where(^{name_bases.}^.full_name is not null) .
    iri:blanknode1(name_bases.id) :shortName name_bases.short_name where(^{name_bases.}^.short_name is not null) .
    iri:blanknode1(name_bases.id) :fullRegionName name_bases.full_region_name where(^{name_bases.}^.full_region_name is not null) .
    iri:blanknode1(name_bases.id) :shortRegionName name_bases.short_region_name where(^{name_bases.}^.short_region_name is not null) .
    iri:blanknode1(name_bases.id) :ecEnzymeName name_bases.ec_enzyme_name where(^{name_bases.}^.ec_enzyme_name is not null) .
    iri:blanknode1(name_bases.id) :cdAntigenCdAntigen name_bases.cd_antigen where(^{name_bases.}^.cd_antigen is not null) .
    iri:blanknode1(name_bases.id) :innInternationalNonproprietaryNames name_bases.inn_name where(^{name_bases.}^.inn_name is not null) .
    iri:blanknode1(name_bases.id) :allergenAllergen name_bases.allergen where(^{name_bases.}^.allergen is not null) .
    iri:blanknode2(name_list_bases.id) rdf:type :NameList .
    iri:blanknode2(name_list_recommended_names.list) :recommendedName iri:blanknode1(name_list_recommended_names.name) .
    iri:blanknode2(name_list_alternative_names.list) :alternativeName iri:blanknode1(name_list_alternative_names.name) .
    iri:context(context_bases.id) rdf:type :ExperimentalContext .
    iri:context(context_bases.id) :metadata iri:publication(context_bases.metadata) .
    iri:context(context_bases.id) :detectionMethod iri:terminology(terminology_bases.iri) where(^{context_bases.}^.method = ^{terminology_bases.}^.id) .
    iri:context(context_bases.id) :disease iri:terminology(terminology_bases.iri) where(^{context_bases.}^.disease = ^{terminology_bases.}^.id) .
    iri:context(context_bases.id) :tissue iri:terminology(terminology_bases.iri) where(^{context_bases.}^.tissue = ^{terminology_bases.}^.id) .
    iri:context(context_bases.id) :cellLine iri:terminology(terminology_bases.iri) where(^{context_bases.}^.line = ^{terminology_bases.}^.id) .
    iri:context(context_bases.id) :developmentalStage iri:terminology(terminology_bases.iri) where(^{context_bases.}^.stage = ^{terminology_bases.}^.id) .
    iri:isoform(isoform_bases.iri) :lowExpression iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_low_expressions.}^.isoform and ^{isoform_low_expressions.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_low_expressions) .
    iri:isoform(isoform_bases.iri) :mediumExpression iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_medium_expressions.}^.isoform and ^{isoform_medium_expressions.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_medium_expressions) .
    iri:isoform(isoform_bases.iri) :highExpression iri:annotation(annotation_bases.iri) where(^{isoform_bases.}^.id = ^{isoform_high_expressions.}^.isoform and ^{isoform_high_expressions.}^.annotation = ^{annotation_bases.}^.id) option (using isoform_high_expressions) .
    iri:publication(publication_bases.id) rdf:type :Publication .
    iri:publication(publication_bases.id) rdf:type :LargeScalePublication where(^{publication_bases.}^.large) .
    iri:publication(publication_bases.id) :title publication_bases.title where(^{publication_bases.}^.title is not null) .
    iri:publication(publication_bases.id) :journal publication_bases.journal where(^{publication_bases.}^.journal is not null) .
    iri:publication(publication_bases.id) :year publication_bases.year where(^{publication_bases.}^.year is not null) .
    iri:publication(publication_bases.id) :volume publication_bases.volume where(^{publication_bases.}^.volume is not null) .
    iri:publication(publication_bases.id) :issue publication_bases.issue where(^{publication_bases.}^.issue is not null) .
    iri:publication(publication_bases.id) :pubType publication_bases.pub_type where(^{publication_bases.}^.pub_type is not null) .
    iri:publication(publication_bases.id) :firstPage publication_bases.first_page where(^{publication_bases.}^.first_page is not null) .
    iri:publication(publication_bases.id) :lastPage publication_bases.last_page where(^{publication_bases.}^.last_page is not null) .
    iri:publication(publication_bases.id) :publisher publication_bases.publisher where(^{publication_bases.}^.publisher is not null) .
    iri:publication(publication_bases.id) :city publication_bases.city where(^{publication_bases.}^.city is not null) .
    iri:publication(publication_links.publication) :from publication_links.link .
    iri:blanknode12(publication_authors.id) rdf:type :Person where(^{publication_authors.}^.person) .
    iri:blanknode12(publication_authors.id) rdf:type :Consortium where(not ^{publication_authors.}^.person) .
    iri:blanknode12(publication_authors.id) :name publication_authors.name .
    iri:blanknode12(publication_authors.id) :suffix publication_authors.suffix where(^{publication_authors.}^.suffix is not null) .
    iri:publication(publication_authors.publication) :author iri:blanknode12(publication_authors.id) .
    iri:blanknode13(publication_editors.id) rdf:type :Person .
    iri:blanknode13(publication_editors.id) :name publication_editors.name .
    iri:publication(publication_editors.publication) :editor iri:blanknode13(publication_editors.id) .
    iri:source(source_bases.iri) rdf:type :Source .
    iri:source(source_bases.iri) rdfs:comment source_bases.comment where(^{source_bases.}^.comment is not null) .
    iri:source(source_bases.iri) rdfs:seeAlso source_bases.reference where(^{source_bases.}^.reference is not null) .
    iri:database(database_bases.iri) rdf:type :Database .
    iri:database(database_bases.iri) rdfs:seeAlso database_bases.reference where(^{database_bases.}^.reference is not null) .
    iri:database(database_bases.iri) rdfs:comment database_comments.comment where(^{database_bases.}^.id = ^{database_comments.}^.db) .
    iri:database(database_bases.iri) :category database_categories.category where(^{database_bases.}^.id = ^{database_categories.}^.db) .
    iri:schema(schema_bases.iri) rdf:type iri:schema(schema_bases2.iri) where(^{schema_bases.}^.type = ^{schema_bases2.}^.id) .
    iri:schema(schema_bases.iri) rdfs:label schema_bases.label where(^{schema_bases.}^.label is not null) .
    iri:schema(schema_bases.iri) rdfs:comment schema_bases.comment where(^{schema_bases.}^.comment is not null) .
    iri:schema(schema_bases.iri) rdfs:seeAlso schema_bases.reference where(^{schema_bases.}^.reference is not null) .
    iri:schema(schema_bases.iri) rdf:type owl:Class where(^{schema_bases.}^.id = ^{schema_classes.}^.entity) option (using schema_classes) .
    iri:schema(schema_bases.iri) rdfs:subClassOf owl:Thing where(^{schema_bases.}^.id = ^{schema_thing_subclasses.}^.entity) option (using schema_thing_subclasses) .
    iri:schema(schema_bases.iri) :notIn iri:schema(schema_bases2.iri) where(^{schema_bases.}^.id = ^{schema_restrictions.}^.entity and ^{schema_restrictions.}^.notin = ^{schema_bases2.}^.id) option (using schema_restrictions) .
    iri:schema(schema_bases.iri) :related iri:terminology(schema_bases2.iri) where(^{schema_bases.}^.id = ^{schema_related_terms.}^.entity and ^{schema_related_terms.}^.related = ^{schema_bases2.}^.id) option (using schema_related_terms) .
    iri:schema(schema_bases.iri) rdfs:subClassOf iri:schema(schema_bases2.iri) where(^{schema_bases.}^.id = ^{schema_parent_classes.}^.entity and ^{schema_parent_classes.}^.parent = ^{schema_bases2.}^.id) option (using schema_parent_classes) .
    <http://np.org/rdf> rdf:type owl:Ontology .
    <http://np.org/rdf> owl:imports owl: .
    <http://np.org/rdf> owl:imports <http://www.w3.org/2004/02/skos/core> .
    <http://np.org/rdf> owl:versionInfo "Initial release"^^xsd:string .
    :Version :ttlGenerationDate "2021-12-12"^^xsd:date .
    :Version :databaseRelease "2021-11-19"^^xsd:string .
    :Version :apiRelease "2.31.0 (build 6462#4abd80b [branch develop])"^^xsd:string .
    :Version :genomeAssembly: "GRCh38"^^xsd:string .
    :assignedBy rdf:type rdf:Property .
    :assignedBy rdf:type owl:FunctionalProperty .
    :assignedBy rdfs:domain :Evidence .
    :assignedBy rdfs:range :Source .
    :childOf rdf:type rdf:Property .
    :childOf rdf:type owl:TransitiveProperty .
    :childOf rdfs:domain :Term .
    :childOf rdfs:range :Term .
    :related rdf:type rdf:Property .
    :related rdfs:domain :Term .
    :related rdfs:range :Term .
    :Entry owl:equivalentClass up:Protein .
    :Term owl:equivalentClass up:Concept .
    :NotDetected owl:sameAs :Negative .
    cv:ECO_0000045 rdfs:subClassOf :IHC .
    cv:ECO_0000104 rdfs:subClassOf :EST .
    cv:ECO_0000220 rdfs:subClassOf :Microarray .
    cv:ECO_0000045 rdfs:subClassOf cv:ECO_0000045 .
    cv:ECO_0000220 rdfs:subClassOf cv:ECO_0000220 .
    cv:ECO_0000104 rdfs:subClassOf cv:ECO_0000104 .
    owl:Thing rdfs:subClassOf owl:Thing .
    :Evidence_at_protein_level :level "1"^^xsd:integer .
    :Evidence_at_transcript_level :level "2"^^xsd:integer .
    :Inferred_from_homology :level "3"^^xsd:integer .
    :Predicted :level "4"^^xsd:integer .
    :Uncertain :level "5"^^xsd:integer .
    iri:terminology(terminology_bases.iri) rdf:type iri:schema(schema_bases.iri) where(^{terminology_bases.}^.type = ^{schema_bases.}^.id) .
    iri:terminology(terminology_bases.iri) :childOf iri:terminology(terminology_bases2.iri) where(^{terminology_bases.}^.id = ^{terminology_parents.}^.term and ^{terminology_parents.}^.parent = ^{terminology_bases2.}^.id) option (using terminology_parents) .
    iri:terminology(terminology_bases.iri) :related iri:terminology(terminology_bases2.iri) where(^{terminology_bases.}^.id = ^{terminology_related_terms.}^.term and ^{terminology_related_terms.}^.related = ^{terminology_bases2.}^.id) option (using terminology_related_terms) .
    iri:terminology(terminology_bases.iri) rdfs:label terminology_labels.label where(^{terminology_bases.}^.id = ^{terminology_labels.}^.term) .
  }
};
