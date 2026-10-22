package com.smartpantry.manager.activities;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.smartpantry.manager.R;

/**
 * Shared toolbar behaviour for the main screens: the Up arrow and a
 * navigation menu with Home, My Pantry, Suggested Recipes and Settings.
 * Screens that extend this class need a Toolbar with the id "toolbar" in their layout.
 */
public abstract class BaseNavigationActivity extends AppCompatActivity {

    private static final int[] NAV_ITEM_IDS = {
            R.id.action_home, R.id.action_pantry, R.id.action_recipes, R.id.action_settings
    };

    /** The menu item for this screen. It is hidden so the user is not offered the screen they are on. */
    protected int getCurrentNavItemId() {
        return 0;
    }

    /** Screens such as the add/edit form can switch the navigation menu off. */
    protected boolean showNavigationMenu() {
        return true;
    }

    protected void setUpToolbar(@StringRes int titleRes) {
        setUpToolbar(getString(titleRes));
    }

    protected void setUpToolbar(CharSequence title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!showNavigationMenu()) {
            return false;
        }
        getMenuInflater().inflate(R.menu.menu_navigation, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        int current = getCurrentNavItemId();
        for (int id : NAV_ITEM_IDS) {
            MenuItem item = menu.findItem(id);
            if (item != null) {
                item.setVisible(id != current);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_home) {
            navigateTo(MainActivity.class);
            return true;
        } else if (id == R.id.action_pantry) {
            navigateTo(PantryActivity.class);
            return true;
        } else if (id == R.id.action_recipes) {
            navigateTo(SuggestedRecipesActivity.class);
            return true;
        } else if (id == R.id.action_settings) {
            navigateTo(SettingsActivity.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Opens a screen with an explicit Intent. CLEAR_TOP and SINGLE_TOP reuse the screen
     * if it is already open further back, so the back stack does not fill up with copies.
     */
    private void navigateTo(Class<?> target) {
        Intent intent = new Intent(this, target);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
