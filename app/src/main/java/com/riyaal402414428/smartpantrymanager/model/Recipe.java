package com.riyaal402414428.smartpantrymanager.model;

import com.google.firebase.firestore.Exclude;

import java.util.List;

/**
 * Represents one recipe. Maps to a document in the "recipes" Firestore
 * collection. Recipes are seeded once on first run (Step 3) and are not
 * normally edited by the user.
 */
public class Recipe {

    private String id; // Firestore document ID (not stored as a field)
    private String name;
    private List<RecipeIngredient> ingredients;
    private List<String> steps;

    // Required no-arg constructor for Firestore's automatic deserialization
    public Recipe() {
    }

    public Recipe(String name, List<RecipeIngredient> ingredients, List<String> steps) {
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
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

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }
}
