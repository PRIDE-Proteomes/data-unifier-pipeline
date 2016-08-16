package uk.ac.ebi.pride.proteomes.pipeline.unifier.peptide.symbolic;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User: ntoro
 * Date: 07/10/2013
 * Time: 17:08
 */


public class SymbolicPeptideRowMapper implements RowMapper<Long> {

    public static final String ID_COLUMN = "PEPTIDE_ID";

    @Override
    public Long mapRow(ResultSet rs, int rowNum) throws SQLException {

        return rs.getLong(ID_COLUMN);
    }
}
