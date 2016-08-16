package uk.ac.ebi.pride.proteomes.pipeline.unifier.integration.writer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.ItemWriter;
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
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.Peptide;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.PeptideRepository;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.SymbolicPeptide;
import uk.ac.ebi.pride.proteomes.db.core.api.quality.ScoreRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ntoro
 * Date: 17/10/2013
 * Time: 11:34
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Rollback
@Transactional(transactionManager = "transactionManager")
@ContextConfiguration(locations = {"classpath:/META-INF/context/data-unifier-hsql-test-context.xml"})
@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
public class SymbolicPeptideFilterUpdaterIntegrationTest  {

    private static final String SEQUENCE = "EWKSNVYLAR";
    private static final Integer TAXID = 9606;
    private static final String REPRESENTATION = "[EWKSNVYLAR|9606]";
    private static final long DEFAULT_SCORE = 1;

    @Autowired
    @Qualifier(value = "symbolicPeptideFilterUpdater")
    private ItemWriter<Peptide> symbolicPeptideFilterUpdater;

    @Autowired
    PeptideRepository peptideRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Test
    @DirtiesContext
    @Transactional
    public void testDeleteOneElement() throws Exception {

        SymbolicPeptide symbolicPeptide = new SymbolicPeptide();
        symbolicPeptide.setSequence(SEQUENCE);
        symbolicPeptide.setTaxid(TAXID);
        symbolicPeptide.setPeptideRepresentation(REPRESENTATION);
        symbolicPeptide.setScore(scoreRepository.findOne(DEFAULT_SCORE));

        List<SymbolicPeptide> list = new ArrayList<SymbolicPeptide>();
        list.add(symbolicPeptide);

        symbolicPeptideFilterUpdater.write(list);

        symbolicPeptide = peptideRepository.findSymbolicPeptideBySequenceAndTaxid(SEQUENCE,TAXID);

        Assert.assertNull(symbolicPeptide);

        // Two keep consistent the DB we write again the item
        SymbolicPeptide rewritePeptide = new SymbolicPeptide();
        rewritePeptide.setSequence(SEQUENCE);
        rewritePeptide.setTaxid(TAXID);
        rewritePeptide.setPeptideRepresentation(REPRESENTATION);
        rewritePeptide.setScore(scoreRepository.findOne(DEFAULT_SCORE));

        peptideRepository.save(rewritePeptide);

    }
}
