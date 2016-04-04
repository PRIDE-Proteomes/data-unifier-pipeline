package uk.ac.ebi.pride.proteomes.pipeline.unifier.release.report.classifier;

import org.springframework.batch.support.annotation.Classifier;
import uk.ac.ebi.pride.proteomes.pipeline.unifier.release.report.PeptideMappingReportLine;

/**
 * @author ntoro
 * @since 01/04/2016 11:09
 */
public class UniquePeptideClassifier{

    @Classifier
    public String classify(PeptideMappingReportLine classifiable) {
        return classifiable.getUniquePeptideToProtein();
    }
}
