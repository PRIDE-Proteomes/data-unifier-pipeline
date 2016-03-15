package uk.ac.ebi.pride.proteomes.pipeline.unifier.peptiform.indexer;

import org.apache.solr.client.solrj.SolrServer;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.PeptideRepository;
import uk.ac.ebi.pride.proteomes.index.ProteomesIndexer;
import uk.ac.ebi.pride.proteomes.index.service.ProteomesIndexService;
import uk.ac.ebi.pride.proteomes.pipeline.unifier.partitioner.RangePartitioner;

import javax.annotation.Resource;

/**
 * @author ntoro
 * @since 01/05/15 10:53
 */
public class PeptiformIndexerTasklet implements Tasklet {

    @Autowired
    private PeptideRepository peptideRepository;

    @Resource
    private ProteomesIndexService proteomesIndexService;

    @Autowired
    private SolrServer proteomesSolrServer;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        Long minValue = (Long) chunkContext.getStepContext().getStepExecutionContext().get(RangePartitioner.MIN_KEY_NAME);
        Long maxValue = (Long) chunkContext.getStepContext().getStepExecutionContext().get(RangePartitioner.MAX_KEY_NAME);
        Integer taxId = (Integer) chunkContext.getStepContext().getStepExecutionContext().get(RangePartitioner.TAXID_KEY_NAME);

        //By default the index it will not take into account contaminant proteins)
        ProteomesIndexer indexer = new ProteomesIndexer(proteomesIndexService, peptideRepository, proteomesSolrServer);

        try {
            indexer.indexBySymbolicPeptidesTaxidAndPeptideIdInterval(taxId, minValue, maxValue, false);
        } catch (Exception e) {
            System.out.println("Unexpected exception: " + e.toString());
            e.printStackTrace();
        }

        return RepeatStatus.FINISHED;
    }

}
