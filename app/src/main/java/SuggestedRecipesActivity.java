package com.riyaal402414428.smartpantrymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.riyaal402414428.smartpantrymanager.adapter.RecipeAdapter;
import com.riyaal402414428.smartpantrymanager.model.PantryItem;
import com.riyaal402414428.smartpantrymanager.model.Recipe;
import com.riyaal402414428.smartpantrymanager.util.RecipeMatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Runs the strict-matching rule (Section 2.3) against the user's current
 * pantry and shows two lists: recipes that are fully makeable right now
 * ("Suggested"), and recipes missing exactly one ingredient ("Almost There").
 *
 * Recipes are fetched once (they don't change at runtime). Pantry items are
 * watched live, so the moment an ingredient is added/removed on the Pantry
 * screen, coming back here re-runs the match against the fresh data.
 */
public class SuggestedRecipesActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecipeAdapter suggestedAdapter;
    private RecipeAdapter almostThereAdapter;
    private TextView textSuggestedEmpty;
    private TextView textAlmostThereEmpty;

    private List<Recipe> allRecipes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        db = FirebaseFirestore.getInstance();
        textSuggestedEmpty = findViewById(R.id.textSuggestedEmpty);
        textAlmostThereEmpty = findViewById(R.id.textAlmostThereEmpty);

        findViewById(R.id.buttonBack).setOnClickListener(v -> finish());

        RecyclerView recyclerSuggested = findViewById(R.id.recyclerSuggested);
        recyclerSuggested.setLayoutManager(new LinearLayoutManager(this));
        suggestedAdapter = new RecipeAdapter(
                recipe -> "", // no subtitle needed - these are fully makeable
                this::openRecipeDetail);
        recyclerSuggested.setAdapter(suggestedAdapter);

        RecyclerView recyclerAlmostThere = findViewById(R.id.recyclerAlmostThere);
        recyclerAlmostThere.setLayoutManager(new LinearLayoutManager(this));
        // Subtitle looked up from the entry map built each time we re-match.
        almostThereAdapter = new RecipeAdapter(this::lookupMissingIngredientSubtitle, this::openRecipeDetail);
        recyclerAlmostThere.setAdapter(almostThereAdapter);

        loadRecipesThenListenToPantry();
    }

    private final Map<String, String> missingIngredientByRecipeId = new HashMap<>();

    private String lookupMissingIngredientSubtitle(Recipe recipe) {
        String missing = missingIngredientByRecipeId.get(recipe.getId());
        return missing != null ? "Missing: " + missing : "";
    }

    private void loadRecipesThenListenToPantry() {
        db.collection("recipes").get().addOnSuccessListener(snapshot -> {
            allRecipes.clear();
            snapshot.forEach(doc -> {
                Recipe recipe = doc.toObject(Recipe.class);
                recipe.setId(doc.getId());
                allRecipes.add(recipe);
            });
            listenToPantryAndMatch();
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to load recipes", Toast.LENGTH_SHORT).show());
    }

    private void listenToPantryAndMatch() {
        db.collection("pantryItems").addSnapshotListener((snapshots, error) -> {
            if (error != null || snapshots == null) {
                Toast.makeText(this, "Failed to load pantry items", Toast.LENGTH_SHORT).show();
                return;
            }

            List<PantryItem> pantryItems = new ArrayList<>();
            snapshots.forEach(doc -> {
                PantryItem item = doc.toObject(PantryItem.class);
                item.setId(doc.getId());
                pantryItems.add(item);
            });

            RecipeMatcher.MatchResult result = RecipeMatcher.match(pantryItems, allRecipes);

            missingIngredientByRecipeId.clear();
            List<Recipe> almostThereRecipes = new ArrayList<>();
            for (RecipeMatcher.AlmostThereEntry entry : result.almostThere) {
                missingIngredientByRecipeId.put(entry.recipe.getId(), entry.missingIngredientName);
                almostThereRecipes.add(entry.recipe);
            }

            suggestedAdapter.setRecipes(result.suggested);
            textSuggestedEmpty.setVisibility(result.suggested.isEmpty() ? View.VISIBLE : View.GONE);

            almostThereAdapter.setRecipes(almostThereRecipes);
            textAlmostThereEmpty.setVisibility(almostThereRecipes.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private void openRecipeDetail(Recipe recipe) {
        android.content.Intent intent = new android.content.Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}
