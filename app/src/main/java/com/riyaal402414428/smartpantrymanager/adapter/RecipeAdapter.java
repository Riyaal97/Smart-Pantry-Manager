package com.riyaal402414428.smartpantrymanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.riyaal402414428.smartpantrymanager.R;
import com.riyaal402414428.smartpantrymanager.model.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays a list of recipes with a customizable subtitle (e.g. blank for
 * the strict Suggested list, or "Missing: X" for the Almost There list).
 * Reused for both screens rather than writing two near-identical adapters.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public interface SubtitleProvider {
        String getSubtitle(Recipe recipe);
    }

    public interface OnRecipeClickListener {
        void onRecipeClicked(Recipe recipe);
    }

    private final List<Recipe> recipes = new ArrayList<>();
    private final SubtitleProvider subtitleProvider;
    private final OnRecipeClickListener listener;

    public RecipeAdapter(SubtitleProvider subtitleProvider, OnRecipeClickListener listener) {
        this.subtitleProvider = subtitleProvider;
        this.listener = listener;
    }

    public void setRecipes(List<Recipe> newRecipes) {
        recipes.clear();
        recipes.addAll(newRecipes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.textName.setText(recipe.getName());

        String subtitle = subtitleProvider.getSubtitle(recipe);
        if (subtitle == null || subtitle.isEmpty()) {
            holder.textSubtitle.setVisibility(View.GONE);
        } else {
            holder.textSubtitle.setVisibility(View.VISIBLE);
            holder.textSubtitle.setText(subtitle);
        }

        holder.itemView.setOnClickListener(v -> listener.onRecipeClicked(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        final TextView textName;
        final TextView textSubtitle;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textRecipeName);
            textSubtitle = itemView.findViewById(R.id.textRecipeSubtitle);
        }
    }
}
