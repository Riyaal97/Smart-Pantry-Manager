package com.riyaal402414428.smartpantrymanager.util;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.riyaal402414428.smartpantrymanager.model.Recipe;
import com.riyaal402414428.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Seeds the "recipes" Firestore collection with a fixed list of recipes the
 * first time the app runs. Section 2.2 of the brief requires 15-20 pre-loaded
 * recipes; this class provides 20, mixing South African classics with
 * American fried-food dishes.
 *
 * Uses a single WriteBatch so all 20 recipes are written atomically - either
 * all of them succeed or none do, avoiding a partially-seeded collection if
 * the app closes mid-write.
 *
 * Call RecipeSeeder.seedIfEmpty() once, e.g. from MainActivity's onCreate,
 * after Firebase is initialized.
 */
public class RecipeSeeder {

    private static final String TAG = "RecipeSeeder";

    private RecipeSeeder() {
        // utility class, no instances
    }

    /**
     * Checks if the "recipes" collection already has documents. If it's
     * empty (first run), uploads the full seed list in one batch. If it
     * already has data, does nothing, so this is safe to call every time
     * the app opens.
     */
    public static void seedIfEmpty() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("recipes").limit(1).get().addOnSuccessListener(snapshot -> {
            if (snapshot.isEmpty()) {
                WriteBatch batch = db.batch();

                for (Recipe recipe : buildSeedRecipes()) {
                    DocumentReference ref = db.collection("recipes").document();
                    batch.set(ref, recipe);
                }

                batch.commit()
                        .addOnSuccessListener(unused -> Log.d(TAG, "Seeded all 20 recipes successfully"))
                        .addOnFailureListener(e -> Log.e(TAG, "Recipe batch seed failed", e));
            } else {
                Log.d(TAG, "Recipes already seeded, skipping");
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Failed to check if recipes collection is empty", e));
    }

    // Small helper so every ingredient automatically gets its normalizedName
    // filled in via IngredientNormalizer, instead of typing it out 100+ times.
    private static RecipeIngredient ing(String name, double quantity, String unit) {
        return new RecipeIngredient(name, IngredientNormalizer.normalize(name), quantity, unit);
    }

    private static List<Recipe> buildSeedRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        // ---------- SOUTH AFRICAN ----------

        recipes.add(new Recipe(
                "Bobotie",
                Arrays.asList(
                        ing("Mince", 500, "g"),
                        ing("Onion", 1, "pieces"),
                        ing("Curry powder", 2, "tbsp"),
                        ing("Bread", 2, "slices"),
                        ing("Milk", 250, "ml"),
                        ing("Eggs", 2, "pieces"),
                        ing("Raisins", 50, "g")
                ),
                Arrays.asList(
                        "Soak the bread slices in milk, then mash into the mince.",
                        "Fry the onion until soft, add mince, curry powder and raisins, cook through.",
                        "Spoon into a baking dish and smooth the top.",
                        "Whisk eggs with the remaining milk and pour over the top.",
                        "Bake at 180°C for 35 minutes until the topping is set and golden."
                )
        ));

        recipes.add(new Recipe(
                "Chakalaka",
                Arrays.asList(
                        ing("Onion", 1, "pieces"),
                        ing("Carrots", 2, "pieces"),
                        ing("Bell pepper", 1, "pieces"),
                        ing("Baked beans", 400, "g"),
                        ing("Curry powder", 1, "tbsp"),
                        ing("Tomato", 2, "pieces")
                ),
                Arrays.asList(
                        "Fry the onion and bell pepper until soft.",
                        "Add grated carrots and curry powder, cook for 3 minutes.",
                        "Stir in chopped tomato and baked beans.",
                        "Simmer for 10 minutes until thickened."
                )
        ));

        recipes.add(new Recipe(
                "Pap and Wors",
                Arrays.asList(
                        ing("Maize meal", 500, "g"),
                        ing("Water", 1, "l"),
                        ing("Boerewors", 400, "g"),
                        ing("Salt", 1, "tsp")
                ),
                Arrays.asList(
                        "Bring salted water to the boil.",
                        "Stir in maize meal, reduce heat and simmer, stirring often, for 25 minutes.",
                        "Meanwhile, grill or pan-fry the boerewors until cooked through.",
                        "Serve the pap alongside sliced boerewors."
                )
        ));

        recipes.add(new Recipe(
                "Bunny Chow",
                Arrays.asList(
                        ing("Bread", 1, "pieces"),
                        ing("Chicken", 500, "g"),
                        ing("Curry powder", 2, "tbsp"),
                        ing("Onion", 1, "pieces"),
                        ing("Potato", 2, "pieces"),
                        ing("Tomato", 2, "pieces")
                ),
                Arrays.asList(
                        "Fry the onion until soft, add curry powder and toast briefly.",
                        "Add chicken and browned tomato, cook through.",
                        "Add cubed potato and enough water to simmer until tender, about 25 minutes.",
                        "Hollow out a quarter loaf of bread and spoon the curry inside.",
                        "Serve with the bread lid on the side."
                )
        ));

        recipes.add(new Recipe(
                "Vetkoek",
                Arrays.asList(
                        ing("Flour", 500, "g"),
                        ing("Yeast", 10, "g"),
                        ing("Sugar", 1, "tbsp"),
                        ing("Salt", 1, "tsp"),
                        ing("Water", 300, "ml"),
                        ing("Oil", 1, "l")
                ),
                Arrays.asList(
                        "Mix flour, yeast, sugar and salt, then work in warm water to a soft dough.",
                        "Cover and let rise for 45 minutes.",
                        "Shape into balls and deep fry in hot oil until golden on both sides.",
                        "Drain and serve warm."
                )
        ));

        recipes.add(new Recipe(
                "Sosaties (Skewers)",
                Arrays.asList(
                        ing("Lamb", 500, "g"),
                        ing("Onion", 1, "pieces"),
                        ing("Apricot jam", 2, "tbsp"),
                        ing("Curry powder", 1, "tbsp"),
                        ing("Vinegar", 2, "tbsp")
                ),
                Arrays.asList(
                        "Mix apricot jam, curry powder and vinegar into a marinade.",
                        "Cube the lamb and marinate for at least an hour.",
                        "Thread lamb and onion pieces onto skewers.",
                        "Grill over hot coals or a griddle pan, turning until cooked through."
                )
        ));

        recipes.add(new Recipe(
                "Potjiekos",
                Arrays.asList(
                        ing("Beef", 600, "g"),
                        ing("Potato", 4, "pieces"),
                        ing("Carrots", 3, "pieces"),
                        ing("Onion", 1, "pieces"),
                        ing("Beef stock", 500, "ml")
                ),
                Arrays.asList(
                        "Brown the beef and onion in a pot.",
                        "Layer potato and carrots on top without stirring.",
                        "Pour over the stock, cover and simmer gently for 2 hours.",
                        "Serve straight from the pot."
                )
        ));

        recipes.add(new Recipe(
                "Boerewors Rolls",
                Arrays.asList(
                        ing("Boerewors", 400, "g"),
                        ing("Bread rolls", 4, "pieces"),
                        ing("Onion", 1, "pieces"),
                        ing("Tomato sauce", 4, "tbsp")
                ),
                Arrays.asList(
                        "Grill the boerewors until browned and cooked through.",
                        "Fry sliced onion until caramelized.",
                        "Slice bread rolls and fill with boerewors and onion.",
                        "Top with tomato sauce and serve."
                )
        ));

        recipes.add(new Recipe(
                "Mielie Pap with Tomato Relish",
                Arrays.asList(
                        ing("Maize meal", 400, "g"),
                        ing("Water", 800, "ml"),
                        ing("Tomato", 4, "pieces"),
                        ing("Onion", 1, "pieces"),
                        ing("Sugar", 1, "tsp")
                ),
                Arrays.asList(
                        "Cook maize meal in boiling water, stirring, for 25 minutes to make pap.",
                        "Fry onion until soft, add chopped tomato and sugar.",
                        "Simmer the relish for 15 minutes until saucy.",
                        "Serve the pap topped with tomato relish."
                )
        ));

        recipes.add(new Recipe(
                "Milk Tart (Melktert)",
                Arrays.asList(
                        ing("Milk", 500, "ml"),
                        ing("Flour", 60, "g"),
                        ing("Sugar", 100, "g"),
                        ing("Eggs", 2, "pieces"),
                        ing("Cinnamon", 1, "tsp")
                ),
                Arrays.asList(
                        "Heat the milk until just below boiling.",
                        "Whisk flour, sugar and eggs together, then slowly add the hot milk.",
                        "Return to the pot and stir over low heat until thickened.",
                        "Pour into a baked pastry crust and dust with cinnamon.",
                        "Chill for at least 2 hours before serving."
                )
        ));

        // ---------- USA FRIED FOOD ----------

        recipes.add(new Recipe(
                "Southern Fried Chicken",
                Arrays.asList(
                        ing("Chicken", 800, "g"),
                        ing("Flour", 250, "g"),
                        ing("Eggs", 2, "pieces"),
                        ing("Buttermilk", 250, "ml"),
                        ing("Paprika", 1, "tbsp"),
                        ing("Oil", 1, "l")
                ),
                Arrays.asList(
                        "Marinate chicken pieces in buttermilk for at least an hour.",
                        "Mix flour with paprika, salt and pepper in a bowl.",
                        "Dip chicken in beaten egg, then coat thoroughly in the flour mix.",
                        "Deep fry in hot oil until golden and cooked through, about 12-15 minutes.",
                        "Drain on paper towel before serving."
                )
        ));

        recipes.add(new Recipe(
                "Chicken and Waffles",
                Arrays.asList(
                        ing("Chicken", 500, "g"),
                        ing("Flour", 200, "g"),
                        ing("Eggs", 2, "pieces"),
                        ing("Milk", 300, "ml"),
                        ing("Baking powder", 1, "tbsp"),
                        ing("Syrup", 4, "tbsp")
                ),
                Arrays.asList(
                        "Coat chicken pieces in seasoned flour and fry until golden and cooked through.",
                        "Mix flour, baking powder, eggs and milk into a waffle batter.",
                        "Cook the batter in a waffle iron until golden.",
                        "Serve the fried chicken on top of the waffles, drizzled with syrup."
                )
        ));

        recipes.add(new Recipe(
                "Corn Dogs",
                Arrays.asList(
                        ing("Sausages", 6, "pieces"),
                        ing("Maize meal", 150, "g"),
                        ing("Flour", 100, "g"),
                        ing("Eggs", 1, "pieces"),
                        ing("Milk", 200, "ml"),
                        ing("Oil", 1, "l")
                ),
                Arrays.asList(
                        "Mix maize meal, flour, egg and milk into a thick batter.",
                        "Skewer each sausage on a stick and dip into the batter, coating fully.",
                        "Deep fry until golden brown, about 4-5 minutes.",
                        "Drain and serve hot."
                )
        ));

        recipes.add(new Recipe(
                "Fried Okra",
                Arrays.asList(
                        ing("Okra", 300, "g"),
                        ing("Maize meal", 100, "g"),
                        ing("Buttermilk", 150, "ml"),
                        ing("Oil", 500, "ml")
                ),
                Arrays.asList(
                        "Slice okra into rounds and soak in buttermilk for 10 minutes.",
                        "Coat the okra in maize meal.",
                        "Deep fry in hot oil until crisp and golden.",
                        "Drain on paper towel and season with salt."
                )
        ));

        recipes.add(new Recipe(
                "Mac and Cheese",
                Arrays.asList(
                        ing("Pasta", 300, "g"),
                        ing("Cheese", 200, "g"),
                        ing("Milk", 400, "ml"),
                        ing("Flour", 30, "g"),
                        ing("Butter", 30, "g")
                ),
                Arrays.asList(
                        "Boil the pasta until just tender, then drain.",
                        "Melt butter, whisk in flour, then gradually add milk to make a smooth sauce.",
                        "Stir in grated cheese until melted.",
                        "Mix the sauce through the pasta and serve."
                )
        ));

        recipes.add(new Recipe(
                "Cheeseburger",
                Arrays.asList(
                        ing("Mince", 400, "g"),
                        ing("Bread rolls", 4, "pieces"),
                        ing("Cheese", 4, "slices"),
                        ing("Onion", 1, "pieces"),
                        ing("Tomato", 1, "pieces"),
                        ing("Tomato sauce", 4, "tbsp")
                ),
                Arrays.asList(
                        "Shape mince into patties and season.",
                        "Fry or grill the patties to your liking, adding cheese near the end to melt.",
                        "Toast the bread rolls.",
                        "Assemble with onion, tomato and tomato sauce."
                )
        ));

        recipes.add(new Recipe(
                "Fried Catfish",
                Arrays.asList(
                        ing("Fish", 500, "g"),
                        ing("Maize meal", 200, "g"),
                        ing("Buttermilk", 200, "ml"),
                        ing("Paprika", 1, "tbsp"),
                        ing("Oil", 500, "ml")
                ),
                Arrays.asList(
                        "Soak fish fillets in buttermilk for 15 minutes.",
                        "Mix maize meal with paprika, salt and pepper.",
                        "Coat the fish in the maize meal mixture.",
                        "Deep fry until golden and cooked through, about 6-8 minutes."
                )
        ));

        recipes.add(new Recipe(
                "Biscuits and Gravy",
                Arrays.asList(
                        ing("Flour", 300, "g"),
                        ing("Butter", 80, "g"),
                        ing("Milk", 250, "ml"),
                        ing("Sausages", 200, "g"),
                        ing("Baking powder", 1, "tbsp")
                ),
                Arrays.asList(
                        "Mix flour, baking powder and butter into a crumbly dough, then add milk to bring together.",
                        "Shape and bake the biscuits at 200°C for 12-15 minutes.",
                        "Fry crumbled sausage, then stir in a little flour and milk to make a gravy.",
                        "Split the biscuits and spoon gravy over the top."
                )
        ));

        recipes.add(new Recipe(
                "BBQ Pulled Pork Sandwich",
                Arrays.asList(
                        ing("Pork", 700, "g"),
                        ing("BBQ sauce", 150, "ml"),
                        ing("Bread rolls", 4, "pieces"),
                        ing("Onion", 1, "pieces")
                ),
                Arrays.asList(
                        "Slow cook the pork with onion until it shreds easily, about 3 hours.",
                        "Shred the pork and mix through the BBQ sauce.",
                        "Pile onto bread rolls and serve."
                )
        ));

        recipes.add(new Recipe(
                "Loaded Fries",
                Arrays.asList(
                        ing("Potato", 5, "pieces"),
                        ing("Cheese", 150, "g"),
                        ing("Bacon", 100, "g"),
                        ing("Oil", 500, "ml"),
                        ing("Spring onion", 2, "pieces")
                ),
                Arrays.asList(
                        "Cut potatoes into fries and deep fry until golden and crisp.",
                        "Fry bacon until crisp and chop into pieces.",
                        "Pile fries onto a plate, top with grated cheese and bacon.",
                        "Grill briefly until cheese melts, then top with sliced spring onion."
                )
        ));

        return recipes;
    }
}
