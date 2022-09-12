if(SERVER-MODE, `virtuoso-indirect', `create index evidence_bases__iri on QNAME(evidence_bases)(iri)IDXPARAMS();')
create index evidence_bases__evidence_code on QNAME(evidence_bases)(evidence_code)IDXPARAMS();
create index evidence_bases__assigned_by on QNAME(evidence_bases)(assigned_by)IDXPARAMS();
create index evidence_bases__expression_level on QNAME(evidence_bases)(expression_level)IDXPARAMS()WHERE(expression_level is not null);
create index evidence_bases__from_xref on QNAME(evidence_bases)(from_xref)IDXPARAMS()WHERE(from_xref is not null);
create index evidence_bases__integration_level on QNAME(evidence_bases)(integration_level)IDXPARAMS()WHERE(integration_level is not null);
create index evidence_bases__quality on QNAME(evidence_bases)(quality)IDXPARAMS()WHERE(quality is not null);
create index evidence_bases__experimental_context on QNAME(evidence_bases)(experimental_context)IDXPARAMS()WHERE(experimental_context is not null);
create index evidence_bases__number_of_experiments on QNAME(evidence_bases)(number_of_experiments)IDXPARAMS()WHERE(number_of_experiments is not null);
create index evidence_bases__negative on QNAME(evidence_bases)(negative)IDXPARAMS();
create index evidence_bases__isoform_specificity on QNAME(evidence_bases)(isoform_specificity)IDXPARAMS()WHERE(isoform_specificity is not null);
create index evidence_bases__antibodies_acc on QNAME(evidence_bases)(antibodies_acc)IDXPARAMS()WHERE(antibodies_acc is not null);
create index evidence_bases__assoc_type on QNAME(evidence_bases)(assoc_type)IDXPARAMS();
create index evidence_bases__cell_line on QNAME(evidence_bases)(cell_line)IDXPARAMS()WHERE(cell_line is not null);
create index evidence_bases__go_qualifier on QNAME(evidence_bases)(go_qualifier)IDXPARAMS()WHERE(go_qualifier is not null);
create index evidence_bases__intensity on QNAME(evidence_bases)(intensity)IDXPARAMS()WHERE(intensity is not null);
create index evidence_bases__negative_isoform_specificity on QNAME(evidence_bases)(negative_isoform_specificity)IDXPARAMS()WHERE(negative_isoform_specificity is not null);
create index evidence_bases__interaction_detection_method on QNAME(evidence_bases)(interaction_detection_method)IDXPARAMS()WHERE(interaction_detection_method is not null);
create index evidence_bases__expression_score on QNAME(evidence_bases)(expression_score)IDXPARAMS()WHERE(expression_score is not null);
create index evidence_bases__homozygote_count on QNAME(evidence_bases)(homozygote_count)IDXPARAMS()WHERE(homozygote_count is not null);
create index evidence_bases__allele_number on QNAME(evidence_bases)(allele_number)IDXPARAMS()WHERE(allele_number is not null);
create index evidence_bases__allele_count on QNAME(evidence_bases)(allele_count)IDXPARAMS()WHERE(allele_count is not null);
create index evidence_bases__allele_frequency on QNAME(evidence_bases)(allele_frequency)IDXPARAMS()WHERE(allele_frequency is not null);
grant select on QNAME(evidence_bases) to SPARQL;

create index evidence_publication_references__evidence on QNAME(evidence_publication_references)(evidence)IDXPARAMS();
create index evidence_publication_references__publication on QNAME(evidence_publication_references)(publication)IDXPARAMS();
grant select on QNAME(evidence_publication_references) to SPARQL;

if(SERVER-MODE, `virtuoso-indirect', `create index annotation_bases__iri on QNAME(annotation_bases)(iri)IDXPARAMS();')
create index annotation_bases__type on QNAME(annotation_bases)(type)IDXPARAMS()WHERE(type is not null);
create index annotation_bases__quality on QNAME(annotation_bases)(quality)IDXPARAMS()WHERE(quality is not null);
create index annotation_bases__term on QNAME(annotation_bases)(term)IDXPARAMS()WHERE(term is not null);
create index annotation_bases__impacted_object on QNAME(annotation_bases)(impacted_object)IDXPARAMS()WHERE(impacted_object is not null);
create index annotation_bases__entry_annotation on QNAME(annotation_bases)(entry_annotation)IDXPARAMS()WHERE(entry_annotation is not null);
create index annotation_bases__variation on QNAME(annotation_bases)(variation)IDXPARAMS()WHERE(variation is not null);
create index annotation_bases__original on QNAME(annotation_bases)(original)IDXPARAMS()WHERE(original is not null);
create index annotation_bases__hgvs on QNAME(annotation_bases)(original)IDXPARAMS()WHERE(hgvs is not null);
create index annotation_bases__method on QNAME(annotation_bases)(method)IDXPARAMS()WHERE(method is not null);
create index annotation_bases__peptide_name on QNAME(annotation_bases)(peptide_name)IDXPARAMS()WHERE(peptide_name is not null);
create index annotation_bases__peptide_unicity on QNAME(annotation_bases)(peptide_unicity)IDXPARAMS()WHERE(peptide_unicity is not null);
create index annotation_bases__antibody_name on QNAME(annotation_bases)(antibody_name)IDXPARAMS()WHERE(antibody_name is not null);
create index annotation_bases__antibody_unicity on QNAME(annotation_bases)(antibody_unicity)IDXPARAMS()WHERE(antibody_unicity is not null);
create index annotation_bases__position_start on QNAME(annotation_bases)(position_start)IDXPARAMS()WHERE(position_start is not null);
create index annotation_bases__position_end on QNAME(annotation_bases)(position_end)IDXPARAMS()WHERE(position_end is not null);
create index annotation_bases__resolution on QNAME(annotation_bases)(resolution)IDXPARAMS()WHERE(resolution is not null);
create index annotation_bases__proteotypic on QNAME(annotation_bases)(proteotypic)IDXPARAMS()WHERE(proteotypic is not null);
create index annotation_bases__self_interaction on QNAME(annotation_bases)(self_interaction)IDXPARAMS()WHERE(self_interaction is not null);
grant select on QNAME(annotation_bases) to SPARQL;

create index annotation_evidences__annotation on QNAME(annotation_evidences)(annotation)IDXPARAMS();
create index annotation_evidences__evidence on QNAME(annotation_evidences)(evidence)IDXPARAMS();
grant select on QNAME(annotation_evidences) to SPARQL;

create index annotation_negative_evidences__annotation on QNAME(annotation_negative_evidences)(annotation)IDXPARAMS();
create index annotation_negative_evidences__evidence on QNAME(annotation_negative_evidences)(evidence)IDXPARAMS();
grant select on QNAME(annotation_negative_evidences) to SPARQL;

create index annotation_diseases__annotation on QNAME(annotation_diseases)(annotation)IDXPARAMS();
create index annotation_diseases__disease on QNAME(annotation_diseases)(disease)IDXPARAMS();
grant select on QNAME(annotation_diseases) to SPARQL;

create index annotation_comments__annotation on QNAME(annotation_comments)(annotation)IDXPARAMS();
--create index annotation_comments__comment on QNAME(annotation_comments)(comment)IDXPARAMS();
grant select on QNAME(annotation_comments) to SPARQL;

create index annotation_isoform_specificities__annotation on QNAME(annotation_isoform_specificities)(annotation)IDXPARAMS();
create index annotation_isoform_specificities__specificity on QNAME(annotation_isoform_specificities)(specificity)IDXPARAMS();
grant select on QNAME(annotation_isoform_specificities) to SPARQL;

create index annotation_entry_interactants__annotation on QNAME(annotation_entry_interactants)(annotation)IDXPARAMS();
create index annotation_entry_interactants__interactant on QNAME(annotation_entry_interactants)(interactant)IDXPARAMS();
grant select on QNAME(annotation_entry_interactants) to SPARQL;

create index annotation_isoform_interactants__annotation on QNAME(annotation_isoform_interactants)(annotation)IDXPARAMS();
create index annotation_isoform_interactants__interactant on QNAME(annotation_isoform_interactants)(interactant)IDXPARAMS();
grant select on QNAME(annotation_isoform_interactants) to SPARQL;

create index annotation_peptide_sets__annotation on QNAME(annotation_peptide_sets)(annotation)IDXPARAMS();
create index annotation_peptide_sets__peptide_set on QNAME(annotation_peptide_sets)(peptide_set)IDXPARAMS();
grant select on QNAME(annotation_peptide_sets) to SPARQL;

if(SERVER-MODE, `virtuoso-indirect', `create index isoform_bases__iri on QNAME(isoform_bases)(iri)IDXPARAMS();')
create index isoform_bases__entry on QNAME(isoform_bases)(entry)IDXPARAMS();
create index isoform_bases__absorption_max on QNAME(isoform_bases)(absorption_max)IDXPARAMS()WHERE(absorption_max is not null);
create index isoform_bases__absorption_note on QNAME(isoform_bases)(absorption_note)IDXPARAMS()WHERE(absorption_note is not null);
create index isoform_bases__allergen on QNAME(isoform_bases)(allergen)IDXPARAMS()WHERE(allergen is not null);
create index isoform_bases__initiator_methionine on QNAME(isoform_bases)(initiator_methionine)IDXPARAMS()WHERE(initiator_methionine is not null);
create index isoform_bases__kinetic_k_m on QNAME(isoform_bases)(kinetic_k_m)IDXPARAMS()WHERE(kinetic_k_m is not null);
create index isoform_bases__kinetic_note on QNAME(isoform_bases)(kinetic_note)IDXPARAMS()WHERE(kinetic_note is not null);
create index isoform_bases__kinetic_vmax on QNAME(isoform_bases)(kinetic_vmax)IDXPARAMS()WHERE(kinetic_vmax is not null);
create index isoform_bases__non_consecutive_residue on QNAME(isoform_bases)(non_consecutive_residue)IDXPARAMS()WHERE(non_consecutive_residue is not null);
create index isoform_bases__peroxisome_transit_peptide on QNAME(isoform_bases)(peroxisome_transit_peptide)IDXPARAMS()WHERE(peroxisome_transit_peptide is not null);
create index isoform_bases__pharmaceutical on QNAME(isoform_bases)(pharmaceutical)IDXPARAMS()WHERE(pharmaceutical is not null);
create index isoform_bases__ph_dependence on QNAME(isoform_bases)(ph_dependence)IDXPARAMS()WHERE(ph_dependence is not null);
create index isoform_bases__redox_potential on QNAME(isoform_bases)(redox_potential)IDXPARAMS()WHERE(redox_potential is not null);
create index isoform_bases__signal_peptide on QNAME(isoform_bases)(signal_peptide)IDXPARAMS()WHERE(signal_peptide is not null);
create index isoform_bases__temperature_dependence on QNAME(isoform_bases)(temperature_dependence)IDXPARAMS()WHERE(temperature_dependence is not null);
create index isoform_bases__canonical_isoform on QNAME(isoform_bases)(canonical_isoform)IDXPARAMS();
create index isoform_bases__swissprot_displayed on QNAME(isoform_bases)(swissprot_displayed)IDXPARAMS();
grant select on QNAME(isoform_bases) to SPARQL;

create index isoform_proteoforms__isoform on QNAME(isoform_proteoforms)(isoform)IDXPARAMS();
create index isoform_proteoforms__proteoform on QNAME(isoform_proteoforms)(proteoform)IDXPARAMS();
grant select on QNAME(isoform_proteoforms) to SPARQL;

create index isoform_active_sites__isoform on QNAME(isoform_active_sites)(isoform)IDXPARAMS();
create index isoform_active_sites__annotation on QNAME(isoform_active_sites)(annotation)IDXPARAMS();
grant select on QNAME(isoform_active_sites) to SPARQL;

create index isoform_activity_regulations__isoform on QNAME(isoform_activity_regulations)(isoform)IDXPARAMS();
create index isoform_activity_regulations__annotation on QNAME(isoform_activity_regulations)(annotation)IDXPARAMS();
grant select on QNAME(isoform_activity_regulations) to SPARQL;

create index isoform_antibody_mappings__isoform on QNAME(isoform_antibody_mappings)(isoform)IDXPARAMS();
create index isoform_antibody_mappings__annotation on QNAME(isoform_antibody_mappings)(annotation)IDXPARAMS();
grant select on QNAME(isoform_antibody_mappings) to SPARQL;

create index isoform_beta_strands__isoform on QNAME(isoform_beta_strands)(isoform)IDXPARAMS();
create index isoform_beta_strands__annotation on QNAME(isoform_beta_strands)(annotation)IDXPARAMS();
grant select on QNAME(isoform_beta_strands) to SPARQL;

create index isoform_binary_interactions__isoform on QNAME(isoform_binary_interactions)(isoform)IDXPARAMS();
create index isoform_binary_interactions__annotation on QNAME(isoform_binary_interactions)(annotation)IDXPARAMS();
grant select on QNAME(isoform_binary_interactions) to SPARQL;

create index isoform_binding_sites__isoform on QNAME(isoform_binding_sites)(isoform)IDXPARAMS();
create index isoform_binding_sites__annotation on QNAME(isoform_binding_sites)(annotation)IDXPARAMS();
grant select on QNAME(isoform_binding_sites) to SPARQL;

create index isoform_biophysicochemical_properties__isoform on QNAME(isoform_biophysicochemical_properties)(isoform)IDXPARAMS();
create index isoform_biophysicochemical_properties__annotation on QNAME(isoform_biophysicochemical_properties)(annotation)IDXPARAMS();
grant select on QNAME(isoform_biophysicochemical_properties) to SPARQL;

create index isoform_calcium_binding_regions__isoform on QNAME(isoform_calcium_binding_regions)(isoform)IDXPARAMS();
create index isoform_calcium_binding_regions__annotation on QNAME(isoform_calcium_binding_regions)(annotation)IDXPARAMS();
grant select on QNAME(isoform_calcium_binding_regions) to SPARQL;

create index isoform_catalytic_activities__isoform on QNAME(isoform_catalytic_activities)(isoform)IDXPARAMS();
create index isoform_catalytic_activities__annotation on QNAME(isoform_catalytic_activities)(annotation)IDXPARAMS();
grant select on QNAME(isoform_catalytic_activities) to SPARQL;

create index isoform_cautions__isoform on QNAME(isoform_cautions)(isoform)IDXPARAMS();
create index isoform_cautions__annotation on QNAME(isoform_cautions)(annotation)IDXPARAMS();
grant select on QNAME(isoform_cautions) to SPARQL;

create index isoform_cellular_components__isoform on QNAME(isoform_cellular_components)(isoform)IDXPARAMS();
create index isoform_cellular_components__annotation on QNAME(isoform_cellular_components)(annotation)IDXPARAMS();
grant select on QNAME(isoform_cellular_components) to SPARQL;

create index isoform_cleavage_sites__isoform on QNAME(isoform_cleavage_sites)(isoform)IDXPARAMS();
create index isoform_cleavage_sites__annotation on QNAME(isoform_cleavage_sites)(annotation)IDXPARAMS();
grant select on QNAME(isoform_cleavage_sites) to SPARQL;

create index isoform_cofactors__isoform on QNAME(isoform_cofactors)(isoform)IDXPARAMS();
create index isoform_cofactors__annotation on QNAME(isoform_cofactors)(annotation)IDXPARAMS();
grant select on QNAME(isoform_cofactors) to SPARQL;

create index isoform_cofactor_infos__isoform on QNAME(isoform_cofactor_infos)(isoform)IDXPARAMS();
create index isoform_cofactor_infos__annotation on QNAME(isoform_cofactor_infos)(annotation)IDXPARAMS();
grant select on QNAME(isoform_cofactor_infos) to SPARQL;

create index isoform_coiled_coil_regions__isoform on QNAME(isoform_coiled_coil_regions)(isoform)IDXPARAMS();
create index isoform_coiled_coil_regions__annotation on QNAME(isoform_coiled_coil_regions)(annotation)IDXPARAMS();
grant select on QNAME(isoform_coiled_coil_regions) to SPARQL;

create index isoform_compositionally_biased_regions__isoform on QNAME(isoform_compositionally_biased_regions)(isoform)IDXPARAMS();
create index isoform_compositionally_biased_regions__annotation on QNAME(isoform_compositionally_biased_regions)(annotation)IDXPARAMS();
grant select on QNAME(isoform_compositionally_biased_regions) to SPARQL;

create index isoform_cross_links__isoform on QNAME(isoform_cross_links)(isoform)IDXPARAMS();
create index isoform_cross_links__annotation on QNAME(isoform_cross_links)(annotation)IDXPARAMS();
grant select on QNAME(isoform_cross_links) to SPARQL;

create index isoform_detected_expressions__isoform on QNAME(isoform_detected_expressions)(isoform)IDXPARAMS();
create index isoform_detected_expressions__annotation on QNAME(isoform_detected_expressions)(annotation)IDXPARAMS();
grant select on QNAME(isoform_detected_expressions) to SPARQL;

create index isoform_developmental_stage_infos__isoform on QNAME(isoform_developmental_stage_infos)(isoform)IDXPARAMS();
create index isoform_developmental_stage_infos__annotation on QNAME(isoform_developmental_stage_infos)(annotation)IDXPARAMS();
grant select on QNAME(isoform_developmental_stage_infos) to SPARQL;

create index isoform_diseases__isoform on QNAME(isoform_diseases)(isoform)IDXPARAMS();
create index isoform_diseases__annotation on QNAME(isoform_diseases)(annotation)IDXPARAMS();
grant select on QNAME(isoform_diseases) to SPARQL;

create index isoform_disulfide_bonds__isoform on QNAME(isoform_disulfide_bonds)(isoform)IDXPARAMS();
create index isoform_disulfide_bonds__annotation on QNAME(isoform_disulfide_bonds)(annotation)IDXPARAMS();
grant select on QNAME(isoform_disulfide_bonds) to SPARQL;

create index isoform_dna_binding_regions__isoform on QNAME(isoform_dna_binding_regions)(isoform)IDXPARAMS();
create index isoform_dna_binding_regions__annotation on QNAME(isoform_dna_binding_regions)(annotation)IDXPARAMS();
grant select on QNAME(isoform_dna_binding_regions) to SPARQL;

create index isoform_domains__isoform on QNAME(isoform_domains)(isoform)IDXPARAMS();
create index isoform_domains__annotation on QNAME(isoform_domains)(annotation)IDXPARAMS();
grant select on QNAME(isoform_domains) to SPARQL;

create index isoform_domain_infos__isoform on QNAME(isoform_domain_infos)(isoform)IDXPARAMS();
create index isoform_domain_infos__annotation on QNAME(isoform_domain_infos)(annotation)IDXPARAMS();
grant select on QNAME(isoform_domain_infos) to SPARQL;

create index isoform_electrophysiological_parameters__isoform on QNAME(isoform_electrophysiological_parameters)(isoform)IDXPARAMS();
create index isoform_electrophysiological_parameters__annotation on QNAME(isoform_electrophysiological_parameters)(annotation)IDXPARAMS();
grant select on QNAME(isoform_electrophysiological_parameters) to SPARQL;

create index isoform_enzyme_classifications__isoform on QNAME(isoform_enzyme_classifications)(isoform)IDXPARAMS();
create index isoform_enzyme_classifications__annotation on QNAME(isoform_enzyme_classifications)(annotation)IDXPARAMS();
grant select on QNAME(isoform_enzyme_classifications) to SPARQL;

create index isoform_expressions__isoform on QNAME(isoform_expressions)(isoform)IDXPARAMS();
create index isoform_expressions__annotation on QNAME(isoform_expressions)(annotation)IDXPARAMS();
grant select on QNAME(isoform_expressions) to SPARQL;

create index isoform_expression_infos__isoform on QNAME(isoform_expression_infos)(isoform)IDXPARAMS();
create index isoform_expression_infos__annotation on QNAME(isoform_expression_infos)(annotation)IDXPARAMS();
grant select on QNAME(isoform_expression_infos) to SPARQL;

create index isoform_expression_profiles__isoform on QNAME(isoform_expression_profiles)(isoform)IDXPARAMS();
create index isoform_expression_profiles__annotation on QNAME(isoform_expression_profiles)(annotation)IDXPARAMS();
grant select on QNAME(isoform_expression_profiles) to SPARQL;

create index isoform_functions__isoform on QNAME(isoform_functions)(isoform)IDXPARAMS();
create index isoform_functions__annotation on QNAME(isoform_functions)(annotation)IDXPARAMS();
grant select on QNAME(isoform_functions) to SPARQL;

create index isoform_function_infos__isoform on QNAME(isoform_function_infos)(isoform)IDXPARAMS();
create index isoform_function_infos__annotation on QNAME(isoform_function_infos)(annotation)IDXPARAMS();
grant select on QNAME(isoform_function_infos) to SPARQL;

create index isoform_general_annotations__isoform on QNAME(isoform_general_annotations)(isoform)IDXPARAMS();
create index isoform_general_annotations__annotation on QNAME(isoform_general_annotations)(annotation)IDXPARAMS();
grant select on QNAME(isoform_general_annotations) to SPARQL;

create index isoform_glycosylation_sites__isoform on QNAME(isoform_glycosylation_sites)(isoform)IDXPARAMS();
create index isoform_glycosylation_sites__annotation on QNAME(isoform_glycosylation_sites)(annotation)IDXPARAMS();
grant select on QNAME(isoform_glycosylation_sites) to SPARQL;

create index isoform_go_biological_processs__isoform on QNAME(isoform_go_biological_processs)(isoform)IDXPARAMS();
create index isoform_go_biological_processs__annotation on QNAME(isoform_go_biological_processs)(annotation)IDXPARAMS();
grant select on QNAME(isoform_go_biological_processs) to SPARQL;

create index isoform_go_cellular_components__isoform on QNAME(isoform_go_cellular_components)(isoform)IDXPARAMS();
create index isoform_go_cellular_components__annotation on QNAME(isoform_go_cellular_components)(annotation)IDXPARAMS();
grant select on QNAME(isoform_go_cellular_components) to SPARQL;

create index isoform_go_molecular_functions__isoform on QNAME(isoform_go_molecular_functions)(isoform)IDXPARAMS();
create index isoform_go_molecular_functions__annotation on QNAME(isoform_go_molecular_functions)(annotation)IDXPARAMS();
grant select on QNAME(isoform_go_molecular_functions) to SPARQL;

create index isoform_helixs__isoform on QNAME(isoform_helixs)(isoform)IDXPARAMS();
create index isoform_helixs__annotation on QNAME(isoform_helixs)(annotation)IDXPARAMS();
grant select on QNAME(isoform_helixs) to SPARQL;

create index isoform_inductions__isoform on QNAME(isoform_inductions)(isoform)IDXPARAMS();
create index isoform_inductions__annotation on QNAME(isoform_inductions)(annotation)IDXPARAMS();
grant select on QNAME(isoform_inductions) to SPARQL;

create index isoform_interacting_regions__isoform on QNAME(isoform_interacting_regions)(isoform)IDXPARAMS();
create index isoform_interacting_regions__annotation on QNAME(isoform_interacting_regions)(annotation)IDXPARAMS();
grant select on QNAME(isoform_interacting_regions) to SPARQL;

create index isoform_interactions__isoform on QNAME(isoform_interactions)(isoform)IDXPARAMS();
create index isoform_interactions__annotation on QNAME(isoform_interactions)(annotation)IDXPARAMS();
grant select on QNAME(isoform_interactions) to SPARQL;

create index isoform_interaction_infos__isoform on QNAME(isoform_interaction_infos)(isoform)IDXPARAMS();
create index isoform_interaction_infos__annotation on QNAME(isoform_interaction_infos)(annotation)IDXPARAMS();
grant select on QNAME(isoform_interaction_infos) to SPARQL;

create index isoform_intramembrane_regions__isoform on QNAME(isoform_intramembrane_regions)(isoform)IDXPARAMS();
create index isoform_intramembrane_regions__annotation on QNAME(isoform_intramembrane_regions)(annotation)IDXPARAMS();
grant select on QNAME(isoform_intramembrane_regions) to SPARQL;

create index isoform_keywords__isoform on QNAME(isoform_keywords)(isoform)IDXPARAMS();
create index isoform_keywords__annotation on QNAME(isoform_keywords)(annotation)IDXPARAMS();
grant select on QNAME(isoform_keywords) to SPARQL;

create index isoform_lipidation_sites__isoform on QNAME(isoform_lipidation_sites)(isoform)IDXPARAMS();
create index isoform_lipidation_sites__annotation on QNAME(isoform_lipidation_sites)(annotation)IDXPARAMS();
grant select on QNAME(isoform_lipidation_sites) to SPARQL;

create index isoform_mappings__isoform on QNAME(isoform_mappings)(isoform)IDXPARAMS();
create index isoform_mappings__annotation on QNAME(isoform_mappings)(annotation)IDXPARAMS();
grant select on QNAME(isoform_mappings) to SPARQL;

create index isoform_mature_proteins__isoform on QNAME(isoform_mature_proteins)(isoform)IDXPARAMS();
create index isoform_mature_proteins__annotation on QNAME(isoform_mature_proteins)(annotation)IDXPARAMS();
grant select on QNAME(isoform_mature_proteins) to SPARQL;

create index isoform_medicals__isoform on QNAME(isoform_medicals)(isoform)IDXPARAMS();
create index isoform_medicals__annotation on QNAME(isoform_medicals)(annotation)IDXPARAMS();
grant select on QNAME(isoform_medicals) to SPARQL;

create index isoform_metal_binding_sites__isoform on QNAME(isoform_metal_binding_sites)(isoform)IDXPARAMS();
create index isoform_metal_binding_sites__annotation on QNAME(isoform_metal_binding_sites)(annotation)IDXPARAMS();
grant select on QNAME(isoform_metal_binding_sites) to SPARQL;

create index isoform_miscellaneouss__isoform on QNAME(isoform_miscellaneouss)(isoform)IDXPARAMS();
create index isoform_miscellaneouss__annotation on QNAME(isoform_miscellaneouss)(annotation)IDXPARAMS();
grant select on QNAME(isoform_miscellaneouss) to SPARQL;

create index isoform_miscellaneous_regions__isoform on QNAME(isoform_miscellaneous_regions)(isoform)IDXPARAMS();
create index isoform_miscellaneous_regions__annotation on QNAME(isoform_miscellaneous_regions)(annotation)IDXPARAMS();
grant select on QNAME(isoform_miscellaneous_regions) to SPARQL;

create index isoform_miscellaneous_sites__isoform on QNAME(isoform_miscellaneous_sites)(isoform)IDXPARAMS();
create index isoform_miscellaneous_sites__annotation on QNAME(isoform_miscellaneous_sites)(annotation)IDXPARAMS();
grant select on QNAME(isoform_miscellaneous_sites) to SPARQL;

create index isoform_mitochondrial_transit_peptides__isoform on QNAME(isoform_mitochondrial_transit_peptides)(isoform)IDXPARAMS();
create index isoform_mitochondrial_transit_peptides__annotation on QNAME(isoform_mitochondrial_transit_peptides)(annotation)IDXPARAMS();
grant select on QNAME(isoform_mitochondrial_transit_peptides) to SPARQL;

create index isoform_modified_residues__isoform on QNAME(isoform_modified_residues)(isoform)IDXPARAMS();
create index isoform_modified_residues__annotation on QNAME(isoform_modified_residues)(annotation)IDXPARAMS();
grant select on QNAME(isoform_modified_residues) to SPARQL;

create index isoform_mutagenesiss__isoform on QNAME(isoform_mutagenesiss)(isoform)IDXPARAMS();
create index isoform_mutagenesiss__annotation on QNAME(isoform_mutagenesiss)(annotation)IDXPARAMS();
grant select on QNAME(isoform_mutagenesiss) to SPARQL;

create index isoform_non_terminal_residues__isoform on QNAME(isoform_non_terminal_residues)(isoform)IDXPARAMS();
create index isoform_non_terminal_residues__annotation on QNAME(isoform_non_terminal_residues)(annotation)IDXPARAMS();
grant select on QNAME(isoform_non_terminal_residues) to SPARQL;

create index isoform_nucleotide_phosphate_binding_regions__isoform on QNAME(isoform_nucleotide_phosphate_binding_regions)(isoform)IDXPARAMS();
create index isoform_nucleotide_phosphate_binding_regions__annotation on QNAME(isoform_nucleotide_phosphate_binding_regions)(annotation)IDXPARAMS();
grant select on QNAME(isoform_nucleotide_phosphate_binding_regions) to SPARQL;

create index isoform_pathways__isoform on QNAME(isoform_pathways)(isoform)IDXPARAMS();
create index isoform_pathways__annotation on QNAME(isoform_pathways)(annotation)IDXPARAMS();
grant select on QNAME(isoform_pathways) to SPARQL;

create index isoform_pdb_mappings__isoform on QNAME(isoform_pdb_mappings)(isoform)IDXPARAMS();
create index isoform_pdb_mappings__annotation on QNAME(isoform_pdb_mappings)(annotation)IDXPARAMS();
grant select on QNAME(isoform_pdb_mappings) to SPARQL;

create index isoform_peptide_mappings__isoform on QNAME(isoform_peptide_mappings)(isoform)IDXPARAMS();
create index isoform_peptide_mappings__annotation on QNAME(isoform_peptide_mappings)(annotation)IDXPARAMS();
grant select on QNAME(isoform_peptide_mappings) to SPARQL;

create index isoform_positional_annotations__isoform on QNAME(isoform_positional_annotations)(isoform)IDXPARAMS();
create index isoform_positional_annotations__annotation on QNAME(isoform_positional_annotations)(annotation)IDXPARAMS();
grant select on QNAME(isoform_positional_annotations) to SPARQL;

create index isoform_processing_products__isoform on QNAME(isoform_processing_products)(isoform)IDXPARAMS();
create index isoform_processing_products__annotation on QNAME(isoform_processing_products)(annotation)IDXPARAMS();
grant select on QNAME(isoform_processing_products) to SPARQL;

create index isoform_propeptides__isoform on QNAME(isoform_propeptides)(isoform)IDXPARAMS();
create index isoform_propeptides__annotation on QNAME(isoform_propeptides)(annotation)IDXPARAMS();
grant select on QNAME(isoform_propeptides) to SPARQL;

create index isoform_ptms__isoform on QNAME(isoform_ptms)(isoform)IDXPARAMS();
create index isoform_ptms__annotation on QNAME(isoform_ptms)(annotation)IDXPARAMS();
grant select on QNAME(isoform_ptms) to SPARQL;

create index isoform_ptm_infos__isoform on QNAME(isoform_ptm_infos)(isoform)IDXPARAMS();
create index isoform_ptm_infos__annotation on QNAME(isoform_ptm_infos)(annotation)IDXPARAMS();
grant select on QNAME(isoform_ptm_infos) to SPARQL;

create index isoform_regions__isoform on QNAME(isoform_regions)(isoform)IDXPARAMS();
create index isoform_regions__annotation on QNAME(isoform_regions)(annotation)IDXPARAMS();
grant select on QNAME(isoform_regions) to SPARQL;

create index isoform_repeats__isoform on QNAME(isoform_repeats)(isoform)IDXPARAMS();
create index isoform_repeats__annotation on QNAME(isoform_repeats)(annotation)IDXPARAMS();
grant select on QNAME(isoform_repeats) to SPARQL;

create index isoform_secondary_structures__isoform on QNAME(isoform_secondary_structures)(isoform)IDXPARAMS();
create index isoform_secondary_structures__annotation on QNAME(isoform_secondary_structures)(annotation)IDXPARAMS();
grant select on QNAME(isoform_secondary_structures) to SPARQL;

create index isoform_selenocysteines__isoform on QNAME(isoform_selenocysteines)(isoform)IDXPARAMS();
create index isoform_selenocysteines__annotation on QNAME(isoform_selenocysteines)(annotation)IDXPARAMS();
grant select on QNAME(isoform_selenocysteines) to SPARQL;

create index isoform_sequence_cautions__isoform on QNAME(isoform_sequence_cautions)(isoform)IDXPARAMS();
create index isoform_sequence_cautions__annotation on QNAME(isoform_sequence_cautions)(annotation)IDXPARAMS();
grant select on QNAME(isoform_sequence_cautions) to SPARQL;

create index isoform_sequence_conflicts__isoform on QNAME(isoform_sequence_conflicts)(isoform)IDXPARAMS();
create index isoform_sequence_conflicts__annotation on QNAME(isoform_sequence_conflicts)(annotation)IDXPARAMS();
grant select on QNAME(isoform_sequence_conflicts) to SPARQL;

create index isoform_short_sequence_motifs__isoform on QNAME(isoform_short_sequence_motifs)(isoform)IDXPARAMS();
create index isoform_short_sequence_motifs__annotation on QNAME(isoform_short_sequence_motifs)(annotation)IDXPARAMS();
grant select on QNAME(isoform_short_sequence_motifs) to SPARQL;

create index isoform_sites__isoform on QNAME(isoform_sites)(isoform)IDXPARAMS();
create index isoform_sites__annotation on QNAME(isoform_sites)(annotation)IDXPARAMS();
grant select on QNAME(isoform_sites) to SPARQL;

create index isoform_small_molecule_interactions__isoform on QNAME(isoform_small_molecule_interactions)(isoform)IDXPARAMS();
create index isoform_small_molecule_interactions__annotation on QNAME(isoform_small_molecule_interactions)(annotation)IDXPARAMS();
grant select on QNAME(isoform_small_molecule_interactions) to SPARQL;

create index isoform_srm_peptide_mappings__isoform on QNAME(isoform_srm_peptide_mappings)(isoform)IDXPARAMS();
create index isoform_srm_peptide_mappings__annotation on QNAME(isoform_srm_peptide_mappings)(annotation)IDXPARAMS();
grant select on QNAME(isoform_srm_peptide_mappings) to SPARQL;

create index isoform_subcellular_locations__isoform on QNAME(isoform_subcellular_locations)(isoform)IDXPARAMS();
create index isoform_subcellular_locations__annotation on QNAME(isoform_subcellular_locations)(annotation)IDXPARAMS();
grant select on QNAME(isoform_subcellular_locations) to SPARQL;

create index isoform_subcellular_location_notes__isoform on QNAME(isoform_subcellular_location_notes)(isoform)IDXPARAMS();
create index isoform_subcellular_location_notes__annotation on QNAME(isoform_subcellular_location_notes)(annotation)IDXPARAMS();
grant select on QNAME(isoform_subcellular_location_notes) to SPARQL;

create index isoform_topological_domains__isoform on QNAME(isoform_topological_domains)(isoform)IDXPARAMS();
create index isoform_topological_domains__annotation on QNAME(isoform_topological_domains)(annotation)IDXPARAMS();
grant select on QNAME(isoform_topological_domains) to SPARQL;

create index isoform_topologies__isoform on QNAME(isoform_topologies)(isoform)IDXPARAMS();
create index isoform_topologies__annotation on QNAME(isoform_topologies)(annotation)IDXPARAMS();
grant select on QNAME(isoform_topologies) to SPARQL;

create index isoform_transmembrane_regions__isoform on QNAME(isoform_transmembrane_regions)(isoform)IDXPARAMS();
create index isoform_transmembrane_regions__annotation on QNAME(isoform_transmembrane_regions)(annotation)IDXPARAMS();
grant select on QNAME(isoform_transmembrane_regions) to SPARQL;

create index isoform_transport_activities__isoform on QNAME(isoform_transport_activities)(isoform)IDXPARAMS();
create index isoform_transport_activities__annotation on QNAME(isoform_transport_activities)(annotation)IDXPARAMS();
grant select on QNAME(isoform_transport_activities) to SPARQL;

create index isoform_turns__isoform on QNAME(isoform_turns)(isoform)IDXPARAMS();
create index isoform_turns__annotation on QNAME(isoform_turns)(annotation)IDXPARAMS();
grant select on QNAME(isoform_turns) to SPARQL;

create index isoform_undetected_expressions__isoform on QNAME(isoform_undetected_expressions)(isoform)IDXPARAMS();
create index isoform_undetected_expressions__annotation on QNAME(isoform_undetected_expressions)(annotation)IDXPARAMS();
grant select on QNAME(isoform_undetected_expressions) to SPARQL;

create index isoform_uniprot_keywords__isoform on QNAME(isoform_uniprot_keywords)(isoform)IDXPARAMS();
create index isoform_uniprot_keywords__annotation on QNAME(isoform_uniprot_keywords)(annotation)IDXPARAMS();
grant select on QNAME(isoform_uniprot_keywords) to SPARQL;

create index isoform_variants__isoform on QNAME(isoform_variants)(isoform)IDXPARAMS();
create index isoform_variants__annotation on QNAME(isoform_variants)(annotation)IDXPARAMS();
grant select on QNAME(isoform_variants) to SPARQL;

create index isoform_variant_infos__isoform on QNAME(isoform_variant_infos)(isoform)IDXPARAMS();
create index isoform_variant_infos__annotation on QNAME(isoform_variant_infos)(annotation)IDXPARAMS();
grant select on QNAME(isoform_variant_infos) to SPARQL;

create index isoform_zinc_finger_regions__isoform on QNAME(isoform_zinc_finger_regions)(isoform)IDXPARAMS();
create index isoform_zinc_finger_regions__annotation on QNAME(isoform_zinc_finger_regions)(annotation)IDXPARAMS();
grant select on QNAME(isoform_zinc_finger_regions) to SPARQL;

create index isoform_interaction_mappings__isoform on QNAME(isoform_interaction_mappings)(isoform)IDXPARAMS();
create index isoform_interaction_mappings__annotation on QNAME(isoform_interaction_mappings)(annotation)IDXPARAMS();
grant select on QNAME(isoform_interaction_mappings) to SPARQL;

if(SERVER-MODE, `virtuoso-indirect', `create index entry_bases__iri on QNAME(entry_bases)(iri)IDXPARAMS();')
create index entry_bases__existence on QNAME(entry_bases)(existence)IDXPARAMS();
create index entry_bases__uniprot on QNAME(entry_bases)(uniprot)IDXPARAMS();
create index entry_bases__isoform_count on QNAME(entry_bases)(isoform_count)IDXPARAMS();
grant select on QNAME(entry_bases) to SPARQL;

create index entry_classifiers__entry on QNAME(entry_classifiers)(entry)IDXPARAMS();
create index entry_classifiers__classifier on QNAME(entry_classifiers)(classifier)IDXPARAMS();
grant select on QNAME(entry_classifiers) to SPARQL;

create index entry_genes__entry on QNAME(entry_genes)(entry)IDXPARAMS();
create index entry_genes__gene on QNAME(entry_genes)(gene)IDXPARAMS();
grant select on QNAME(entry_genes) to SPARQL;

create index entry_publication_references__entry on QNAME(entry_publication_references)(entry)IDXPARAMS();
create index entry_publication_references__publication on QNAME(entry_publication_references)(publication)IDXPARAMS();
grant select on QNAME(entry_publication_references) to SPARQL;

create index entry_recommended_names__entry on QNAME(entry_recommended_names)(entry)IDXPARAMS();
create index entry_recommended_names__name on QNAME(entry_recommended_names)(name)IDXPARAMS();
grant select on QNAME(entry_recommended_names) to SPARQL;

create index entry_alternative_names__entry on QNAME(entry_alternative_names)(entry)IDXPARAMS();
create index entry_alternative_names__name on QNAME(entry_alternative_names)(name)IDXPARAMS();
grant select on QNAME(entry_alternative_names) to SPARQL;

create index entry_additional_names__entry on QNAME(entry_additional_names)(entry)IDXPARAMS();
create index entry_additional_names__name_list on QNAME(entry_additional_names)(name_list)IDXPARAMS();
grant select on QNAME(entry_additional_names) to SPARQL;

create index entry_cleaved_region_names__entry on QNAME(entry_cleaved_region_names)(entry)IDXPARAMS();
create index entry_cleaved_region_names__name_list on QNAME(entry_cleaved_region_names)(name_list)IDXPARAMS();
grant select on QNAME(entry_cleaved_region_names) to SPARQL;

create index entry_functional_region_names__entry on QNAME(entry_functional_region_names)(entry)IDXPARAMS();
create index entry_functional_region_names__name_list on QNAME(entry_functional_region_names)(name_list)IDXPARAMS();
grant select on QNAME(entry_functional_region_names) to SPARQL;

if(SERVER-MODE, `virtuoso-indirect', `create index proteoform_bases__iri on QNAME(proteoform_bases)(iri)IDXPARAMS();')
create index proteoform_bases__label on QNAME(proteoform_bases)(label)IDXPARAMS();
grant select on QNAME(proteoform_bases) to SPARQL;

create index proteoform_general_annotations__proteoform on QNAME(proteoform_general_annotations)(proteoform)IDXPARAMS();
create index proteoform_general_annotations__annotation on QNAME(proteoform_general_annotations)(annotation)IDXPARAMS();
grant select on QNAME(proteoform_general_annotations) to SPARQL;

create index proteoform_generic_phenotypes__proteoform on QNAME(proteoform_generic_phenotypes)(proteoform)IDXPARAMS();
create index proteoform_generic_phenotypes__annotation on QNAME(proteoform_generic_phenotypes)(annotation)IDXPARAMS();
grant select on QNAME(proteoform_generic_phenotypes) to SPARQL;

create index proteoform_modifications__proteoform on QNAME(proteoform_modifications)(proteoform)IDXPARAMS();
create index proteoform_modifications__annotation on QNAME(proteoform_modifications)(annotation)IDXPARAMS();
grant select on QNAME(proteoform_modifications) to SPARQL;

create index proteoform_phenotypic_variations__proteoform on QNAME(proteoform_phenotypic_variations)(proteoform)IDXPARAMS();
create index proteoform_phenotypic_variations__annotation on QNAME(proteoform_phenotypic_variations)(annotation)IDXPARAMS();
grant select on QNAME(proteoform_phenotypic_variations) to SPARQL;

create index proteoform_positional_annotations__proteoform on QNAME(proteoform_positional_annotations)(proteoform)IDXPARAMS();
create index proteoform_positional_annotations__annotation on QNAME(proteoform_positional_annotations)(annotation)IDXPARAMS();
grant select on QNAME(proteoform_positional_annotations) to SPARQL;

create index proteoform_disease_related_variants__proteoform on QNAME(proteoform_disease_related_variants)(proteoform)IDXPARAMS();
create index proteoform_disease_related_variants__annotation on QNAME(proteoform_disease_related_variants)(annotation)IDXPARAMS();
grant select on QNAME(proteoform_disease_related_variants) to SPARQL;

if(SERVER-MODE, `virtuoso-indirect', `create index gene_bases__iri on QNAME(gene_bases)(iri)IDXPARAMS();')
create index gene_bases__gene_begin on QNAME(gene_bases)(gene_begin)IDXPARAMS();
create index gene_bases__gene_end on QNAME(gene_bases)(gene_end)IDXPARAMS();
create index gene_bases__length on QNAME(gene_bases)(length)IDXPARAMS();
create index gene_bases__band on QNAME(gene_bases)(band)IDXPARAMS();
create index gene_bases__chromosome on QNAME(gene_bases)(chromosome)IDXPARAMS();
create index gene_bases__strand on QNAME(gene_bases)(strand)IDXPARAMS();
grant select on QNAME(gene_bases) to SPARQL;

create index gene_best_mappings__gene on QNAME(gene_best_mappings)(gene)IDXPARAMS();
create index gene_best_mappings__mapping on QNAME(gene_best_mappings)(mapping)IDXPARAMS();
grant select on QNAME(gene_best_mappings) to SPARQL;

create index gene_names__gene on QNAME(gene_names)(gene)IDXPARAMS();
create index gene_names__name on QNAME(gene_names)(name)IDXPARAMS();
grant select on QNAME(gene_names) to SPARQL;

create index protein_sequence_bases__isoform on QNAME(protein_sequence_bases)(isoform)IDXPARAMS();
create index protein_sequence_bases__length on QNAME(protein_sequence_bases)(length)IDXPARAMS();
create index protein_sequence_bases__molecular_weight on QNAME(protein_sequence_bases)(molecular_weight)IDXPARAMS();
create index protein_sequence_bases__isoelectric_point on QNAME(protein_sequence_bases)(isoelectric_point)IDXPARAMS();
--create index protein_sequence_bases__chain on QNAME(protein_sequence_bases)(chain);
grant select on QNAME(protein_sequence_bases) to SPARQL;

create index family_info_bases__entry on QNAME(family_info_bases)(entry)IDXPARAMS();
create index family_info_bases__term on QNAME(family_info_bases)(term)IDXPARAMS();
create index family_info_bases__region on QNAME(family_info_bases)(region)IDXPARAMS()WHERE(region is not null);
create index family_info_bases__description on QNAME(family_info_bases)(description)IDXPARAMS();
grant select on QNAME(family_info_bases) to SPARQL;

create index history_bases__entry on QNAME(history_bases)(entry)IDXPARAMS();
create index history_bases__integrated on QNAME(history_bases)(integrated)IDXPARAMS();
create index history_bases__updated on QNAME(history_bases)(updated)IDXPARAMS();
create index history_bases__last_sequence_update on QNAME(history_bases)(last_sequence_update)IDXPARAMS()WHERE(last_sequence_update is not null);
create index history_bases__version on QNAME(history_bases)(version)IDXPARAMS()WHERE(version is not null);
create index history_bases__sequence_version on QNAME(history_bases)(sequence_version)IDXPARAMS()WHERE(sequence_version is not null);
create index history_bases__name on QNAME(history_bases)(name)IDXPARAMS();
grant select on QNAME(history_bases) to SPARQL;

create index identifier_bases__entry on QNAME(identifier_bases)(entry)IDXPARAMS();
create index identifier_bases__provenance on QNAME(identifier_bases)(provenance)IDXPARAMS();
create index identifier_bases__accession on QNAME(identifier_bases)(accession)IDXPARAMS();
grant select on QNAME(identifier_bases) to SPARQL;

create index entry_xref_bases__entry on QNAME(entry_xref_bases)(entry)IDXPARAMS();
create index entry_xref_bases__provenance on QNAME(entry_xref_bases)(provenance)IDXPARAMS();
create index entry_xref_bases__accession on QNAME(entry_xref_bases)(accession)IDXPARAMS();
grant select on QNAME(entry_xref_bases) to SPARQL;

create index evidence_xref_bases__evidence on QNAME(evidence_xref_bases)(evidence)IDXPARAMS();
create index evidence_xref_bases__provenance on QNAME(evidence_xref_bases)(provenance)IDXPARAMS();
create index evidence_xref_bases__accession on QNAME(evidence_xref_bases)(accession)IDXPARAMS();
grant select on QNAME(evidence_xref_bases) to SPARQL;

create index chebi_xref_bases__annotation on QNAME(chebi_xref_bases)(annotation)IDXPARAMS();
create index chebi_xref_bases__chebi on QNAME(chebi_xref_bases)(chebi)IDXPARAMS();
create index chebi_xref_bases__accession on QNAME(chebi_xref_bases)(accession)IDXPARAMS();
create index chebi_xref_bases__label on QNAME(chebi_xref_bases)(label)IDXPARAMS();
grant select on QNAME(chebi_xref_bases) to SPARQL;

create index drugbank_xref_bases__annotation on QNAME(drugbank_xref_bases)(annotation)IDXPARAMS();
create index drugbank_xref_bases__drugbank on QNAME(drugbank_xref_bases)(drugbank)IDXPARAMS();
create index drugbank_xref_bases__accession on QNAME(drugbank_xref_bases)(accession)IDXPARAMS();
create index drugbank_xref_bases__label on QNAME(drugbank_xref_bases)(label)IDXPARAMS();
grant select on QNAME(drugbank_xref_bases) to SPARQL;

create index uniprot_xref_bases__annotation on QNAME(uniprot_xref_bases)(annotation)IDXPARAMS();
create index uniprot_xref_bases__accession on QNAME(uniprot_xref_bases)(accession)IDXPARAMS();
create index uniprot_xref_bases__label on QNAME(uniprot_xref_bases)(label)IDXPARAMS();
create index uniprot_xref_bases__uniprot on QNAME(uniprot_xref_bases)(uniprot)IDXPARAMS();
grant select on QNAME(uniprot_xref_bases) to SPARQL;

create index name_bases__full_name on QNAME(name_bases)(full_name)IDXPARAMS()WHERE(full_name is not null);
create index name_bases__short_name on QNAME(name_bases)(short_name)IDXPARAMS()WHERE(short_name is not null);
create index name_bases__full_region_name on QNAME(name_bases)(full_region_name)IDXPARAMS()WHERE(full_region_name is not null);
create index name_bases__short_region_name on QNAME(name_bases)(short_region_name)IDXPARAMS()WHERE(short_region_name is not null);
create index name_bases__ec_enzyme_name on QNAME(name_bases)(ec_enzyme_name)IDXPARAMS()WHERE(ec_enzyme_name is not null);
create index name_bases__cd_antigen on QNAME(name_bases)(cd_antigen)IDXPARAMS()WHERE(cd_antigen is not null);
create index name_bases__inn_name on QNAME(name_bases)(inn_name)IDXPARAMS()WHERE(inn_name is not null);
create index name_bases__allergen on QNAME(name_bases)(allergen)IDXPARAMS()WHERE(allergen is not null);
grant select on QNAME(name_bases) to SPARQL;

grant select on QNAME(name_list_bases) to SPARQL;

create index name_list_recommended_names__list on QNAME(name_list_recommended_names)(list)IDXPARAMS();
create index name_list_recommended_names__name on QNAME(name_list_recommended_names)(name)IDXPARAMS();
grant select on QNAME(name_list_recommended_names) to SPARQL;

create index name_list_alternative_names__list on QNAME(name_list_alternative_names)(list)IDXPARAMS();
create index name_list_alternative_names__name on QNAME(name_list_alternative_names)(name)IDXPARAMS();
grant select on QNAME(name_list_alternative_names) to SPARQL;

