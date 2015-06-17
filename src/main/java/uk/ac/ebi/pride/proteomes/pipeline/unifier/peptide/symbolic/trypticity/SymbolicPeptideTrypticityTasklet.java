package uk.ac.ebi.pride.proteomes.pipeline.unifier.peptide.symbolic.trypticity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.PeptideRepository;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.SymbolicPeptide;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.protein.PeptideProteinRepository;
import uk.ac.ebi.pride.proteomes.db.core.api.utils.PeptideUtils;
import uk.ac.ebi.pride.proteomes.pipeline.unifier.partitioner.RangePartitioner;

/**
 * User: ntoro
 * Date: 10/02/2014
 * Time: 13:28
 */


public class SymbolicPeptideTrypticityTasklet implements Tasklet {

    private static final Logger logger = LoggerFactory.getLogger(SymbolicPeptideTrypticityTasklet.class);

    @Autowired
    private PeptideRepository peptideRepository;

    @Autowired
    private PeptideProteinRepository peptideProteinRepository;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        // The trypticity calculation belongs to the symbolic peptide instead of the mapping part of the pipeline

        Long minValue = (Long) chunkContext.getStepContext().getStepExecutionContext().get(RangePartitioner.MIN_KEY_NAME);
        Long maxValue = (Long) chunkContext.getStepContext().getStepExecutionContext().get(RangePartitioner.MAX_KEY_NAME);
        Integer taxId = (Integer) chunkContext.getStepContext().getStepExecutionContext().get(RangePartitioner.TAXID_KEY_NAME);

        Iterable<SymbolicPeptide> peptides = peptideRepository.findAllSymbolicPeptidesByTaxidAndPeptideIdBetween(taxId, minValue, maxValue);

        for (SymbolicPeptide peptide : peptides) {

            peptide.setMissedCleavages(PeptideUtils.numMissedCleavageSites(peptide.getSequence()));
            logger.debug("Sequence: " + peptide.getSequence() + " has " + peptide.getMissedCleavages() + " missed cleavages");

            peptideProteinRepository.save(peptide.getProteins());
        }

        return RepeatStatus.FINISHED;
    }

}
