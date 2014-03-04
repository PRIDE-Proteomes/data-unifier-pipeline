package uk.ac.ebi.pride.proteomes.pipeline.unifier.mapping.uniqueness;

import org.springframework.batch.item.ItemWriter;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.protein.PeptideProtein;

import java.util.List;

/**
 * User: ntoro
 * Date: 17/10/2013
 * Time: 12:03
 */

/**
 * An {@link org.springframework.batch.item.ItemWriter} that pulls data from a {@link java.util.Iterator} or
 * {@link Iterable} using the constructors.
 */
public class ListItemWriter implements ItemWriter<List<PeptideProtein>> {

    /**
     * Internal writer
     */
    private ItemWriter<PeptideProtein> delegate;

    /**
     * Implementation of {@link ItemWriter#write(java.util.List)} that just iterates over the
     * iterator provided.
     */
    @Override
    public void write(List<? extends List<PeptideProtein>> items) throws Exception {
        if (!items.isEmpty()) {
            for (List<PeptideProtein> item : items) {
                delegate.write(item);
            }
        }

    }

    public void setDelegate(ItemWriter<PeptideProtein> delegate) {
        this.delegate = delegate;
    }
}
