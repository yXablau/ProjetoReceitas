package com.unifebe.edu.projetoreceitas.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.unifebe.edu.projetoreceitas.R;

/**
 * Activity que exibe os detalhes completos de uma receita selecionada.
 * Recebe os dados via Intent extras: título, ingredientes e instruções.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    // Constantes usadas como chaves para passar dados via Intent
    public static final String EXTRA_RECIPE_TITLE = "extra_recipe_title";
    public static final String EXTRA_RECIPE_INGREDIENTS = "extra_recipe_ingredients";
    public static final String EXTRA_RECIPE_INSTRUCTIONS = "extra_recipe_instructions";

    // Views do layout para exibir as informações da receita
    private TextView titleTextView, ingredientsTextView, instructionsTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Referências para as views no layout
        titleTextView = findViewById(R.id.titleTextView);
        ingredientsTextView = findViewById(R.id.ingredientsTextView);
        instructionsTextView = findViewById(R.id.instructionsTextView);

        // Recupera os dados passados pela intent da activity anterior
        String title = getIntent().getStringExtra(EXTRA_RECIPE_TITLE);
        String ingredients = getIntent().getStringExtra(EXTRA_RECIPE_INGREDIENTS);
        String instructions = getIntent().getStringExtra(EXTRA_RECIPE_INSTRUCTIONS);

        // Atualiza as views com os dados da receita
        titleTextView.setText(title);
        ingredientsTextView.setText(ingredients);
        instructionsTextView.setText(instructions);
    }
}
