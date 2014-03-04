package uk.ac.ebi.pride.proteomes.pipeline.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import java.sql.Timestamp;

/**
 * User: ntoro
 * Date: 08/10/2013
 * Time: 14:20
 */
public class StepListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("StepExecutionListener - " + stepExecution.getStepName() + " begins at: "
                + new Timestamp(System.currentTimeMillis()));
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println("StepExecutionListener - " + stepExecution.getStepName() + " ends at: "
                + new Timestamp(System.currentTimeMillis()));
        return null;
    }

}
