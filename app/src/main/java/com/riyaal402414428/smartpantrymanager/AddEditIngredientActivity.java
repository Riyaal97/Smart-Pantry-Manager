package com.riyaal402414428.smartpantrymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.riyaal402414428.smartpantrymanager.model.PantryItem;
import com.riyaal402414428.smartpantrymanager.util.IngredientNormalizer;

import java.util.Calendar;
import java.util.Locale;

/**
 * Handles BOTH adding a new pantry item and editing an existing one.
 *
 * Launch with no extras -> Add mode (creates a new Firestore document).
 * Launch with EXTRA_ITEM_ID set -> Edit mode (updates that existing document).
 *
 * Satisfies the "input validation on any data entry form" requirement in
 * Section 3.1: name must not be empty, quantity must be a number > 0.
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "extra_item_id";
    public static final String EXTRA_ITEM_NAME = "extra_item_name";
    public static final String EXTRA_ITEM_QUANTITY = "extra_item_quantity";
    public static final String EXTRA_ITEM_UNIT = "extra_item_unit";
    public static final String EXTRA_ITEM_EXPIRY = "extra_item_expiry";

    private static final String[] UNITS = {
            "pieces", "g", "kg", "ml", "l", "tbsp", "tsp", "slices", "cups"
    };

    private EditText editName;
    private EditText editQuantity;
    private Spinner spinnerUnit;
    private EditText editExpiry;
    private TextView errorName;
    private TextView errorQuantity;

    private FirebaseFirestore db;
    private String editingItemId; // null when adding a new item
    private Long expiryMillis;    // null if no expiry date chosen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        db = FirebaseFirestore.getInstance();


        editName = findViewById(R.id.editName);
        editQuantity = findViewById(R.id.editQuantity);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        editExpiry = findViewById(R.id.editExpiry);
        errorName = findViewById(R.id.errorName);
        errorQuantity = findViewById(R.id.errorQuantity);
        Button buttonSave = findViewById(R.id.buttonSave);
        TextView title = findViewById(R.id.textScreenTitle);
        findViewById(R.id.buttonBack).setOnClickListener(v -> finish());

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, UNITS);
        spinnerUnit.setAdapter(unitAdapter);

        editExpiry.setOnClickListener(v -> showDatePicker());

        editingItemId = getIntent().getStringExtra(EXTRA_ITEM_ID);
        if (editingItemId != null) {
            title.setText("Edit Ingredient");
            prefillFromIntent();
        }

        buttonSave.setOnClickListener(v -> attemptSave());
    }

    private void prefillFromIntent() {
        editName.setText(getIntent().getStringExtra(EXTRA_ITEM_NAME));
        double quantity = getIntent().getDoubleExtra(EXTRA_ITEM_QUANTITY, 0);
        editQuantity.setText(formatQuantity(quantity));

        String unit = getIntent().getStringExtra(EXTRA_ITEM_UNIT);
        for (int i = 0; i < UNITS.length; i++) {
            if (UNITS[i].equals(unit)) {
                spinnerUnit.setSelection(i);
                break;
            }
        }

        if (getIntent().hasExtra(EXTRA_ITEM_EXPIRY)) {
            expiryMillis = getIntent().getLongExtra(EXTRA_ITEM_EXPIRY, -1);
            if (expiryMillis == -1) {
                expiryMillis = null;
            } else {
                editExpiry.setText(formatDate(expiryMillis));
            }
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        if (expiryMillis != null) {
            calendar.setTimeInMillis(expiryMillis);
        }

        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar picked = Calendar.getInstance();
            picked.set(year, month, dayOfMonth, 0, 0, 0);
            expiryMillis = picked.getTimeInMillis();
            editExpiry.setText(formatDate(expiryMillis));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private String formatDate(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        return String.format(Locale.getDefault(), "%02d/%02d/%04d",
                c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR));
    }

    private String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.valueOf(quantity);
    }

    /**
     * Validates the form, showing inline error messages if anything is
     * wrong, and only proceeds to save if everything passes.
     */
    private void attemptSave() {
        boolean isValid = true;

        String name = editName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            errorName.setVisibility(android.view.View.VISIBLE);
            isValid = false;
        } else {
            errorName.setVisibility(android.view.View.GONE);
        }

        double quantity = 0;
        String quantityText = editQuantity.getText().toString().trim();
        try {
            quantity = Double.parseDouble(quantityText);
            if (quantity <= 0) {
                errorQuantity.setVisibility(android.view.View.VISIBLE);
                isValid = false;
            } else {
                errorQuantity.setVisibility(android.view.View.GONE);
            }
        } catch (NumberFormatException e) {
            errorQuantity.setVisibility(android.view.View.VISIBLE);
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        String unit = (String) spinnerUnit.getSelectedItem();
        String normalizedName = IngredientNormalizer.normalize(name);

        PantryItem item = new PantryItem(name, normalizedName, quantity, unit, expiryMillis);

        if (editingItemId == null) {
            db.collection("pantryItems").add(item)
                    .addOnSuccessListener(ref -> {
                        Toast.makeText(this, name + " added", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show());
        } else {
            db.collection("pantryItems").document(editingItemId).set(item)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, name + " updated", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to update item", Toast.LENGTH_SHORT).show());
        }
    }
}
