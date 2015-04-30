package uk.ac.ebi.pride.proteomes.pipeline.unifier.partitioner;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Map;

import static org.hamcrest.Matchers.is;

/**
 * User: ntoro
 * Date: 16/10/2013
 * Time: 13:52
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/context/data-unifier-hsql-test-context.xml"})
public class RangePartitionerTest {

    private static final int HUMAN = 9606;
    private static final int MOUSE = 10090;
    private static final int ARAB = 3702;
    private static final int RAT = 10116;
    private static final int GRID_SIZE = 10;
    private static final int MAP_SIZE = 11;

    @Autowired
    RangePartitioner rangePartitioner;

    @Test
    public void testPartition() throws Exception {

        rangePartitioner.setTaxids(Arrays.asList(HUMAN, MOUSE, ARAB, RAT));
        Map<String,ExecutionContext> executionContextMap = rangePartitioner.partition(GRID_SIZE);

        Assert.assertThat(executionContextMap.size(), is(MAP_SIZE));

        for (String s : executionContextMap.keySet()) {
            Assert.assertTrue(s.startsWith("rangePartition:"));
            Assert.assertTrue(executionContextMap.get(s).containsKey(RangePartitioner.TAXID_KEY_NAME));
            Assert.assertTrue(executionContextMap.get(s).containsKey(RangePartitioner.MIN_KEY_NAME));
            Assert.assertTrue(executionContextMap.get(s).containsKey(RangePartitioner.MAX_KEY_NAME));
        }
    }
}
