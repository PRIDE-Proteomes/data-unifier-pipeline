package uk.ac.ebi.pride.proteomes.pipeline.unifier.integration.reader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.pride.proteomes.db.core.api.protein.groups.ProteinGroup;

import static junit.framework.Assert.assertEquals;
import static org.springframework.batch.test.MetaDataInstanceFactory.createStepExecution;

/**
 * User: ntoro
 * Date: 10/10/2013
 * Time: 11:25
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/context/data-unifier-gene-grouping-hsql-test-context.xml"})
@TestExecutionListeners({StepScopeTestExecutionListener.class})
public class ProteinGeneGroupingReaderIntegrationTest extends AbstractJUnit4SpringContextTests {

    private static final int GENE_GROUPS_IN_FILE = 11;

    @Autowired
    @Qualifier(value = "proteinGroupingReader")
    private ItemStreamReader<ProteinGroup> proteinGroupingItemStreamReader;

    public StepExecution getStepExecution() {
        StepExecution execution = createStepExecution();
        execution.getExecutionContext().putString("taxid", "3702");
        return execution;
    }

    @Before
    public void setUp() throws Exception {
        ((ItemStreamReader) proteinGroupingItemStreamReader).open(new ExecutionContext());
    }

    @After
    public void tearDown() throws Exception {
        ((ItemStreamReader) proteinGroupingItemStreamReader).close();
    }

    @Test
    @DirtiesContext
    public void testReader() throws Exception {
        int count = 0;
        ProteinGroup group;
        while ((group = proteinGroupingItemStreamReader.read()) != null) {
            count++;
            System.out.println(group);
        }

        assertEquals(GENE_GROUPS_IN_FILE, count);
    }
}
