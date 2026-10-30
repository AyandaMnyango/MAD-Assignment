package com.example.madassignment;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // 1. Get the recipe ID passed from the adapter
        int recipeId = getIntent().getIntExtra("recipe_id", -1);
        DBHelper dbHelper = new DBHelper(this);

        // 2. Set the Title
        ((TextView) findViewById(R.id.tvDetailName)).setText(getIntent().getStringExtra("recipe_name"));

        // 3. Fetch and Set the Ingredients from the database
        String ingredients = dbHelper.getRecipeIngredientsString(recipeId);
        ((TextView) findViewById(R.id.tvDetailIngredients)).setText(ingredients);

        // 4. Set the Instructions
        ((TextView) findViewById(R.id.tvDetailInstructions)).setText(getIntent().getStringExtra("recipe_instructions"));
    }
}