package com.unifebe.edu.projetoreceitas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;  // Usar Button se no layout for Button
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    public interface OnRecipeListener {
        void onDeleteClick(Recipe recipe);
        void onViewClick(Recipe recipe);
    }

    private List<Recipe> recipes;
    private OnRecipeListener listener;

    public RecipeAdapter(List<Recipe> recipes, OnRecipeListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.titleTextView.setText(recipe.title);

        holder.viewButton.setOnClickListener(v -> listener.onViewClick(recipe));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        Button viewButton, deleteButton;  // Corrigido para Button, pois o erro indica que o layout usa Button

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            viewButton = itemView.findViewById(R.id.viewButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
