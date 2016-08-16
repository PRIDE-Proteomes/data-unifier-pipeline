package uk.ac.ebi.pride.proteomes.pipeline.unifier.integration.tasklet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.pride.proteomes.pipeline.unifier.partitioner.RangePartitioner;

import static org.springframework.batch.test.MetaDataInstanceFactory.createStepExecution;

/**
 * User: ntoro
 * Date: 17/01/2014
 * Time: 16:03
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Rollback
@Transactional(transactionManager = "transactionManager")
@ContextConfiguration(locations = {"classpath*:/META-INF/context/data-unifier-indexer-hsql-test-context.xml"})
@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        StepScopeTestExecutionListener.class})
public class PeptiformIndexerTaskletIntegrationTest {

    @Autowired
    private Tasklet peptiformIndexerTasklet;


    @Test
    @DirtiesContext
    @Transactional(readOnly = true)
    public void testTasklet() throws Exception {

        StepExecution execution = createStepExecution();
        execution.getExecutionContext().putInt(RangePartitioner.TAXID_KEY_NAME, 9606);
        execution.getExecutionContext().putLong(RangePartitioner.MIN_KEY_NAME, 7);
        execution.getExecutionContext().putLong(RangePartitioner.MAX_KEY_NAME, 10);


        ChunkContext chunkContext = new ChunkContext(new StepContext(execution));
        StepContribution stepContribution = new StepContribution(execution);

        peptiformIndexerTasklet.execute(stepContribution, chunkContext);

    }
}
