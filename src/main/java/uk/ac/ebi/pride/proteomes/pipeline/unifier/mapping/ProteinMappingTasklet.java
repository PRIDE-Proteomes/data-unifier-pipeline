package uk.ac.ebi.pride.proteomes.pipeline.unifier.mapping;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.PeptideRepository;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.SymbolicPeptide;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.protein.PeptideProtein;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.protein.PeptideProteinRepository;
import uk.ac.ebi.pride.proteomes.db.core.api.protein.Protein;
import uk.ac.ebi.pride.proteomes.db.core.api.utils.ScoreUtils;
import uk.ac.ebi.pride.proteomes.pipeline.unifier.partitioner.RangePartitioner;
import uk.ac.ebi.pride.proteomes.pipeline.unifier.utils.DefaultHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * User: ntoro
 * Date: 02/10/2013
 * Time: 16:25
 */
public class ProteinMappingTasklet implements Tasklet {

    private static final Log logger = LogFactory.getLog(ProteinMappingTasklet.class);

    @Autowired
    private PeptideRepository peptideRepository;

    @Autowired
    private PeptideProteinRepository peptideProteinRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        Long minValue = (Long) chunkContext.getStepContext().getStepExecutionContext().get(RangePartitioner.MIN_KEY_NAME);
        Long maxValue = (Long) chunkContext.getStepContext().getStepExecutionContext().get(RangePartitioner.MAX_KEY_NAME);
        Integer taxId = (Integer) chunkContext.getStepContext().getStepExecutionContext().get(RangePartitioner.TAXID_KEY_NAME);

        Iterable<SymbolicPeptide> peptides = peptideRepository.findAllSymbolicPeptidesByTaxidAndPeptideIdBetween(taxId, minValue, maxValue);
        ProteinRowMapper rowMapper = new ProteinRowMapper();

        List<Protein> proteins = jdbcTemplate.query("SELECT PROTEIN.PROTEIN_ID AS PROTEIN_ID," +
                " PROTEIN.SEQUENCE AS PROTEIN_SEQUENCE FROM PRIDEPROT.PROTEIN WHERE TAXID = " + taxId, rowMapper);

        List<PeptideProtein> referencedPeptides = new ArrayList<PeptideProtein>();

        DefaultHashMap<SymbolicPeptide, Integer> uniquenessCount = new DefaultHashMap<SymbolicPeptide, Integer>();

        for (Protein protein : proteins) {
            for (SymbolicPeptide peptide : peptides) {
                Pattern pattern = Pattern.compile(peptide.getSequence());
                Matcher peptideMatcher = pattern.matcher(protein.getSequence());

                //Start looking in the next amino acid
                boolean hasNext = peptideMatcher.find();
                boolean matched = false;
                while (hasNext) {
                    PeptideProtein peptideProtein = new PeptideProtein();
                    peptideProtein.setProteinAccession(protein.getProteinAccession());
                    peptideProtein.setPeptideId(peptide.getPeptideId());
                    peptideProtein.setPeptide(peptide);
                    peptideProtein.setProtein(protein);
                    peptideProtein.setScore(ScoreUtils.defaultScore());

                    //The index of start is staring on zero, we need to shift it to 1
                    peptideProtein.setStartPosition(peptideMatcher.start() + 1);
                    referencedPeptides.add(peptideProtein);
                    hasNext = peptideMatcher.find();
                    matched = true;
                }

                if (matched) {
                    uniquenessCount.put(peptide, uniquenessCount.get(peptide, 0) + 1);
                }
            }
        }

        if (!referencedPeptides.isEmpty()) {
            // Update the peptides with the uniqueness factor
            for (PeptideProtein peptideProtein : referencedPeptides) {
                peptideProtein.setUniqueness(uniquenessCount.get((SymbolicPeptide) peptideProtein.getPeptide(), 0));
                logger.debug(peptideProtein);
            }

            peptideProteinRepository.save(referencedPeptides);
            logger.info("Num of referencedPeptides: " + referencedPeptides.size());
        }

        return RepeatStatus.FINISHED;
    }
}
