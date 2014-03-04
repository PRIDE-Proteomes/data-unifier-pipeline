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

import static org.junit.Assert.assertEquals;

/**
 * User: ntoro
 * Date: 16/10/2013
 * Time: 17:49
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/context/data-unifier-hsql-test-context.xml"})
public class ProteinGroupingStepTest extends AbstractJUnit4SpringContextTests {


    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    // We use the test data to run the test and we delete the table that needs
    // to be generated in the pipeline
    @Before
    public void setUp() throws Exception {
        jdbcTemplate.update("DELETE FROM PRIDEPROT.PROT_PGRP");
        jdbcTemplate.update("DELETE FROM PRIDEPROT.PROT_GROUP");
    }

    @After
    public void tearDown() throws Exception {
        //We clean the generated tables
        jdbcTemplate.update("DELETE FROM PRIDEPROT.PROT_PGRP");
        jdbcTemplate.update("DELETE FROM PRIDEPROT.PROT_GROUP");
    }

    @Test
    @DirtiesContext
    public void launchStep() throws Exception {
        //Testing a individual step
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("proteinGroupingStep");
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

    }
}

