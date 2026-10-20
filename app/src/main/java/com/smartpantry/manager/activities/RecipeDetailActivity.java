package com.smartpantry.manager.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.smartpantry.manager.R;
import com.smartpantry.manager.database.PantryDbHelper;
import com.smartpantry.manager.models.Recipe;
import com.smartpantry.manager.models.RecipeIngredient;
import com.smartpantry.manager.utils.FormatUtils;

/** Shows one recipe: its ingredients with quantities and the preparation steps. */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "com.smartpantry.manager.EXTRA_RECIPE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, Recipe.NO_ID);
        PantryDbHelper dbHelper = new PantryDbHelper(this);
        Recipe recipe = dbHelper.getRecipeById(recipeId);
        dbHelper.close();

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
