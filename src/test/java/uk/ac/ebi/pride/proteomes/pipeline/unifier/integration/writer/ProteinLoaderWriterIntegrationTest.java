package uk.ac.ebi.pride.proteomes.pipeline.unifier.integration.writer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.database.JpaItemWriter;
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
import uk.ac.ebi.pride.proteomes.db.core.api.quality.Score;
import uk.ac.ebi.pride.proteomes.db.core.api.quality.ScoreRepository;
import uk.ac.ebi.pride.proteomes.db.core.api.utils.ScoreUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ntoro
 * Date: 08/10/2013
 * Time: 22:35
 */

@RunWith(SpringJUnit4ClassRunner.class)
@Rollback
@Transactional(transactionManager = "transactionManager")
@ContextConfiguration(locations = {"classpath:/META-INF/context/data-unifier-hsql-test-context.xml"})
@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
public class ProteinLoaderWriterIntegrationTest {

    private static final String SEQUENCE = "GPAVLIGPAVLIGPAVLIGPAVLIGPAVLIGPAVLIGPAVLIGPAVLIGPAVLIGPAVLI";
    private static final Integer TAXID = 9606;
    private static final long DEFAULT_SCORE = 1;
    private static final String ACCESSION = "P12345";
    private static final String DESCRIPTION = "Default";

    @Autowired
    @Qualifier(value = "proteinLoaderWriter")
    private JpaItemWriter<Protein> jpaItemWriter;

    @Autowired
    private ProteinRepository proteinRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Test
    @DirtiesContext
    @Transactional
    public void testWriteFirstElement() throws Exception {

        Protein protein = new Protein();
        protein.setProteinAccession(ACCESSION);
        protein.setSequence(SEQUENCE);
        protein.setTaxid(TAXID);
        protein.setDescription(DESCRIPTION);
        protein.setScore(ScoreUtils.defaultScore());
        protein.setContaminant(Boolean.FALSE);
        protein.setIsoform(Boolean.FALSE);

        List<Protein> list = new ArrayList<Protein>();
        list.add(protein);

        jpaItemWriter.write(list);

        Protein other = proteinRepository.findByProteinAccession(ACCESSION);
        Score otherScore = scoreRepository.findOne(DEFAULT_SCORE);

        Assert.assertEquals(protein.getTaxid(), other.getTaxid());
        Assert.assertEquals(protein.getSequence(), other.getSequence());
        Assert.assertEquals(protein.getScore(), other.getScore());
        Assert.assertEquals(protein.getDescription(), other.getDescription());
        Assert.assertEquals(protein.getScore(), otherScore);

        proteinRepository.delete(other.getProteinAccession());
    }

}
