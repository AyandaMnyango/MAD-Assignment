package com.example.madassignment;

import android.os.Bundle; import android.view.LayoutInflater; import android.view.View; import android.view.ViewGroup; import android.widget.TextView;
import androidx.annotation.NonNull; import androidx.annotation.Nullable; import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager; import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecipesFragment extends Fragment {
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipes, container, false);
        DBHelper dbHelper = new DBHelper(getContext());
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewRecipes); TextView tvNoRecipes = view.findViewById(R.id.tvNoRecipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Recipe> suggestions = dbHelper.getSuggestedRecipes();
        if (suggestions.isEmpty()) { tvNoRecipes.setVisibility(View.VISIBLE); recyclerView.setVisibility(View.GONE); }
        else { tvNoRecipes.setVisibility(View.GONE); recyclerView.setVisibility(View.VISIBLE); recyclerView.setAdapter(new RecipeAdapter(suggestions, getContext())); }
        return view;
    }
}