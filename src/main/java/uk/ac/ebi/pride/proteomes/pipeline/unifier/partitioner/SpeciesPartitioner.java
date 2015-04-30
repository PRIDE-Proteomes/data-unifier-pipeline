package uk.ac.ebi.pride.proteomes.pipeline.unifier.partitioner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: ntoro
 * Date: 09/10/2013
 * Time: 16:46
 */
public class SpeciesPartitioner implements Partitioner {

    private static final Logger logger = LoggerFactory.getLogger(SpeciesPartitioner.class);


    public static final String TAXID_KEY_NAME = "taxid";
    private static final String PARTITION_KEY = "speciesPartition:";
    private List<Integer> taxids;

    /**
     * Assign the taxid of each of the injected resources to an
     * {@link ExecutionContext}.
     *
     * @see Partitioner#partition(int)
     */
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {

        // TODO: What happen if we have more species than grid? Apparently nothing
        Map<String, ExecutionContext> map = new HashMap<String, ExecutionContext>(gridSize);
        for (Integer taxid : taxids) {
            ExecutionContext context = new ExecutionContext();
            Assert.state(taxid > 0, "Invalid taxid: " + taxid);

            context.putInt(TAXID_KEY_NAME, taxid);
            map.put(PARTITION_KEY + taxid, context);
        }
        return map;
    }

    /**
     * The species taxid to assign to each partition.
     *
     * @param taxids the resources to use
     */
    public void setTaxids(List<Integer> taxids) {
        this.taxids = taxids;
    }
}
