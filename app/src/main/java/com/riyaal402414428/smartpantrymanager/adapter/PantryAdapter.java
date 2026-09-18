package com.riyaal402414428.smartpantrymanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.riyaal402414428.smartpantrymanager.R;
import com.riyaal402414428.smartpantrymanager.model.PantryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays the user's pantry items in a RecyclerView, satisfying the
 * "at least one RecyclerView with a custom Adapter" requirement (Section 3.1).
 *
 * Delete is fully wired here since it needs no other screen. Edit is
 * delegated to a listener that MainActivity implements - Step 5 wires that
 * up to the actual Add/Edit screen.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    public interface OnPantryActionListener {
        void onEditClicked(PantryItem item);
        void onDeleteClicked(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final OnPantryActionListener listener;

    public PantryAdapter(OnPantryActionListener listener) {
        this.listener = listener;
    }

    /**
     * Replaces the full list of displayed items, e.g. every time Firestore
     * pushes an updated snapshot. Simple and correct for a pantry list of
     * modest size; not optimized with DiffUtil, which is beyond this brief's
     * scope.
     */
    public void setItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);

        holder.textName.setText(item.getName());

        String quantityText = formatQuantity(item.getQuantity()) + " " + item.getUnit();
        holder.textQuantity.setText(quantityText);

        holder.buttonEdit.setOnClickListener(v -> listener.onEditClicked(item));
        holder.buttonDelete.setOnClickListener(v -> listener.onDeleteClicked(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Avoids showing "3.0 pieces" - shows "3 pieces" when there's no fraction.
    private String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.valueOf(quantity);
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        final TextView textName;
        final TextView textQuantity;
        final ImageButton buttonEdit;
        final ImageButton buttonDelete;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textQuantity = itemView.findViewById(R.id.textQuantity);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
