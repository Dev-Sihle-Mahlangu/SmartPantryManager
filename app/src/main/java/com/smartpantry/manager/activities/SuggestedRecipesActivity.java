package com.smartpantry.manager.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.smartpantry.manager.R;
import com.smartpantry.manager.adapters.RecipeAdapter;
import com.smartpantry.manager.database.PantryDbHelper;
import com.smartpantry.manager.models.PantryItem;
import com.smartpantry.manager.models.Recipe;
import com.smartpantry.manager.utils.AppSettings;
import com.smartpantry.manager.utils.ExpiryUtils;
import com.smartpantry.manager.utils.RecipeMatcher;

import java.time.LocalDate;
import java.util.List;

/** Shows only the recipes that can be made completely from the current pantry. */
public class SuggestedRecipesActivity extends BaseNavigationActivity
        implements RecipeAdapter.OnRecipeClickListener {

    private PantryDbHelper dbHelper;
    private AppSettings settings;
    private RecipeAdapter adapter;
    private RecyclerView recyclerRecipes;
    private TextView textSummary;
    private View layoutEmpty;
    private TextView textEmptyMessage;

    @Override
    protected int getCurrentNavItemId() {
        return R.id.action_recipes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);
        setUpToolbar(R.string.title_recipes);

        dbHelper = new PantryDbHelper(this);
        settings = new AppSettings(this);

        recyclerRecipes = findViewById(R.id.recyclerRecipes);
        textSummary = findViewById(R.id.textSummary);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        textEmptyMessage = findViewById(R.id.textEmptyMessage);
        MaterialButton buttonGoToPantry = findViewById(R.id.buttonGoToPantry);

        adapter = new RecipeAdapter(this);
        recyclerRecipes.setLayoutManager(new LinearLayoutManager(this));
        recyclerRecipes.setAdapter(adapter);

        buttonGoToPantry.setOnClickListener(v ->
                startActivity(new Intent(this, PantryActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recalculated every time the screen is shown, so suggestions always
        // reflect the latest pantry and settings
        loadSuggestions();
    }

    private void loadSuggestions() {
        List<PantryItem> allItems;
        List<Recipe> allRecipes;
        try {
            allItems = dbHelper.getAllItems();
            allRecipes = dbHelper.getAllRecipes();
        } catch (SQLiteException e) {
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_LONG).show();
            return;
        }

        // The user can choose not to use expired food when suggesting recipes
        List<PantryItem> usableItems = allItems;
        if (settings.isIgnoreExpiredInSuggestions()) {
            usableItems = ExpiryUtils.withoutExpired(allItems, LocalDate.now());
        }

        List<Recipe> makeable = RecipeMatcher.findMakeableRecipes(allRecipes, usableItems);
        adapter.setRecipes(makeable);

        if (makeable.isEmpty()) {
            recyclerRecipes.setVisibility(View.GONE);
            textSummary.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
            textEmptyMessage.setText(allItems.isEmpty()
                    ? R.string.suggestions_empty_pantry
                    : R.string.suggestions_empty_no_match);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            recyclerRecipes.setVisibility(View.VISIBLE);
            textSummary.setVisibility(View.VISIBLE);
            textSummary.setText(getResources().getQuantityString(
                    R.plurals.suggestions_summary, makeable.size(), makeable.size()));
        }
    }

    @Override
    public void onRecipeClicked(Recipe recipe) {
        // Only the id is passed. The detail screen loads the recipe itself.
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
