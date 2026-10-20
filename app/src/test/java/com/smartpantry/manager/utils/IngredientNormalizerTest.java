package com.smartpantry.manager.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IngredientNormalizerTest {

    @Test
    public void ignoresCaseAndExtraSpaces() {
        assertEquals("brown rice", IngredientNormalizer.normalize("  BROWN   Rice "));
    }

    @Test
    public void pluralsBecomeSingular() {
        assertEquals("egg", IngredientNormalizer.normalize("Eggs"));
        assertEquals("tomato", IngredientNormalizer.normalize("Tomatoes"));
        assertEquals("potato", IngredientNormalizer.normalize("Potatoes"));
        assertEquals("carrot", IngredientNormalizer.normalize("Carrots"));
        assertEquals("cherry", IngredientNormalizer.normalize("Cherries"));
        assertEquals("peach", IngredientNormalizer.normalize("Peaches"));
    }

    @Test
    public void onlyTheLastWordIsMadeSingular() {
        assertEquals("cherry tomato", IngredientNormalizer.normalize("Cherry Tomatoes"));
        assertEquals("chicken breast", IngredientNormalizer.normalize("chicken breasts"));
    }

    @Test
    public void wordsThatAreNotPluralStayTheSame() {
        assertEquals("rice", IngredientNormalizer.normalize("Rice"));
        assertEquals("couscous", IngredientNormalizer.normalize("Couscous"));
        assertEquals("hummus", IngredientNormalizer.normalize("Hummus"));
        assertEquals("cheese", IngredientNormalizer.normalize("Cheese"));
    }

    @Test
    public void emptyAndNullGiveEmptyText() {
        assertEquals("", IngredientNormalizer.normalize(null));
        assertEquals("", IngredientNormalizer.normalize("  "));
    }
}
