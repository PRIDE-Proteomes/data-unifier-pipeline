package uk.ac.ebi.pride.proteomes.pipeline.unifier.grouping;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.core.io.Resource;


/**
 * User: ntoro
 * Date: 05/12/2013
 * Time: 10:28
 */
public class ProteinGroupingItemReader implements ResourceAwareItemReaderItemStream<Group> {

    private static final Log log = LogFactory.getLog(ProteinGroupingItemReader.class);

    private Group result;

    private ResourceAwareItemReaderItemStream<Group> delegate;


    // We assume for this reader that the records are sorted by sequence, so when de sequence is different we create a new
    // PeptideVariant and released the previous one
    @Override
    public Group read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        Group aux = null;

        //Consume a new group
        Group current = delegate.read();

        do {
            //Last element
            if (current == null) {
                log.debug(result);
                aux = result;
                result = null;
            } else {
                //First element
                if (result == null) {
                    result = current;
                } else {
                    if (result.getId().equals(current.getId())) {
                        updateItem(current);
                        current = delegate.read();
                    } else {
                        aux = result;
                        result = current;
                    }
                }
            }

        } while (aux == null && result != null);


        log.debug(aux);

        return aux;


    }

    private void updateItem(Group currentGroup) {

        assert result != null;
        assert result.getProteinAccessions() != null;
        result.getProteinAccessions().addAll(currentGroup.getProteinAccessions());

    }

    public void setDelegate(ResourceAwareItemReaderItemStream<Group> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        delegate.open(executionContext);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        delegate.update(executionContext);
    }

    @Override
    public void close() throws ItemStreamException {
        delegate.close();
    }

    @Override
    public void setResource(Resource resource) {
        delegate.setResource(resource);
    }
}
