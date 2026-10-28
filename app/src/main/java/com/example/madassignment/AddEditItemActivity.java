package com.example.madassignment;

import android.os.Bundle; import android.widget.Button; import android.widget.EditText; import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditItemActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_add_edit);
        DBHelper dbHelper = new DBHelper(this);
        EditText etName = findViewById(R.id.etItemName), etQuantity = findViewById(R.id.etItemQuantity), etUnit = findViewById(R.id.etItemUnit);
        Button btnSave = findViewById(R.id.btnSaveItem);

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim(), qtyStr = etQuantity.getText().toString().trim(), unit = etUnit.getText().toString().trim();
            if (name.isEmpty() || qtyStr.isEmpty() || unit.isEmpty()) { Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show(); return; }
            try {
                double quantity = Double.parseDouble(qtyStr);
                if (quantity <= 0) throw new NumberFormatException();
                if (dbHelper.addPantryItem(name, quantity, unit)) { Toast.makeText(this, "Item Saved", Toast.LENGTH_SHORT).show(); finish(); }
            } catch (NumberFormatException e) { Toast.makeText(this, "Quantity must be positive", Toast.LENGTH_SHORT).show(); }
        });
    }
}