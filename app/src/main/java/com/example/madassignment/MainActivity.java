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
            if (item.getItemId() == R.id.nav_pantry) selectedFragment = new PantryFragment();
            else if (item.getItemId() == R.id.nav_recipes) selectedFragment = new RecipesFragment();
            if (selectedFragment != null) getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        });

        if (savedInstanceState == null) getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PantryFragment()).commit();
    }
}