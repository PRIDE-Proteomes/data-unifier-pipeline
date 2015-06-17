package uk.ac.ebi.pride.proteomes.pipeline.unifier.funtional.jobs;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: ntoro
 * Date: 03/10/2013
 * Time: 17:54
 */

/**
 * This test can only be executed under the data-unifier-oracle-test-context.xml context configuration because the query to
 * find filter the symbolic peptides is oracle dependent nowadays. Maybe in the future it can be generalized to a standard sql one.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/context/data-unifier-hsql-test-context.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class})
public class DataUnifierJobTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    // We use the test data to run the test only with the peptide variants and we delete all the tables that needs
    // to be generated in the pipeline
    @Before
    public void setUp() throws Exception {
        jdbcTemplate.update("DELETE FROM PRIDEPROT.PROT_PGRP");
        jdbcTemplate.update("DELETE FROM PRIDEPROT.PEP_PROT");
        jdbcTemplate.update("DELETE FROM PRIDEPROT.PROT_GROUP");
        jdbcTemplate.update("DELETE FROM PRIDEPROT.PROTEIN");
        jdbcTemplate.update("DELETE FROM PRIDEPROT.PEP_ASSAY");
        jdbcTemplate.update("DELETE FROM PRIDEPROT.PEPTIDE WHERE SYMBOLIC='TRUE'");
    }

    @After
    public void tearDown() throws Exception {
        //We clean the generated tables
        jdbcTemplate.update("DELETE FROM PRIDEPROT.PROT_PGRP");
        jdbcTemplate.update("DELETE FROM PRIDEPROT.PEP_PROT");
        jdbcTemplate.update("DELETE FROM PRIDEPROT.PROT_GROUP");
        jdbcTemplate.update("DELETE FROM PRIDEPROT.PROTEIN");
        jdbcTemplate.update("DELETE FROM PRIDEPROT.PEP_ASSAY");
        jdbcTemplate.update("DELETE FROM PRIDEPROT.PEPTIDE WHERE SYMBOLIC='TRUE'");
    }

    @Test
    @DirtiesContext
    @Transactional
    @Ignore("This test depends of the PRIDEPROT Oracle DB, run carefully")
    public void launchJob() throws Exception {

        //testing a job
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        Assert.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

    }
}
