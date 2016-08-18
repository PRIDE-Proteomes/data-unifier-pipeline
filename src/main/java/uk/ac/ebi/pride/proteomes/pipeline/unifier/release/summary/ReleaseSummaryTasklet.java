package uk.ac.ebi.pride.proteomes.pipeline.unifier.release.summary;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.PeptideRepository;
import uk.ac.ebi.pride.proteomes.db.core.api.protein.ProteinRepository;
import uk.ac.ebi.pride.proteomes.db.core.api.protein.groups.ProteinGroupRepository;
import uk.ac.ebi.pride.proteomes.db.core.api.release.ReleaseSummary;
import uk.ac.ebi.pride.proteomes.db.core.api.release.ReleaseSummaryRepository;
import uk.ac.ebi.pride.proteomes.pipeline.unifier.partitioner.SpeciesPartitioner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @author ntoro
 * @since 11/03/2016 11:22
 */
public class ReleaseSummaryTasklet implements Tasklet, StepListener {

    @Autowired
    private PeptideRepository peptideRepository;

    @Autowired
    private ProteinRepository proteinRepository;

    @Autowired
    private ProteinGroupRepository proteinGroupRepository;

    @Autowired
    private ReleaseSummaryRepository releaseSummaryRepository;

    private static final Integer EXPERIMENT_LEVEL = 1;
    private static final Integer TRANSCRIPT_LEVEL = 2;
    private static final Integer INFERRED_BY_HOMOLOGY = 3;
    private static final Integer PREDICTED = 4;
    private static final Integer UNCERTAIN = 5;
    private static final Integer NOT_REPORTED = -1;


    private String referenceDatabaseKey;
    private String referenceDatabaseVersionKey;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        final Integer taxid = (Integer) chunkContext.getStepContext().getStepExecutionContext().get(SpeciesPartitioner.TAXID_KEY_NAME);
        final String referenceDatabase = (String) ((Properties) chunkContext.getStepContext().getStepExecutionContext().get("releaseProperties")).get(referenceDatabaseKey);
        final String referenceDatabaseVersion = (String) ((Properties) chunkContext.getStepContext().getStepExecutionContext().get("releaseProperties")).get(referenceDatabaseVersionKey);

        //General numbers
        final long numPeptiforms = peptideRepository.countPeptiformsByTaxid(taxid);
        final long numPeptides = peptideRepository.countSymbolicPeptideByTaxid(taxid);
        final long numProteins = proteinRepository.countByTaxidAndIsNotContaminant(taxid);
        final long numIsoformProteins = proteinRepository.countByTaxidAndIsNotContaminantAndIsIsoform(taxid);
        final long numCanonicalProteins = proteinRepository.countByTaxidAndIsNotContaminantAndIsCanonical(taxid);
        final long numGenes = proteinGroupRepository.countGeneGroupsByTaxid(taxid);

        //Proteins mapped
        final long numMappedProteins = proteinRepository.countByTaxidAndIsNotContaminantAndHasPeptides(taxid);
        final long numMappedCanonicalProteins = proteinRepository.countByTaxidAndIsNotContaminantAndIsCanonicalAndHasPeptides(taxid);
        final long numMappedIsoformProteins = proteinRepository.countByTaxidAndIsNotContaminantAndIsIsoformAndHasPeptides(taxid);
        final long numMappedGenes = proteinGroupRepository.countGeneGroupsByTaxidAndHasPeptides(taxid);

        //Peptides counts
        final long numMappedPeptidesToProteins = peptideRepository.countSymbolicPeptideByTaxidAndHasProteinsWithoutContaminants(taxid);
        final long numMappedUniquePeptidesToProteins = peptideRepository.countSymbolicPeptideByIsUniqueAndTaxidAndHasProteinsWithoutContaminants(taxid);
        final long numMappedUniquePeptidesToCanonicalProteins = peptideRepository.countSymbolicPeptideByIsUniqueAndTaxidAndHasCanonicalProteinsWithoutContaminants(taxid);
        final long numMappedUniquePeptidesToIsoformProteins = peptideRepository.countSymbolicPeptideByIsUniqueAndTaxidAndHasIsoformProteinsWithoutContaminants(taxid);
        final long numMappedUniquePeptidesToGenes = peptideRepository.countSymbolicPeptideByTaxidAndHasGenes(taxid);

        //Proteins with at least one unique peptide
        final long numMappedProteinsWithUniquePeptides = proteinRepository.countByTaxidAndIsNotContaminantAndHasUniquePeptides(taxid);
        final long numMappedCanonicalProteinsWithUniquePeptides = proteinRepository.countByTaxidAndIsNotContaminantAndIsCanonicalAndHasUniquePeptides(taxid);
        final long numMappedIsoformProteinsWithUniquePeptides = proteinRepository.countByTaxidAndIsNotContaminantAndIsIsoformAndHasUniquePeptides(taxid);

        //Genes with at least one unique peptide
        final long numMappedGenesWithUniquePeptides = proteinGroupRepository.countGeneGroupsByTaxidAndHasUniquePeptides(taxid);

        //General protein evidence for proteins
        final long numProteinsWithExpEvidence = proteinRepository.countByTaxidAndEvidenceAndIsNotContaminant(taxid, EXPERIMENT_LEVEL);
        final long numProteinsWithExpEvidenceAtTranscript = proteinRepository.countByTaxidAndEvidenceAndIsNotContaminant(taxid, TRANSCRIPT_LEVEL);
        final long numProteinsWithEvidenceInferredByHomology = proteinRepository.countByTaxidAndEvidenceAndIsNotContaminant(taxid, INFERRED_BY_HOMOLOGY);
        final long numProteinWithEvidencePredicted = proteinRepository.countByTaxidAndEvidenceAndIsNotContaminant(taxid, PREDICTED);
        final long numProteinWithEvidenceUncertain = proteinRepository.countByTaxidAndEvidenceAndIsNotContaminant(taxid, UNCERTAIN);
        final long numProteinWithEvidenceNotReported = proteinRepository.countByTaxidAndEvidenceAndIsNotContaminant(taxid, NOT_REPORTED);

        //Protein evidence for mapped proteins
        final long numMappedProteinsWithExpEvidence = proteinRepository.countByTaxidAndEvidenceAndIsNotContaminantAndHasPeptides(taxid, EXPERIMENT_LEVEL);
        final long numMappedProteinsWithExpEvidenceAtTranscript = proteinRepository.countByTaxidAndEvidenceAndIsNotContaminantAndHasPeptides(taxid, TRANSCRIPT_LEVEL);
        final long numMappedProteinsWithEvidenceInferredByHomology = proteinRepository.countByTaxidAndEvidenceAndIsNotContaminantAndHasPeptides(taxid, INFERRED_BY_HOMOLOGY);
        final long numMappedProteinWithEvidencePredicted = proteinRepository.countByTaxidAndEvidenceAndIsNotContaminantAndHasPeptides(taxid, PREDICTED);
        final long numMappedProteinWithEvidenceUncertain = proteinRepository.countByTaxidAndEvidenceAndIsNotContaminantAndHasPeptides(taxid, UNCERTAIN);
        final long numMappedProteinWithEvidenceNotReported = proteinRepository.countByTaxidAndEvidenceAndIsNotContaminantAndHasPeptides(taxid, NOT_REPORTED);


        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        ReleaseSummary releaseSummary = new ReleaseSummary(taxid, sdf.format(new Date()));

        releaseSummary.setReferenceDatabase(referenceDatabase);
        releaseSummary.setReferenceDatabaseVersion(referenceDatabaseVersion);

        //General numbers
        releaseSummary.setNumPeptiforms(numPeptiforms);
        releaseSummary.setNumPeptides(numPeptides);
        releaseSummary.setNumProteins(numProteins);
        releaseSummary.setNumIsoformProteins(numIsoformProteins);
        releaseSummary.setNumCanonicalProteins(numCanonicalProteins);
        releaseSummary.setNumGenes(numGenes);

        //Proteins mapped
        releaseSummary.setNumMappedProteins(numMappedProteins);
        releaseSummary.setNumMappedCanonicalProteins(numMappedCanonicalProteins);
        releaseSummary.setNumMappedIsoformProteins(numMappedIsoformProteins);
        releaseSummary.setNumMappedGenes(numMappedGenes);

        //Peptides counts
        releaseSummary.setNumMappedPeptidesToProteins(numMappedPeptidesToProteins);
        releaseSummary.setNumMappedUniquePeptidesToProteins(numMappedUniquePeptidesToProteins);
        releaseSummary.setNumMappedUniquePeptidesToIsoformProteins(numMappedUniquePeptidesToIsoformProteins);
        releaseSummary.setNumMappedUniquePeptidesToCanonicalProteins(numMappedUniquePeptidesToCanonicalProteins);
        releaseSummary.setNumMappedUniquePeptidesToGenes(numMappedUniquePeptidesToGenes);

        //Proteins with at least one unique peptide
        releaseSummary.setNumMappedProteinsWithUniquePeptides(numMappedProteinsWithUniquePeptides);
        releaseSummary.setNumMappedCanonicalProteinsWithUniquePeptides(numMappedCanonicalProteinsWithUniquePeptides);
        releaseSummary.setNumMappedIsoformProteinsWithUniquePeptides(numMappedIsoformProteinsWithUniquePeptides);

        //Genes with at least one unique peptide
        releaseSummary.setNumMappedGenesWithUniquePeptides(numMappedGenesWithUniquePeptides);

        //General protein evidence for proteins
        releaseSummary.setNumProteinsWithExpEvidence(numProteinsWithExpEvidence);
        releaseSummary.setNumProteinsWithExpEvidenceAtTranscript(numProteinsWithExpEvidenceAtTranscript);
        releaseSummary.setNumProteinsWithEvidenceInferredByHomology(numProteinsWithEvidenceInferredByHomology);
        releaseSummary.setNumProteinsWithEvidencePredicted(numProteinWithEvidencePredicted);
        releaseSummary.setNumProteinsWithEvidenceUncertain(numProteinWithEvidenceUncertain);
        releaseSummary.setNumProteinsWithEvidenceNotReported(numProteinWithEvidenceNotReported);

        //Protein evidence for mapped proteins
        releaseSummary.setNumMappedProteinsWithExpEvidence(numMappedProteinsWithExpEvidence);
        releaseSummary.setNumMappedProteinsWithExpEvidenceAtTranscript(numMappedProteinsWithExpEvidenceAtTranscript);
        releaseSummary.setNumMappedProteinsWithEvidenceInferredByHomology(numMappedProteinsWithEvidenceInferredByHomology);
        releaseSummary.setNumMappedProteinWithEvidencePredicted(numMappedProteinWithEvidencePredicted);
        releaseSummary.setNumMappedProteinWithEvidenceUncertain(numMappedProteinWithEvidenceUncertain);
        releaseSummary.setNumMappedProteinsWithEvidenceNotReported(numMappedProteinWithEvidenceNotReported);

        releaseSummaryRepository.save(releaseSummary);

        return RepeatStatus.FINISHED;
    }

    public String getReferenceDatabaseKey() {
        return referenceDatabaseKey;
    }

    public void setReferenceDatabaseKey(String referenceDatabaseKey) {
        this.referenceDatabaseKey = referenceDatabaseKey;
    }

    public String getReferenceDatabaseVersionKey() {
        return referenceDatabaseVersionKey;
    }

    public void setReferenceDatabaseVersionKey(String referenceDatabaseVersionKey) {
        this.referenceDatabaseVersionKey = referenceDatabaseVersionKey;
    }
}
