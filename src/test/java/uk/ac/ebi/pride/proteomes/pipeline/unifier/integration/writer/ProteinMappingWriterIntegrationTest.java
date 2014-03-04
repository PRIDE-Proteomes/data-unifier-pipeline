package uk.ac.ebi.pride.proteomes.pipeline.unifier.integration.writer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.PeptideRepository;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.protein.PeptideProtein;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.protein.PeptideProteinPK;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.protein.PeptideProteinRepository;
import uk.ac.ebi.pride.proteomes.db.core.api.protein.ProteinRepository;

import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;


/**
 * User: ntoro
 * Date: 08/10/2013
 * Time: 22:35
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/context/data-unifier-hsql-test-context.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
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
    private ItemWriter<List<PeptideProtein>> proteinMappingWriter;


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

        PeptideProtein peptideProtein = new PeptideProtein();

        peptideProtein.setProteinAccession(PROTEIN_ID);
        peptideProtein.setPeptideId(PEPTIDE_ID);
        peptideProtein.setStartPosition(START_POSITION);
        peptideProtein.setUniqueness(UNIQUENESS);
        peptideProtein.setPeptide(peptideRepository.findOne(PEPTIDE_ID));
        peptideProtein.setProtein(proteinRepository.findOne(PROTEIN_ID));

        List<PeptideProtein> list = Collections.singletonList(peptideProtein);
        List<List<PeptideProtein>> wrapperList = Collections.singletonList(list);


        proteinMappingWriter.write(wrapperList);

        PeptideProtein other = peptideProteinRepository.findOne(new PeptideProteinPK(PEPTIDE_ID, PROTEIN_ID, START_POSITION));

        assertEquals(peptideProtein.getPeptide(),other.getPeptide());
        assertEquals(peptideProtein.getProtein(),other.getProtein());
        assertEquals(peptideProtein.getStartPosition(),other.getStartPosition());
        assertEquals(peptideProtein.getUniqueness(),other.getUniqueness());

        peptideProteinRepository.delete(other);

    }

}
