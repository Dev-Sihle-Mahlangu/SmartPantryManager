package com.smartpantry.manager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.smartpantry.manager.R;
import com.smartpantry.manager.adapters.RecipeAdapter;
import com.smartpantry.manager.database.PantryDbHelper;
import com.smartpantry.manager.models.PantryItem;
import com.smartpantry.manager.models.Recipe;
import com.smartpantry.manager.utils.RecipeMatcher;

import java.util.List;

/** Shows only the recipes that can be made completely from the current pantry. */
public class SuggestedRecipesActivity extends AppCompatActivity
        implements RecipeAdapter.OnRecipeClickListener {

    private PantryDbHelper dbHelper;
    private RecipeAdapter adapter;
    private RecyclerView recyclerRecipes;
    private TextView textSummary;
    private View layoutEmpty;
    private TextView textEmptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_recipes);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new PantryDbHelper(this);

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
        // reflect the latest pantry changes
        loadSuggestions();
    }

    private void loadSuggestions() {
        List<PantryItem> pantry = dbHelper.getAllItems();
        List<Recipe> allRecipes = dbHelper.getAllRecipes();
        List<Recipe> makeable = RecipeMatcher.findMakeableRecipes(allRecipes, pantry);
        adapter.setRecipes(makeable);

        if (makeable.isEmpty()) {
            recyclerRecipes.setVisibility(View.GONE);
            textSummary.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
            textEmptyMessage.setText(pantry.isEmpty()
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
