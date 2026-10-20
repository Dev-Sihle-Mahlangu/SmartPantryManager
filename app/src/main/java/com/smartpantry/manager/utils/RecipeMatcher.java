package com.smartpantry.manager.utils;

import com.smartpantry.manager.models.PantryItem;
import com.smartpantry.manager.models.Recipe;
import com.smartpantry.manager.models.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Strict recipe matching.
 *
 * A recipe qualifies ONLY if every ingredient is in the pantry, in a large
 * enough quantity, in units that can be compared reliably. If any single
 * ingredient fails, the whole recipe is rejected. There are no partial matches.
 *
 * This class has no Android code, so it can be unit tested on a normal JVM.
 */
public final class RecipeMatcher {

    // Allows for tiny floating point errors, e.g. 0.1 + 0.2
    private static final double EPSILON = 1e-9;

    private RecipeMatcher() {
    }

    /** Returns only the recipes that can be made completely from the pantry. */
    public static List<Recipe> findMakeableRecipes(List<Recipe> recipes, List<PantryItem> pantryItems) {
        Map<String, Double> stock = buildStock(pantryItems);
        List<Recipe> makeable = new ArrayList<>();
        for (Recipe recipe : recipes) {
            // Each recipe gets its own copy of the stock, because checking one
            // recipe must not use up ingredients for the next recipe
            if (canMake(recipe, new HashMap<>(stock))) {
                makeable.add(recipe);
            }
        }
        return makeable;
    }

    /** True when the pantry holds every ingredient of this recipe in sufficient quantity. */
    public static boolean canMake(Recipe recipe, List<PantryItem> pantryItems) {
        return canMake(recipe, buildStock(pantryItems));
    }

    private static boolean canMake(Recipe recipe, Map<String, Double> stock) {
        List<RecipeIngredient> required = recipe.getIngredients();
        if (required.isEmpty()) {
            return false; // a recipe with no ingredients is bad data, never suggest it
        }

        for (RecipeIngredient ingredient : required) {
            UnitConverter.Category category = UnitConverter.categoryOf(ingredient.getUnit());
            Double needed = UnitConverter.toBaseQuantity(ingredient.getQuantity(), ingredient.getUnit());
            String name = IngredientNormalizer.normalize(ingredient.getName());
            if (category == null || needed == null || name.isEmpty()) {
                return false; // unit or name cannot be understood, so it cannot be compared reliably
            }

            // The key includes the category. Rice in grams is therefore never
            // found when a recipe asks for rice in millilitres.
            String key = stockKey(name, category);
            Double have = stock.get(key);
            if (have == null || have < needed - EPSILON) {
                return false; // missing, wrong kind of unit, or not enough
            }
            // Take the amount out so a recipe that lists an ingredient twice cannot reuse it
            stock.put(key, have - needed);
        }
        return true;
    }

    /** Adds up the pantry per ingredient and unit category, in base units. */
    private static Map<String, Double> buildStock(List<PantryItem> pantryItems) {
        Map<String, Double> stock = new HashMap<>();
        for (PantryItem item : pantryItems) {
            UnitConverter.Category category = UnitConverter.categoryOf(item.getUnit());
            Double base = UnitConverter.toBaseQuantity(item.getQuantity(), item.getUnit());
            String name = IngredientNormalizer.normalize(item.getName());
            if (category == null || base == null || name.isEmpty()) {
                continue; // cannot be compared, so it does not count towards any recipe
            }
            String key = stockKey(name, category);
            Double existing = stock.get(key);
            stock.put(key, (existing == null ? 0 : existing) + base);
        }
        return stock;
    }

    private static String stockKey(String normalizedName, UnitConverter.Category category) {
        return normalizedName + "|" + category.name();
    }
}
