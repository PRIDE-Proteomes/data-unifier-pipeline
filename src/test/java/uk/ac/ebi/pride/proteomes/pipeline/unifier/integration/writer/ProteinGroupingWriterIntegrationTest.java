package uk.ac.ebi.pride.proteomes.pipeline.unifier.integration.writer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.database.JpaItemWriter;
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
import uk.ac.ebi.pride.proteomes.db.core.api.protein.Protein;
import uk.ac.ebi.pride.proteomes.db.core.api.protein.ProteinRepository;
import uk.ac.ebi.pride.proteomes.db.core.api.protein.groups.GeneGroup;
import uk.ac.ebi.pride.proteomes.db.core.api.protein.groups.ProteinGroupRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * User: ntoro
 * Date: 08/10/2013
 * Time: 22:35
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/context/data-unifier-hsql-test-context.xml"})
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class ProteinGroupingWriterIntegrationTest extends AbstractJUnit4SpringContextTests {


    private static final String GENE_ID = "ENSG00000055609";
    private static final String GENE_DESCRIPTION = "Histone-lysine N-methyltransferase";
    private static final String GENE_PROTEINS = "Q8NEZ4,H0Y765,H7BY37";

    private static final Integer TAXID = 9606;

    private static final String PROTEIN_DELIMITER = ",";


    @Autowired
    @Qualifier(value = "proteinGroupingWriter")
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

        List<String> proteinAcs= Arrays.asList(GENE_PROTEINS.split(PROTEIN_DELIMITER));
        List<Protein> proteins = (List<Protein>) proteinRepository.findAll(proteinAcs);
        gene.setProteins(proteins);

        List<GeneGroup> list = new ArrayList<GeneGroup>();
        list.add(gene);

        jpaItemWriter.write(list);

        GeneGroup other = (GeneGroup) proteinGroupRepository.findById(GENE_ID);
		assertEquals(gene.getTaxid(),other.getTaxid());
		assertEquals(gene.getDescription(),other.getDescription());
		assertEquals(gene.getProteins(),other.getProteins());
        proteinGroupRepository.delete(other.getId());
	}

}
