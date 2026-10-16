package com.smartpantry.manager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.smartpantry.manager.R;

/**
 * Home screen. Introduces the app and lets the user jump to the
 * main sections using explicit Intents.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Each card opens one section of the app
        openOnClick(findViewById(R.id.cardPantry), PantryActivity.class);
        openOnClick(findViewById(R.id.cardRecipes), SuggestedRecipesActivity.class);
        openOnClick(findViewById(R.id.cardSettings), SettingsActivity.class);
    }

    // Small helper so the three card listeners are not copy-pasted
    private void openOnClick(View card, Class<?> target) {
        card.setOnClickListener(v -> startActivity(new Intent(this, target)));
    }
}
