package uk.ac.ebi.pride.proteomes.pipeline.unifier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;

public class DataUnifierApp {

    private static final Log log = LogFactory.getLog(DataUnifierApp.class);

    public static void main(String[] args) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        ApplicationContext context = new ClassPathXmlApplicationContext("launch-data-unifier-job.xml");
        JobLauncher jobLauncher = context.getBean(JobLauncher.class);
        Job job = context.getBean("proteomesDataUnifierJob", Job.class);
        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addDate("date", new Date());
        JobExecution jobExecution = jobLauncher.run(job, builder.toJobParameters());
        log.info(jobExecution.getExitStatus().getExitCode());
    }
}
