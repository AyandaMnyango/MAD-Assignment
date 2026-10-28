package com.example.madassignment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {
    private List<PantryItem> items;
    private Context context;
    private DBHelper dbHelper;
    private PantryFragment fragment;

    public PantryAdapter(List<PantryItem> items, Context context, DBHelper dbHelper, PantryFragment fragment) {
        this.items = items;
        this.context = context;
        this.dbHelper = dbHelper;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This inflates your custom layout (item_pantry.xml)
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_pantry, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PantryItem item = items.get(position);

        // Set the text using the IDs from item_pantry.xml
        holder.tvName.setText(item.getName().toUpperCase());
        holder.tvQuantity.setText(item.getQuantity() + " " + item.getUnit());

        // SINGLE TAP LISTENER (Opens the Popup Menu)
        holder.itemView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Manage " + item.getName());

            String[] options = {"Edit Item", "Delete Item"};

            builder.setItems(options, (dialog, which) -> {
                if (which == 0) {
                    // USER CHOSE EDIT
                    Intent intent = new Intent(context, AddEditItemActivity.class);
                    intent.putExtra("id", item.getId());
                    intent.putExtra("name", item.getName());
                    intent.putExtra("quantity", item.getQuantity());
                    intent.putExtra("unit", item.getUnit());
                    context.startActivity(intent);

                } else if (which == 1) {
                    // USER CHOSE DELETE
                    dbHelper.deletePantryItem(item.getId());
                    items.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, items.size());

                    // Refresh the fragment to show the empty state if the list is now empty
                    if (items.isEmpty()) {
                        fragment.onResume();
                    }
                }
            });
            builder.show();
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // These IDs must match what is in item_pantry.xml
            tvName = itemView.findViewById(R.id.tvPantryName);
            tvQuantity = itemView.findViewById(R.id.tvPantryQuantity);
        }
    }
}