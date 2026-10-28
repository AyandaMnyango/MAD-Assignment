package com.example.madassignment;

import android.os.Bundle; import android.widget.TextView; import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_recipe_detail);
        ((TextView) findViewById(R.id.tvDetailName)).setText(getIntent().getStringExtra("recipe_name"));
        ((TextView) findViewById(R.id.tvDetailInstructions)).setText(getIntent().getStringExtra("recipe_instructions"));
    }
}