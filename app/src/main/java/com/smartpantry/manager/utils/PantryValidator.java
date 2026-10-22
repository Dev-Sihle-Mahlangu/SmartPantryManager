package com.smartpantry.manager.utils;

import com.smartpantry.manager.R;

import java.util.Arrays;
import java.util.List;

/**
 * Input rules for the add/edit ingredient form.
 * Each validate method returns VALID (0) or the string resource id of the
 * error message. Returning ids instead of text keeps this class free of
 * Context, so it can be unit tested on the plain JVM.
 */
public final class PantryValidator {

    public static final int VALID = 0;
    public static final int MAX_NAME_LENGTH = 50;
    public static final double MAX_QUANTITY = 100000;
    /** Below this the quantity would be shown as 0, so it is rejected instead. */
    public static final double MIN_QUANTITY = 0.001;

    /** The units the user can pick from. Phase 3 will use the same list for conversions. */
    public static final List<String> UNITS =
            Arrays.asList("g", "kg", "ml", "l", "tsp", "tbsp", "cup", "piece");

    private PantryValidator() {
    }

    /** Trims the name and collapses repeated spaces, e.g. "  Brown   rice " becomes "Brown rice". */
    public static String normaliseName(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.trim().replaceAll("\\s+", " ");
    }

    public static int validateName(String raw) {
        String name = normaliseName(raw);
        if (name.isEmpty()) {
            return R.string.error_name_blank;
        }
        if (name.length() > MAX_NAME_LENGTH) {
            return R.string.error_name_too_long;
        }
        // A name such as "123" is not a real ingredient, so at least one letter is required
        for (int i = 0; i < name.length(); i++) {
            if (Character.isLetter(name.charAt(i))) {
                return VALID;
            }
        }
        return R.string.error_name_invalid;
    }

    /**
     * Converts the typed quantity to a number. A comma is accepted as the decimal
     * separator because many South African keyboards use it. Returns null when the
     * text is not a plain positive-looking number.
     */
    public static Double parseQuantity(String raw) {
        if (raw == null) {
            return null;
        }
        String text = raw.trim().replace(',', '.');
        if (!text.matches("\\d+(\\.\\d*)?|\\.\\d+")) {
            return null;
        }
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static int validateQuantity(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return R.string.error_quantity_blank;
        }
        if (raw.trim().startsWith("-")) {
            return R.string.error_quantity_not_positive;
        }
        Double quantity = parseQuantity(raw);
        if (quantity == null) {
            return R.string.error_quantity_invalid;
        }
        if (quantity <= 0) {
            return R.string.error_quantity_not_positive;
        }
        if (quantity < MIN_QUANTITY) {
            return R.string.error_quantity_too_small;
        }
        if (quantity > MAX_QUANTITY) {
            return R.string.error_quantity_too_large;
        }
        return VALID;
    }

    public static int validateUnit(String unit) {
        if (unit != null && UNITS.contains(unit.trim())) {
            return VALID;
        }
        return R.string.error_unit_invalid;
    }
}
