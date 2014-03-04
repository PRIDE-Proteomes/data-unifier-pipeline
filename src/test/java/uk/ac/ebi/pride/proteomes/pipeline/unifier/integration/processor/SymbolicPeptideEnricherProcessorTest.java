package uk.ac.ebi.pride.proteomes.pipeline.unifier.integration.processor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.PeptideRepository;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.SymbolicPeptide;

import static junit.framework.Assert.assertEquals;

/**
 * User: ntoro
 * Date: 27/01/2014
 * Time: 10:32
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/context/data-unifier-hsql-test-context.xml"})
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class SymbolicPeptideEnricherProcessorTest extends AbstractJUnit4SpringContextTests {

    private static final int NUM_ASSAYS = 5;
    private static final int NUM_TISSUES = 1;
    private static final int NUM_DISEASES = 0;
    private static final int NUM_CELL_TYPES = 1;
    private static final int NUM_MODS = 6;

    @Autowired
    @Qualifier(value = "symbolicPeptideEnricherProcessor")
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

        assertEquals(other.getAssays().size(), NUM_ASSAYS);
        assertEquals(other.getTissues().size(), NUM_TISSUES);
        assertEquals(other.getDiseases().size(), NUM_DISEASES);
        assertEquals(other.getCellTypes().size(), NUM_CELL_TYPES);
        assertEquals(other.getModificationLocations().size(),NUM_MODS);

    }
}
