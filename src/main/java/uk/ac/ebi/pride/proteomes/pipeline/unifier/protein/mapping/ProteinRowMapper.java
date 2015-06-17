package uk.ac.ebi.pride.proteomes.pipeline.unifier.protein.mapping;

import org.springframework.jdbc.core.RowMapper;
import uk.ac.ebi.pride.proteomes.db.core.api.protein.Protein;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User: ntoro
 * Date: 24/10/2013
 * Time: 14:38
 */
public class ProteinRowMapper implements RowMapper<Protein> {

    private static final String PROTEIN_AC_COLUMN = "PROTEIN_ID";
    private static final String PROTEIN_SEQUENCE = "PROTEIN_SEQUENCE";

    @Override
    public Protein mapRow(ResultSet rs, int rowNum) throws SQLException {
        Protein protein = new Protein();
        protein.setProteinAccession(rs.getString(PROTEIN_AC_COLUMN));
        protein.setSequence(rs.getString(PROTEIN_SEQUENCE));

        return protein;
    }
}
