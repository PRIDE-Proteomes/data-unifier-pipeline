package uk.ac.ebi.pride.proteomes.pipeline.unifier.release.report;

/**
 * @author ntoro
 * @since 22/03/2016 10:21
 */
public class PeptideMappingReportLine {

    private Long peptideId;
    private String peptideSequence;
    private Long startPosition;
    private Integer length;
    private Long numProteinMappings;
    private String uniquePeptideToProtein;
    private String proteinAccession;
    private String proteinEvidence;
    private Long numGeneMappings;
    private String uniquePeptideToGene;
    private String gene;
    private String tissues;
    private Long taxid;

    public Long getPeptideId() {
        return peptideId;
    }

    public void setPeptideId(Long peptideId) {
        this.peptideId = peptideId;
    }

    public String getPeptideSequence() {
        return peptideSequence;
    }

    public void setPeptideSequence(String peptideSequence) {
        this.peptideSequence = peptideSequence;
    }

    public Long getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Long startPosition) {
        this.startPosition = startPosition;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Long getNumProteinMappings() {
        return numProteinMappings;
    }

    public void setNumProteinMappings(Long numProteinMappings) {
        this.numProteinMappings = numProteinMappings;
    }

    public String getUniquePeptideToProtein() {
        return uniquePeptideToProtein;
    }

    public void setUniquePeptideToProtein(String uniquePeptideToProtein) {
        this.uniquePeptideToProtein = uniquePeptideToProtein;
    }

    public String getProteinAccession() {
        return proteinAccession;
    }

    public void setProteinAccession(String proteinAccession) {
        this.proteinAccession = proteinAccession;
    }

    public String getProteinEvidence() {
        return proteinEvidence;
    }

    public void setProteinEvidence(String proteinEvidence) {
        this.proteinEvidence = proteinEvidence;
    }

    public String getUniquePeptideToGene() {
        return uniquePeptideToGene;
    }

    public void setUniquePeptideToGene(String uniquePeptideToGene) {
        this.uniquePeptideToGene = uniquePeptideToGene;
    }

    public Long getNumGeneMappings() {
        return numGeneMappings;
    }

    public void setNumGeneMappings(Long numGeneMappings) {
        this.numGeneMappings = numGeneMappings;
    }

    public String getGene() {
        return gene;
    }

    public void setGene(String gene) {
        this.gene = gene;
    }

    public String getTissues() {
        return tissues;
    }

    public void setTissues(String tissues) {
        this.tissues = tissues;
    }

    public Long getTaxid() {
        return taxid;
    }

    public void setTaxid(Long taxid) {
        this.taxid = taxid;
    }

    @Override
    public String toString() {
        return "PeptideMappingReportLine{" +
                "peptideId=" + peptideId +
                ", peptideSequence='" + peptideSequence + '\'' +
                ", startPosition=" + startPosition +
                ", length=" + length +
                ", numProteinMappings=" + numProteinMappings +
                ", uniquePeptideToProtein='" + uniquePeptideToProtein + '\'' +
                ", proteinAccession='" + proteinAccession + '\'' +
                ", proteinEvidence='" + proteinEvidence + '\'' +
                ", numGeneMappings=" + numGeneMappings +
                ", uniquePeptideToGene='" + uniquePeptideToGene + '\'' +
                ", gene='" + gene + '\'' +
                ", tissues='" + tissues + '\'' +
                ", taxid=" + taxid +
                '}';
    }
}


