package com.example.madassignment;

import android.content.Intent;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class PantryFragment extends Fragment {
    private RecyclerView recyclerView; private PantryAdapter adapter; private DBHelper dbHelper; private TextView emptyView;

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pantry, container, false);
        dbHelper = new DBHelper(getContext()); recyclerView = view.findViewById(R.id.recyclerViewPantry); emptyView = view.findViewById(R.id.tvEmptyPantry);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        FloatingActionButton fab = view.findViewById(R.id.fabAddItem);
        fab.setOnClickListener(v -> startActivity(new Intent(getContext(), AddEditItemActivity.class)));
        loadPantryItems(); return view;
    }
    @Override public void onResume() { super.onResume(); loadPantryItems(); }
    private void loadPantryItems() {
        List<PantryItem> items = dbHelper.getAllPantryItems();
        if (items.isEmpty()) { emptyView.setVisibility(View.VISIBLE); recyclerView.setVisibility(View.GONE); }
        else { emptyView.setVisibility(View.GONE); recyclerView.setVisibility(View.VISIBLE); adapter = new PantryAdapter(items, getContext(), dbHelper, this); recyclerView.setAdapter(adapter); }
    }
}