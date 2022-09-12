create function nextprot.nextprot.iri_isoform (in id_value integer) returns varchar
{
    vectored;
    return (select 'http://nextprot.org/rdf/isoform/' || iri from nextprot.nextprot.isoform_bases where id = id_value);
};

create function nextprot.nextprot.iri_isoform_INVERSE (in iri_value varchar) returns integer
{
    vectored;
    return (select id from nextprot.nextprot.isoform_bases where iri = sprintf_inverse(iri_value, 'http://nextprot.org/rdf/isoform/%U', 2)[0]);
};

create function nextprot.nextprot.iri_annotation (in id_value integer) returns varchar
{
    vectored;
    return (select 'http://nextprot.org/rdf/annotation/' || iri from nextprot.nextprot.annotation_bases where id = id_value);
};

create function nextprot.nextprot.iri_annotation_INVERSE (in iri_value varchar) returns integer
{
    vectored;
    return (select id from nextprot.nextprot.annotation_bases where iri = sprintf_inverse(iri_value, 'http://nextprot.org/rdf/annotation/%U', 2)[0]);
};

create function nextprot.nextprot.iri_evidence (in id_value integer) returns varchar
{
    vectored;
    return (select 'http://nextprot.org/rdf/evidence/' || iri from nextprot.nextprot.evidence_bases where id = id_value);
};

create function nextprot.nextprot.iri_evidence_INVERSE (in iri_value varchar) returns integer
{
    vectored;
    return (select id from nextprot.nextprot.evidence_bases where iri = sprintf_inverse(iri_value, 'http://nextprot.org/rdf/evidence/%U', 2)[0]);
};

create function nextprot.nextprot.iri_entry (in id_value integer) returns varchar
{
    vectored;
    return (select 'http://nextprot.org/rdf/entry/' || iri from nextprot.nextprot.entry_bases where id = id_value);
};

create function nextprot.nextprot.iri_entry_INVERSE (in iri_value varchar) returns integer
{
    vectored;
    return (select id from nextprot.nextprot.entry_bases where iri = sprintf_inverse(iri_value, 'http://nextprot.org/rdf/entry/%U', 2)[0]);
};

create function nextprot.nextprot.iri_proteoform (in id_value integer) returns varchar
{
    vectored;
    return (select 'http://nextprot.org/rdf/proteoform/' || iri from nextprot.nextprot.proteoform_bases where id = id_value);
};

create function nextprot.nextprot.iri_proteoform_INVERSE (in iri_value varchar) returns integer
{
    vectored;
    return (select id from nextprot.nextprot.proteoform_bases where iri = sprintf_inverse(iri_value, 'http://nextprot.org/rdf/proteoform/%U', 2)[0]);
};

create function nextprot.nextprot.iri_gene (in id_value integer) returns varchar
{
    vectored;
    return (select 'http://nextprot.org/rdf/gene/' || iri from nextprot.nextprot.gene_bases where id = id_value);
};

create function nextprot.nextprot.iri_gene_INVERSE (in iri_value varchar) returns integer
{
    vectored;
    return (select id from nextprot.nextprot.gene_bases where iri = sprintf_inverse(iri_value, 'http://nextprot.org/rdf/gene/%U', 2)[0]);
};

create function nextprot.nextprot.iri_source (in id_value integer) returns varchar
{
    vectored;
    return (select 'http://nextprot.org/rdf/source/' || iri from nextprot.nextprot.source_bases where id = id_value);
};

create function nextprot.nextprot.iri_source_INVERSE (in iri_value varchar) returns integer
{
    vectored;
    return (select id from nextprot.nextprot.source_bases where iri = sprintf_inverse(iri_value, 'http://nextprot.org/rdf/source/%U', 2)[0]);
};

create function nextprot.nextprot.iri_database (in id_value integer) returns varchar
{
    vectored;
    return (select 'http://nextprot.org/rdf/db/' || iri from nextprot.nextprot.database_bases where id = id_value);
};

create function nextprot.nextprot.iri_database_INVERSE (in iri_value varchar) returns integer
{
    vectored;
    return (select id from nextprot.nextprot.database_bases where iri = sprintf_inverse(iri_value, 'http://nextprot.org/rdf/db/%U', 2)[0]);
};

create function nextprot.nextprot.iri_schema (in id_value integer) returns varchar
{
    vectored;
    return (select 'http://nextprot.org/rdf#' || iri from nextprot.nextprot.schema_bases where id = id_value);
};

create function nextprot.nextprot.iri_schema_INVERSE (in iri_value varchar) returns integer
{
    vectored;
    return (select id from nextprot.nextprot.schema_bases where iri = sprintf_inverse(iri_value, 'http://nextprot.org/rdf#%U', 2)[0]);
};

create function nextprot.nextprot.iri_terminology (in id_value integer) returns varchar
{
    vectored;
    return (select 'http://nextprot.org/rdf/terminology/' || iri from nextprot.nextprot.terminology_bases where id = id_value);
};

create function nextprot.nextprot.iri_terminology_INVERSE (in iri_value varchar) returns integer
{
    vectored;
    return (select id from nextprot.nextprot.terminology_bases where iri = sprintf_inverse(iri_value, 'http://nextprot.org/rdf/terminology/%U', 2)[0]);
};

grant execute on nextprot.nextprot.iri_isoform to "SPARQL";
grant execute on nextprot.nextprot.iri_isoform_INVERSE to "SPARQL";
grant execute on nextprot.nextprot.iri_annotation to "SPARQL";
grant execute on nextprot.nextprot.iri_annotation_INVERSE to "SPARQL";
grant execute on nextprot.nextprot.iri_evidence to "SPARQL";
grant execute on nextprot.nextprot.iri_evidence_INVERSE to "SPARQL";
grant execute on nextprot.nextprot.iri_entry to "SPARQL";
grant execute on nextprot.nextprot.iri_entry_INVERSE to "SPARQL";
grant execute on nextprot.nextprot.iri_proteoform to "SPARQL";
grant execute on nextprot.nextprot.iri_proteoform_INVERSE to "SPARQL";
grant execute on nextprot.nextprot.iri_gene to "SPARQL";
grant execute on nextprot.nextprot.iri_gene_INVERSE to "SPARQL";
grant execute on nextprot.nextprot.iri_source to "SPARQL";
grant execute on nextprot.nextprot.iri_source_INVERSE to "SPARQL";
grant execute on nextprot.nextprot.iri_database to "SPARQL";
grant execute on nextprot.nextprot.iri_database_INVERSE to "SPARQL";
grant execute on nextprot.nextprot.iri_schema to "SPARQL";
grant execute on nextprot.nextprot.iri_schema_INVERSE to "SPARQL";
grant execute on nextprot.nextprot.iri_terminology to "SPARQL";
grant execute on nextprot.nextprot.iri_terminology_INVERSE to "SPARQL";

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
prefix iri: <http://www.openlinksw.com/schemas/virtrdf/integer#>

create iri class iri:uniprot "http://purl.uniprot.org/uniprot/%U"(in id varchar not null) option (bijection).
create iri class iri:uniprotpage "http://www.uniprot.org/uniprot/%U"(in id varchar not null) option (bijection).
create iri class iri:chebi "http://purl.obolibrary.org/obo/CHEBI_%d"(in id integer not null) option (bijection).
create iri class iri:drugbank "http://wifo5-04.informatik.uni-mannheim.de/drugbank/resource/drugs/DB%U"(in id varchar not null) option (bijection).
create iri class iri:context "http://nextprot.org/rdf/context/%d"(in id integer not null) option (bijection).
create iri class iri:publication "http://nextprot.org/rdf/publication/%d"(in id integer not null) option (bijection).
create iri class iri:isoform using
  function nextprot.nextprot.iri_isoform(in id integer) returns varchar,
  function nextprot.nextprot.iri_isoform_INVERSE(in id varchar) returns integer
  option (bijection, returns "http://nextprot.org/rdf/isoform/%U").
create iri class iri:annotation using
  function nextprot.nextprot.iri_annotation(in id integer) returns varchar,
  function nextprot.nextprot.iri_annotation_INVERSE(in id varchar) returns integer
  option (bijection, returns "http://nextprot.org/rdf/annotation/%U").
create iri class iri:evidence using
  function nextprot.nextprot.iri_evidence(in id integer) returns varchar,
  function nextprot.nextprot.iri_evidence_INVERSE(in id varchar) returns integer
  option (bijection, returns "http://nextprot.org/rdf/evidence/%U").
create iri class iri:entry using
  function nextprot.nextprot.iri_entry(in id integer) returns varchar,
  function nextprot.nextprot.iri_entry_INVERSE(in id varchar) returns integer
  option (bijection, returns "http://nextprot.org/rdf/entry/%U").
create iri class iri:proteoform using
  function nextprot.nextprot.iri_proteoform(in id integer) returns varchar,
  function nextprot.nextprot.iri_proteoform_INVERSE(in id varchar) returns integer
  option (bijection, returns "http://nextprot.org/rdf/proteoform/%U").
create iri class iri:gene using
  function nextprot.nextprot.iri_gene(in id integer) returns varchar,
  function nextprot.nextprot.iri_gene_INVERSE(in id varchar) returns integer
  option (bijection, returns "http://nextprot.org/rdf/gene/%U").
create iri class iri:source using
  function nextprot.nextprot.iri_source(in id integer) returns varchar,
  function nextprot.nextprot.iri_source_INVERSE(in id varchar) returns integer
  option (bijection, returns "http://nextprot.org/rdf/source/%U").
create iri class iri:database using
  function nextprot.nextprot.iri_database(in id integer) returns varchar,
  function nextprot.nextprot.iri_database_INVERSE(in id varchar) returns integer
  option (bijection, returns "http://nextprot.org/rdf/db/%U").
create iri class iri:schema using
  function nextprot.nextprot.iri_schema(in id integer) returns varchar,
  function nextprot.nextprot.iri_schema_INVERSE(in id varchar) returns integer
  option (bijection, returns "http://nextprot.org/rdf#%U").
create iri class iri:terminology using
  function nextprot.nextprot.iri_terminology(in id integer) returns varchar,
  function nextprot.nextprot.iri_terminology_INVERSE(in id varchar) returns integer
  option (bijection, returns "http://nextprot.org/rdf/terminology/%U").
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

create quad storage virtrdf:NeXtProtIntegerQuadStorage
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
  from nextprot.nextprot.annotation_peptide_sets as annotation_peptide_sets
  from nextprot.nextprot.entry_bases as entry_bases
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
  from nextprot.nextprot.annotation_isoform_interactants as annotation_isoform_interactants
  from nextprot.nextprot.terminology_bases as terminology_bases
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
{
  create virtrdf:NeXtProtInteger as graph iri ("http://nextprot.org/rdf")
  {
    iri:isoform(isoform_bases.id) rdf:type :Isoform .
    iri:isoform(isoform_bases.id) :canonicalIsoform isoform_bases.canonical_isoform .
    iri:isoform(isoform_bases.id) :swissprotDisplayed isoform_bases.swissprot_displayed .
    iri:isoform(isoform_bases.id) :absorptionMax iri:annotation(isoform_bases.absorption_max) where(^{isoform_bases.}^.absorption_max is not null) .
    iri:isoform(isoform_bases.id) :absorptionNote iri:annotation(isoform_bases.absorption_note) where(^{isoform_bases.}^.absorption_note is not null) .
    iri:isoform(isoform_bases.id) :allergen iri:annotation(isoform_bases.allergen) where(^{isoform_bases.}^.allergen is not null) .
    iri:isoform(isoform_bases.id) :initiatorMethionine iri:annotation(isoform_bases.initiator_methionine) where(^{isoform_bases.}^.initiator_methionine is not null) .
    iri:isoform(isoform_bases.id) :kineticKM iri:annotation(isoform_bases.kinetic_k_m) where(^{isoform_bases.}^.kinetic_k_m is not null) .
    iri:isoform(isoform_bases.id) :kineticNote iri:annotation(isoform_bases.kinetic_note) where(^{isoform_bases.}^.kinetic_note is not null) .
    iri:isoform(isoform_bases.id) :kineticVmax iri:annotation(isoform_bases.kinetic_vmax) where(^{isoform_bases.}^.kinetic_vmax is not null) .
    iri:isoform(isoform_bases.id) :nonConsecutiveResidue iri:annotation(isoform_bases.non_consecutive_residue) where(^{isoform_bases.}^.non_consecutive_residue is not null) .
    iri:isoform(isoform_bases.id) :peroxisomeTransitPeptide iri:annotation(isoform_bases.peroxisome_transit_peptide) where(^{isoform_bases.}^.peroxisome_transit_peptide is not null) .
    iri:isoform(isoform_bases.id) :pharmaceutical iri:annotation(isoform_bases.pharmaceutical) where(^{isoform_bases.}^.pharmaceutical is not null) .
    iri:isoform(isoform_bases.id) :phDependence iri:annotation(isoform_bases.ph_dependence) where(^{isoform_bases.}^.ph_dependence is not null) .
    iri:isoform(isoform_bases.id) :redoxPotential iri:annotation(isoform_bases.redox_potential) where(^{isoform_bases.}^.redox_potential is not null) .
    iri:isoform(isoform_bases.id) :signalPeptide iri:annotation(isoform_bases.signal_peptide) where(^{isoform_bases.}^.signal_peptide is not null) .
    iri:isoform(isoform_bases.id) :temperatureDependence iri:annotation(isoform_bases.temperature_dependence) where(^{isoform_bases.}^.temperature_dependence is not null) .
    iri:isoform(isoform_proteoforms.isoform) :proteoform iri:proteoform(isoform_proteoforms.proteoform) .
    iri:isoform(isoform_active_sites.isoform) :activeSite iri:annotation(isoform_active_sites.annotation) .
    iri:isoform(isoform_activity_regulations.isoform) :activityRegulation iri:annotation(isoform_activity_regulations.annotation) .
    iri:isoform(isoform_antibody_mappings.isoform) :antibodyMapping iri:annotation(isoform_antibody_mappings.annotation) .
    iri:isoform(isoform_beta_strands.isoform) :betaStrand iri:annotation(isoform_beta_strands.annotation) .
    iri:isoform(isoform_binary_interactions.isoform) :binaryInteraction iri:annotation(isoform_binary_interactions.annotation) .
    iri:isoform(isoform_binding_sites.isoform) :bindingSite iri:annotation(isoform_binding_sites.annotation) .
    iri:isoform(isoform_biophysicochemical_properties.isoform) :biophysicochemicalProperty iri:annotation(isoform_biophysicochemical_properties.annotation) .
    iri:isoform(isoform_calcium_binding_regions.isoform) :calciumBindingRegion iri:annotation(isoform_calcium_binding_regions.annotation) .
    iri:isoform(isoform_catalytic_activities.isoform) :catalyticActivity iri:annotation(isoform_catalytic_activities.annotation) .
    iri:isoform(isoform_cautions.isoform) :caution iri:annotation(isoform_cautions.annotation) .
    iri:isoform(isoform_cellular_components.isoform) :cellularComponent iri:annotation(isoform_cellular_components.annotation) .
    iri:isoform(isoform_cleavage_sites.isoform) :cleavageSite iri:annotation(isoform_cleavage_sites.annotation) .
    iri:isoform(isoform_cofactors.isoform) :cofactor iri:annotation(isoform_cofactors.annotation) .
    iri:isoform(isoform_cofactor_infos.isoform) :cofactorInfo iri:annotation(isoform_cofactor_infos.annotation) .
    iri:isoform(isoform_coiled_coil_regions.isoform) :coiledCoilRegion iri:annotation(isoform_coiled_coil_regions.annotation) .
    iri:isoform(isoform_compositionally_biased_regions.isoform) :compositionallyBiasedRegion iri:annotation(isoform_compositionally_biased_regions.annotation) .
    iri:isoform(isoform_cross_links.isoform) :crossLink iri:annotation(isoform_cross_links.annotation) .
    iri:isoform(isoform_detected_expressions.isoform) :detectedExpression iri:annotation(isoform_detected_expressions.annotation) .
    iri:isoform(isoform_developmental_stage_infos.isoform) :developmentalStageInfo iri:annotation(isoform_developmental_stage_infos.annotation) .
    iri:isoform(isoform_diseases.isoform) :disease iri:annotation(isoform_diseases.annotation) .
    iri:isoform(isoform_disulfide_bonds.isoform) :disulfideBond iri:annotation(isoform_disulfide_bonds.annotation) .
    iri:isoform(isoform_dna_binding_regions.isoform) :dnaBindingRegion iri:annotation(isoform_dna_binding_regions.annotation) .
    iri:isoform(isoform_domains.isoform) :domain iri:annotation(isoform_domains.annotation) .
    iri:isoform(isoform_domain_infos.isoform) :domainInfo iri:annotation(isoform_domain_infos.annotation) .
    iri:isoform(isoform_electrophysiological_parameters.isoform) :electrophysiologicalParameter iri:annotation(isoform_electrophysiological_parameters.annotation) .
    iri:isoform(isoform_enzyme_classifications.isoform) :enzymeClassification iri:annotation(isoform_enzyme_classifications.annotation) .
    iri:isoform(isoform_expressions.isoform) :expression iri:annotation(isoform_expressions.annotation) .
    iri:isoform(isoform_expression_infos.isoform) :expressionInfo iri:annotation(isoform_expression_infos.annotation) .
    iri:isoform(isoform_expression_profiles.isoform) :expressionProfile iri:annotation(isoform_expression_profiles.annotation) .
    iri:isoform(isoform_functions.isoform) :function iri:annotation(isoform_functions.annotation) .
    iri:isoform(isoform_function_infos.isoform) :functionInfo iri:annotation(isoform_function_infos.annotation) .
    iri:isoform(isoform_general_annotations.isoform) :generalAnnotation iri:annotation(isoform_general_annotations.annotation) .
    iri:isoform(isoform_glycosylation_sites.isoform) :glycosylationSite iri:annotation(isoform_glycosylation_sites.annotation) .
    iri:isoform(isoform_go_biological_processs.isoform) :goBiologicalProcess iri:annotation(isoform_go_biological_processs.annotation) .
    iri:isoform(isoform_go_cellular_components.isoform) :goCellularComponent iri:annotation(isoform_go_cellular_components.annotation) .
    iri:isoform(isoform_go_molecular_functions.isoform) :goMolecularFunction iri:annotation(isoform_go_molecular_functions.annotation) .
    iri:isoform(isoform_helixs.isoform) :helix iri:annotation(isoform_helixs.annotation) .
    iri:isoform(isoform_inductions.isoform) :induction iri:annotation(isoform_inductions.annotation) .
    iri:isoform(isoform_interacting_regions.isoform) :interactingRegion iri:annotation(isoform_interacting_regions.annotation) .
    iri:isoform(isoform_interactions.isoform) :interaction iri:annotation(isoform_interactions.annotation) .
    iri:isoform(isoform_interaction_infos.isoform) :interactionInfo iri:annotation(isoform_interaction_infos.annotation) .
    iri:isoform(isoform_intramembrane_regions.isoform) :intramembraneRegion iri:annotation(isoform_intramembrane_regions.annotation) .
    iri:isoform(isoform_keywords.isoform) :keyword iri:annotation(isoform_keywords.annotation) .
    iri:isoform(isoform_lipidation_sites.isoform) :lipidationSite iri:annotation(isoform_lipidation_sites.annotation) .
    iri:isoform(isoform_mappings.isoform) :mapping iri:annotation(isoform_mappings.annotation) .
    iri:isoform(isoform_mature_proteins.isoform) :matureProtein iri:annotation(isoform_mature_proteins.annotation) .
    iri:isoform(isoform_medicals.isoform) :medical iri:annotation(isoform_medicals.annotation) .
    iri:isoform(isoform_metal_binding_sites.isoform) :metalBindingSite iri:annotation(isoform_metal_binding_sites.annotation) .
    iri:isoform(isoform_miscellaneouss.isoform) :miscellaneous iri:annotation(isoform_miscellaneouss.annotation) .
    iri:isoform(isoform_miscellaneous_regions.isoform) :miscellaneousRegion iri:annotation(isoform_miscellaneous_regions.annotation) .
    iri:isoform(isoform_miscellaneous_sites.isoform) :miscellaneousSite iri:annotation(isoform_miscellaneous_sites.annotation) .
    iri:isoform(isoform_mitochondrial_transit_peptides.isoform) :mitochondrialTransitPeptide iri:annotation(isoform_mitochondrial_transit_peptides.annotation) .
    iri:isoform(isoform_modified_residues.isoform) :modifiedResidue iri:annotation(isoform_modified_residues.annotation) .
    iri:isoform(isoform_mutagenesiss.isoform) :mutagenesis iri:annotation(isoform_mutagenesiss.annotation) .
    iri:isoform(isoform_non_terminal_residues.isoform) :nonTerminalResidue iri:annotation(isoform_non_terminal_residues.annotation) .
    iri:isoform(isoform_nucleotide_phosphate_binding_regions.isoform) :nucleotidePhosphateBindingRegion iri:annotation(isoform_nucleotide_phosphate_binding_regions.annotation) .
    iri:isoform(isoform_pathways.isoform) :pathway iri:annotation(isoform_pathways.annotation) .
    iri:isoform(isoform_pdb_mappings.isoform) :pdbMapping iri:annotation(isoform_pdb_mappings.annotation) .
    iri:isoform(isoform_peptide_mappings.isoform) :peptideMapping iri:annotation(isoform_peptide_mappings.annotation) .
    iri:isoform(isoform_positional_annotations.isoform) :positionalAnnotation iri:annotation(isoform_positional_annotations.annotation) .
    iri:isoform(isoform_processing_products.isoform) :processingProduct iri:annotation(isoform_processing_products.annotation) .
    iri:isoform(isoform_propeptides.isoform) :propeptide iri:annotation(isoform_propeptides.annotation) .
    iri:isoform(isoform_ptms.isoform) :ptm iri:annotation(isoform_ptms.annotation) .
    iri:isoform(isoform_ptm_infos.isoform) :ptmInfo iri:annotation(isoform_ptm_infos.annotation) .
    iri:isoform(isoform_regions.isoform) :region iri:annotation(isoform_regions.annotation) .
    iri:isoform(isoform_repeats.isoform) :repeat iri:annotation(isoform_repeats.annotation) .
    iri:isoform(isoform_secondary_structures.isoform) :secondaryStructure iri:annotation(isoform_secondary_structures.annotation) .
    iri:isoform(isoform_selenocysteines.isoform) :selenocysteine iri:annotation(isoform_selenocysteines.annotation) .
    iri:isoform(isoform_sequence_cautions.isoform) :sequenceCaution iri:annotation(isoform_sequence_cautions.annotation) .
    iri:isoform(isoform_sequence_conflicts.isoform) :sequenceConflict iri:annotation(isoform_sequence_conflicts.annotation) .
    iri:isoform(isoform_short_sequence_motifs.isoform) :shortSequenceMotif iri:annotation(isoform_short_sequence_motifs.annotation) .
    iri:isoform(isoform_sites.isoform) :site iri:annotation(isoform_sites.annotation) .
    iri:isoform(isoform_small_molecule_interactions.isoform) :smallMoleculeInteraction iri:annotation(isoform_small_molecule_interactions.annotation) .
    iri:isoform(isoform_srm_peptide_mappings.isoform) :srmPeptideMapping iri:annotation(isoform_srm_peptide_mappings.annotation) .
    iri:isoform(isoform_subcellular_locations.isoform) :subcellularLocation iri:annotation(isoform_subcellular_locations.annotation) .
    iri:isoform(isoform_subcellular_location_notes.isoform) :subcellularLocationNote iri:annotation(isoform_subcellular_location_notes.annotation) .
    iri:isoform(isoform_topological_domains.isoform) :topologicalDomain iri:annotation(isoform_topological_domains.annotation) .
    iri:isoform(isoform_topologies.isoform) :topology iri:annotation(isoform_topologies.annotation) .
    iri:isoform(isoform_transmembrane_regions.isoform) :transmembraneRegion iri:annotation(isoform_transmembrane_regions.annotation) .
    iri:isoform(isoform_transport_activities.isoform) :transportActivity iri:annotation(isoform_transport_activities.annotation) .
    iri:isoform(isoform_turns.isoform) :turn iri:annotation(isoform_turns.annotation) .
    iri:isoform(isoform_undetected_expressions.isoform) :undetectedExpression iri:annotation(isoform_undetected_expressions.annotation) .
    iri:isoform(isoform_uniprot_keywords.isoform) :uniprotKeyword iri:annotation(isoform_uniprot_keywords.annotation) .
    iri:isoform(isoform_variants.isoform) :variant iri:annotation(isoform_variants.annotation) .
    iri:isoform(isoform_variant_infos.isoform) :variantInfo iri:annotation(isoform_variant_infos.annotation) .
    iri:isoform(isoform_zinc_finger_regions.isoform) :zincFingerRegion iri:annotation(isoform_zinc_finger_regions.annotation) .
    iri:isoform(isoform_interaction_mappings.isoform) :interactionMapping iri:annotation(isoform_interaction_mappings.annotation) .
    iri:annotation(annotation_bases.id) rdf:type iri:schema(annotation_bases.type) where(^{annotation_bases.}^.type is not null) .
    iri:annotation(annotation_bases.id) :quality iri:schema(annotation_bases.quality) where(^{annotation_bases.}^.quality is not null) .
    iri:annotation(annotation_bases.id) :term iri:terminology(annotation_bases.term) where(^{annotation_bases.}^.term is not null) .
    iri:annotation(annotation_bases.id) :impactedObject iri:annotation(annotation_bases.impacted_object) where(^{annotation_bases.}^.impacted_object is not null) .
    iri:annotation(annotation_bases.id) :entryAnnotationId annotation_bases.entry_annotation where(^{annotation_bases.}^.entry_annotation is not null) .
    iri:annotation(annotation_bases.id) :variation annotation_bases.variation where(^{annotation_bases.}^.variation is not null) .
    iri:annotation(annotation_bases.id) :original annotation_bases.original where(^{annotation_bases.}^.original is not null) .
    iri:annotation(annotation_bases.id) :hgvs annotation_bases.hgvs .
    iri:annotation(annotation_bases.id) :method annotation_bases.method where(^{annotation_bases.}^.method is not null) .
    iri:annotation(annotation_bases.id) :peptideUnicity annotation_bases.peptide_unicity where(^{annotation_bases.}^.peptide_unicity is not null) .
    iri:annotation(annotation_bases.id) :peptideName annotation_bases.peptide_name where(^{annotation_bases.}^.peptide_name is not null) .
    iri:annotation(annotation_bases.id) :antibodyUnicity annotation_bases.antibody_unicity where(^{annotation_bases.}^.antibody_unicity is not null) .
    iri:annotation(annotation_bases.id) :antibodyName annotation_bases.antibody_name where(^{annotation_bases.}^.antibody_name is not null) .
    iri:annotation(annotation_bases.id) :start annotation_bases.position_start where(^{annotation_bases.}^.position_start is not null) .
    iri:annotation(annotation_bases.id) :end annotation_bases.position_end where(^{annotation_bases.}^.position_end is not null) .
    iri:annotation(annotation_bases.id) :resolution annotation_bases.resolution where(^{annotation_bases.}^.resolution is not null) .
    iri:annotation(annotation_bases.id) :proteotypic annotation_bases.proteotypic where(^{annotation_bases.}^.proteotypic is not null) .
    iri:annotation(annotation_bases.id) :selfInteraction annotation_bases.self_interaction where(^{annotation_bases.}^.self_interaction is not null) .
    iri:annotation(annotation_evidences.annotation) :evidence iri:evidence(annotation_evidences.evidence) .
    iri:annotation(annotation_negative_evidences.annotation) :negativeEvidence iri:evidence(annotation_negative_evidences.evidence) .
    iri:annotation(annotation_diseases.annotation) :disease iri:terminology(annotation_diseases.disease) .
    iri:annotation(annotation_comments.annotation) rdfs:comment annotation_comments.comment .
    iri:annotation(annotation_isoform_specificities.annotation) :isoformSpecificity iri:schema(annotation_isoform_specificities.specificity) .
    iri:annotation(annotation_entry_interactants.annotation) :interactant iri:entry(annotation_entry_interactants.interactant) .
    iri:annotation(annotation_isoform_interactants.annotation) :interactant iri:isoform(annotation_isoform_interactants.interactant) .
    iri:annotation(annotation_peptide_sets.annotation) :peptideSet annotation_peptide_sets.peptide_set .
    iri:evidence(evidence_bases.id) rdf:type :Evidence .
    iri:evidence(evidence_bases.id) :negative evidence_bases.negative .
    iri:evidence(evidence_bases.id) :numberOfExperiments evidence_bases.number_of_experiments where(^{evidence_bases.}^.number_of_experiments is not null) .
    iri:evidence(evidence_bases.id) :evidenceCode iri:terminology(evidence_bases.evidence_code) .
    iri:evidence(evidence_bases.id) :experimentalContext iri:context(evidence_bases.experimental_context) where(^{evidence_bases.}^.experimental_context is not null) .
    iri:evidence(evidence_bases.id) :assignedBy iri:source(evidence_bases.assigned_by) .
    iri:evidence(evidence_bases.id) :expressionLevel iri:schema(evidence_bases.expression_level) where(^{evidence_bases.}^.expression_level is not null) .
    iri:evidence(evidence_bases.id) :fromXref iri:database(evidence_bases.from_xref) where(^{evidence_bases.}^.from_xref is not null) .
    iri:evidence(evidence_bases.id) :integrationLevel iri:schema(evidence_bases.integration_level) where(^{evidence_bases.}^.integration_level is not null) .
    iri:evidence(evidence_bases.id) :quality iri:schema(evidence_bases.quality) where(^{evidence_bases.}^.quality is not null) .
    iri:evidence(evidence_bases.id) :isoformSpecificity evidence_bases.isoform_specificity where(^{evidence_bases.}^.isoform_specificity is not null) .
    iri:evidence(evidence_bases.id) :antibodiesAcc evidence_bases.antibodies_acc where(^{evidence_bases.}^.antibodies_acc is not null) .
    iri:evidence(evidence_bases.id) :assocType evidence_bases.assoc_type .
    iri:evidence(evidence_bases.id) :cellLine evidence_bases.cell_line where(^{evidence_bases.}^.cell_line is not null) .
    iri:evidence(evidence_bases.id) :goQualifier evidence_bases.go_qualifier where(^{evidence_bases.}^.go_qualifier is not null) .
    iri:evidence(evidence_bases.id) :intensity evidence_bases.intensity where(^{evidence_bases.}^.intensity is not null) .
    iri:evidence(evidence_bases.id) :negativeIsoformSpecificity evidence_bases.negative_isoform_specificity where(^{evidence_bases.}^.negative_isoform_specificity is not null) .
    iri:evidence(evidence_bases.id) :interactionDetectionMethod iri:terminology(evidence_bases.interaction_detection_method) where(^{evidence_bases.}^.interaction_detection_method is not null) .
    iri:evidence(evidence_bases.id) :expressionScore evidence_bases.expression_score where(^{evidence_bases.}^.expression_score is not null) .
    iri:evidence(evidence_bases.id) :homozygote-count evidence_bases.homozygote_count where(^{evidence_bases.}^.homozygote_count is not null) .
    iri:evidence(evidence_bases.id) :allele-number evidence_bases.allele_number where(^{evidence_bases.}^.allele_number is not null) .
    iri:evidence(evidence_bases.id) :allele-count evidence_bases.allele_count where(^{evidence_bases.}^.allele_count is not null) .
    iri:evidence(evidence_bases.id) :allele-frequency evidence_bases.allele_frequency where(^{evidence_bases.}^.allele_frequency is not null) .
    iri:evidence(evidence_publication_references.evidence) :reference iri:publication(evidence_publication_references.publication) .
    iri:entry(entry_bases.id) rdf:type :Entry .
    iri:entry(entry_bases.id) :isoformCount entry_bases.isoform_count .
    iri:entry(entry_bases.id) :existence iri:schema(entry_bases.existence) .
    iri:entry(entry_bases.id) skos:exactMatch iri:uniprot(entry_bases.uniprot) .
    iri:entry(entry_bases.id) :swissprotPage iri:uniprotpage(entry_bases.uniprot) .
    iri:entry(entry_classifiers.entry) :classifiedWith iri:terminology(entry_classifiers.classifier) .
    iri:entry(entry_genes.entry) :gene iri:gene(entry_genes.gene) .
    iri:entry(isoform_bases.entry) :isoform iri:isoform(isoform_bases.id) .
    iri:entry(entry_publication_references.entry) :reference iri:publication(entry_publication_references.publication) .
    iri:entry(entry_recommended_names.entry) :recommendedName iri:blanknode1(entry_recommended_names.name) .
    iri:entry(entry_alternative_names.entry) :alternativeName iri:blanknode1(entry_alternative_names.name) .
    iri:entry(entry_additional_names.entry) :additionalNames iri:blanknode2(entry_additional_names.name_list) .
    iri:entry(entry_cleaved_region_names.entry) :cleavedRegionNames iri:blanknode2(entry_cleaved_region_names.name_list) .
    iri:entry(entry_functional_region_names.entry) :fonctionalRegionNames iri:blanknode2(entry_functional_region_names.name_list) .
    iri:proteoform(proteoform_bases.id) rdf:type :Proteoform .
    iri:proteoform(proteoform_bases.id) rdfs:label proteoform_bases.label .
    iri:proteoform(proteoform_general_annotations.proteoform) :generalAnnotation iri:annotation(proteoform_general_annotations.annotation) .
    iri:proteoform(proteoform_generic_phenotypes.proteoform) :genericPhenotype iri:annotation(proteoform_generic_phenotypes.annotation) .
    iri:proteoform(proteoform_modifications.proteoform) :modification iri:annotation(proteoform_modifications.annotation) .
    iri:proteoform(proteoform_phenotypic_variations.proteoform) :phenotypicVariation iri:annotation(proteoform_phenotypic_variations.annotation) .
    iri:proteoform(proteoform_positional_annotations.proteoform) :positionalAnnotation iri:annotation(proteoform_positional_annotations.annotation) .
    iri:proteoform(proteoform_disease_related_variants.proteoform) :diseaseRelatedVariant iri:annotation(proteoform_disease_related_variants.annotation) .
    iri:gene(gene_bases.id) rdf:type :Gene .
    iri:gene(gene_bases.id) :begin gene_bases.gene_begin .
    iri:gene(gene_bases.id) :end gene_bases.gene_end .
    iri:gene(gene_bases.id) :length gene_bases.length .
    iri:gene(gene_bases.id) :band gene_bases.band .
    iri:gene(gene_bases.id) :chromosome gene_bases.chromosome .
    iri:gene(gene_bases.id) :strand gene_bases.strand .
    iri:gene(gene_best_mappings.gene) :bestGeneMapping iri:entry(gene_best_mappings.mapping) .
    iri:gene(gene_names.gene) :name gene_names.name .
    iri:blanknode3(protein_sequence_bases.id) rdf:type :ProteinSequence .
    iri:blanknode3(protein_sequence_bases.id) :length protein_sequence_bases.length .
    iri:blanknode3(protein_sequence_bases.id) :molecularWeight protein_sequence_bases.molecular_weight .
    iri:blanknode3(protein_sequence_bases.id) :isoelectricPoint protein_sequence_bases.isoelectric_point .
    iri:blanknode3(protein_sequence_bases.id) :chain protein_sequence_bases.chain .
    iri:isoform(protein_sequence_bases.isoform) :sequence iri:blanknode3(protein_sequence_bases.id) .
    iri:blanknode4(family_info_bases.id) rdf:type :FamilyInfo .
    iri:blanknode4(family_info_bases.id) :term iri:terminology(family_info_bases.term) .
    iri:blanknode4(family_info_bases.id) :region family_info_bases.region where(^{family_info_bases.}^.region is not null) .
    iri:blanknode4(family_info_bases.id) :description family_info_bases.description .
    iri:entry(family_info_bases.entry) :family iri:blanknode4(family_info_bases.id) .
    iri:blanknode5(history_bases.id) rdf:type :History .
    iri:blanknode5(history_bases.id) :integrated history_bases.integrated .
    iri:blanknode5(history_bases.id) :updated history_bases.updated .
    iri:blanknode5(history_bases.id) :lastSequenceUpdate history_bases.last_sequence_update where(^{history_bases.}^.last_sequence_update is not null) .
    iri:blanknode5(history_bases.id) :version history_bases.version where(^{history_bases.}^.version is not null) .
    iri:blanknode5(history_bases.id) :sequenceVersion history_bases.sequence_version where(^{history_bases.}^.sequence_version is not null) .
    iri:blanknode5(history_bases.id) :name history_bases.name .
    iri:entry(history_bases.entry) :history iri:blanknode5(history_bases.id) .
    iri:blanknode6(identifier_bases.id) rdf:type :Identifier .
    iri:blanknode6(identifier_bases.id) :provenance iri:database(identifier_bases.provenance) .
    iri:blanknode6(identifier_bases.id) :accession identifier_bases.accession .
    iri:entry(identifier_bases.entry) :reference iri:blanknode6(identifier_bases.id) .
    iri:blanknode7(entry_xref_bases.id) rdf:type :Xref .
    iri:blanknode7(entry_xref_bases.id) :provenance iri:database(entry_xref_bases.provenance) .
    iri:blanknode7(entry_xref_bases.id) :accession entry_xref_bases.accession .
    iri:entry(entry_xref_bases.entry) :reference iri:blanknode7(entry_xref_bases.id) .
    iri:blanknode8(evidence_xref_bases.id) rdf:type :Xref .
    iri:blanknode8(evidence_xref_bases.id) :provenance iri:database(evidence_xref_bases.provenance) .
    iri:blanknode8(evidence_xref_bases.id) :accession evidence_xref_bases.accession .
    iri:evidence(evidence_xref_bases.evidence) :reference iri:blanknode8(evidence_xref_bases.id) .
    iri:blanknode9(chebi_xref_bases.id) rdf:type :Xref .
    iri:blanknode9(chebi_xref_bases.id) :provenance db:ChEBI .
    iri:blanknode9(chebi_xref_bases.id) skos:exactMatch iri:chebi(chebi_xref_bases.chebi) .
    iri:blanknode9(chebi_xref_bases.id) :accession chebi_xref_bases.accession .
    iri:blanknode9(chebi_xref_bases.id) rdfs:label chebi_xref_bases.label .
    iri:annotation(chebi_xref_bases.annotation) :interactant iri:blanknode9(chebi_xref_bases.id) .
    iri:blanknode10(drugbank_xref_bases.id) rdf:type :Xref .
    iri:blanknode10(drugbank_xref_bases.id) :provenance db:DrugBank .
    iri:blanknode10(drugbank_xref_bases.id) skos:exactMatch iri:drugbank(drugbank_xref_bases.drugbank) .
    iri:blanknode10(drugbank_xref_bases.id) :accession drugbank_xref_bases.accession .
    iri:blanknode10(drugbank_xref_bases.id) rdfs:label drugbank_xref_bases.label .
    iri:annotation(drugbank_xref_bases.annotation) :interactant iri:blanknode10(drugbank_xref_bases.id) .
    iri:blanknode11(uniprot_xref_bases.id) rdf:type :Xref .
    iri:blanknode11(uniprot_xref_bases.id) :provenance db:UniProtKB .
    iri:blanknode11(uniprot_xref_bases.id) skos:exactMatch iri:uniprot(uniprot_xref_bases.uniprot) .
    iri:blanknode11(uniprot_xref_bases.id) :accession uniprot_xref_bases.accession .
    iri:blanknode11(uniprot_xref_bases.id) rdfs:label uniprot_xref_bases.label .
    iri:annotation(uniprot_xref_bases.annotation) :interactant iri:blanknode11(uniprot_xref_bases.id) .
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
    iri:context(context_bases.id) :detectionMethod iri:terminology(context_bases.method) where(^{context_bases.}^.method is not null) .
    iri:context(context_bases.id) :disease iri:terminology(context_bases.disease) where(^{context_bases.}^.disease is not null) .
    iri:context(context_bases.id) :tissue iri:terminology(context_bases.tissue) where(^{context_bases.}^.tissue is not null) .
    iri:context(context_bases.id) :cellLine iri:terminology(context_bases.line) where(^{context_bases.}^.line is not null) .
    iri:context(context_bases.id) :developmentalStage iri:terminology(context_bases.stage) where(^{context_bases.}^.stage is not null) .
    iri:isoform(isoform_low_expressions.isoform) :lowExpression iri:annotation(isoform_low_expressions.annotation) .
    iri:isoform(isoform_medium_expressions.isoform) :mediumExpression iri:annotation(isoform_medium_expressions.annotation) .
    iri:isoform(isoform_high_expressions.isoform) :highExpression iri:annotation(isoform_high_expressions.annotation) .
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
    iri:source(source_bases.id) rdf:type :Source .
    iri:source(source_bases.id) rdfs:comment source_bases.comment where(^{source_bases.}^.comment is not null) .
    iri:source(source_bases.id) rdfs:seeAlso source_bases.reference where(^{source_bases.}^.reference is not null) .
    iri:database(database_bases.id) rdf:type :Database .
    iri:database(database_bases.id) rdfs:seeAlso database_bases.reference where(^{database_bases.}^.reference is not null) .
    iri:database(database_comments.db) rdfs:comment database_comments.comment .
    iri:database(database_categories.db) :category database_categories.category .
    iri:schema(schema_bases.id) rdf:type iri:schema(schema_bases.type) where(^{schema_bases.}^.type is not null) .
    iri:schema(schema_bases.id) rdfs:label schema_bases.label where(^{schema_bases.}^.label is not null) .
    iri:schema(schema_bases.id) rdfs:comment schema_bases.comment where(^{schema_bases.}^.comment is not null) .
    iri:schema(schema_bases.id) rdfs:seeAlso schema_bases.reference where(^{schema_bases.}^.reference is not null) .
    iri:schema(schema_classes.entity) rdf:type owl:Class .
    iri:schema(schema_thing_subclasses.entity) rdfs:subClassOf owl:Thing .
    iri:schema(schema_restrictions.entity) :notIn iri:schema(schema_restrictions.notin) .
    iri:schema(schema_related_terms.entity) :related iri:terminology(schema_related_terms.related) .
    iri:schema(schema_parent_classes.entity) rdfs:subClassOf iri:schema(schema_parent_classes.parent) .
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
    iri:terminology(terminology_bases.id) rdf:type iri:schema(terminology_bases.type) where(^{terminology_bases.}^.type is not null) .
    iri:terminology(terminology_parents.term) :childOf iri:terminology(terminology_parents.parent) .
    iri:terminology(terminology_related_terms.term) :related iri:terminology(terminology_related_terms.related) .
    iri:terminology(terminology_labels.term) rdfs:label terminology_labels.label .
  }
};
