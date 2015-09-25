package uk.ac.ebi.pride.proteomes.pipeline.unifier.peptiform.indexer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import uk.ac.ebi.pride.proteomes.index.service.ProteomesIndexService;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author ntoro
 * @since 01/05/15 10:53
 */
public class IndexCleanerTasklet implements Tasklet {

    private static final Logger logger = LoggerFactory.getLogger(IndexCleanerTasklet.class);

    @Resource
    private ProteomesIndexService proteomesIndexService;


    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {


        //By default the index it will not take into account contaminant proteins)
        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
        cal.setTimeInMillis(System.currentTimeMillis());
        System.out.println("Started cleaning process at: " + df.format(cal.getTime()));

        try {
            long start = System.currentTimeMillis();
            proteomesIndexService.deleteAll();
            long end = System.currentTimeMillis();
            System.out.println("Cleaning time [ms]: " + (end-start));
        } catch (Exception e) {
            cal.setTimeInMillis(System.currentTimeMillis());
            System.out.println("Unexpected exception at: " + df.format(cal.getTime()) + " Exception: " + e.toString());
            e.printStackTrace();
        }

        return RepeatStatus.FINISHED;
    }

}
