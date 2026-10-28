package com.example.madassignment;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            // Navigation logic for the three tabs
            if (itemId == R.id.nav_pantry) {
                selectedFragment = new PantryFragment();
            } else if (itemId == R.id.nav_recipes) {
                selectedFragment = new RecipesFragment();
            } else if (itemId == R.id.nav_settings) {
                selectedFragment = new SettingsFragment(); // Added Settings Screen
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
            return true;
        });

        // Set default fragment to Pantry on first launch
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PantryFragment()).commit();
        }
    }
}