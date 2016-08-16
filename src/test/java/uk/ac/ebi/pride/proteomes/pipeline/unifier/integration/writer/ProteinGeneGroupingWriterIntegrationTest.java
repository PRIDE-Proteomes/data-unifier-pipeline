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
import uk.ac.ebi.pride.proteomes.db.core.api.protein.groups.GeneGroup;
import uk.ac.ebi.pride.proteomes.db.core.api.protein.groups.ProteinGroupRepository;

import java.util.*;

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
public class ProteinGeneGroupingWriterIntegrationTest {


    private static final String GENE_ID = "ENSG00000055609";
    private static final String GENE_DESCRIPTION = "Histone-lysine N-methyltransferase";
    private static final String GENE_PROTEINS = "Q8NEZ4,H0Y765,H7BY37";

    private static final Integer TAXID = 9606;

    private static final String PROTEIN_DELIMITER = ",";


    @Autowired
    @Qualifier(value = "proteinGeneGroupingWriter")
    private JpaItemWriter<GeneGroup> jpaItemWriter;

    @Autowired
    private ProteinRepository proteinRepository;

    @Autowired
    private ProteinGroupRepository proteinGroupRepository;

    @Test
    @DirtiesContext
    @Transactional
    public void testWriteFirstElement() throws Exception {

        GeneGroup gene = new GeneGroup();
        gene.setId(GENE_ID);
        gene.setDescription(GENE_DESCRIPTION);
        gene.setTaxid(TAXID);

        List<String> proteinAcs = Arrays.asList(GENE_PROTEINS.split(PROTEIN_DELIMITER));
        Set<Protein> proteins = new HashSet<Protein>(proteinRepository.findAll(proteinAcs));
        gene.setProteins(proteins);

        List<GeneGroup> list = new ArrayList<GeneGroup>();
        list.add(gene);

        jpaItemWriter.write(list);

        GeneGroup other = (GeneGroup) proteinGroupRepository.findById(GENE_ID);
        Assert.assertEquals(gene.getTaxid(), other.getTaxid());
        Assert.assertEquals(gene.getDescription(), other.getDescription());
        Assert.assertEquals(gene.getProteins(), other.getProteins());
        proteinGroupRepository.delete(other.getId());
    }

}
