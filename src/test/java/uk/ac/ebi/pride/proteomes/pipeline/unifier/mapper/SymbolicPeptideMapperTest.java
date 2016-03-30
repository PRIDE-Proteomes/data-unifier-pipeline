package uk.ac.ebi.pride.proteomes.pipeline.unifier.mapper;

import org.junit.Test;
import uk.ac.ebi.pride.proteomes.pipeline.unifier.peptide.symbolic.generator.SymbolicPeptideRowMapper;

import java.sql.ResultSet;

import static org.mockito.Mockito.*;

/**
 * User: ntoro
 * Date: 08/10/2013
 * Time: 16:04
 */
public class SymbolicPeptideMapperTest {

    @Test
    public void testMapRow() throws Exception {
        ResultSet resultSet = mock(ResultSet.class);

        SymbolicPeptideRowMapper mapper = new SymbolicPeptideRowMapper();
        mapper.mapRow(resultSet, 1);

        verify(resultSet, times(1)).getString(SymbolicPeptideRowMapper.SEQUENCE_COLUMN);
        verify(resultSet, times(1)).getInt(SymbolicPeptideRowMapper.TAXID_COLUMN);

        verifyNoMoreInteractions(resultSet);

    }
}
