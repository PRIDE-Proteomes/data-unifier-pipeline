package uk.ac.ebi.pride.proteomes.pipeline.unifier.integration.reader;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.pride.proteomes.pipeline.unifier.release.report.PeptideMappingReportLine;

import static org.springframework.batch.test.MetaDataInstanceFactory.createStepExecution;

/**
 * User: ntoro
 * Date: 08/10/2013
 * Time: 22:34
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/context/data-unifier-hsql-test-context.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        StepScopeTestExecutionListener.class})
public class ReleaseReportPeptideMappingReaderIntegrationTest {

    private static final int NUM_MAPPED_PEPS = 6;

    @Autowired
    @Qualifier(value = "releaseReportPeptideMappingReader")
    private ItemReader<PeptideMappingReportLine> jdbcPagingItemReader;

    public StepExecution getStepExecution() {
        StepExecution execution = createStepExecution();
        execution.getExecutionContext().putString("taxid", "9606");
        return execution;
    }

    @Test
    @DirtiesContext
    @Transactional(readOnly = true)
    public void testReader() throws Exception {
        int count = 0;
        PeptideMappingReportLine reportLine;

        while ((reportLine = jdbcPagingItemReader.read()) != null) {
            count++;
            System.out.println(reportLine);
        }

        Assert.assertEquals(NUM_MAPPED_PEPS, count);
    }

// Other method to test the reader, it substitute setUp, tearDown add testReader
//	@Test
//	@DirtiesContext
//	public void testReader() throws Exception {
//		int count = StepScopeTestUtils.doInStepScope(getStepExecution(),
//				new Callable<Integer>() {
//					@Override
//					public Integer call() throws Exception {
//						int count = 0;
//						try {
//							(jdbcPagingItemReader).open(new ExecutionContext());
//							while (jdbcPagingItemReader.read() != null) {
//								count++;
//							}
//							return count;
//						} finally {
//							jdbcPagingItemReader.close();
//						}
//					}
//				});
//		Assert.assertEquals(DIFF_SEQUENCES_BY_SPECIES, count);
//	}
}
