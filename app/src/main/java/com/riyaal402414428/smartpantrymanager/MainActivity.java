package com.riyaal402414428.smartpantrymanager;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.riyaal402414428.smartpantrymanager.util.RecipeSeeder;

/**
 * MainActivity will host the Pantry List screen.
 * Full RecyclerView + Firebase wiring is added in Step 4 (Pantry List screen).
 *
 * On every launch it calls RecipeSeeder.seedIfEmpty(), which only actually
 * writes data the very first time the recipes collection is empty.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecipeSeeder.seedIfEmpty();
    }
}
