package com.example.smartpantrymanager.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.models.RecipeIngredient;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Recipe Details");
        }

        TextView tvTitle = findViewById(R.id.tvDetailRecipeTitle);
        TextView tvPrepTime = findViewById(R.id.tvDetailPrepTime);
        TextView tvIngredientsList = findViewById(R.id.tvDetailIngredientsList);
        TextView tvInstructions = findViewById(R.id.tvDetailInstructions);

        Recipe recipe = (Recipe) getIntent().getSerializableExtra("EXTRA_RECIPE");

        if (recipe != null) {
            tvTitle.setText(recipe.getName());
            tvPrepTime.setText(String.format("Preparation Time: %s", recipe.getPreparationTime()));

            StringBuilder sb = new StringBuilder();
            for (RecipeIngredient ing : recipe.getIngredients()) {
                sb.append("• ")
                        .append(ing.getIngredientName())
                        .append(" - ")
                        .append(ing.getRequiredQuantity())
                        .append(" ")
                        .append(ing.getUnit())
                        .append("  [In Pantry: OK]\n");
            }
            tvIngredientsList.setText(sb.toString().trim());
            tvInstructions.setText(recipe.getInstructions());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}