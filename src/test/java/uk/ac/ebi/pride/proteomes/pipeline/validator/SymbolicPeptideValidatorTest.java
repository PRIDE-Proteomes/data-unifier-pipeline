package uk.ac.ebi.pride.proteomes.pipeline.validator;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.validator.ValidationException;
import uk.ac.ebi.pride.proteomes.db.core.api.peptide.SymbolicPeptide;


/**
 * User: ntoro
 * Date: 08/10/2013
 * Time: 15:03
 */

/**
 * Tests the BeanValidationValidator for SymbolicPeptides
 */
public class SymbolicPeptideValidatorTest {

    private static final String SHORT_SEQUENCE = "GPAV";
    private static final String LONG_SEQUENCE =
            "GPAVLIMCFYWHKRQNEDST" +
            "GPAVLIMCFYWHKRQNEDST" +
            "GPAVLIMCFYWHKRQNEDST" +
            "GPAVLIMCFYWHKRQNEDST" +
            "GPAVLIMCFYWHKRQNEDST" +
            "GPAVLIMCFYWHKRQNEDST";

    private static final String WRONG_SEQUENCE = "GPA1LI";
    private static final String RIGHT_SEQUENCE = "GPAVLI";
    private static final String REPRESENTATION = "[GPAVLI|9606]";

    private BeanValidationValidator<SymbolicPeptide> validator;
    private SymbolicPeptide symbolicPeptide;

    @Before
    public void setUp() throws Exception {
        validator = new BeanValidationValidator<SymbolicPeptide>();
        symbolicPeptide = new SymbolicPeptide();
    }

    @Test(expected = ValidationException.class)
    public void testShortPeptideSequence() throws Exception {
        symbolicPeptide.setPeptideRepresentation(REPRESENTATION);
        symbolicPeptide.setSequence(SHORT_SEQUENCE);
        validator.validate(symbolicPeptide);
    }

    @Test(expected = ValidationException.class)
    public void testLongPeptideSequence() throws Exception {
        symbolicPeptide.setPeptideRepresentation(REPRESENTATION);
        symbolicPeptide.setSequence(LONG_SEQUENCE);
        validator.validate(symbolicPeptide);
    }

    @Test(expected = ValidationException.class)
    public void testWrongAminoAcidPeptideSequence() throws Exception {
        symbolicPeptide.setPeptideRepresentation(REPRESENTATION);
        symbolicPeptide.setSequence(WRONG_SEQUENCE);
        validator.validate(symbolicPeptide);
    }

    @Test(expected = ValidationException.class)
    public void testNullPeptideSequence() throws Exception {
        symbolicPeptide.setPeptideRepresentation(REPRESENTATION);
        validator.validate(symbolicPeptide);
    }

    @Test
    public void testRightPeptideSequence() throws Exception {
        symbolicPeptide.setPeptideRepresentation(REPRESENTATION);
        symbolicPeptide.setSequence(RIGHT_SEQUENCE);
        validator.validate(symbolicPeptide);
    }
}
