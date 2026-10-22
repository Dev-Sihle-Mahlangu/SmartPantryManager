package com.smartpantry.manager.activities;

import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.smartpantry.manager.R;
import com.smartpantry.manager.database.PantryDbHelper;
import com.smartpantry.manager.models.Recipe;
import com.smartpantry.manager.models.RecipeIngredient;
import com.smartpantry.manager.utils.FormatUtils;

/** Shows one recipe: its ingredients with quantities and the preparation steps. */
public class RecipeDetailActivity extends BaseNavigationActivity {

    public static final String EXTRA_RECIPE_ID = "com.smartpantry.manager.EXTRA_RECIPE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        setUpToolbar("");

        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, Recipe.NO_ID);
        Recipe recipe = null;
        PantryDbHelper dbHelper = new PantryDbHelper(this);
        try {
            recipe = dbHelper.getRecipeById(recipeId);
        } catch (SQLiteException e) {
            // recipe stays null and is handled below
        } finally {
            dbHelper.close();
        }

        if (recipe == null) {
            Toast.makeText(this, R.string.error_recipe_not_found, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setTitle(recipe.getName());

        StringBuilder ingredientLines = new StringBuilder();
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            if (ingredientLines.length() > 0) {
                ingredientLines.append("\n");
            }
            ingredientLines.append(getString(R.string.recipe_ingredient_line,
                    FormatUtils.formatQuantity(ingredient.getQuantity()),
                    ingredient.getUnit(),
                    ingredient.getName()));
        }

        TextView textIngredients = findViewById(R.id.textIngredients);
        TextView textInstructions = findViewById(R.id.textInstructions);
        textIngredients.setText(ingredientLines.toString());
        textInstructions.setText(recipe.getInstructions());
    }
}
