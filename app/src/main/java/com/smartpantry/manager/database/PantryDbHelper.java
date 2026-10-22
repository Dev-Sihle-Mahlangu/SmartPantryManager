package com.smartpantry.manager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.smartpantry.manager.models.PantryItem;
import com.smartpantry.manager.models.Recipe;
import com.smartpantry.manager.models.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles the SQLite database for Smart Pantry Manager.
 *
 * Tables:
 * - pantry_item: what the user has at home (Phase 2)
 * - recipe: recipe name and preparation steps (Phase 3)
 * - recipe_ingredient: the ingredients each recipe needs, linked to recipe by recipe_id (Phase 3)
 *
 * Version history:
 * 1 = pantry_item only
 * 2 = adds the recipe tables and seeds the recipes. Upgrading from 1 keeps all pantry data.
 */
public class PantryDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "smart_pantry.db";
    public static final int DATABASE_VERSION = 2;

    // pantry_item
    public static final String TABLE_PANTRY = "pantry_item";
    public static final String COL_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY = "expiry_date";

    // recipe
    public static final String TABLE_RECIPE = "recipe";
    public static final String COL_INSTRUCTIONS = "instructions";

    // recipe_ingredient
    public static final String TABLE_RECIPE_INGREDIENT = "recipe_ingredient";
    public static final String COL_RECIPE_ID = "recipe_id";
    public static final String COL_INGREDIENT_NAME = "ingredient_name";

    private static final String CREATE_PANTRY_TABLE =
            "CREATE TABLE " + TABLE_PANTRY + " ("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_NAME + " TEXT NOT NULL, "
                    + COL_QUANTITY + " REAL NOT NULL CHECK(" + COL_QUANTITY + " > 0), "
                    + COL_UNIT + " TEXT NOT NULL, "
                    + COL_EXPIRY + " TEXT)";

    private static final String CREATE_RECIPE_TABLE =
            "CREATE TABLE " + TABLE_RECIPE + " ("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_NAME + " TEXT NOT NULL UNIQUE, "
                    + COL_INSTRUCTIONS + " TEXT NOT NULL)";

    // Each ingredient row belongs to one recipe. Deleting a recipe removes its ingredients too.
    private static final String CREATE_RECIPE_INGREDIENT_TABLE =
            "CREATE TABLE " + TABLE_RECIPE_INGREDIENT + " ("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_RECIPE_ID + " INTEGER NOT NULL, "
                    + COL_INGREDIENT_NAME + " TEXT NOT NULL, "
                    + COL_QUANTITY + " REAL NOT NULL CHECK(" + COL_QUANTITY + " > 0), "
                    + COL_UNIT + " TEXT NOT NULL, "
                    + "FOREIGN KEY(" + COL_RECIPE_ID + ") REFERENCES " + TABLE_RECIPE
                    + "(" + COL_ID + ") ON DELETE CASCADE)";

    public PantryDbHelper(Context context) {
        this(context, DATABASE_NAME);
    }

    /** Lets tests use a separate database file so they never touch the real pantry data. */
    public PantryDbHelper(Context context, String databaseName) {
        // Application context avoids leaking an Activity
        super(context.getApplicationContext(), databaseName, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Makes SQLite enforce the recipe_ingredient to recipe foreign key
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PANTRY_TABLE);
        createRecipeTables(db);
        RecipeSeeder.seed(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Never drops tables, so a user's pantry survives an upgrade
        if (oldVersion < 2) {
            createRecipeTables(db);
            RecipeSeeder.seed(db);
        }
    }

    private void createRecipeTables(SQLiteDatabase db) {
        db.execSQL(CREATE_RECIPE_TABLE);
        db.execSQL(CREATE_RECIPE_INGREDIENT_TABLE);
    }

    // =============================================================== PANTRY

    /** Inserts an item and returns its new row id, or -1 if the insert failed. */
    public long insertItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_PANTRY, null, toContentValues(item));
    }

    /** Returns every pantry item, sorted by name ignoring upper/lower case. */
    public List<PantryItem> getAllItems() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_PANTRY, null, null, null, null, null,
                COL_NAME + " COLLATE NOCASE ASC")) {
            while (cursor.moveToNext()) {
                items.add(pantryItemFromCursor(cursor));
            }
        }
        return items;
    }

    /** Returns one item, or null when no row has that id. */
    public PantryItem getItemById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_PANTRY, null, COL_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null, "1")) {
            if (cursor.moveToFirst()) {
                return pantryItemFromCursor(cursor);
            }
        }
        return null;
    }

    /** Returns the number of rows changed (1 on success, 0 if the id was not found). */
    public int updateItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        return db.update(TABLE_PANTRY, toContentValues(item), COL_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }

    /** Returns the number of rows deleted. */
    public int deleteItem(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_PANTRY, COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    /**
     * Deletes several pantry items in one transaction, so either all of them are
     * removed or (if something fails) none are. Returns the number of rows deleted.
     */
    public int deleteItems(List<Long> ids) {
        SQLiteDatabase db = getWritableDatabase();
        int total = 0;
        db.beginTransaction();
        try {
            for (long id : ids) {
                total += db.delete(TABLE_PANTRY, COL_ID + " = ?", new String[]{String.valueOf(id)});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return total;
    }

    private ContentValues toContentValues(PantryItem item) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, item.getName());
        values.put(COL_QUANTITY, item.getQuantity());
        values.put(COL_UNIT, item.getUnit());
        if (item.getExpiryDate() == null) {
            values.putNull(COL_EXPIRY);
        } else {
            values.put(COL_EXPIRY, item.getExpiryDate());
        }
        return values;
    }

    private PantryItem pantryItemFromCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
        double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_QUANTITY));
        String unit = cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIT));
        int expiryIndex = cursor.getColumnIndexOrThrow(COL_EXPIRY);
        String expiry = cursor.isNull(expiryIndex) ? null : cursor.getString(expiryIndex);
        return new PantryItem(id, name, quantity, unit, expiry);
    }

    // =============================================================== RECIPES

    /** Returns every recipe with its ingredients, sorted by name. */
    public List<Recipe> getAllRecipes() {
        SQLiteDatabase db = getReadableDatabase();

        // Read all ingredient rows once and group them by recipe id
        Map<Long, List<RecipeIngredient>> ingredientsByRecipe = new HashMap<>();
        try (Cursor cursor = db.query(TABLE_RECIPE_INGREDIENT, null, null, null, null, null,
                COL_RECIPE_ID + ", " + COL_ID)) {
            while (cursor.moveToNext()) {
                RecipeIngredient ingredient = ingredientFromCursor(cursor);
                List<RecipeIngredient> group = ingredientsByRecipe.get(ingredient.getRecipeId());
                if (group == null) {
                    group = new ArrayList<>();
                    ingredientsByRecipe.put(ingredient.getRecipeId(), group);
                }
                group.add(ingredient);
            }
        }

        List<Recipe> recipes = new ArrayList<>();
        try (Cursor cursor = db.query(TABLE_RECIPE, null, null, null, null, null,
                COL_NAME + " COLLATE NOCASE ASC")) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
                List<RecipeIngredient> ingredients = ingredientsByRecipe.get(id);
                if (ingredients == null) {
                    ingredients = new ArrayList<>();
                }
                recipes.add(new Recipe(id,
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_INSTRUCTIONS)),
                        ingredients));
            }
        }
        return recipes;
    }

    /** Returns one recipe with its ingredients, or null when no recipe has that id. */
    public Recipe getRecipeById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        String idArg = String.valueOf(id);

        List<RecipeIngredient> ingredients = new ArrayList<>();
        try (Cursor cursor = db.query(TABLE_RECIPE_INGREDIENT, null, COL_RECIPE_ID + " = ?",
                new String[]{idArg}, null, null, COL_ID)) {
            while (cursor.moveToNext()) {
                ingredients.add(ingredientFromCursor(cursor));
            }
        }

        try (Cursor cursor = db.query(TABLE_RECIPE, null, COL_ID + " = ?",
                new String[]{idArg}, null, null, null, "1")) {
            if (cursor.moveToFirst()) {
                return new Recipe(id,
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_INSTRUCTIONS)),
                        ingredients);
            }
        }
        return null;
    }

    private RecipeIngredient ingredientFromCursor(Cursor cursor) {
        return new RecipeIngredient(
                cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COL_RECIPE_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_INGREDIENT_NAME)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COL_QUANTITY)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIT)));
    }
}
