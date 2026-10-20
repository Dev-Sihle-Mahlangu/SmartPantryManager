package com.smartpantry.manager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.smartpantry.manager.models.PantryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the SQLite database for Smart Pantry Manager.
 * Phase 2 only has the pantry_item table. The recipe tables will be added in
 * Phase 3 by raising DATABASE_VERSION and extending onUpgrade, so existing
 * pantry data is kept.
 */
public class PantryDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "smart_pantry.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_PANTRY = "pantry_item";
    public static final String COL_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY = "expiry_date";

    private static final String CREATE_PANTRY_TABLE =
            "CREATE TABLE " + TABLE_PANTRY + " ("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_NAME + " TEXT NOT NULL, "
                    + COL_QUANTITY + " REAL NOT NULL CHECK(" + COL_QUANTITY + " > 0), "
                    + COL_UNIT + " TEXT NOT NULL, "
                    + COL_EXPIRY + " TEXT)";

    public PantryDbHelper(Context context) {
        // Application context avoids leaking an Activity
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Needed in Phase 3 for the recipe to ingredient foreign keys
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PANTRY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Deliberately does NOT drop tables, so a user's pantry survives upgrades.
        // Later versions will add CREATE TABLE / ALTER TABLE steps here.
    }

    // ---------------------------------------------------------------- CREATE

    /** Inserts an item and returns its new row id, or -1 if the insert failed. */
    public long insertItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_PANTRY, null, toContentValues(item));
    }

    // ------------------------------------------------------------------ READ

    /** Returns every pantry item, sorted by name ignoring upper/lower case. */
    public List<PantryItem> getAllItems() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_PANTRY, null, null, null, null, null,
                COL_NAME + " COLLATE NOCASE ASC")) {
            while (cursor.moveToNext()) {
                items.add(fromCursor(cursor));
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
                return fromCursor(cursor);
            }
        }
        return null;
    }

    // ---------------------------------------------------------------- UPDATE

    /** Returns the number of rows changed (1 on success, 0 if the id was not found). */
    public int updateItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        return db.update(TABLE_PANTRY, toContentValues(item), COL_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }

    // ---------------------------------------------------------------- DELETE

    /** Returns the number of rows deleted. */
    public int deleteItem(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_PANTRY, COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // --------------------------------------------------------------- helpers

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

    private PantryItem fromCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
        double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_QUANTITY));
        String unit = cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIT));
        int expiryIndex = cursor.getColumnIndexOrThrow(COL_EXPIRY);
        String expiry = cursor.isNull(expiryIndex) ? null : cursor.getString(expiryIndex);
        return new PantryItem(id, name, quantity, unit, expiry);
    }
}
