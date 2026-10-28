package com.example.madassignment;

import android.content.Context; import android.content.Intent; import android.view.LayoutInflater; import android.view.View; import android.view.ViewGroup; import android.widget.TextView;
import androidx.annotation.NonNull; import androidx.recyclerview.widget.RecyclerView; import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    private List<Recipe> recipes; private Context context;
    public RecipeAdapter(List<Recipe> recipes, Context context) { this.recipes = recipes; this.context = context; }
    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { return new ViewHolder(LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)); }
    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position); holder.textView.setText(recipe.getName());
        holder.itemView.setOnClickListener(v -> { Intent intent = new Intent(context, RecipeDetailActivity.class); intent.putExtra("recipe_name", recipe.getName()); intent.putExtra("recipe_instructions", recipe.getInstructions()); context.startActivity(intent); });
    }
    @Override public int getItemCount() { return recipes.size(); }
    public static class ViewHolder extends RecyclerView.ViewHolder { TextView textView; public ViewHolder(@NonNull View itemView) { super(itemView); textView = itemView.findViewById(android.R.id.text1); } }
}