package uk.ac.ebi.pride.proteomes.pipeline.listener;

import org.springframework.batch.core.ItemWriteListener;
import uk.ac.ebi.pride.proteomes.db.core.api.protein.Protein;

import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: noedelta
 * Date: 14/10/2013
 * Time: 23:03
 */
public class WriteListener implements ItemWriteListener<Protein> {

	int itemWritten = 0;

	@Override
	public void beforeWrite(List items) {
		System.out.println("BeforeWriteListener - " + items.size() + " - " + itemWritten);
	}

	@Override
	public void afterWrite(List items) {
		itemWritten += items.size();
		System.out.println("AfterWriteListener - " + items.size() + " - " + itemWritten );
	}

	@Override
	public void onWriteError(Exception exception, List items) {
		System.out.println("OnWriteErrorWriteListener - " + Arrays.toString(items.toArray()) + exception.getCause());
	}
}
