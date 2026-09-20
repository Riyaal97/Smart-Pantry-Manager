package com.riyaal402414428.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.riyaal402414428.smartpantrymanager.adapter.PantryAdapter;
import com.riyaal402414428.smartpantrymanager.model.PantryItem;
import com.riyaal402414428.smartpantrymanager.util.RecipeSeeder;

import java.util.ArrayList;
import java.util.List;

/**
 * Hosts the Pantry List screen (Section 2.2 of the brief). Shows all pantry
 * items live from Firestore in a RecyclerView, lets the user delete items
 * directly, and provides the app's main navigation element (Section 3.1)
 * via a BottomNavigationView linking to Pantry, Recipes, and Settings.
 */
public class MainActivity extends AppCompatActivity implements PantryAdapter.OnPantryActionListener {

    private FirebaseFirestore db;
    private PantryAdapter adapter;
    private TextView textEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecipeSeeder.seedIfEmpty();

        db = FirebaseFirestore.getInstance();
        textEmptyState = findViewById(R.id.textEmptyState);

        RecyclerView recyclerView = findViewById(R.id.recyclerPantry);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PantryAdapter(this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fabAddItem = findViewById(R.id.fabAddItem);
        fabAddItem.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditIngredientActivity.class)));

        setupBottomNavigation();
        listenToPantryItems();
    }

    /**
     * Wires the app's main navigation element (Section 3.1 requirement).
     * "Pantry" is this screen itself, so it does nothing when tapped.
     * "Recipes" and "Settings" launch their existing screens via Intent,
     * then the nav bar's selection is reset back to Pantry immediately -
     * since those are separate Activities, not fragments swapped in place,
     * this avoids the nav bar looking "stuck" on the wrong tab if the user
     * presses Back to return here.
     */
    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_pantry);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_pantry) {
                return true;
            } else if (id == R.id.nav_recipes) {
                startActivity(new Intent(this, SuggestedRecipesActivity.class));
                bottomNav.post(() -> bottomNav.setSelectedItemId(R.id.nav_pantry));
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                bottomNav.post(() -> bottomNav.setSelectedItemId(R.id.nav_pantry));
                return true;
            }
            return false;
        });
    }

    /**
     * Attaches a live Firestore snapshot listener so the pantry list updates
     * automatically the moment data changes - no manual refresh needed.
     */
    private void listenToPantryItems() {
        db.collection("pantryItems")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) {
                        Toast.makeText(this, "Failed to load pantry items", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<PantryItem> items = new ArrayList<>();
                    snapshots.forEach(doc -> {
                        PantryItem item = doc.toObject(PantryItem.class);
                        item.setId(doc.getId());
                        items.add(item);
                    });

                    adapter.setItems(items);
                    textEmptyState.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    @Override
    public void onEditClicked(PantryItem item) {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_NAME, item.getName());
        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_QUANTITY, item.getQuantity());
        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_UNIT, item.getUnit());
        if (item.getExpiryDate() != null) {
            intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_EXPIRY, item.getExpiryDate());
        }
        startActivity(intent);
    }

    @Override
    public void onDeleteClicked(PantryItem item) {
        db.collection("pantryItems").document(item.getId())
                .delete()
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, item.getName() + " removed", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to delete item", Toast.LENGTH_SHORT).show());
    }
}
