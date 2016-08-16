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
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.PeptideRepository;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.protein.PeptideProtein;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.protein.PeptideProteinPK;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.protein.PeptideProteinRepository;
import uk.ac.ebi.pride.proteomes.db.core.api.protein.ProteinRepository;

import java.util.Collection;
import java.util.Collections;
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
public class ProteinMappingWriterIntegrationTest {

    //1	Q8NEZ4	4373
    //1	H0Y765	990
    //1	H7BY37	1934
    //2	Q8NEZ4	4322
    //2	H0Y765	939
    //2	H7BY37	1883

    private static final String PROTEIN_ID = "H0Y765";
    private static final Long PEPTIDE_ID = 1L;
    private static final Integer START_POSITION = 17;
    private static final Integer UNIQUENESS = 3;

    @Autowired
    @Qualifier(value = "proteinMappingUniquenessUpdater")
    private ItemWriter<Collection<PeptideProtein>> proteinMappingUniquenessUpdater;


    @Autowired
    private PeptideProteinRepository peptideProteinRepository;

    @Autowired
    private PeptideRepository peptideRepository;

    @Autowired
    private ProteinRepository proteinRepository;

    @Test
    @DirtiesContext
    @Transactional
    public void testWriteFirstElement() throws Exception {

        PeptideProtein peptideProtein = new PeptideProtein(PEPTIDE_ID, PROTEIN_ID, START_POSITION);

        peptideProtein.setUniqueness(UNIQUENESS);
        peptideProtein.setPeptide(peptideRepository.findOne(PEPTIDE_ID));
        peptideProtein.setProtein(proteinRepository.findOne(PROTEIN_ID));

        List<PeptideProtein> list = Collections.singletonList(peptideProtein);
        List<List<PeptideProtein>> wrapperList = Collections.singletonList(list);


        proteinMappingUniquenessUpdater.write(wrapperList);

        PeptideProtein other = peptideProteinRepository.findOne(new PeptideProteinPK(PEPTIDE_ID, PROTEIN_ID, START_POSITION));

        Assert.assertEquals(peptideProtein.getPeptide(), other.getPeptide());
        Assert.assertEquals(peptideProtein.getProtein(), other.getProtein());
        Assert.assertEquals(peptideProtein.getStartPosition(), other.getStartPosition());
        //Sometimes this test fails here. I couldn't find the reason yet
        Assert.assertEquals(peptideProtein.getUniqueness(), other.getUniqueness());

        peptideProteinRepository.delete(other);

    }

}
