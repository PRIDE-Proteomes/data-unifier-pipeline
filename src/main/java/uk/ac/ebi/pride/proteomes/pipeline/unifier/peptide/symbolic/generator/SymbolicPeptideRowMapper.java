package uk.ac.ebi.pride.proteomes.pipeline.unifier.peptide.symbolic.generator;

import org.springframework.jdbc.core.RowMapper;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.SymbolicPeptide;
import uk.ac.ebi.pride.proteomes.db.core.api.utils.PeptideUtils;
import uk.ac.ebi.pride.proteomes.db.core.api.utils.ScoreUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User: ntoro
 * Date: 07/10/2013
 * Time: 17:08
 */

/**
 * The symbolic peptide mapper transform the sequence and taxid read from the database to a
 * SymbolicPeptide that after the validation should be persisted in the Proteomes DB
 * <p/>
 * As the score is a mandatory field we assign the default value that should exist in the Proteomes
 * DB
 */
public class SymbolicPeptideRowMapper implements RowMapper<SymbolicPeptide> {

    public static final String SEQUENCE_COLUMN = "SEQUENCE";
    public static final String TAXID_COLUMN = "TAXID";

    @Override
    public SymbolicPeptide mapRow(ResultSet rs, int rowNum) throws SQLException {

        SymbolicPeptide symbolicPeptide = new SymbolicPeptide();

        symbolicPeptide.setSequence(rs.getString(SEQUENCE_COLUMN));
        symbolicPeptide.setTaxid(rs.getInt(TAXID_COLUMN));

        symbolicPeptide.setScore(ScoreUtils.defaultScore());

        String representation = PeptideUtils.peptideRepresentationGenerator(symbolicPeptide);
        symbolicPeptide.setPeptideRepresentation(representation);

        return symbolicPeptide;
    }
}
