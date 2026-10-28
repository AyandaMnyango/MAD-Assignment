package com.example.madassignment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SmartPantry.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, quantity REAL, unit TEXT)");
        db.execSQL("CREATE TABLE recipes (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, instructions TEXT)");
        db.execSQL("CREATE TABLE recipe_ingredients (id INTEGER PRIMARY KEY AUTOINCREMENT, recipe_id INTEGER, name TEXT, quantity REAL, unit TEXT)");
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS pantry"); db.execSQL("DROP TABLE IF EXISTS recipes"); db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        onCreate(db);
    }

    public boolean addPantryItem(String name, double quantity, String unit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", normalize(name)); cv.put("quantity", quantity); cv.put("unit", unit);
        return db.insert("pantry", null, cv) != -1;
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> list = new ArrayList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM pantry", null);
        if (cursor.moveToFirst()) {
            do { list.add(new PantryItem(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2), cursor.getString(3))); } while (cursor.moveToNext());
        }
        cursor.close(); return list;
    }

    public void deletePantryItem(int id) { this.getWritableDatabase().delete("pantry", "id=?", new String[]{String.valueOf(id)}); }

    public List<Recipe> getSuggestedRecipes() {
        List<Recipe> suggested = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor recipeCursor = db.rawQuery("SELECT * FROM recipes", null);
        if (recipeCursor.moveToFirst()) {
            do {
                int recipeId = recipeCursor.getInt(0); boolean hasAllIngredients = true;
                Cursor ingCursor = db.rawQuery("SELECT name, quantity FROM recipe_ingredients WHERE recipe_id=?", new String[]{String.valueOf(recipeId)});
                if (ingCursor.moveToFirst()) {
                    do {
                        String reqName = normalize(ingCursor.getString(0)); double reqQty = ingCursor.getDouble(1);
                        Cursor pantryCursor = db.rawQuery("SELECT quantity FROM pantry WHERE name=?", new String[]{reqName});
                        if (pantryCursor.moveToFirst()) {
                            if (pantryCursor.getDouble(0) < reqQty) hasAllIngredients = false;
                        } else { hasAllIngredients = false; }
                        pantryCursor.close(); if (!hasAllIngredients) break;
                    } while (ingCursor.moveToNext());
                }
                ingCursor.close();
                if (hasAllIngredients) suggested.add(new Recipe(recipeId, recipeCursor.getString(1), recipeCursor.getString(2)));
            } while (recipeCursor.moveToNext());
        }
        recipeCursor.close(); return suggested;
    }

    private String normalize(String input) {
        if (input == null) return "";
        String norm = input.toLowerCase().trim();
        if (norm.endsWith("es")) norm = norm.substring(0, norm.length() - 2);
        else if (norm.endsWith("s")) norm = norm.substring(0, norm.length() - 1);
        return norm;
    }

    private void seedRecipes(SQLiteDatabase db) {
        db.execSQL("INSERT INTO recipes (name, instructions) VALUES ('Cheese Omelette', 'Beat eggs, pour into hot pan, add cheese.')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (1, 'egg', 2, 'units')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (1, 'cheese', 50, 'g')");
        db.execSQL("INSERT INTO recipes (name, instructions) VALUES ('Tomato Pasta', 'Boil pasta. Fry tomatoes with garlic.')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (2, 'pasta', 200, 'g')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (2, 'tomato', 2, 'units')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (2, 'garlic', 1, 'clove')");
        db.execSQL("INSERT INTO recipes (name, instructions) VALUES ('PB Sandwich', 'Spread peanut butter on bread.')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (3, 'bread', 2, 'slices')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (3, 'peanut butter', 2, 'tbsp')");
        // Add 12 more of these to reach the 15 required by the assignment!
    }
}