package uk.ac.ebi.pride.proteomes.pipeline.unifier.peptide.symbolic.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.Peptide;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.PeptideRepository;

import java.util.List;

/**
 * User: ntoro
 * Date: 15/11/2013
 * Time: 12:15
 */
public class SymbolicPeptideFilterUpdater implements ItemWriter<Peptide> {

    private static final Logger logger = LoggerFactory.getLogger(SymbolicPeptideFilterUpdater.class);

    @Autowired
    PeptideRepository peptideRepository;

    @Override
    public void write(List<? extends Peptide> items) throws Exception {


        if (!items.isEmpty() && items != null) {
            for (Peptide item : items) {
                String representation = item.getPeptideRepresentation();
                String info = "The symbolic peptide " + representation + " has been deleted because the only evidence is a n/c terminal modification.";
                item = peptideRepository.findSymbolicPeptideBySequenceAndTaxid(item.getSequence(), item.getTaxid());
                if(item!=null) { //Maybe was deleted in a previous run
                    peptideRepository.delete(item.getPeptideId());
                    logger.info(info);
                }
                else {
                    logger.info("The symbolic peptide " + representation + " has not been found");
                }
            }
        }
    }
}
