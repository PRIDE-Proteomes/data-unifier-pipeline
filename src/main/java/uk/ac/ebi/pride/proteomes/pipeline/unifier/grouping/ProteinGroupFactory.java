package uk.ac.ebi.pride.proteomes.pipeline.unifier.grouping;

import uk.ac.ebi.pride.proteomes.db.core.api.protein.groups.EntryGroup;
import uk.ac.ebi.pride.proteomes.db.core.api.protein.groups.GeneGroup;
import uk.ac.ebi.pride.proteomes.db.core.api.protein.groups.ProteinGroup;
import uk.ac.ebi.pride.proteomes.db.core.api.protein.groups.ProteinGroupType;


/**
 * User: ntoro
 * Date: 10/12/2013
 * Time: 10:43
 */
public class ProteinGroupFactory {
    public static ProteinGroup createProteinGroup(Group group) {
        ProteinGroup proteinGroup = null;

        if (group.getType().equals(ProteinGroupType.ENTRY.getGroupName())) {
            proteinGroup = new EntryGroup();
        } else if (group.getType().equals(ProteinGroupType.GENE.getGroupName())) {
            proteinGroup = new GeneGroup();
        } else {
            throw new IllegalArgumentException("Unknown Protein Group type.");
        }

        proteinGroup.setId(group.getId());
        proteinGroup.setDescription(group.getDescription());
        proteinGroup.setTaxid(group.getTaxid());
        //We assume that all the proteins for this group are collected in this point
        proteinGroup.setProteins(group.getProteins());

        return proteinGroup;
    }
}

