package com.example.madassignment;

import android.content.Context; import android.view.LayoutInflater; import android.view.View; import android.view.ViewGroup; import android.widget.TextView;
import androidx.annotation.NonNull; import androidx.recyclerview.widget.RecyclerView; import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {
    private List<PantryItem> items; private Context context; private DBHelper dbHelper; private PantryFragment fragment;
    public PantryAdapter(List<PantryItem> items, Context context, DBHelper dbHelper, PantryFragment fragment) { this.items = items; this.context = context; this.dbHelper = dbHelper; this.fragment = fragment; }
    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { return new ViewHolder(LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false)); }
    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.text1.setText(item.getName().toUpperCase()); holder.text2.setText(item.getQuantity() + " " + item.getUnit());
        holder.itemView.setOnLongClickListener(v -> { dbHelper.deletePantryItem(item.getId()); items.remove(position); notifyItemRemoved(position); notifyItemRangeChanged(position, items.size()); if (items.isEmpty()) fragment.onResume(); return true; });
    }
    @Override public int getItemCount() { return items.size(); }
    public static class ViewHolder extends RecyclerView.ViewHolder { TextView text1, text2; public ViewHolder(@NonNull View itemView) { super(itemView); text1 = itemView.findViewById(android.R.id.text1); text2 = itemView.findViewById(android.R.id.text2); } }
}