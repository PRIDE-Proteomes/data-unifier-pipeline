package uk.ac.ebi.pride.proteomes.pipeline.unifier.protein.enricher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.kraken.interfaces.uniprot.features.Feature;
import uk.ac.ebi.kraken.interfaces.uniprot.features.FeatureLocation;
import uk.ac.ebi.kraken.interfaces.uniprot.features.HasFeatureDescription;
import uk.ac.ebi.kraken.uuw.services.remoting.EntryRetrievalService;
import uk.ac.ebi.kraken.uuw.services.remoting.RemoteDataAccessException;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtJAPI;
import uk.ac.ebi.pride.proteomes.db.core.api.param.CvParamProteomesRepository;
import uk.ac.ebi.pride.proteomes.db.core.api.param.FeatureType;
import uk.ac.ebi.pride.proteomes.db.core.api.protein.Protein;
import uk.ac.ebi.pride.proteomes.db.core.api.protein.ProteinRepository;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ntoro
 * @since 11/06/15 14:23
 */
public class ProteinEnricherItemProcessor implements ItemProcessor<Protein, Protein> {

    private static final Logger logger = LoggerFactory.getLogger(ProteinEnricherItemProcessor.class);

    @Autowired
    ProteinRepository proteinRepository;

    @Autowired
    CvParamProteomesRepository cvParamProteomesRepository;

    @Override
    @Transactional(readOnly = true)
    public Protein process(Protein item) throws Exception {

        String proteinAccession = item.getProteinAccession();
        logger.debug("Protein Accession: " + proteinAccession);

        Set<uk.ac.ebi.pride.proteomes.db.core.api.feature.Feature> features = new HashSet<uk.ac.ebi.pride.proteomes.db.core.api.feature.Feature>();

        //TODO Move the query service outside of the pipeline if it keeps growing
        EntryRetrievalService entryRetrievalService = UniProtJAPI.factory.getEntryRetrievalService();

        try {
            Collection<Feature> collection = (Collection<Feature>) entryRetrievalService.getUniProtAttribute(proteinAccession, "ognl:features");

            for (Feature uniProtFeature : collection) {
                // process feature
                switch (uniProtFeature.getType()) {
                    case SIGNAL:
                    case TRANSMEM:
                    case TOPO_DOM:
                    case INTRAMEM:
                        String description = null;
                        FeatureLocation featureLocation = uniProtFeature.getFeatureLocation();
                        int start = featureLocation.getStart();
                        int end = featureLocation.getEnd();

                        if (uniProtFeature instanceof HasFeatureDescription) {
                            final HasFeatureDescription hasFeatureDescription = (HasFeatureDescription) uniProtFeature;
                            description = hasFeatureDescription.getFeatureDescription().getValue();
                        }

                        logger.debug("Feature Type: " + uniProtFeature.getType().getDisplayName() + " Range: [" + start + ", " + end + "]" + " Description: " + description);

                        FeatureType featureType = cvParamProteomesRepository.findFeatureTypeByCvName(uniProtFeature.getType().getName());

                        assert (featureType != null);
//                            logger.error("The feature: " + uniProtFeature.getType().getDisplayName() + "is not stored in the proteomes database");

                        uk.ac.ebi.pride.proteomes.db.core.api.feature.Feature feature = new uk.ac.ebi.pride.proteomes.db.core.api.feature.Feature();
                        feature.setFeatureType(featureType);
                        feature.setProteinAccession(proteinAccession);
                        feature.setStartPosition(start);
                        feature.setEndPosition(end);
                        feature.setDescription(description);

                        features.add(feature);

                        break;
                    default:
                        //do nothing
                }
            }
            if (!features.isEmpty()) {
                item.getFeatures().addAll(features);
            } else {
                //We specified to the batch process that doesn't need to do anything
                item = null;
            }

        } catch (RemoteDataAccessException e) {
            logger.debug("Uniprot remote service return: " + e.getMessage() + " for protein accession " + proteinAccession + ". This message is normal in the case of an isoform accession.");
            item = null;
        }
        return item;
    }


////   Version for the beta api 0.0.2 compatibility problems with solr 5
//
//    @Override
//    @Transactional(readOnly = true)
//    public Protein process(Protein item) throws Exception {
//
//        String proteinAccession = item.getProteinAccession();
//        logger.debug("Protein Accession: " + proteinAccession);
//
//        /*
//        * Client Class has a couple of static methods to create a ServiceFactory instance.
//        * From ServiceFactory, you can fetch the JAPI Services.
//        */
//        ServiceFactory serviceFactoryInstance = Client.getServiceFactoryInstance();
//
//        // UniProtService
//        UniProtService uniProtService = serviceFactoryInstance.getUniProtQueryService();
//
//         /*
//         * After you obtain a service, you will need to call start() to connect the server
//         * before you can use the service to retrieve data from the server
//         */
//        uniProtService.start();
//
//        /*
//        * The UniProtService provides convenience methods to access just a single component type.
//        *
//        * Note: Remember, each method invocation is a separate request. Therefore, if you require multiple components
//        * it would be faster to use getResults() and specify which components you want.
//        */
//
//        //Retrieve features from entries
//        Query query = UniProtQueryBuilder.accession(proteinAccession);
//        QueryResult<UniProtComponent<Feature>> featureComponents = uniProtService.getFeatures(query);
//
//        Set<uk.ac.ebi.pride.proteomes.db.core.api.feature.Feature> features = new HashSet<uk.ac.ebi.pride.proteomes.db.core.api.feature.Feature>();
//
//        while (featureComponents.hasNext()) {
//            UniProtComponent<Feature> featureComponent = featureComponents.next();
//            if (!featureComponent.getComponent().isEmpty()) {
//                System.out.println("accession: " + featureComponent.getAccession().getValue());
//
//                for (Feature uniProtFeature : featureComponent.getComponent()) {
//
//                    // process feature
//                    switch (uniProtFeature.getType()) {
//                        case SIGNAL:
//                        case TRANSMEM:
//                        case TOPO_DOM:
//                        case INTRAMEM:
//                            String description = null;
//                            FeatureLocation featureLocation = uniProtFeature.getFeatureLocation();
//                            int start = featureLocation.getStart();
//                            int end = featureLocation.getEnd();
//
//                            if (uniProtFeature instanceof HasFeatureDescription) {
//                                final HasFeatureDescription hasFeatureDescription = (HasFeatureDescription) uniProtFeature;
//                                description = hasFeatureDescription.getFeatureDescription().getValue();
//                            }
//
//                            logger.debug("Feature Type: " + uniProtFeature.getType().getDisplayName() + " Range: [" + start + ", " + end + "]" + " Description: " + description);
//
//                            uk.ac.ebi.pride.proteomes.db.core.api.param.FeatureType featureType = cvParamProteomesRepository.findFeatureTypeByCvName(uniProtFeature.getType().getName());
//
//                            assert (featureType != null);
//                            //logger.error("The feature: " + uniProtFeature.getType().getDisplayName() + "is not stored in the proteomes database");
//
//                            uk.ac.ebi.pride.proteomes.db.core.api.feature.Feature feature = new uk.ac.ebi.pride.proteomes.db.core.api.feature.Feature();
//                            feature.setFeatureType(featureType);
//                            feature.setProteinAccession(proteinAccession);
//                            feature.setStartPosition(start);
//                            feature.setEndPosition(end);
//                            feature.setDescription(description);
//
//                            features.add(feature);
//
//                            break;
//                        default:
//                            //do nothing
//                    }
//                }
//            }
//        }
//
//        if (!features.isEmpty()) {
//            item.getFeatures().addAll(features);
//        }
//
//        // After you finish using the service, you will need to call stop() to disconnect the service from server
//        uniProtService.stop();
//
//        return item;
//    }
}