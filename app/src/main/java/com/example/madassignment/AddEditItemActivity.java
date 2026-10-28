package com.example.madassignment;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditItemActivity extends AppCompatActivity {

    // -1 means we are adding a new item. Any other number means we are editing.
    private int itemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        DBHelper dbHelper = new DBHelper(this);
        EditText etName = findViewById(R.id.etItemName);
        EditText etQuantity = findViewById(R.id.etItemQuantity);
        EditText etUnit = findViewById(R.id.etItemUnit);
        Button btnSave = findViewById(R.id.btnSaveItem);

        // CHECK IF WE ARE EDITING AN EXISTING ITEM
        if (getIntent().hasExtra("id")) {
            itemId = getIntent().getIntExtra("id", -1);

            // Populate the text fields with the existing data
            etName.setText(getIntent().getStringExtra("name"));
            etQuantity.setText(String.valueOf(getIntent().getDoubleExtra("quantity", 0)));
            etUnit.setText(getIntent().getStringExtra("unit"));

            // Change button text so the user knows they are updating
            btnSave.setText("Update Item");
        }

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String qtyStr = etQuantity.getText().toString().trim();
            String unit = etUnit.getText().toString().trim();

            // Input Validation
            if (name.isEmpty() || qtyStr.isEmpty() || unit.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double quantity = Double.parseDouble(qtyStr);
                if (quantity <= 0) throw new NumberFormatException();

                boolean success;
                // If itemId is -1, add new item. Otherwise, update the existing one.
                if (itemId == -1) {
                    success = dbHelper.addPantryItem(name, quantity, unit);
                } else {
                    success = dbHelper.updatePantryItem(itemId, name, quantity, unit);
                }

                if (success) {
                    Toast.makeText(this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close the screen and go back to the pantry list
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Quantity must be a positive number", Toast.LENGTH_SHORT).show();
            }
        });
    }
}