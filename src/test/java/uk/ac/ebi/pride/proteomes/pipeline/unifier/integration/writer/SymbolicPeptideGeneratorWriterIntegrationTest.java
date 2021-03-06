package uk.ac.ebi.pride.proteomes.pipeline.unifier.integration.writer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.database.JpaItemWriter;
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
import uk.ac.ebi.pride.proteomes.db.core.api.quality.ScoreRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ntoro
 * Date: 08/10/2013
 * Time: 22:35
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/context/data-unifier-hsql-test-context.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
public class SymbolicPeptideGeneratorWriterIntegrationTest {

    private static final String SEQUENCE = "GPAVLI";
    private static final Integer TAXID = 9606;
    private static final String REPRESENTATION = "[GPAVLI|9606]";
    private static final long DEFAULT_SCORE = 1;

    @Autowired
    @Qualifier(value = "symbolicPeptideGeneratorWriter")
    private JpaItemWriter<SymbolicPeptide> jpaItemWriter;

    @Autowired
    private PeptideRepository peptideRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Test
    @DirtiesContext
    @Transactional
    public void testWriteFirstElement() throws Exception {
        SymbolicPeptide symbolicPeptide = new SymbolicPeptide();
        symbolicPeptide.setSequence(SEQUENCE);
        symbolicPeptide.setTaxid(TAXID);
        symbolicPeptide.setPeptideRepresentation(REPRESENTATION);
        symbolicPeptide.setScore(scoreRepository.findOne(DEFAULT_SCORE));
        List<SymbolicPeptide> list = new ArrayList<SymbolicPeptide>();
        list.add(symbolicPeptide);

        jpaItemWriter.write(list);

        SymbolicPeptide other = peptideRepository.findSymbolicPeptideBySequenceAndTaxid(SEQUENCE, TAXID);

        Assert.assertEquals(symbolicPeptide.getTaxid(), other.getTaxid());
        Assert.assertEquals(symbolicPeptide.getSequence(), other.getSequence());
        Assert.assertEquals(symbolicPeptide.getScore(), other.getScore());

        peptideRepository.delete(other.getPeptideId());
    }

}
