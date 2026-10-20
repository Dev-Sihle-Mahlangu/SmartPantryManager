package com.smartpantry.manager.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UnitConverterTest {

    @Test
    public void thousandGramsEqualsOneKilogram() {
        assertEquals(UnitConverter.toBaseQuantity(1000, "g"), UnitConverter.toBaseQuantity(1, "kg"));
    }

    @Test
    public void litresAndSpoonsConvertToMillilitres() {
        assertEquals(1000.0, UnitConverter.toBaseQuantity(1, "l"), 0.0001);
        assertEquals(15.0, UnitConverter.toBaseQuantity(1, "tbsp"), 0.0001);
        assertEquals(5.0, UnitConverter.toBaseQuantity(1, "tsp"), 0.0001);
        assertEquals(250.0, UnitConverter.toBaseQuantity(1, "cup"), 0.0001);
    }

    @Test
    public void massAndVolumeCannotBeCompared() {
        assertFalse(UnitConverter.canCompare("g", "ml"));
        assertFalse(UnitConverter.canCompare("kg", "cup"));
        assertFalse(UnitConverter.canCompare("piece", "g"));
    }

    @Test
    public void sameCategoryCanBeCompared() {
        assertTrue(UnitConverter.canCompare("g", "kg"));
        assertTrue(UnitConverter.canCompare("tbsp", "l"));
    }

    @Test
    public void unknownUnitsAreNotSupported() {
        assertNull(UnitConverter.toBaseQuantity(2, "bags"));
        assertFalse(UnitConverter.isSupported(""));
        assertFalse(UnitConverter.isSupported(null));
        assertFalse(UnitConverter.canCompare("bags", "bags"));
    }

    @Test
    public void unitSymbolsIgnoreCase() {
        assertTrue(UnitConverter.isSupported("KG"));
    }
}
