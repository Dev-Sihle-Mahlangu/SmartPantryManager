package com.smartpantry.manager.database;

import android.content.ContentValues;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.smartpantry.manager.models.Recipe;
import com.smartpantry.manager.models.RecipeIngredient;

/** Puts the built-in recipes into the database, but only if there are none yet. */
final class RecipeSeeder {

    private RecipeSeeder() {
    }

    static void seed(SQLiteDatabase db) {
        // Guard 1: if any recipe exists the table is already seeded, so do nothing
        if (DatabaseUtils.queryNumEntries(db, PantryDbHelper.TABLE_RECIPE) > 0) {
            return;
        }
        for (Recipe recipe : SeedRecipes.getAll()) {
            insertRecipe(db, recipe);
        }
    }

    private static void insertRecipe(SQLiteDatabase db, Recipe recipe) {
        ContentValues values = new ContentValues();
        values.put(PantryDbHelper.COL_NAME, recipe.getName());
        values.put(PantryDbHelper.COL_INSTRUCTIONS, recipe.getInstructions());

        // Guard 2: recipe names are UNIQUE, so a repeat is ignored instead of duplicated
        long recipeId = db.insertWithOnConflict(PantryDbHelper.TABLE_RECIPE, null, values,
                SQLiteDatabase.CONFLICT_IGNORE);
        if (recipeId == -1) {
            return;
        }

        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            ContentValues row = new ContentValues();
            row.put(PantryDbHelper.COL_RECIPE_ID, recipeId);
            row.put(PantryDbHelper.COL_INGREDIENT_NAME, ingredient.getName());
            row.put(PantryDbHelper.COL_QUANTITY, ingredient.getQuantity());
            row.put(PantryDbHelper.COL_UNIT, ingredient.getUnit());
            db.insert(PantryDbHelper.TABLE_RECIPE_INGREDIENT, null, row);
        }
    }
}
