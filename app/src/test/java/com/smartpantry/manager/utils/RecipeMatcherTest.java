package com.smartpantry.manager.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.smartpantry.manager.models.PantryItem;
import com.smartpantry.manager.models.Recipe;
import com.smartpantry.manager.models.RecipeIngredient;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Tests for the strict matching rule. */
public class RecipeMatcherTest {

    private static PantryItem pantry(String name, double qty, String unit) {
        return new PantryItem(name, qty, unit, null);
    }

    private static RecipeIngredient need(String name, double qty, String unit) {
        return new RecipeIngredient(name, qty, unit);
    }

    private static Recipe recipe(String name, RecipeIngredient... ingredients) {
        return new Recipe(Recipe.NO_ID, name, "1. Cook it.", Arrays.asList(ingredients));
    }

    private static Recipe omelette() {
        return recipe("Omelette", need("Eggs", 3, "piece"), need("Cheese", 50, "g"));
    }

    @Test
    public void allIngredientsAvailable_recipeQualifies() {
        List<PantryItem> pantry = Arrays.asList(pantry("Eggs", 6, "piece"), pantry("Cheese", 100, "g"));
        assertTrue(RecipeMatcher.canMake(omelette(), pantry));
    }

    @Test
    public void oneIngredientMissing_recipeIsRejected() {
        List<PantryItem> pantry = Arrays.asList(pantry("Eggs", 6, "piece"));
        assertFalse(RecipeMatcher.canMake(omelette(), pantry));
    }

    @Test
    public void fiveIngredientsWithOnlyFourInPantry_isRejected() {
        Recipe five = recipe("Big meal", need("A", 1, "piece"), need("B", 1, "piece"),
                need("C", 1, "piece"), need("D", 1, "piece"), need("E", 1, "piece"));
        List<PantryItem> pantry = Arrays.asList(pantry("A", 1, "piece"), pantry("B", 1, "piece"),
                pantry("C", 1, "piece"), pantry("D", 1, "piece"));
        assertFalse(RecipeMatcher.canMake(five, pantry));
    }

    @Test
    public void insufficientQuantity_recipeIsRejected() {
        List<PantryItem> pantry = Arrays.asList(pantry("Eggs", 2, "piece"), pantry("Cheese", 100, "g"));
        assertFalse(RecipeMatcher.canMake(omelette(), pantry));
    }

    @Test
    public void exactQuantity_recipeQualifies() {
        List<PantryItem> pantry = Arrays.asList(pantry("Eggs", 3, "piece"), pantry("Cheese", 50, "g"));
        assertTrue(RecipeMatcher.canMake(omelette(), pantry));
    }

    @Test
    public void kilogramsAndGramsAreConverted() {
        Recipe needsKilo = recipe("Bread", need("Flour", 1, "kg"));
        assertTrue(RecipeMatcher.canMake(needsKilo, Arrays.asList(pantry("Flour", 1000, "g"))));
        assertFalse(RecipeMatcher.canMake(needsKilo, Arrays.asList(pantry("Flour", 900, "g"))));

        Recipe needsGrams = recipe("Cake", need("Flour", 500, "g"));
        assertTrue(RecipeMatcher.canMake(needsGrams, Arrays.asList(pantry("Flour", 1, "kg"))));
    }

    @Test
    public void incompatibleUnits_areNeverTreatedAsEqual() {
        Recipe needsMilk = recipe("Porridge", need("Milk", 250, "ml"));
        assertFalse(RecipeMatcher.canMake(needsMilk, Arrays.asList(pantry("Milk", 250, "g"))));

        Recipe needsEggs = recipe("Eggs dish", need("Eggs", 2, "piece"));
        assertFalse(RecipeMatcher.canMake(needsEggs, Arrays.asList(pantry("Eggs", 500, "g"))));
    }

    @Test
    public void unsupportedUnit_isRejected() {
        Recipe odd = recipe("Odd", need("Rice", 1, "bag"));
        assertFalse(RecipeMatcher.canMake(odd, Arrays.asList(pantry("Rice", 1, "bag"))));
    }

    @Test
    public void pluralAndCaseDifferencesStillMatch() {
        Recipe sauce = recipe("Sauce", need("Tomato", 2, "piece"));
        assertTrue(RecipeMatcher.canMake(sauce, Arrays.asList(pantry("TOMATOES", 3, "piece"))));

        Recipe eggs = recipe("Boiled", need("Eggs", 2, "piece"));
        assertTrue(RecipeMatcher.canMake(eggs, Arrays.asList(pantry("egg", 2, "piece"))));
    }

    @Test
    public void samePantryIngredientEnteredTwice_isAddedTogether() {
        Recipe rice = recipe("Big rice", need("Rice", 1200, "g"));
        List<PantryItem> pantry = Arrays.asList(pantry("Rice", 500, "g"), pantry("Rice", 1, "kg"));
        assertTrue(RecipeMatcher.canMake(rice, pantry));
    }

    @Test
    public void ingredientListedTwiceInRecipe_cannotBeReused() {
        Recipe twice = recipe("Butter twice", need("Butter", 20, "g"), need("Butter", 20, "g"));
        assertFalse(RecipeMatcher.canMake(twice, Arrays.asList(pantry("Butter", 30, "g"))));
        assertTrue(RecipeMatcher.canMake(twice, Arrays.asList(pantry("Butter", 40, "g"))));
    }

    @Test
    public void recipeWithNoIngredients_isNeverSuggested() {
        Recipe empty = recipe("Nothing");
        assertFalse(RecipeMatcher.canMake(empty, Arrays.asList(pantry("Eggs", 3, "piece"))));
    }

    @Test
    public void emptyPantry_noRecipesQualify() {
        List<Recipe> all = Arrays.asList(omelette(), recipe("Toast", need("Bread", 2, "piece")));
        assertTrue(RecipeMatcher.findMakeableRecipes(all, new ArrayList<PantryItem>()).isEmpty());
    }

    @Test
    public void findMakeable_returnsOnlyFullMatches() {
        Recipe toast = recipe("Toast", need("Bread", 2, "piece"), need("Butter", 10, "g"));
        Recipe pasta = recipe("Pasta", need("Pasta", 200, "g"), need("Tomatoes", 4, "piece"));
        List<Recipe> all = Arrays.asList(omelette(), toast, pasta);

        List<PantryItem> pantry = Arrays.asList(
                pantry("Eggs", 3, "piece"), pantry("Cheese", 50, "g"),
                pantry("Bread", 4, "piece"), pantry("Butter", 5, "g"),   // not enough butter
                pantry("Pasta", 500, "g"));                              // tomatoes missing

        List<Recipe> result = RecipeMatcher.findMakeableRecipes(all, pantry);
        assertEquals(1, result.size());
        assertEquals("Omelette", result.get(0).getName());
    }

    @Test
    public void oneRecipeDoesNotUseUpStockForAnother() {
        Recipe a = recipe("A", need("Eggs", 3, "piece"));
        Recipe b = recipe("B", need("Eggs", 3, "piece"));
        List<Recipe> result = RecipeMatcher.findMakeableRecipes(
                Arrays.asList(a, b), Arrays.asList(pantry("Eggs", 3, "piece")));
        assertEquals(2, result.size());
    }
}
