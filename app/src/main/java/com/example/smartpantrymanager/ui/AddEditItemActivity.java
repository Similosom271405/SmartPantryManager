package com.example.smartpantrymanager.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.models.PantryItem;

import java.util.Calendar;

public class AddEditItemActivity extends AppCompatActivity {

    private EditText etName, etQuantity, etExpiryDate;
    private AutoCompleteTextView spinnerUnit;
    private Button btnSave;
    private DatabaseHelper dbHelper;
    private PantryItem existingItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etIngredientName);
        etQuantity = findViewById(R.id.etIngredientQuantity);
        spinnerUnit = findViewById(R.id.actvUnit);
        etExpiryDate = findViewById(R.id.etExpiryDate);
        btnSave = findViewById(R.id.btnSaveIngredient);

        String[] units = new String[]{"pcs", "g", "kg", "ml", "L", "slices", "can", "cloves", "tsp", "pinch"};
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, units);
        spinnerUnit.setAdapter(unitAdapter);

        etExpiryDate.setFocusable(false);
        etExpiryDate.setOnClickListener(v -> showDatePicker());

        if (getIntent().hasExtra("EXTRA_PANTRY_ITEM")) {
            existingItem = (PantryItem) getIntent().getSerializableExtra("EXTRA_PANTRY_ITEM");
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Edit Ingredient");
            populateFields(existingItem);
            btnSave.setText("Update Ingredient");
        } else {
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Add New Ingredient");
            btnSave.setText("Add to Pantry");
        }

        btnSave.setOnClickListener(v -> saveItem());
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog picker = new DatePickerDialog(this, (view, y, m, d) -> {
            String formattedDate = String.format("%04d-%02d-%02d", y, m + 1, d);
            etExpiryDate.setText(formattedDate);
        }, year, month, day);
        picker.show();
    }

    private void populateFields(PantryItem item) {
        etName.setText(item.getName());
        etQuantity.setText(String.valueOf(item.getQuantity()));
        spinnerUnit.setText(item.getUnit(), false);
        etExpiryDate.setText(item.getExpiryDate());
    }

    private void saveItem() {
        String name = etName.getText().toString().trim();
        String qtyStr = etQuantity.getText().toString().trim();
        String unit = spinnerUnit.getText().toString().trim();
        String expiry = etExpiryDate.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Ingredient name is required");
            etName.requestFocus();
            return;
        }

        if (qtyStr.isEmpty()) {
            etQuantity.setError("Quantity is required");
            etQuantity.requestFocus();
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(qtyStr);
            if (quantity <= 0) {
                etQuantity.setError("Quantity must be greater than zero");
                etQuantity.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etQuantity.setError("Enter a valid number");
            etQuantity.requestFocus();
            return;
        }

        if (unit.isEmpty()) {
            spinnerUnit.setError("Unit is required");
            spinnerUnit.requestFocus();
            return;
        }

        if (existingItem == null) {
            PantryItem newItem = new PantryItem(name, quantity, unit, expiry);
            long id = dbHelper.addPantryItem(newItem);
            if (id > 0) {
                Toast.makeText(this, name + " added to pantry", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to add ingredient", Toast.LENGTH_SHORT).show();
            }
        } else {
            existingItem.setName(name);
            existingItem.setQuantity(quantity);
            existingItem.setUnit(unit);
            existingItem.setExpiryDate(expiry);

            int rows = dbHelper.updatePantryItem(existingItem);
            if (rows > 0) {
                Toast.makeText(this, name + " updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update ingredient", Toast.LENGTH_SHORT).show();
            }
        }
    }
}