package com.smartpantry.manager.utils;

import java.util.Locale;

/**
 * Turns an ingredient name into a simple comparable form so that
 * "Tomatoes", "tomato" and " TOMATO " all match each other.
 *
 * The rules are deliberately simple and predictable:
 * 1. lower case, punctuation becomes a space, repeated spaces are collapsed
 * 2. only the LAST word is made singular ("cherry tomatoes" becomes "cherry tomato")
 *
 * Both the pantry name and the recipe name go through the same method, so even an
 * odd result still matches itself.
 */
public final class IngredientNormalizer {

    private IngredientNormalizer() {
    }

    public static String normalize(String raw) {
        if (raw == null) {
            return "";
        }
        String cleaned = raw.toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]+", " ").trim();
        if (cleaned.isEmpty()) {
            return "";
        }
        int lastSpace = cleaned.lastIndexOf(' ');
        String head = cleaned.substring(0, lastSpace + 1); // empty when there is only one word
        String lastWord = singularize(cleaned.substring(lastSpace + 1));
        return head + lastWord;
    }

    /** Removes a plural ending from one word. */
    static String singularize(String word) {
        int n = word.length();
        if (n > 4 && word.endsWith("ies")) {
            return word.substring(0, n - 3) + "y";      // cherries -> cherry
        }
        if (n > 3 && word.endsWith("oes")) {
            return word.substring(0, n - 2);            // tomatoes -> tomato
        }
        if (n > 4 && (word.endsWith("ches") || word.endsWith("shes") || word.endsWith("xes"))) {
            return word.substring(0, n - 2);            // peaches -> peach
        }
        if (n > 3 && word.endsWith("s") && !word.endsWith("ss")
                && !word.endsWith("us") && !word.endsWith("is")) {
            return word.substring(0, n - 1);            // eggs -> egg
        }
        return word;                                    // rice, hummus, couscous stay as they are
    }
}
