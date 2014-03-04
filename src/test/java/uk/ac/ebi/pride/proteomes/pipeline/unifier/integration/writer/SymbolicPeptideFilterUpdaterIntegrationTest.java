package uk.ac.ebi.pride.proteomes.pipeline.unifier.integration.writer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.PeptideRepository;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.SymbolicPeptide;
import uk.ac.ebi.pride.proteomes.db.core.api.quality.ScoreRepository;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNull;

/**
 * User: ntoro
 * Date: 17/10/2013
 * Time: 11:34
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/context/data-unifier-hsql-test-context.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class SymbolicPeptideFilterUpdaterIntegrationTest extends AbstractJUnit4SpringContextTests {

    private static final String SEQUENCE = "EWKSNVYLAR";
    private static final Integer TAXID = 9606;
    private static final String REPRESENTATION = "[EWKSNVYLAR|9606]";
    private static final long DEFAULT_SCORE = 1;

    @Autowired
    @Qualifier(value = "symbolicPeptideFilterUpdater")
    private ItemWriter<SymbolicPeptide> symbolicPeptideFilterUpdater;

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

        assertNull(symbolicPeptide);

        // Two keep consistent the DB we write again the item
        SymbolicPeptide rewritePeptide = new SymbolicPeptide();
        rewritePeptide.setSequence(SEQUENCE);
        rewritePeptide.setTaxid(TAXID);
        rewritePeptide.setPeptideRepresentation(REPRESENTATION);
        rewritePeptide.setScore(scoreRepository.findOne(DEFAULT_SCORE));

        peptideRepository.save(rewritePeptide);

    }
}
