package uk.ac.ebi.pride.proteomes.pipeline.unifier.release.report;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.Peptide;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.PeptideRepository;
import uk.ac.ebi.pride.proteomes.pipeline.unifier.utils.TissueUtils;

/**
 * @author ntoro
 * @since 24/03/2016 11:15
 */
public class PeptideMappingProcessor implements ItemProcessor<PeptideMappingReportLine, PeptideMappingReportLine> {

    @Autowired
    PeptideRepository peptideRepository;


    @Override
    @Transactional(readOnly = true)
    public PeptideMappingReportLine process(PeptideMappingReportLine item) throws Exception {

        Peptide peptide = peptideRepository.findOne(item.getPeptideId());
        item.setTissues(TissueUtils.aggregateTissues(peptide.getTissues()));

        return item;
    }

}
