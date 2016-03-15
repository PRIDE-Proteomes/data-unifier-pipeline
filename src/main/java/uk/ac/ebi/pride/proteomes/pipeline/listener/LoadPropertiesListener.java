package uk.ac.ebi.pride.proteomes.pipeline.listener;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;

import java.util.Properties;

/**
 * @author ntoro
 * @since 15/03/2016 14:06
 */
public class LoadPropertiesListener extends StepExecutionListenerSupport {

    Properties releaseProperties;

    public void setReleaseProperties(Properties releaseProperties) {
        this.releaseProperties = releaseProperties;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        super.beforeStep(stepExecution);
        // Store property file content in jobExecutionContext with name "releaseProperties"
        stepExecution.getJobExecution().getExecutionContext().put("releaseProperties", releaseProperties);
    }
}
