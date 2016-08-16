package uk.ac.ebi.pride.proteomes.pipeline.unifier.integration.processor;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.pride.proteomes.db.core.api.protein.Protein;
import uk.ac.ebi.pride.proteomes.db.core.api.protein.ProteinRepository;

/**
 * User: ntoro
 * Date: 27/01/2014
 * Time: 10:32
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Rollback
@Transactional(transactionManager = "transactionManager")
@ContextConfiguration(locations = {"classpath*:/META-INF/context/data-unifier-hsql-test-context.xml"})
@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        StepScopeTestExecutionListener.class})
public class ProteinEnricherItemProcessorTest {

    @Autowired
    @Qualifier(value = "proteinEnricherProcessor")
    private ItemProcessor<String,Protein> itemProcessor;

    @Autowired
    private ProteinRepository proteinRepository;

    private static final String PROTEIN_AC_NO_FEATURES = "Q8NEZ4";
    private static final String PROTEIN_AC_ISO = "C0LGN2-2";
    private static final String PROTEIN_AC_FEATURES = "C0LGN2";
    private static final int NUM_FEATURES = 2;


    @Test
    @Transactional
    @DirtiesContext
    public void testProcess() throws Exception {

        Protein other = itemProcessor.process(PROTEIN_AC_NO_FEATURES);

        //As Q8NEZ4 doesn't have any feature the item return is null to avoid write it again in the db becasue there is no new information
        Assert.assertNull(other);

    }

    @Test
    @Transactional
    @DirtiesContext
    public void testProcessIsoforms() throws Exception {

        Protein other = itemProcessor.process(PROTEIN_AC_ISO);

        //C0LGN2-2 doesn't have any feature because it is an isoform, and the annotations are associated to the canonical
        Assert.assertNull(other);

    }

    @Test
    @Transactional
    @DirtiesContext
    public void testProcessWithFeatures() throws Exception {

        Protein other = itemProcessor.process(PROTEIN_AC_FEATURES);

        Assert.assertEquals(NUM_FEATURES, other.getFeatures().size());


    }
}
