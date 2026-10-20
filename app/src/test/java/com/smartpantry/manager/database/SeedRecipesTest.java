package com.smartpantry.manager.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.smartpantry.manager.models.Recipe;
import com.smartpantry.manager.models.RecipeIngredient;
import com.smartpantry.manager.utils.UnitConverter;

import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Checks the built-in recipe data so a typo in a unit cannot make a recipe impossible to match. */
public class SeedRecipesTest {

    @Test
    public void hasBetween15And20Recipes() {
        int count = SeedRecipes.getAll().size();
        assertTrue("recipe count was " + count, count >= 15 && count <= 20);
    }

    @Test
    public void recipeNamesAreUnique() {
        List<Recipe> recipes = SeedRecipes.getAll();
        Set<String> names = new HashSet<>();
        for (Recipe recipe : recipes) {
            names.add(recipe.getName().toLowerCase());
        }
        assertEquals(recipes.size(), names.size());
    }

    @Test
    public void everyRecipeIsComplete() {
        for (Recipe recipe : SeedRecipes.getAll()) {
            assertTrue(recipe.getName() + " needs ingredients", recipe.getIngredients().size() >= 2);
            assertTrue(recipe.getName() + " needs instructions", !recipe.getInstructions().trim().isEmpty());
            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                assertTrue(recipe.getName() + ": unit " + ingredient.getUnit(),
                        UnitConverter.isSupported(ingredient.getUnit()));
                assertTrue(recipe.getName() + ": quantity", ingredient.getQuantity() > 0);
            }
        }
    }
}
