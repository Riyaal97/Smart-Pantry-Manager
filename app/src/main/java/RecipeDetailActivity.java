package com.riyaal402414428.smartpantrymanager;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.riyaal402414428.smartpantrymanager.model.Recipe;
import com.riyaal402414428.smartpantrymanager.model.RecipeIngredient;

/**
 * Shows the full ingredient list and preparation steps for one recipe
 * (Section 2.2 requirement: "recipe detail screen showing the full
 * ingredient list and method for a selected recipe").
 *
 * Launched with EXTRA_RECIPE_ID set to the Firestore document ID of the
 * recipe to display.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";

    private FirebaseFirestore db;
    private LinearLayout containerIngredients;
    private LinearLayout containerSteps;
    private TextView textRecipeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        db = FirebaseFirestore.getInstance();
        textRecipeName = findViewById(R.id.textRecipeName);
        containerIngredients = findViewById(R.id.containerIngredients);
        containerSteps = findViewById(R.id.containerSteps);

        findViewById(R.id.buttonBack).setOnClickListener(v -> finish());

        String recipeId = getIntent().getStringExtra(EXTRA_RECIPE_ID);
        if (recipeId == null) {
            Toast.makeText(this, "No recipe selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadRecipe(recipeId);
    }

    private void loadRecipe(String recipeId) {
        db.collection("recipes").document(recipeId).get()
                .addOnSuccessListener(doc -> {
                    Recipe recipe = doc.toObject(Recipe.class);
                    if (recipe == null) {
                        Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    displayRecipe(recipe);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load recipe", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void displayRecipe(Recipe recipe) {
        textRecipeName.setText(recipe.getName());

        containerIngredients.removeAllViews();
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            TextView row = new TextView(this);
            String quantityText = formatQuantity(ingredient.getQuantity());
            row.setText("• " + ingredient.getName() + " — " + quantityText + " " + ingredient.getUnit());
            row.setTextColor(getColor(R.color.text_primary));
            row.setTextSize(15f);
            row.setPadding(0, 4, 0, 4);
            containerIngredients.addView(row);
        }

        containerSteps.removeAllViews();
        int stepNumber = 1;
        for (String step : recipe.getSteps()) {
            TextView row = new TextView(this);
            row.setText(stepNumber + ". " + step);
            row.setTextColor(getColor(R.color.text_primary));
            row.setTextSize(15f);
            row.setPadding(0, 8, 0, 8);
            containerSteps.addView(row);
            stepNumber++;
        }
    }

    private String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.valueOf(quantity);
    }
}
