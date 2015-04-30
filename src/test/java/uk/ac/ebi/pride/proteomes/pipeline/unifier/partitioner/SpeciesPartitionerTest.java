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
 * Time: 14:39
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/context/data-unifier-hsql-test-context.xml"})
public class SpeciesPartitionerTest {

    private static final int HUMAN = 9606;
    private static final int MOUSE = 10090;
    private static final int ARAB = 3702;
    private static final int RAT = 10116;
    private static final int GRID_SIZE = 10;
    private static final int MAP_SIZE = 4;

    @Autowired
    SpeciesPartitioner speciesPartitioner;

    //
    @Test
    public void testPartition() throws Exception {

        speciesPartitioner.setTaxids(Arrays.asList(HUMAN,MOUSE,ARAB,RAT));
        Map<String, ExecutionContext> executionContextMap = speciesPartitioner.partition(GRID_SIZE);

        Assert.assertThat(executionContextMap.size(), is(MAP_SIZE));

        Assert.assertTrue(executionContextMap.get("speciesPartition:9606").containsKey(SpeciesPartitioner.TAXID_KEY_NAME));
        Assert.assertTrue(executionContextMap.get("speciesPartition:3702").containsKey(SpeciesPartitioner.TAXID_KEY_NAME));
        Assert.assertTrue(executionContextMap.get("speciesPartition:10090").containsKey(SpeciesPartitioner.TAXID_KEY_NAME));
        Assert.assertTrue(executionContextMap.get("speciesPartition:10116").containsKey(SpeciesPartitioner.TAXID_KEY_NAME));
        Assert.assertTrue(executionContextMap.get("speciesPartition:9606").containsValue(HUMAN));
        Assert.assertTrue(executionContextMap.get("speciesPartition:3702").containsValue(ARAB));
        Assert.assertTrue(executionContextMap.get("speciesPartition:10090").containsValue(MOUSE));
        Assert.assertTrue(executionContextMap.get("speciesPartition:10116").containsValue(RAT));
    }
}
