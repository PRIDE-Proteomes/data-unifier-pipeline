package uk.ac.ebi.pride.proteomes.pipeline.unifier.funtional.jobs;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * User: ntoro
 * Date: 03/10/2013
 * Time: 17:54
 */

/**
 * User: ntoro
 * Date: 05/05/2015
 * Time: 17:54
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/context/data-unifier-indexer-hsql-test-context.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class})
public class IndexerJobTest {


    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;


    @Test
    @DirtiesContext
    public void launchJob() throws Exception {

        //testing a job
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        Assert.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

    }
}
