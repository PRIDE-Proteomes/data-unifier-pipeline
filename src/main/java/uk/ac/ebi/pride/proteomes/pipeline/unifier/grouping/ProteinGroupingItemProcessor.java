package uk.ac.ebi.pride.proteomes.pipeline.unifier.grouping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.pride.proteomes.db.core.api.protein.Protein;
import uk.ac.ebi.pride.proteomes.db.core.api.protein.ProteinRepository;
import uk.ac.ebi.pride.proteomes.db.core.api.protein.groups.ProteinGroup;

import java.util.HashSet;
import java.util.Set;

/**
 * User: ntoro
 * Date: 07/12/2013
 * Time: 01:25
 */
public class ProteinGroupingItemProcessor implements ItemProcessor<Group, ProteinGroup> {

    private static final Logger logger = LoggerFactory.getLogger(ProteinGroupingItemProcessor.class);

    @Autowired
    ProteinRepository proteinRepository;

    @Override
    public ProteinGroup process(Group item) throws Exception {

        Set<Protein> proteins = new HashSet<Protein>(proteinRepository.findAll(item.getProteinAccessions()));
        if (proteins.isEmpty()) {
            logger.warn("The proteins do not exist in the database" + item.getProteinAccessions());
        } else {
            item.setProteins(proteins);
        }

        return ProteinGroupFactory.createProteinGroup(item);

    }
}
