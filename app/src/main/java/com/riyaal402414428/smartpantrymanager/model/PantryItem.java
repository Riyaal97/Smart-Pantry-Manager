package com.riyaal402414428.smartpantrymanager.model;

import com.google.firebase.firestore.Exclude;

/**
 * Represents one ingredient the user has at home.
 * Maps directly to a document in the "pantryItems" Firestore collection.
 *
 * normalizedName exists so the strict-matching logic (Section 2.3 of the brief)
 * can compare ingredients reliably even when the user types "Tomato" in the
 * pantry but a recipe calls for "tomatoes". See IngredientNormalizer for how
 * this field gets generated.
 */
public class PantryItem {

    private String id;          // Firestore document ID (not stored as a field, set after fetch)
    private String name;        // Display name as the user typed it, e.g. "Tomatoes"
    private String normalizedName; // Lowercase, singularized, trimmed, e.g. "tomato"
    private double quantity;    // e.g. 3.0
    private String unit;        // e.g. "pieces", "g", "ml", "cups"
    private Long expiryDate;    // Nullable — epoch millis, or null if not set

    // Required no-arg constructor for Firestore's automatic deserialization
    public PantryItem() {
    }

    public PantryItem(String name, String normalizedName, double quantity, String unit, Long expiryDate) {
        this.name = name;
        this.normalizedName = normalizedName;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNormalizedName() {
        return normalizedName;
    }

    public void setNormalizedName(String normalizedName) {
        this.normalizedName = normalizedName;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }
}
