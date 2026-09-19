package com.riyaal402414428.smartpantrymanager.util;

import com.riyaal402414428.smartpantrymanager.model.PantryItem;
import com.riyaal402414428.smartpantrymanager.model.Recipe;
import com.riyaal402414428.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements the strict-matching rule from Section 2.3 of the brief:
 *
 * A recipe is "suggested" only if EVERY ingredient it needs is present in
 * the pantry in at least the required quantity. A recipe missing even one
 * ingredient (or one ingredient in insufficient quantity) must NOT appear
 * in the main suggestions list.
 *
 * Matching is done on normalizedName (see IngredientNormalizer) rather than
 * exact string comparison, so "tomato" in the pantry still matches a recipe
 * that calls for "tomatoes".
 *
 * Quantity comparison: if the pantry item and the recipe ingredient use the
 * SAME unit, the pantry quantity must be >= the required quantity. If the
 * units differ (e.g. pantry has "500 g" but a recipe wants "2 pieces"), a
 * reliable numeric comparison isn't possible without a full unit-conversion
 * system, which is outside the scope of this assignment (Section 2.3 asks
 * for "reasonably robust" matching, not full unit conversion). In that case
 * the ingredient counts as present as long as the pantry has some non-zero
 * quantity of it, since demanding an exact unit match would incorrectly
 * reject perfectly good matches over unit-labelling differences alone.
 */
public class RecipeMatcher {

    private RecipeMatcher() {
        // utility class, no instances
    }

    /** Pairs an "Almost There" recipe with the single ingredient it's missing. */
    public static class AlmostThereEntry {
        public final Recipe recipe;
        public final String missingIngredientName;

        public AlmostThereEntry(Recipe recipe, String missingIngredientName) {
            this.recipe = recipe;
            this.missingIngredientName = missingIngredientName;
        }
    }

    public static class MatchResult {
        public final List<Recipe> suggested = new ArrayList<>();
        public final List<AlmostThereEntry> almostThere = new ArrayList<>(); // missing exactly 1 ingredient
    }

    public static MatchResult match(List<PantryItem> pantryItems, List<Recipe> allRecipes) {
        Map<String, Double> pantryByName = buildPantryLookup(pantryItems);

        MatchResult result = new MatchResult();

        for (Recipe recipe : allRecipes) {
            List<RecipeIngredient> missing = findMissingIngredients(recipe, pantryByName);

            if (missing.isEmpty()) {
                result.suggested.add(recipe);
            } else if (missing.size() == 1) {
                result.almostThere.add(new AlmostThereEntry(recipe, missing.get(0).getName()));
            }
            // 2+ missing ingredients: excluded from both lists entirely
        }

        return result;
    }

    /**
     * Builds a lookup of normalizedName -> total quantity available in the
     * pantry. If the same ingredient appears in multiple pantry entries
     * (shouldn't normally happen, but is possible), their quantities are
     * summed together.
     */
    private static Map<String, Double> buildPantryLookup(List<PantryItem> pantryItems) {
        Map<String, Double> lookup = new HashMap<>();
        for (PantryItem item : pantryItems) {
            String key = item.getNormalizedName() + "|" + item.getUnit().toLowerCase().trim();
            lookup.merge(key, item.getQuantity(), Double::sum);

            // Also track total quantity per ingredient name regardless of unit,
            // for the "different unit -> presence-only" fallback below.
            lookup.merge(item.getNormalizedName(), item.getQuantity(), Double::sum);
        }
        return lookup;
    }

    private static List<RecipeIngredient> findMissingIngredients(Recipe recipe, Map<String, Double> pantryByName) {
        List<RecipeIngredient> missing = new ArrayList<>();

        for (RecipeIngredient required : recipe.getIngredients()) {
            String sameUnitKey = required.getNormalizedName() + "|" + required.getUnit().toLowerCase().trim();

            Double sameUnitQuantity = pantryByName.get(sameUnitKey);
            if (sameUnitQuantity != null) {
                // Pantry has this ingredient in the same unit - check quantity strictly.
                if (sameUnitQuantity >= required.getQuantity()) {
                    continue; // satisfied
                } else {
                    missing.add(required);
                    continue;
                }
            }

            // Units didn't match anything - fall back to presence-only check.
            Double anyUnitQuantity = pantryByName.get(required.getNormalizedName());
            if (anyUnitQuantity != null && anyUnitQuantity > 0) {
                continue; // satisfied (presence-only, unit mismatch)
            }

            missing.add(required);
        }

        return missing;
    }
}
