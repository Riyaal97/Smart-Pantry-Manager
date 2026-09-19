package com.riyaal402414428.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
 * directly, routes to AddEditIngredientActivity for adding/editing, and to
 * SuggestedRecipesActivity to see what can be cooked right now.
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

        Button buttonViewSuggested = findViewById(R.id.buttonViewSuggested);
        buttonViewSuggested.setOnClickListener(v ->
                startActivity(new Intent(this, SuggestedRecipesActivity.class)));

        listenToPantryItems();
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
