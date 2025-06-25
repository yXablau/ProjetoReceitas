package com.unifebe.edu.projetoreceitas.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unifebe.edu.projetoreceitas.R;
import com.unifebe.edu.projetoreceitas.model.Recipe;

import java.util.List;

/**
 * Adapter personalizado para exibir a lista de receitas em um RecyclerView.
 * Responsável por "ligar" os dados do modelo (Recipe) ao layout visual (item_recipe.xml).
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    /**
     * Interface para capturar eventos de clique nos botões "ver" e "excluir" dentro de cada item.
     */
    public interface OnRecipeListener {
        void onDeleteClick(Recipe recipe);  // Quando o botão "Excluir" for clicado
        void onViewClick(Recipe recipe);    // Quando o botão "Ver" for clicado
    }

    private List<Recipe> recipes;             // Lista de receitas que será exibida
    private OnRecipeListener listener;        // Listener para ações de clique

    /**
     * Construtor do Adapter.
     *
     * @param recipes  Lista de receitas a ser exibida
     * @param listener Listener de cliques para ações no item
     */
    public RecipeAdapter(List<Recipe> recipes, OnRecipeListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    /**
     * Cria uma nova ViewHolder (inflando o layout do item).
     */
    @NonNull
    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout XML do item (item_recipe.xml)
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Associa os dados da receita ao layout visual da ViewHolder.
     */
    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);                          // Obtem a receita da posição atual
        holder.titleTextView.setText(recipe.title);                    // Define o título da receita no TextView

        // Define ações dos botões para o item atual
        holder.viewButton.setOnClickListener(v -> listener.onViewClick(recipe));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(recipe));
    }

    /**
     * Retorna o número total de itens na lista.
     */
    @Override
    public int getItemCount() {
        return recipes.size();
    }

    /**
     * Classe ViewHolder representa cada item da lista no RecyclerView.
     * Contém as views que serão manipuladas (título, botão de ver e excluir).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        Button viewButton, deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Mapeia os elementos do layout XML para as variáveis Java
            titleTextView = itemView.findViewById(R.id.titleTextView);
            viewButton = itemView.findViewById(R.id.viewButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
