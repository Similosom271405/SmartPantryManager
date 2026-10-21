package com.example.smartpantrymanager.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {

    private SwitchCompat switchExpiryAlerts;
    private RadioGroup rgMeasurementUnits;
    private Button btnClearData;
    private SharedPreferences prefs;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("App Preferences & Settings");
        }

        dbHelper = new DatabaseHelper(this);
        prefs = getSharedPreferences("SmartPantryPrefs", MODE_PRIVATE);

        switchExpiryAlerts = findViewById(R.id.switchExpiryAlerts);
        rgMeasurementUnits = findViewById(R.id.rgMeasurementUnits);
        btnClearData = findViewById(R.id.btnClearPantryData);

        boolean alertsEnabled = prefs.getBoolean("key_expiry_alerts", true);
        String unitSystem = prefs.getString("key_unit_system", "metric");

        switchExpiryAlerts.setChecked(alertsEnabled);
        if ("imperial".equals(unitSystem)) {
            ((RadioButton) findViewById(R.id.rbImperial)).setChecked(true);
        } else {
            ((RadioButton) findViewById(R.id.rbMetric)).setChecked(true);
        }

        switchExpiryAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("key_expiry_alerts", isChecked).apply();
            Toast.makeText(this, isChecked ? "Expiry reminders enabled" : "Expiry reminders disabled", Toast.LENGTH_SHORT).show();
        });

        rgMeasurementUnits.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbImperial) {
                prefs.edit().putString("key_unit_system", "imperial").apply();
                Toast.makeText(this, "Standard Units selected", Toast.LENGTH_SHORT).show();
            } else {
                prefs.edit().putString("key_unit_system", "metric").apply();
                Toast.makeText(this, "Metric Units (g, kg, ml) selected", Toast.LENGTH_SHORT).show();
            }
        });

        btnClearData.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Reset Pantry")
                    .setMessage("This will clear all ingredients from your pantry. Seeded recipes will remain intact.")
                    .setPositiveButton("Reset", (dialog, which) -> {
                        dbHelper.clearAllPantryItems();
                        Toast.makeText(this, "Pantry reset successfully", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_settings);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_settings) {
                return true;
            } else if (itemId == R.id.nav_pantry) {
                startActivity(new Intent(this, PantryActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_recipes) {
                startActivity(new Intent(this, SuggestedRecipesActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }
}