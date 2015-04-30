package uk.ac.ebi.pride.proteomes.pipeline.unifier.partitioner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: ntoro
 * Date: 15/10/2013
 * Time: 13:54
 */
public class RangePartitioner implements Partitioner, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(RangePartitioner.class);


    private String table = "PROTEIN.PEPTIDE";
    private String column = "PROTEIN_PK";
    private String symbolic = "'TRUE'"; //Default value
    private JdbcTemplate jdbcTemplate;

    private static final String PARTITION_KEY = "rangePartition:";
    public static final String TAXID_KEY_NAME = "taxid";
    public static final String MIN_KEY_NAME = "minValue";
    public static final String MAX_KEY_NAME = "maxValue";

    private List<Integer> taxids;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(taxids, "The taxids from the species are required");
    }

    /**
     * Assign the taxid of each of the injected resources to an
     * {@link ExecutionContext}.
     *
     * @see Partitioner#partition(int)
     */
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {


        Map<String, ExecutionContext> result = new HashMap<String, ExecutionContext>(gridSize);
        for (Integer taxid : taxids) {
            Assert.state(taxid > 0, "Invalid taxid: " + taxid);

            Integer tMin = jdbcTemplate.queryForObject(
                    "SELECT MIN(" + column + ") from " + table + " WHERE TAXID = " + taxid + " AND SYMBOLIC=" + symbolic, Integer.class);
            Integer tMax = jdbcTemplate.queryForObject(
                    "SELECT MAX(" + column + ") from " + table + " WHERE TAXID = " + taxid + " AND SYMBOLIC=" + symbolic, Integer.class);

            int min = tMin != null ? tMin : 0;
            int max = tMax != null ? tMax : 0;

            int targetSize = (max - min) / gridSize + 1;

            int number = 0;
            int start = min;
            int end = start + targetSize - 1;

            while (start <= max) {

                ExecutionContext context = new ExecutionContext();
                if (end >= max) {
                    end = max;
                }

                result.put(PARTITION_KEY + taxid + ":" + start + ":" + end, context);
                logger.info(PARTITION_KEY + taxid + ":" + start + ":" + end);

                context.putLong(MIN_KEY_NAME, start);
                context.putLong(MAX_KEY_NAME, end);
                context.putInt(TAXID_KEY_NAME, taxid);
                start += targetSize;
                end += targetSize;
                number++;
            }
        }
        return result;
    }

    /**
     * The species taxid to assign to each partition.
     *
     * @param taxids the resources to use
     */
    public void setTaxids(List<Integer> taxids) {
        this.taxids = taxids;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public void setSymbolic(String symbolic) {
        this.symbolic = symbolic;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
