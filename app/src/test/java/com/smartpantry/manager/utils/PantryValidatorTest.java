package com.smartpantry.manager.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.smartpantry.manager.R;

import org.junit.Test;

/** Plain JVM tests for the form validation rules. */
public class PantryValidatorTest {

    @Test
    public void blankName_isRejected() {
        assertEquals(R.string.error_name_blank, PantryValidator.validateName("   "));
    }

    @Test
    public void numericOnlyName_isRejected() {
        assertEquals(R.string.error_name_invalid, PantryValidator.validateName("123"));
    }

    @Test
    public void normalName_isAccepted() {
        assertEquals(PantryValidator.VALID, PantryValidator.validateName("Brown rice"));
    }

    @Test
    public void normaliseName_trimsAndCollapsesSpaces() {
        assertEquals("Brown rice", PantryValidator.normaliseName("  Brown   rice "));
    }

    @Test
    public void emptyQuantity_isRejected() {
        assertEquals(R.string.error_quantity_blank, PantryValidator.validateQuantity(""));
    }

    @Test
    public void zeroAndNegativeQuantity_areRejected() {
        assertEquals(R.string.error_quantity_not_positive, PantryValidator.validateQuantity("0"));
        assertEquals(R.string.error_quantity_not_positive, PantryValidator.validateQuantity("-3"));
    }

    @Test
    public void textQuantity_isRejected() {
        assertEquals(R.string.error_quantity_invalid, PantryValidator.validateQuantity("abc"));
    }

    @Test
    public void commaDecimal_isAccepted() {
        assertEquals(PantryValidator.VALID, PantryValidator.validateQuantity("2,5"));
        assertEquals(2.5, PantryValidator.parseQuantity("2,5"), 0.0001);
    }

    @Test
    public void hugeQuantity_isRejected() {
        assertEquals(R.string.error_quantity_too_large, PantryValidator.validateQuantity("1000000"));
    }

    @Test
    public void invalidText_parsesToNull() {
        assertNull(PantryValidator.parseQuantity("1e5"));
    }

    @Test
    public void unitMustBeInTheList() {
        assertEquals(PantryValidator.VALID, PantryValidator.validateUnit("kg"));
        assertEquals(R.string.error_unit_invalid, PantryValidator.validateUnit("bags"));
        assertEquals(R.string.error_unit_invalid, PantryValidator.validateUnit(""));
    }
}
