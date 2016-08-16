package uk.ac.ebi.pride.proteomes.pipeline.unifier.peptide.symbolic.propagator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.pride.proteomes.db.core.api.assay.Assay;
import uk.ac.ebi.pride.proteomes.db.core.api.cluster.Cluster;
import uk.ac.ebi.pride.proteomes.db.core.api.modification.ModificationLocation;
import uk.ac.ebi.pride.proteomes.db.core.api.param.CellType;
import uk.ac.ebi.pride.proteomes.db.core.api.param.Disease;
import uk.ac.ebi.pride.proteomes.db.core.api.param.Tissue;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.Peptide;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.PeptideRepository;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.Peptiform;

import java.util.*;

/**
 * User: ntoro
 * Date: 27/01/2014
 * Time: 10:00
 */
public class SymbolicPeptidePropagatorProcessor implements ItemProcessor<Long, Peptide> {

    private static final Logger logger = LoggerFactory.getLogger(SymbolicPeptidePropagatorProcessor.class);

    @Autowired
    private PeptideRepository peptideRepository;

    @Override
    @Transactional(readOnly = true)
    public Peptide process(Long itemId) throws Exception {

        List<Peptiform> peptiforms;

        Peptide item = peptideRepository.findOne(itemId);

        if (item == null) {
            logger.error("A symbolic peptide with id " + itemId + " cannot be found in the db");
            return null;
        }

        peptiforms = peptideRepository.findPeptiformBySequenceAndTaxid(item.getSequence(), item.getTaxid());

        if (peptiforms == null) {
            logger.error("A symbolic peptide exists without peptiforms:" + item.getSequence());
            return null;
        }

        Set<CellType> cellTypes = new HashSet<CellType>();
        Set<Disease> diseases = new HashSet<Disease>();
        Set<Tissue> tissues = new HashSet<Tissue>();
        Set<Assay> assays = new HashSet<Assay>();
        Set<Cluster> clusters = new HashSet<Cluster>();
        SortedSet<ModificationLocation> mods = new TreeSet<ModificationLocation>();


        for (Peptiform peptiform : peptiforms) {
            cellTypes.addAll(peptiform.getCellTypes());
            diseases.addAll(peptiform.getDiseases());
            tissues.addAll(peptiform.getTissues());
            assays.addAll(peptiform.getAssays());
            clusters.addAll(peptiform.getClusters());
            // We filter the modification without location from the propagation to the peptide
            // level. If not they will be map to the protein in a wrong location because the value is -1
            for (ModificationLocation modificationLocation : peptiform.getModificationLocations()) {
                if (modificationLocation.getPosition() > 0) {
                    //Unknown position is reported as -1
                    mods.add(modificationLocation);
                }

            }
        }

        if (!cellTypes.isEmpty()) item.setCellTypes(cellTypes);
        if (!diseases.isEmpty()) item.setDiseases(diseases);
        if (!tissues.isEmpty()) item.setTissues(tissues);
        if (!assays.isEmpty()) item.setAssays(assays);
        if (!clusters.isEmpty()) item.setClusters(clusters);
        if (!mods.isEmpty()) item.setModificationLocations(mods);

        return item;
    }
}
