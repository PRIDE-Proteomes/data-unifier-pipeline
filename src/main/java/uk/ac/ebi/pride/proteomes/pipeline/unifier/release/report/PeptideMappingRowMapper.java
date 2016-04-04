package uk.ac.ebi.pride.proteomes.pipeline.unifier.release.report;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author ntoro
 * @since 22/03/2016 10:20
 */
public class PeptideMappingRowMapper implements RowMapper<PeptideMappingReportLine> {

    public static final String PEPTIDE_ID_COLUMN = "PEPTIDE_ID";
    public static final String PEPTIDE_SEQUENCE_COLUMN = "PEPTIDE_SEQUENCE";
    public static final String START_POSITION_COLUMN = "START_POSITION";
    public static final String NUM_PROTEIN_MAPPINGS_COLUMN = "NUM_PROTEIN_MAPPINGS";
    public static final String PROTEIN_ACCESSION_COLUMN = "PROTEIN_ACCESSION";
    public static final String PROTEIN_EVIDENCE_COLUMN = "PROTEIN_EVIDENCE";
    public static final String NUM_GENE_MAPPINGS_COLUMN = "NUM_GENE_MAPPINGS";
    public static final String GENE_COLUMN = "GENE_ID";
    public static final String TAXID_COLUMN = "TAXID";

    @Override
    public PeptideMappingReportLine mapRow(ResultSet resultSet, int i) throws SQLException {

        PeptideMappingReportLine lineReport = new PeptideMappingReportLine();

        lineReport.setPeptideId(resultSet.getLong(PEPTIDE_ID_COLUMN));
        final String peptideSequence = resultSet.getString(PEPTIDE_SEQUENCE_COLUMN);
        lineReport.setPeptideSequence(peptideSequence);
        lineReport.setStartPosition(resultSet.getLong(START_POSITION_COLUMN));
        lineReport.setLength(peptideSequence.length());


        final long numProteinMappings = resultSet.getLong(NUM_PROTEIN_MAPPINGS_COLUMN);
        lineReport.setNumProteinMappings(numProteinMappings);
        String proteinUniqueness = "false";
        if(numProteinMappings ==  1){
            proteinUniqueness = "true";
        }

        lineReport.setUniquePeptideToProtein(proteinUniqueness);
        lineReport.setProteinAccession(resultSet.getString(PROTEIN_ACCESSION_COLUMN));
        lineReport.setProteinEvidence(resultSet.getString(PROTEIN_EVIDENCE_COLUMN));

        final long numGeneMappings = resultSet.getLong(NUM_GENE_MAPPINGS_COLUMN);
        lineReport.setNumGeneMappings(numGeneMappings);
        String geneUniqueness = "false";
        if(numGeneMappings ==  1){
            geneUniqueness = "true";
        }

        lineReport.setUniquePeptideToGene(geneUniqueness);
        String gene = resultSet.getString(GENE_COLUMN);
        if(gene == null || gene.isEmpty()){
            gene = "-";
        }
        lineReport.setGene(gene);
        lineReport.setTaxid(resultSet.getLong(TAXID_COLUMN));

        return lineReport;
    }
}
