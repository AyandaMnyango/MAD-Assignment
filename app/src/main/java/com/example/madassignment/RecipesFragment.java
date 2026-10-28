package com.example.madassignment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecipesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipes, container, false);
        DBHelper dbHelper = new DBHelper(getContext());

        // 1. Setup Suggested Recipes
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewRecipes);
        TextView tvNoRecipes = view.findViewById(R.id.tvNoRecipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Recipe> suggestions = dbHelper.getSuggestedRecipes();

        if (suggestions.isEmpty()) {
            tvNoRecipes.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoRecipes.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(new RecipeAdapter(suggestions, getContext()));
        }

        // 2. Setup Almost There Recipes (BONUS)
        RecyclerView recyclerAlmost = view.findViewById(R.id.recyclerViewAlmostThere);
        TextView tvAlmost = view.findViewById(R.id.tvAlmostThere);
        recyclerAlmost.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Recipe> almost = dbHelper.getAlmostThereRecipes();

        if (almost.isEmpty()) {
            tvAlmost.setVisibility(View.VISIBLE);
            recyclerAlmost.setVisibility(View.GONE);
        } else {
            tvAlmost.setVisibility(View.GONE);
            recyclerAlmost.setVisibility(View.VISIBLE);
            recyclerAlmost.setAdapter(new RecipeAdapter(almost, getContext()));
        }

        return view;
    }
}