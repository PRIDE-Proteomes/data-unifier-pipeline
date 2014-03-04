package uk.ac.ebi.pride.proteomes.pipeline.unifier.mapping.enricher;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.pride.proteomes.db.core.api.modification.ModificationLocation;
import uk.ac.ebi.pride.proteomes.db.core.api.param.CellType;
import uk.ac.ebi.pride.proteomes.db.core.api.param.Disease;
import uk.ac.ebi.pride.proteomes.db.core.api.param.Tissue;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.SymbolicPeptide;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.protein.PeptideProtein;
import uk.ac.ebi.pride.proteomes.db.core.api.protein.Protein;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * User: ntoro
 * Date: 27/01/2014
 * Time: 09:34
 */
public class ProteinMappingEnricherProcessor implements ItemProcessor<Protein, Protein> {

    @Override
    @Transactional(readOnly = true)
    public Protein process(Protein item) throws Exception {


        Set<CellType> cellTypes = new HashSet<CellType>();
        Set<Disease> diseases = new HashSet<Disease>();
        Set<Tissue> tissues = new HashSet<Tissue>();
        SortedSet<ModificationLocation> mods = new TreeSet<ModificationLocation>();

        for (PeptideProtein peptideProtein : item.getPeptides()) {
            SymbolicPeptide symbolicPeptide = (SymbolicPeptide) peptideProtein.getPeptide();

            cellTypes.addAll(symbolicPeptide.getCellTypes());
            diseases.addAll(symbolicPeptide.getDiseases());
            tissues.addAll(symbolicPeptide.getTissues());

            //Inside de loop we translate the modifications from peptide position to protein position
            for (ModificationLocation mod : symbolicPeptide.getModificationLocations()) {
                ModificationLocation aux = new ModificationLocation();
                aux.setModId(mod.getModId());
                //n-terminal mod, we propagate the mod only to the n terminal position of the protein
                if(mod.getPosition()==0 ){
                    if(peptideProtein.getStartPosition()==1){
                        aux.setPosition(mod.getPosition() + peptideProtein.getStartPosition() - 1);
                        mods.add(aux);
                    }
                }
                //c-terminal mod, we propagate the mod only to the c terminal position of the protein
                else if(mod.getPosition()==symbolicPeptide.getSequence().length()+1){
                    if(peptideProtein.getStartPosition()==
                            peptideProtein.getProtein().getSequence().length()-symbolicPeptide.getSequence().length()+1){
                        aux.setPosition(mod.getPosition() + peptideProtein.getStartPosition() - 1);
                        mods.add(aux);
                    }
                }
                else {
                    aux.setPosition(mod.getPosition() + peptideProtein.getStartPosition() - 1);
                    mods.add(aux);
                }
            }
        }

        if (!cellTypes.isEmpty()) item.setCellTypes(cellTypes);
        if (!diseases.isEmpty()) item.setDiseases(diseases);
        if (!tissues.isEmpty()) item.setTissues(tissues);
        if (!mods.isEmpty()) item.setModificationLocations(mods);

        return item;
    }
}
