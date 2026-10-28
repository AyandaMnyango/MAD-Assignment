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
    // IMPORTANT: We changed the version to 2 so the emulator rebuilds the database
    private static final int DATABASE_VERSION = 2;

    public DBHelper(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, quantity REAL, unit TEXT)");
        db.execSQL("CREATE TABLE recipes (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, instructions TEXT)");
        db.execSQL("CREATE TABLE recipe_ingredients (id INTEGER PRIMARY KEY AUTOINCREMENT, recipe_id INTEGER, name TEXT, quantity REAL, unit TEXT)");

        seedRecipes(db);
        seedPantry(db); // Pre-fill the pantry
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS pantry");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        onCreate(db);
    }

    // --- NEW METHOD: Pre-defined Pantry Items ---
    private void seedPantry(SQLiteDatabase db) {
        // These 10 items will guarantee you have a mix of Suggested and Almost There recipes
        db.execSQL("INSERT INTO pantry (name, quantity, unit) VALUES ('egg', 3, 'units')");
        db.execSQL("INSERT INTO pantry (name, quantity, unit) VALUES ('cheese', 100, 'g')");
        db.execSQL("INSERT INTO pantry (name, quantity, unit) VALUES ('bread', 4, 'slices')");
        db.execSQL("INSERT INTO pantry (name, quantity, unit) VALUES ('butter', 20, 'g')");
        db.execSQL("INSERT INTO pantry (name, quantity, unit) VALUES ('garlic', 2, 'clove')");
        db.execSQL("INSERT INTO pantry (name, quantity, unit) VALUES ('pasta', 200, 'g')");
        db.execSQL("INSERT INTO pantry (name, quantity, unit) VALUES ('tomato', 2, 'units')");
        db.execSQL("INSERT INTO pantry (name, quantity, unit) VALUES ('rice', 100, 'g')");
        db.execSQL("INSERT INTO pantry (name, quantity, unit) VALUES ('peanut butter', 1, 'tbsp')"); // Missing 1 tbsp for full match
        db.execSQL("INSERT INTO pantry (name, quantity, unit) VALUES ('milk', 1, 'litre')");
    }

    // --- CRUD Operations for Pantry ---

    public boolean addPantryItem(String name, double quantity, String unit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", normalize(name));
        cv.put("quantity", quantity);
        cv.put("unit", unit);
        return db.insert("pantry", null, cv) != -1;
    }

    public boolean updatePantryItem(int id, String name, double quantity, String unit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", normalize(name));
        cv.put("quantity", quantity);
        cv.put("unit", unit);
        long result = db.update("pantry", cv, "id=?", new String[]{String.valueOf(id)});
        return result != -1;
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> list = new ArrayList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM pantry", null);
        if (cursor.moveToFirst()) {
            do { list.add(new PantryItem(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2), cursor.getString(3))); } while (cursor.moveToNext());
        }
        cursor.close(); return list;
    }

    public void deletePantryItem(int id) {
        this.getWritableDatabase().delete("pantry", "id=?", new String[]{String.valueOf(id)});
    }

    // --- Strict Matching Logic ---

    public List<Recipe> getSuggestedRecipes() {
        List<Recipe> suggested = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor recipeCursor = db.rawQuery("SELECT * FROM recipes", null);
        if (recipeCursor.moveToFirst()) {
            do {
                int recipeId = recipeCursor.getInt(0);
                boolean hasAllIngredients = true;
                Cursor ingCursor = db.rawQuery("SELECT name, quantity FROM recipe_ingredients WHERE recipe_id=?", new String[]{String.valueOf(recipeId)});
                if (ingCursor.moveToFirst()) {
                    do {
                        String reqName = normalize(ingCursor.getString(0));
                        double reqQty = ingCursor.getDouble(1);
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

    public List<Recipe> getAlmostThereRecipes() {
        List<Recipe> almostThere = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor recipeCursor = db.rawQuery("SELECT * FROM recipes", null);

        if (recipeCursor.moveToFirst()) {
            do {
                int recipeId = recipeCursor.getInt(0);
                int missingCount = 0;

                Cursor ingCursor = db.rawQuery("SELECT name, quantity FROM recipe_ingredients WHERE recipe_id=?", new String[]{String.valueOf(recipeId)});
                if (ingCursor.moveToFirst()) {
                    do {
                        String reqName = normalize(ingCursor.getString(0));
                        double reqQty = ingCursor.getDouble(1);
                        Cursor pantryCursor = db.rawQuery("SELECT quantity FROM pantry WHERE name=?", new String[]{reqName});

                        if (pantryCursor.moveToFirst()) {
                            if (pantryCursor.getDouble(0) < reqQty) {
                                missingCount++;
                            }
                        } else {
                            missingCount++;
                        }
                        pantryCursor.close();
                    } while (ingCursor.moveToNext());
                }
                ingCursor.close();

                if (missingCount == 1) {
                    almostThere.add(new Recipe(recipeId, recipeCursor.getString(1), recipeCursor.getString(2)));
                }
            } while (recipeCursor.moveToNext());
        }
        recipeCursor.close();
        return almostThere;
    }

    public String getRecipeIngredientsString(int recipeId) {
        StringBuilder ingredients = new StringBuilder();
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT name, quantity, unit FROM recipe_ingredients WHERE recipe_id=?", new String[]{String.valueOf(recipeId)});
        if (cursor.moveToFirst()) {
            do {
                ingredients.append("• ").append(cursor.getDouble(1)).append(" ").append(cursor.getString(2)).append(" of ").append(cursor.getString(0)).append("\n");
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ingredients.toString();
    }

    private String normalize(String input) {
        if (input == null) return "";
        String norm = input.toLowerCase().trim();
        if (norm.endsWith("es")) norm = norm.substring(0, norm.length() - 2);
        else if (norm.endsWith("s")) norm = norm.substring(0, norm.length() - 1);
        return norm;
    }

    private void seedRecipes(SQLiteDatabase db) {
        // ... (Keep all 20 of your existing recipes here, exactly as they are) ...
        // Recipe 1
        db.execSQL("INSERT INTO recipes (name, instructions) VALUES ('Cheese Omelette', 'Beat eggs, pour into hot pan, add cheese, fold and serve.')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (1, 'egg', 2, 'units')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (1, 'cheese', 50, 'g')");
        // Recipe 2
        db.execSQL("INSERT INTO recipes (name, instructions) VALUES ('Tomato Pasta', 'Boil pasta. Fry tomatoes with garlic. Mix together.')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (2, 'pasta', 200, 'g')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (2, 'tomato', 2, 'units')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (2, 'garlic', 1, 'clove')");
        // Recipe 3
        db.execSQL("INSERT INTO recipes (name, instructions) VALUES ('Peanut Butter Sandwich', 'Spread peanut butter on bread.')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (3, 'bread', 2, 'slices')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (3, 'peanut butter', 2, 'tbsp')");
        // Recipe 4
        db.execSQL("INSERT INTO recipes (name, instructions) VALUES ('Scrambled Eggs', 'Whisk eggs, cook in pan with butter.')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (4, 'egg', 3, 'units')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (4, 'butter', 10, 'g')");
        // Recipe 5
        db.execSQL("INSERT INTO recipes (name, instructions) VALUES ('Rice and Beans', 'Boil rice. Heat beans and mix.')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (5, 'rice', 100, 'g')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (5, 'beans', 1, 'can')");
        // Recipe 6
        db.execSQL("INSERT INTO recipes (name, instructions) VALUES ('Garlic Bread', 'Toast bread, spread butter and garlic.')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (6, 'bread', 2, 'slices')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (6, 'butter', 10, 'g')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (6, 'garlic', 1, 'clove')");
        // Recipe 7
        db.execSQL("INSERT INTO recipes (name, instructions) VALUES ('Cheese Toastie', 'Put cheese between bread and toast.')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (7, 'bread', 2, 'slices')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (7, 'cheese', 30, 'g')");
        // Recipe 8
        db.execSQL("INSERT INTO recipes (name, instructions) VALUES ('Butter Pasta', 'Boil pasta and mix with butter.')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (8, 'pasta', 150, 'g')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (8, 'butter', 20, 'g')");
        // Recipe 9
        db.execSQL("INSERT INTO recipes (name, instructions) VALUES ('Egg Fried Rice', 'Fry cooked rice with egg.')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (9, 'rice', 100, 'g')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (9, 'egg', 1, 'units')");
        // Recipe 10
        db.execSQL("INSERT INTO recipes (name, instructions) VALUES ('Tomato Sandwich', 'Slice tomato and place on bread.')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (10, 'bread', 2, 'slices')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (10, 'tomato', 1, 'units')");
        // Recipe 11
        db.execSQL("INSERT INTO recipes (name, instructions) VALUES ('Beans on Toast', 'Heat beans and pour over toast.')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (11, 'bread', 2, 'slices')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (11, 'beans', 1, 'can')");
        // Recipe 12
        db.execSQL("INSERT INTO recipes (name, instructions) VALUES ('Garlic Pasta', 'Boil pasta, fry with garlic and butter.')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (12, 'pasta', 150, 'g')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (12, 'garlic', 1, 'clove')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (12, 'butter', 10, 'g')");
        // Recipe 13
        db.execSQL("INSERT INTO recipes (name, instructions) VALUES ('Cheesy Beans', 'Mix heated beans with cheese.')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (13, 'beans', 1, 'can')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (13, 'cheese', 30, 'g')");
        // Recipe 14
        db.execSQL("INSERT INTO recipes (name, instructions) VALUES ('Rice and Egg', 'Boil rice, fry an egg and mix.')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (14, 'rice', 100, 'g')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (14, 'egg', 2, 'units')");
        // Recipe 15
        db.execSQL("INSERT INTO recipes (name, instructions) VALUES ('Tomato Omelette', 'Fry chopped tomato into an omelette.')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (15, 'egg', 2, 'units')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (15, 'tomato', 1, 'units')");
        // Recipe 16
        db.execSQL("INSERT INTO recipes (name, instructions) VALUES ('Garlic Egg', 'Scramble eggs with chopped garlic.')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (16, 'egg', 2, 'units')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (16, 'garlic', 1, 'clove')");
        // Recipe 17
        db.execSQL("INSERT INTO recipes (name, instructions) VALUES ('Peanut Butter Toast', 'Toast bread and spread peanut butter.')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (17, 'bread', 2, 'slices')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (17, 'peanut butter', 2, 'tbsp')");
        // Recipe 18
        db.execSQL("INSERT INTO recipes (name, instructions) VALUES ('Cheesy Pasta', 'Boil pasta and mix with cheese.')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (18, 'pasta', 150, 'g')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (18, 'cheese', 50, 'g')");
        // Recipe 19
        db.execSQL("INSERT INTO recipes (name, instructions) VALUES ('Bean Sandwich', 'Mash beans and put on bread.')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (19, 'bread', 2, 'slices')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (19, 'beans', 1, 'can')");
        // Recipe 20
        db.execSQL("INSERT INTO recipes (name, instructions) VALUES ('Tomato Rice', 'Cook rice with chopped tomatoes.')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (20, 'rice', 100, 'g')");
        db.execSQL("INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES (20, 'tomato', 2, 'units')");
    }
}