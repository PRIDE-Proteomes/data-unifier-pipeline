package uk.ac.ebi.pride.proteomes.pipeline.unifier.mapping.uniqueness;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.SymbolicPeptide;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.protein.PeptideProtein;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * User: ntoro
 * Date: 17/12/2013
 * Time: 15:48
 */
public class ProteinMappingUniquenessProcessor implements ItemProcessor<SymbolicPeptide, Collection<PeptideProtein>> {


    @Override
    @Transactional(readOnly = true)
    public Collection<PeptideProtein> process(SymbolicPeptide item) throws Exception {
        Set<String> protSet = new HashSet<String>();

        //In this way we only get the different proteins acs
        for (PeptideProtein peptideProtein : item.getProteins()) {
            protSet.add(peptideProtein.getProteinAccession());
        }

        for (PeptideProtein peptideProtein : item.getProteins()) {
            peptideProtein.setUniqueness(protSet.size());
        }

        protSet = null;

        return item.getProteins();
    }

}
