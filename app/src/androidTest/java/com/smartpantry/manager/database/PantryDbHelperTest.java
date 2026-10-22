package com.smartpantry.manager.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.smartpantry.manager.models.PantryItem;
import com.smartpantry.manager.models.Recipe;
import com.smartpantry.manager.utils.RecipeMatcher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tests the real SQLite database on a device or emulator.
 * A separate database file is used, so the app's own pantry data is never touched.
 */
@RunWith(AndroidJUnit4.class)
public class PantryDbHelperTest {

    private static final String TEST_DB = "test_pantry_manager.db";

    private Context context;
    private PantryDbHelper helper;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase(TEST_DB);
        helper = new PantryDbHelper(context, TEST_DB);
    }

    @After
    public void tearDown() {
        helper.close();
        context.deleteDatabase(TEST_DB);
    }

    @Test
    public void newDatabase_isSeededWithBetween15And20Recipes() {
        int count = helper.getAllRecipes().size();
        assertTrue("recipe count was " + count, count >= 15 && count <= 20);
    }

    @Test
    public void reopeningDatabase_doesNotDuplicateRecipes() {
        int first = helper.getAllRecipes().size();
        helper.close();

        PantryDbHelper reopened = new PantryDbHelper(context, TEST_DB);
        int second = reopened.getAllRecipes().size();
        reopened.close();

        assertEquals(first, second);
    }

    @Test
    public void everySeededRecipe_hasIngredientsAndInstructions() {
        for (Recipe recipe : helper.getAllRecipes()) {
            assertTrue(recipe.getName() + " has no ingredients", recipe.getIngredients().size() >= 2);
            assertTrue(recipe.getName() + " has no instructions", !recipe.getInstructions().isEmpty());
        }
    }

    @Test
    public void pantryCrud_fullCycle() {
        // Create
        long id = helper.insertItem(new PantryItem("Rice", 2, "kg", "2026-12-01"));
        assertNotEquals(-1, id);

        // Read
        PantryItem saved = helper.getItemById(id);
        assertNotNull(saved);
        assertEquals("Rice", saved.getName());
        assertEquals(2.0, saved.getQuantity(), 0.0001);
        assertEquals("kg", saved.getUnit());
        assertEquals("2026-12-01", saved.getExpiryDate());

        // Update
        int updated = helper.updateItem(new PantryItem(id, "Brown rice", 1.5, "kg", null));
        assertEquals(1, updated);
        PantryItem changed = helper.getItemById(id);
        assertEquals("Brown rice", changed.getName());
        assertEquals(1.5, changed.getQuantity(), 0.0001);
        assertNull(changed.getExpiryDate());

        // Delete
        assertEquals(1, helper.deleteItem(id));
        assertNull(helper.getItemById(id));
    }

    @Test
    public void deleteItems_removesEverythingGiven() {
        long a = helper.insertItem(new PantryItem("Eggs", 6, "piece", null));
        long b = helper.insertItem(new PantryItem("Milk", 1, "l", null));
        long c = helper.insertItem(new PantryItem("Flour", 500, "g", null));

        int deleted = helper.deleteItems(Arrays.asList(a, b));

        assertEquals(2, deleted);
        List<PantryItem> left = helper.getAllItems();
        assertEquals(1, left.size());
        assertEquals(c, left.get(0).getId());
    }

    @Test
    public void upgradeFromVersion1_keepsPantryDataAndAddsRecipes() {
        helper.close();
        context.deleteDatabase(TEST_DB);

        // Build a database exactly as version 1 of the app created it (pantry table only)
        File dbFile = context.getDatabasePath(TEST_DB);
        dbFile.getParentFile().mkdirs();
        SQLiteDatabase oldDb = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
        oldDb.execSQL("CREATE TABLE pantry_item (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, quantity REAL NOT NULL CHECK(quantity > 0), "
                + "unit TEXT NOT NULL, expiry_date TEXT)");
        oldDb.execSQL("INSERT INTO pantry_item (name, quantity, unit) VALUES ('Rice', 2, 'kg')");
        oldDb.setVersion(1);
        oldDb.close();

        // Opening it with the current helper runs onUpgrade
        helper = new PantryDbHelper(context, TEST_DB);
        List<PantryItem> items = helper.getAllItems();

        assertEquals(1, items.size());
        assertEquals("Rice", items.get(0).getName());
        assertTrue(helper.getAllRecipes().size() >= 15);
    }

    @Test
    public void suggestions_onlyIncludeFullyMakeableRecipes() {
        helper.insertItem(new PantryItem("Eggs", 3, "piece", null));
        helper.insertItem(new PantryItem("Cheese", 50, "g", null));
        helper.insertItem(new PantryItem("Butter", 10, "g", null));

        List<Recipe> makeable = RecipeMatcher.findMakeableRecipes(
                helper.getAllRecipes(), helper.getAllItems());

        List<String> names = new ArrayList<>();
        for (Recipe recipe : makeable) {
            names.add(recipe.getName());
        }
        assertEquals(Arrays.asList("Cheese Omelette"), names);
    }
}
