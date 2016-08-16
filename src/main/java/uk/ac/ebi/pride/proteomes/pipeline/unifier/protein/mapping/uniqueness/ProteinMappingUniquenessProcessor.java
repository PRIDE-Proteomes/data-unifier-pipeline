package uk.ac.ebi.pride.proteomes.pipeline.unifier.protein.mapping.uniqueness;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.Peptide;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.PeptideRepository;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.protein.PeptideProtein;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * User: ntoro
 * Date: 17/12/2013
 * Time: 15:48
 */
public class ProteinMappingUniquenessProcessor implements ItemProcessor<Long, Collection<PeptideProtein>> {

    private static final Logger logger = LoggerFactory.getLogger(ProteinMappingUniquenessProcessor.class);

    @Autowired
    private PeptideRepository peptideRepository;

    @Override
    @Transactional(readOnly = true)
    public Collection<PeptideProtein> process(Long itemId) throws Exception {
        Set<String> protSet = new HashSet<String>();

        Peptide item = peptideRepository.findOne(itemId);

        if (item == null) {
            logger.error("A symbolic peptide with id " + itemId + " cannot be found in the db");
            return null;
        }

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
