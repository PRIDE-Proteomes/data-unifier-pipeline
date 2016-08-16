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
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.PeptideRepository;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.SymbolicPeptide;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.protein.PeptideProtein;

import java.util.Collection;

/**
 * User: ntoro
 * Date: 27/01/2014
 * Time: 10:32
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Rollback
@Transactional(transactionManager = "transactionManager")
@ContextConfiguration(locations = {"classpath*:/META-INF/context/data-unifier-uniqueness-hsql-test-context.xml"})
@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        StepScopeTestExecutionListener.class})
public class ProteinMappingUniquenessProcessorTest {

    private static final int UNIQUENESS = 3;

    @Autowired
    @Qualifier(value = "proteinMappingUniquenessProcessor")
    private ItemProcessor<Long,Collection<PeptideProtein>> itemProcessor;

    @Autowired
    private PeptideRepository peptideRepository;

    private static final String SEQUENCE = "PPCEDEIDEFLK";
    private static final Integer TAXID = 9606;

    @Test
    @Transactional
    @DirtiesContext
    public void testProcess() throws Exception {

        SymbolicPeptide item = peptideRepository.findSymbolicPeptideBySequenceAndTaxid(SEQUENCE, TAXID);
        Collection<PeptideProtein> other = itemProcessor.process(item.getPeptideId());

        int count = 0;
        for (PeptideProtein peptideProtein : other) {
            peptideProtein.getUniqueness();
            System.out.println(peptideProtein);
            count++;
        }

        Assert.assertEquals(UNIQUENESS, count);

    }
}
