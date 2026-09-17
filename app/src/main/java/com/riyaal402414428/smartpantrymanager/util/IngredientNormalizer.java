package com.riyaal402414428.smartpantrymanager.util;

/**
 * Turns a user-typed or recipe ingredient name into a normalized form so that
 * simple real-world differences (case, whitespace, plurals) don't break the
 * strict-matching rule in Section 2.3 of the brief.
 *
 * This is NOT full NLP — it's a small set of rules that covers the common
 * cases the brief specifically calls out (e.g. "tomato" vs "tomatoes").
 */
public class IngredientNormalizer {

    private IngredientNormalizer() {
        // utility class, no instances
    }

    public static String normalize(String rawName) {
        if (rawName == null) {
            return "";
        }

        String name = rawName.trim().toLowerCase();

        // Collapse multiple internal spaces
        name = name.replaceAll("\\s+", " ");

        // Very common irregular plurals worth handling explicitly
        if (name.endsWith("tomatoes")) {
            return name.substring(0, name.length() - 2); // tomatoes -> tomato
        }
        if (name.endsWith("potatoes")) {
            return name.substring(0, name.length() - 2); // potatoes -> potato
        }
        if (name.endsWith("leaves")) {
            return name.substring(0, name.length() - 6) + "leaf"; // leaves -> leaf
        }

        // General rule: words ending in a consonant + "ies" -> "y"
        // e.g. "berries" -> "berry"
        if (name.endsWith("ies") && name.length() > 3) {
            return name.substring(0, name.length() - 3) + "y";
        }

        // General rule: words ending in "es" after s/x/z/ch/sh -> strip "es"
        // e.g. "boxes" -> "box", "dishes" -> "dish"
        if (name.endsWith("es") && name.length() > 2) {
            String stem = name.substring(0, name.length() - 2);
            if (stem.endsWith("s") || stem.endsWith("x") || stem.endsWith("z")
                    || stem.endsWith("ch") || stem.endsWith("sh")) {
                return stem;
            }
        }

        // General rule: plain trailing "s" (but not already handled above)
        // e.g. "onions" -> "onion", "eggs" -> "egg"
        if (name.endsWith("s") && !name.endsWith("ss") && name.length() > 1) {
            return name.substring(0, name.length() - 1);
        }

        return name;
    }
}
