package com.example.smartpantrymanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.adapters.RecipeAdapter;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.models.Recipe;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private View emptyStateCard;
    private TextView tvStrictRuleNotice;
    private RecipeAdapter adapter;
    private List<Recipe> suggestedRecipesList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Suggested Recipes (Strict Match)");
        }

        dbHelper = new DatabaseHelper(this);
        suggestedRecipesList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerViewSuggestedRecipes);
        emptyStateCard = findViewById(R.id.cardEmptySuggestedState);
        tvStrictRuleNotice = findViewById(R.id.tvStrictNotice);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(this, suggestedRecipesList, recipe -> {
            Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
            intent.putExtra("EXTRA_RECIPE", recipe);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        evaluateStrictSuggestedRecipes();
    }

    private void evaluateStrictSuggestedRecipes() {
        suggestedRecipesList.clear();
        suggestedRecipesList.addAll(dbHelper.getStrictSuggestedRecipes());
        adapter.notifyDataSetChanged();

        if (suggestedRecipesList.isEmpty()) {
            emptyStateCard.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tvStrictRuleNotice.setText("No recipes strictly match your current pantry. Add more ingredients to see meal suggestions!");
        } else {
            emptyStateCard.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            tvStrictRuleNotice.setText(String.format("You can cook %d recipe(s) right now using strictly what you have!", suggestedRecipesList.size()));
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_recipes);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_recipes) {
                return true;
            } else if (itemId == R.id.nav_pantry) {
                startActivity(new Intent(this, PantryActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_settings) {
                return true;
            }
            return false;
        });
    }
}