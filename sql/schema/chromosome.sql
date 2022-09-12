create table QNAME(evidence_bases)
(
    expression_score                double precision,
    allele_frequency                double precision,
if(XSDINTEGER_SIZE, `8B', `    number_of_experiments           XSDINTEGER,')
if(XSDINTEGER_SIZE, `8B', `    homozygote_count                XSDINTEGER,')
if(XSDINTEGER_SIZE, `8B', `    allele_number                   XSDINTEGER,')
if(XSDINTEGER_SIZE, `8B', `    allele_count                    XSDINTEGER,')
    experimental_context            integer,
ifnot(KEYTYPE_SIZE, `4B', `    negative                        XSDBOOLEAN not null,')
    id                              KEYTYPE(13) not null,
    evidence_code                   KEYTYPE(85) not null,
    assigned_by                     KEYTYPE(68) not null,
    expression_level                KEYTYPE(37),
    from_xref                       KEYTYPE(27),
    integration_level               KEYTYPE(37),
    quality                         KEYTYPE(37),
    interaction_detection_method    KEYTYPE(85),
if(KEYTYPE_SIZE, `4B', `    negative                        XSDBOOLEAN not null,')
ifnot(XSDINTEGER_SIZE, `8B', `    number_of_experiments           XSDINTEGER,')
ifnot(XSDINTEGER_SIZE, `8B', `    homozygote_count                XSDINTEGER,')
ifnot(XSDINTEGER_SIZE, `8B', `    allele_number                   XSDINTEGER,')
ifnot(XSDINTEGER_SIZE, `8B', `    allele_count                    XSDINTEGER,')
if(KEYTYPE, `integer', `    iri                             KEYCHAR(13) UNIQUE()not null,')
    isoform_specificity             VARCHAR(73),
    antibodies_acc                  VARCHAR(86),
    assoc_type                      VARCHAR(8) not null,
    cell_line                       VARCHAR(13),
    go_qualifier                    VARCHAR(27),
    intensity                       VARCHAR(8),
    negative_isoform_specificity    VARCHAR(35),
    primary key(id)IDXPARAMS()
);

create table QNAME(evidence_publication_references)
(
    publication     integer not null,
    evidence        KEYTYPE(13) not null,
    primary key(evidence, publication)IDXPARAMS()
);

create table QNAME(annotation_bases)
(
    resolution          double precision,
if(XSDINTEGER_SIZE, `8B', `    position_start      XSDINTEGER,')
if(XSDINTEGER_SIZE, `8B', `    position_end        XSDINTEGER,')
ifnot(KEYTYPE_SIZE, `4B', `    self_interaction    XSDBOOLEAN,')
ifnot(KEYTYPE_SIZE, `4B', `    proteotypic         XSDBOOLEAN,')
    id                  KEYTYPE(51) not null,
    type                KEYTYPE(37),
    quality             KEYTYPE(37),
    term                KEYTYPE(85),
    impacted_object     KEYTYPE(51),
if(KEYTYPE_SIZE, `4B', `    self_interaction    XSDBOOLEAN,')
if(KEYTYPE_SIZE, `4B', `    proteotypic         XSDBOOLEAN,')
ifnot(XSDINTEGER_SIZE, `8B', `    position_start      XSDINTEGER,')
ifnot(XSDINTEGER_SIZE, `8B', `    position_end        XSDINTEGER,')
if(KEYTYPE, `integer', `    iri                 KEYCHAR(51) UNIQUE()not null,')
    entry_annotation    VARCHAR(35),
    variation           VARCHAR(210),
    original            VARCHAR(998),
    hgvs                VARCHAR(28),
    method              VARCHAR(7),
    peptide_name        VARCHAR(15),
    peptide_unicity     VARCHAR(13),
    antibody_name       VARCHAR(12),
    antibody_unicity    VARCHAR(13),
    primary key(id)IDXPARAMS()
);

create table QNAME(annotation_evidences)
(
    annotation  KEYTYPE(51) not null,
    evidence    KEYTYPE(13) not null,
    primary key(annotation, evidence)IDXPARAMS()
);

create table QNAME(annotation_negative_evidences)
(
    annotation  KEYTYPE(51) not null,
    evidence    KEYTYPE(13) not null,
    primary key(annotation, evidence)IDXPARAMS()
);

create table QNAME(annotation_diseases)
(
    annotation  KEYTYPE(51) not null,
    disease     KEYTYPE(85) not null,
    primary key(annotation, disease)IDXPARAMS()
);

create table QNAME(annotation_comments)
(
    seq         integer not null,
    annotation  KEYTYPE(51) not null,
    comment     LONGVAR not null,
    primary key(seq)IDXPARAMS()
);

create table QNAME(annotation_isoform_specificities)
(
    annotation  KEYTYPE(51) not null,
    specificity KEYTYPE(37) not null,
    primary key(annotation, specificity)IDXPARAMS()
);

create table QNAME(annotation_entry_interactants)
(
    annotation  KEYTYPE(51) not null,
    interactant KEYTYPE(13) not null,
    primary key(annotation, interactant)IDXPARAMS()
);

create table QNAME(annotation_isoform_interactants)
(
    annotation  KEYTYPE(51) not null,
    interactant KEYTYPE(15) not null,
    primary key(annotation, interactant)IDXPARAMS()
);

create table QNAME(annotation_peptide_sets)
(
    annotation  KEYTYPE(51) not null,
    peptide_set VARCHAR(34) not null,
    primary key(annotation, peptide_set)IDXPARAMS()
);

create table QNAME(isoform_bases)
(
ifnot(KEYTYPE_SIZE, `4B', `    canonical_isoform           XSDBOOLEAN not null,')
ifnot(KEYTYPE_SIZE, `4B', `    swissprot_displayed         XSDBOOLEAN not null,')
    id                          KEYTYPE(15) not null,
    entry                       KEYTYPE(13) not null,
    absorption_max              KEYTYPE(51),
    absorption_note             KEYTYPE(51),
    allergen                    KEYTYPE(51),
    initiator_methionine        KEYTYPE(51),
    kinetic_k_m                 KEYTYPE(51),
    kinetic_note                KEYTYPE(51),
    kinetic_vmax                KEYTYPE(51),
    non_consecutive_residue     KEYTYPE(51),
    peroxisome_transit_peptide  KEYTYPE(51),
    pharmaceutical              KEYTYPE(51),
    ph_dependence               KEYTYPE(51),
    redox_potential             KEYTYPE(51),
    signal_peptide              KEYTYPE(51),
    temperature_dependence      KEYTYPE(51),
if(KEYTYPE_SIZE, `4B', `    canonical_isoform           XSDBOOLEAN not null,')
if(KEYTYPE_SIZE, `4B', `    swissprot_displayed         XSDBOOLEAN not null,')
if(KEYTYPE, `integer', `    iri                         KEYCHAR(15) UNIQUE()not null,')
    primary key(id)IDXPARAMS()
);

create table QNAME(isoform_proteoforms)
(
    isoform     KEYTYPE(15) not null,
    proteoform  KEYTYPE(308) not null,
    primary key(isoform, proteoform)IDXPARAMS()
);

create table QNAME(isoform_active_sites)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_activity_regulations)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_antibody_mappings)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_beta_strands)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_binary_interactions)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_binding_sites)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_biophysicochemical_properties)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_calcium_binding_regions)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_catalytic_activities)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_cautions)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_cellular_components)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_cleavage_sites)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_cofactors)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_cofactor_infos)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_coiled_coil_regions)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_compositionally_biased_regions)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_cross_links)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_detected_expressions)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_developmental_stage_infos)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_diseases)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_disulfide_bonds)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_dna_binding_regions)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_domains)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_domain_infos)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_electrophysiological_parameters)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_enzyme_classifications)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_expressions)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_expression_infos)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_expression_profiles)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_functions)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_function_infos)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_general_annotations)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_glycosylation_sites)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_go_biological_processs)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_go_cellular_components)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_go_molecular_functions)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_helixs)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_inductions)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_interacting_regions)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_interactions)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_interaction_infos)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_intramembrane_regions)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_keywords)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_lipidation_sites)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_mappings)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_mature_proteins)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_medicals)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_metal_binding_sites)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_miscellaneouss)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_miscellaneous_regions)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_miscellaneous_sites)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_mitochondrial_transit_peptides)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_modified_residues)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_mutagenesiss)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_non_terminal_residues)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_nucleotide_phosphate_binding_regions)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_pathways)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_pdb_mappings)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_peptide_mappings)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_positional_annotations)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_processing_products)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_propeptides)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_ptms)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_ptm_infos)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_regions)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_repeats)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_secondary_structures)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_selenocysteines)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_sequence_cautions)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_sequence_conflicts)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_short_sequence_motifs)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_sites)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_small_molecule_interactions)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_srm_peptide_mappings)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_subcellular_locations)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_subcellular_location_notes)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_topological_domains)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_topologies)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_transmembrane_regions)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_transport_activities)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_turns)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_undetected_expressions)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_uniprot_keywords)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_variants)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_variant_infos)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_zinc_finger_regions)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(isoform_interaction_mappings)
(
    isoform     KEYTYPE(15) not null,
    annotation  KEYTYPE(51) not null,
    primary key(isoform, annotation)IDXPARAMS()
);

create table QNAME(entry_bases)
(
if(XSDINTEGER_SIZE, `8B', `    isoform_count   XSDINTEGER not null,')
    id              KEYTYPE(13) not null,
    existence       KEYTYPE(37) not null,
ifnot(XSDINTEGER_SIZE, `8B', `    isoform_count   XSDINTEGER not null,')
if(KEYTYPE, `integer', `    iri             KEYCHAR(13) UNIQUE()not null,')
    uniprot         VARCHAR(10) not null,
    primary key(id)IDXPARAMS()
);

create table QNAME(entry_classifiers)
(
    entry       KEYTYPE(13) not null,
    classifier  KEYTYPE(85) not null,
    primary key(entry, classifier)IDXPARAMS()
);

create table QNAME(entry_genes)
(
    entry       KEYTYPE(13) not null,
    gene        KEYTYPE(34) not null,
    primary key(entry, gene)IDXPARAMS()
);

create table QNAME(entry_publication_references)
(
    publication  integer not null,
    entry        KEYTYPE(13) not null,
    primary key(entry, publication)IDXPARAMS()
);

create table QNAME(entry_recommended_names)
(
    name     integer  not null,
    entry    KEYTYPE(13) not null,
    primary key(entry, name)IDXPARAMS()
);

create table QNAME(entry_alternative_names)
(
    name     integer  not null,
    entry    KEYTYPE(13) not null,
    primary key(entry, name)IDXPARAMS()
);

create table QNAME(entry_additional_names)
(
    name_list   integer  not null,
    entry       KEYTYPE(13) not null,
    primary key(entry, name_list)IDXPARAMS()
);

create table QNAME(entry_cleaved_region_names)
(
    name_list   integer  not null,
    entry       KEYTYPE(13) not null,
    primary key(entry, name_list)IDXPARAMS()
);

create table QNAME(entry_functional_region_names)
(
    name_list   integer  not null,
    entry       KEYTYPE(13) not null,
    primary key(entry, name_list)IDXPARAMS()
);

create table QNAME(proteoform_bases)
(
    id      KEYTYPE(308) not null,
if(KEYTYPE, `integer', `    iri     KEYCHAR(308) UNIQUE()not null,')
    label   VARCHAR(248) not null,
    primary key(id)IDXPARAMS()
);

create table QNAME(proteoform_general_annotations)
(
    proteoform   KEYTYPE(308) not null,
    annotation   KEYTYPE(51) not null,
    primary key(proteoform, annotation)IDXPARAMS()
);

create table QNAME(proteoform_generic_phenotypes)
(
    proteoform   KEYTYPE(308) not null,
    annotation   KEYTYPE(51) not null,
    primary key(proteoform, annotation)IDXPARAMS()
);

create table QNAME(proteoform_modifications)
(
    proteoform   KEYTYPE(308) not null,
    annotation   KEYTYPE(51) not null,
    primary key(proteoform, annotation)IDXPARAMS()
);

create table QNAME(proteoform_phenotypic_variations)
(
    proteoform   KEYTYPE(308) not null,
    annotation   KEYTYPE(51) not null,
    primary key(proteoform, annotation)IDXPARAMS()
);

create table QNAME(proteoform_positional_annotations)
(
    proteoform   KEYTYPE(308) not null,
    annotation   KEYTYPE(51) not null,
    primary key(proteoform, annotation)IDXPARAMS()
);

create table QNAME(proteoform_disease_related_variants)
(
    proteoform   KEYTYPE(308) not null,
    annotation   KEYTYPE(51) not null,
    primary key(proteoform, annotation)IDXPARAMS()
);

create table QNAME(gene_bases)
(
if(XSDINTEGER_SIZE, `8B', `    gene_begin  XSDINTEGER not null,')
if(XSDINTEGER_SIZE, `8B', `    gene_end    XSDINTEGER not null,')
if(XSDINTEGER_SIZE, `8B', `    length      XSDINTEGER not null,')
    id          KEYTYPE(34) not null,
ifnot(XSDINTEGER_SIZE, `8B', `    gene_begin  XSDINTEGER not null,')
ifnot(XSDINTEGER_SIZE, `8B', `    gene_end    XSDINTEGER not null,')
ifnot(XSDINTEGER_SIZE, `8B', `    length      XSDINTEGER not null,')
if(KEYTYPE, `integer', `    iri         KEYCHAR(34) UNIQUE()not null,')
    band        VARCHAR(11) not null,
    chromosome  VARCHAR(7) not null,
    strand      VARCHAR(2) not null,
    primary key(id)IDXPARAMS()
);

create table QNAME(gene_best_mappings)
(
    gene            KEYTYPE(34) not null,
    mapping         KEYTYPE(13) not null,
    primary key(gene, mapping)IDXPARAMS()
);

create table QNAME(gene_names)
(
    gene   KEYTYPE(34) not null,
    name   VARCHAR(14) not null,
    primary key(gene, name)IDXPARAMS()
);

create table QNAME(protein_sequence_bases)
(
    molecular_weight    double precision not null,
    isoelectric_point   double precision not null,
if(XSDINTEGER_SIZE, `8B', `    length              XSDINTEGER not null,')
    id                  integer not null,
    isoform             KEYTYPE(15) not null,
ifnot(XSDINTEGER_SIZE, `8B', `    length              XSDINTEGER not null,')
    chain               LONGVAR not null,
    primary key(id)IDXPARAMS()
);

create table QNAME(family_info_bases)
(
    id              integer not null,
    entry           KEYTYPE(13) not null,
    term            KEYTYPE(85) not null,
    region          VARCHAR(25),
    description     VARCHAR(150) not null,
    primary key(id)IDXPARAMS()
);

create table QNAME(history_bases)
(
    id                      integer not null,
    entry                   KEYTYPE(13) not null,
    integrated              VARCHAR(10) not null,
    updated                 VARCHAR(10) not null,
    last_sequence_update    VARCHAR(10),
    version                 VARCHAR(3),
    sequence_version        VARCHAR(1),
    name                    VARCHAR(9) not null,
    primary key(id)IDXPARAMS()
);

create table QNAME(identifier_bases)
(
    id              integer not null,
    entry           KEYTYPE(13) not null,
    provenance      KEYTYPE(27) not null,
    accession       VARCHAR(27) not null,
    primary key(id)IDXPARAMS()
);

create table QNAME(entry_xref_bases)
(
    id              integer not null,
    entry           KEYTYPE(13) not null,
    provenance      KEYTYPE(27) not null,
    accession       VARCHAR(76) not null,
    primary key(id)IDXPARAMS()
);

create table QNAME(evidence_xref_bases)
(
    id              integer not null,
    evidence        KEYTYPE(13) not null,
    provenance      KEYTYPE(27) not null,
    accession       VARCHAR(76) not null,
    primary key(id)IDXPARAMS()
);

create table QNAME(chebi_xref_bases)
(
    id              integer not null,
    annotation      KEYTYPE(51) not null,
    chebi           integer not null,
    accession       VARCHAR(12) not null,
    label           VARCHAR(295) not null,
    primary key(id)IDXPARAMS()
);

create table QNAME(drugbank_xref_bases)
(
    id              integer not null,
    annotation      KEYTYPE(51) not null,
    drugbank        VARCHAR(5) not null,
    accession       VARCHAR(7) not null,
    label           VARCHAR(217) not null,
    primary key(id)IDXPARAMS()
);

create table QNAME(uniprot_xref_bases)
(
    id              integer not null,
    annotation      KEYTYPE(51) not null,
    uniprot         VARCHAR(10) not null,
    accession       VARCHAR(10) not null,
    label           VARCHAR(19) not null,
    primary key(id)IDXPARAMS()
);

create table QNAME(name_bases)
(
    id                  integer not null,
    full_name           VARCHAR(122),
    short_name          VARCHAR(75),
    full_region_name    VARCHAR(63),
    short_region_name   VARCHAR(23),
    ec_enzyme_name      VARCHAR(11),
    cd_antigen          VARCHAR(7),
    inn_name            VARCHAR(16),
    allergen            VARCHAR(9),
    primary key(id)IDXPARAMS()
);

create table QNAME(name_list_bases)
(
    id      integer not null,
    primary key(id)IDXPARAMS()
);

create table QNAME(name_list_recommended_names)
(
    list    integer not null,
    name    integer not null,
    primary key(list, name)IDXPARAMS()
);

create table QNAME(name_list_alternative_names)
(
    list    integer not null,
    name    integer not null,
    primary key(list, name)IDXPARAMS()
);

