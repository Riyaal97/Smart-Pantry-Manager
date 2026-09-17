package com.riyaal402414428.smartpantrymanager.model;

/**
 * One required ingredient inside a Recipe. This is NOT its own Firestore
 * collection — it lives as an item inside a Recipe's "ingredients" list field.
 */
public class RecipeIngredient {

    private String name;           // e.g. "Tomatoes"
    private String normalizedName; // e.g. "tomato" — used to match against pantry items
    private double quantity;       // e.g. 2.0
    private String unit;           // e.g. "pieces", "g", "ml"

    // Required no-arg constructor for Firestore's automatic deserialization
    public RecipeIngredient() {
    }

    public RecipeIngredient(String name, String normalizedName, double quantity, String unit) {
        this.name = name;
        this.normalizedName = normalizedName;
        this.quantity = quantity;
        this.unit = unit;
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
}
