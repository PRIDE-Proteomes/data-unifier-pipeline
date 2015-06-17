package uk.ac.ebi.pride.proteomes.pipeline.unifier.integration.processor;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.ItemProcessor;
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
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.PeptideRepository;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.SymbolicPeptide;

/**
 * User: ntoro
 * Date: 27/01/2014
 * Time: 10:32
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/context/data-unifier-hsql-test-context.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        StepScopeTestExecutionListener.class})
public class SymbolicPeptidePropagatorProcessorTest {

    private static final int NUM_ASSAYS = 5;
    private static final int NUM_TISSUES = 1;
    private static final int NUM_DISEASES = 0;
    private static final int NUM_CELL_TYPES = 1;
    private static final int NUM_MODS = 6;

    @Autowired
    @Qualifier(value = "symbolicPeptidePropagatorProcessor")
    private ItemProcessor<SymbolicPeptide,SymbolicPeptide> itemProcessor;

    @Autowired
    private PeptideRepository peptideRepository;

    private static final String SEQUENCE = "LKSGIGAVVLPGVSTADISSNK";
    private static final Integer TAXID = 9606;

    @Test
    @Transactional
    @DirtiesContext
    public void testProcess() throws Exception {

        SymbolicPeptide item = peptideRepository.findSymbolicPeptideBySequenceAndTaxid(SEQUENCE, TAXID);
        SymbolicPeptide other = itemProcessor.process(item);

        Assert.assertEquals(other.getAssays().size(), NUM_ASSAYS);
        Assert.assertEquals(other.getTissues().size(), NUM_TISSUES);
        Assert.assertEquals(other.getDiseases().size(), NUM_DISEASES);
        Assert.assertEquals(other.getCellTypes().size(), NUM_CELL_TYPES);
        Assert.assertEquals(other.getModificationLocations().size(), NUM_MODS);

    }
}
