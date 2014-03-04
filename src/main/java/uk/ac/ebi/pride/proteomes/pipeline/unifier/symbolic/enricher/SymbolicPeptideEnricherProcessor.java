package uk.ac.ebi.pride.proteomes.pipeline.unifier.symbolic.enricher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.pride.proteomes.db.core.api.assay.Assay;
import uk.ac.ebi.pride.proteomes.db.core.api.modification.ModificationLocation;
import uk.ac.ebi.pride.proteomes.db.core.api.param.CellType;
import uk.ac.ebi.pride.proteomes.db.core.api.param.Disease;
import uk.ac.ebi.pride.proteomes.db.core.api.param.Tissue;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.PeptideRepository;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.Peptiform;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.SymbolicPeptide;

import java.util.*;

/**
 * User: ntoro
 * Date: 27/01/2014
 * Time: 10:00
 */
public class SymbolicPeptideEnricherProcessor implements ItemProcessor<SymbolicPeptide, SymbolicPeptide> {

    private static final Log logger = LogFactory.getLog(SymbolicPeptideEnricherProcessor.class);

    @Autowired
    private PeptideRepository peptideRepository;

    @Override
    @Transactional(readOnly = true)
    public SymbolicPeptide process(SymbolicPeptide item) throws Exception {

        List<Peptiform> peptiforms;

        peptiforms = peptideRepository.findPeptiformBySequenceAndTaxid(item.getSequence(), item.getTaxid());

        if (peptiforms == null) {
            logger.error("A symbolic peptide exists without peptiforms:" + item.getSequence());
            return null;
        }

        Set<CellType> cellTypes = new HashSet<CellType>();
        Set<Disease> diseases = new HashSet<Disease>();
        Set<Tissue> tissues = new HashSet<Tissue>();
        Set<Assay> assays = new HashSet<Assay>();
        SortedSet<ModificationLocation> mods = new TreeSet<ModificationLocation>();

        for (Peptiform peptiform : peptiforms) {
            cellTypes.addAll(peptiform.getCellTypes());
            diseases.addAll(peptiform.getDiseases());
            tissues.addAll(peptiform.getTissues());
            assays.addAll(peptiform.getAssays());
            mods.addAll(peptiform.getModificationLocations());
        }

        if (!cellTypes.isEmpty()) item.setCellTypes(cellTypes);
        if (!diseases.isEmpty()) item.setDiseases(diseases);
        if (!tissues.isEmpty()) item.setTissues(tissues);
        if (!assays.isEmpty()) item.setAssays(assays);
        if (!mods.isEmpty()) item.setModificationLocations(mods);

        return item;
    }
}
