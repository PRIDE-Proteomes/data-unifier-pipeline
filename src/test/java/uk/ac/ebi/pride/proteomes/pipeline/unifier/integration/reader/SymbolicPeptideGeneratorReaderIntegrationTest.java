package uk.ac.ebi.pride.proteomes.pipeline.unifier.integration.reader;

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
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.SymbolicPeptide;

import static junit.framework.Assert.assertEquals;
import static org.springframework.batch.test.MetaDataInstanceFactory.createStepExecution;

/**
 * User: ntoro
 * Date: 08/10/2013
 * Time: 22:34
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/context/data-unifier-hsql-test-context.xml"})
@TestExecutionListeners({StepScopeTestExecutionListener.class})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class SymbolicPeptideGeneratorReaderIntegrationTest extends AbstractJUnit4SpringContextTests {

	private static final int DIFF_SEQUENCES_BY_SPECIES = 31;

	@Autowired
    @Qualifier(value = "symbolicPeptideGeneratorReader")
	private ItemReader<SymbolicPeptide> jdbcPagingItemReader;

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
        SymbolicPeptide peptide;

		while ((peptide = jdbcPagingItemReader.read()) != null) {
			count++;
            System.out.println(peptide);
        }

		assertEquals(DIFF_SEQUENCES_BY_SPECIES, count);
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
//		assertEquals(DIFF_SEQUENCES_BY_SPECIES, count);
//	}
}
