package com.unifebe.edu.projetoreceitas;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_TITLE = "extra_recipe_title";
    public static final String EXTRA_RECIPE_INGREDIENTS = "extra_recipe_ingredients";
    public static final String EXTRA_RECIPE_INSTRUCTIONS = "extra_recipe_instructions";

    private TextView titleTextView, ingredientsTextView, instructionsTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        titleTextView = findViewById(R.id.titleTextView);
        ingredientsTextView = findViewById(R.id.ingredientsTextView);
        instructionsTextView = findViewById(R.id.instructionsTextView);

        String title = getIntent().getStringExtra(EXTRA_RECIPE_TITLE);
        String ingredients = getIntent().getStringExtra(EXTRA_RECIPE_INGREDIENTS);
        String instructions = getIntent().getStringExtra(EXTRA_RECIPE_INSTRUCTIONS);

        titleTextView.setText(title);
        ingredientsTextView.setText(ingredients);
        instructionsTextView.setText(instructions);
    }
}
