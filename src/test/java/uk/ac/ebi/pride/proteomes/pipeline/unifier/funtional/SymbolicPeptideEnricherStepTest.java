package uk.ac.ebi.pride.proteomes.pipeline.unifier.funtional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;

/**
 * User: ntoro
 * Date: 11/10/2013
 * Time: 16:50
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/context/data-unifier-hsql-test-context.xml"})
public class SymbolicPeptideEnricherStepTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Before
    public void setUp() throws Exception {
        jdbcTemplate.update("DELETE FROM PRIDEPROT.PEP_ASSAY WHERE PEPTIDE_ID IN " +
                "(SELECT PEP_ASSAY.PEPTIDE_ID FROM PRIDEPROT.PEP_ASSAY,PRIDEPROT.PEPTIDE " +
                "WHERE PEPTIDE.PEPTIDE_ID = PEP_ASSAY.PEPTIDE_ID AND PEPTIDE.SYMBOLIC='TRUE')");
        jdbcTemplate.update("DELETE FROM PRIDEPROT.PEP_CV  WHERE PEPTIDE_ID IN " +
                "(SELECT PEP_CV.PEPTIDE_ID FROM PRIDEPROT.PEP_CV,PRIDEPROT.PEPTIDE " +
                "WHERE PEPTIDE.PEPTIDE_ID = PEP_CV.PEPTIDE_ID AND PEPTIDE.SYMBOLIC='TRUE')");
        jdbcTemplate.update("DELETE FROM PRIDEPROT.PEP_MOD WHERE PEPTIDE_ID IN " +
                "(SELECT PEP_MOD.PEPTIDE_ID FROM PRIDEPROT.PEP_MOD,PRIDEPROT.PEPTIDE " +
                "WHERE PEPTIDE.PEPTIDE_ID = PEP_MOD.PEPTIDE_ID AND PEPTIDE.SYMBOLIC='TRUE')");
    }

    @After
    public void tearDown() throws Exception {
        //We clean the generated tables
        jdbcTemplate.update("DELETE FROM PRIDEPROT.PEP_ASSAY WHERE PEPTIDE_ID IN " +
                "(SELECT PEP_ASSAY.PEPTIDE_ID FROM PRIDEPROT.PEP_ASSAY,PRIDEPROT.PEPTIDE " +
                "WHERE PEPTIDE.PEPTIDE_ID = PEP_ASSAY.PEPTIDE_ID AND PEPTIDE.SYMBOLIC='TRUE')");
        jdbcTemplate.update("DELETE FROM PRIDEPROT.PEP_CV  WHERE PEPTIDE_ID IN " +
                "(SELECT PEP_CV.PEPTIDE_ID FROM PRIDEPROT.PEP_CV,PRIDEPROT.PEPTIDE " +
                "WHERE PEPTIDE.PEPTIDE_ID = PEP_CV.PEPTIDE_ID AND PEPTIDE.SYMBOLIC='TRUE')");
        jdbcTemplate.update("DELETE FROM PRIDEPROT.PEP_MOD " +
                "WHERE PEPTIDE_ID IN " +
                "(SELECT PEP_MOD.PEPTIDE_ID FROM PRIDEPROT.PEP_MOD,PRIDEPROT.PEPTIDE " +
                "WHERE PEPTIDE.PEPTIDE_ID = PEP_MOD.PEPTIDE_ID AND PEPTIDE.SYMBOLIC='TRUE')");
    }


    @Test
    @DirtiesContext
    public void launchStep() throws Exception {

        //Testing a individual step
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("symbolicPeptideEnricherStep");
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }
}
