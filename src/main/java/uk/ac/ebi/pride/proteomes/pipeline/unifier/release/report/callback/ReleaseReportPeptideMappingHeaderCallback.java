package uk.ac.ebi.pride.proteomes.pipeline.unifier.release.report.callback;

import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.item.file.FlatFileHeaderCallback;

import java.io.IOException;
import java.io.Writer;

/**
 * @author ntoro
 * @since 21/03/2016 17:16
 */
public class ReleaseReportPeptideMappingHeaderCallback extends StepExecutionListenerSupport implements FlatFileHeaderCallback {
    @Override
    public void writeHeader(Writer writer) throws IOException {
        writer.write("peptide_sequence\tstart_position\tlength\tnum_protein_mappings\tunique_to_protein\tprotein_accession\tprotein_evidence\tgene\ttissues\ttaxid");
    }
}
