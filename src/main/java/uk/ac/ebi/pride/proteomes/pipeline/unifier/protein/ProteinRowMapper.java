package uk.ac.ebi.pride.proteomes.pipeline.unifier.protein;

import org.springframework.jdbc.core.RowMapper;

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
public class ProteinRowMapper implements RowMapper<String> {

    public static final String ID_COLUMN = "PROTEIN_ID";

    @Override
    public String mapRow(ResultSet rs, int rowNum) throws SQLException {

        return rs.getString(ID_COLUMN);
    }
}
