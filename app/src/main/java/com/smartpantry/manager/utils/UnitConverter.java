package com.smartpantry.manager.utils;

import java.util.Locale;

/**
 * Supported units and how to compare them.
 *
 * Every unit belongs to one category (mass, volume or count) and has a factor
 * to that category's base unit (grams, millilitres, pieces). Two quantities can
 * only be compared when their units are in the SAME category, so grams and
 * millilitres are never treated as equal.
 * Spoon and cup sizes use the metric values (1 tsp = 5 ml, 1 tbsp = 15 ml, 1 cup = 250 ml).
 */
public final class UnitConverter {

    public enum Category { MASS, VOLUME, COUNT }

    private enum Unit {
        G("g", Category.MASS, 1),
        KG("kg", Category.MASS, 1000),
        ML("ml", Category.VOLUME, 1),
        L("l", Category.VOLUME, 1000),
        TSP("tsp", Category.VOLUME, 5),
        TBSP("tbsp", Category.VOLUME, 15),
        CUP("cup", Category.VOLUME, 250),
        PIECE("piece", Category.COUNT, 1);

        final String symbol;
        final Category category;
        final double factorToBase;

        Unit(String symbol, Category category, double factorToBase) {
            this.symbol = symbol;
            this.category = category;
            this.factorToBase = factorToBase;
        }
    }

    private UnitConverter() {
    }

    private static Unit find(String symbol) {
        if (symbol == null) {
            return null;
        }
        String cleaned = symbol.trim().toLowerCase(Locale.ROOT);
        for (Unit unit : Unit.values()) {
            if (unit.symbol.equals(cleaned)) {
                return unit;
            }
        }
        return null;
    }

    public static boolean isSupported(String unit) {
        return find(unit) != null;
    }

    /** Returns the category of the unit, or null if the unit is not supported. */
    public static Category categoryOf(String unit) {
        Unit found = find(unit);
        return found == null ? null : found.category;
    }

    /** Converts to grams, millilitres or pieces. Returns null if the unit is not supported. */
    public static Double toBaseQuantity(double quantity, String unit) {
        Unit found = find(unit);
        return found == null ? null : quantity * found.factorToBase;
    }

    /** True only when both units are supported and measure the same kind of thing. */
    public static boolean canCompare(String unitA, String unitB) {
        Category a = categoryOf(unitA);
        Category b = categoryOf(unitB);
        return a != null && a == b;
    }
}
