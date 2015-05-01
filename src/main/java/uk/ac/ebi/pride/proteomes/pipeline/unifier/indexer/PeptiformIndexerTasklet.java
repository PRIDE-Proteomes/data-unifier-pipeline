package uk.ac.ebi.pride.proteomes.pipeline.unifier.indexer;

import org.apache.solr.client.solrj.SolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.PeptideRepository;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.group.PeptideGroupRepository;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.protein.PeptideProteinRepository;
import uk.ac.ebi.pride.proteomes.index.ProteomesIndexer;
import uk.ac.ebi.pride.proteomes.index.service.ProteomesIndexService;
import uk.ac.ebi.pride.proteomes.pipeline.unifier.mapping.ProteinMappingTasklet;
import uk.ac.ebi.pride.proteomes.pipeline.unifier.partitioner.RangePartitioner;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author ntoro
 * @since 01/05/15 10:53
 */
public class PeptiformIndexerTasklet implements Tasklet {

    private static final Logger logger = LoggerFactory.getLogger(ProteinMappingTasklet.class);

    @Autowired
    private PeptideRepository peptideRepository;

    @Autowired
    private PeptideGroupRepository peptideGroupRepository;

    @Autowired
    private PeptideProteinRepository peptideProteinRepository;

    @Resource
    private ProteomesIndexService proteomesIndexService;

    @Autowired
    private SolrServer proteomesSolrServer;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        // The trypticity calculation belongs to the symbolic peptide instead of the mapping part of the pipeline

        Long minValue = (Long) chunkContext.getStepContext().getStepExecutionContext().get(RangePartitioner.MIN_KEY_NAME);
        Long maxValue = (Long) chunkContext.getStepContext().getStepExecutionContext().get(RangePartitioner.MAX_KEY_NAME);
        Integer taxId = (Integer) chunkContext.getStepContext().getStepExecutionContext().get(RangePartitioner.TAXID_KEY_NAME);

        ProteomesIndexer indexer = new ProteomesIndexer(proteomesIndexService, peptideRepository, peptideGroupRepository,  peptideProteinRepository, proteomesSolrServer);
        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("dd:MM:yy:HH:mm:ss");
        cal.setTimeInMillis(System.currentTimeMillis());
        System.out.println("Started indexing process at: " + df.format(cal.getTime()));

        try {
            long start = System.currentTimeMillis();
//                    indexer.deleteAll();
            indexer.indexBySymbolicPeptidesTaxidAndPeptideIdInterval(taxId, minValue, maxValue, false);
            long end = System.currentTimeMillis();
            System.out.println("Indexing time [ms]: " + (end-start));
        } catch (Exception e) {
            cal.setTimeInMillis(System.currentTimeMillis());
            System.out.println("Unexpected exception at: " + df.format(cal.getTime()) + " Exception: " + e.toString());
            e.printStackTrace();
        }

        logger.info("All done");
        return RepeatStatus.FINISHED;
    }

}
