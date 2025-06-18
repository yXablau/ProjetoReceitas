package com.unifebe.edu.projetoreceitas;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddRecipeActivity extends AppCompatActivity {

    private EditText titleEditText, ingredientsEditText, instructionsEditText;
    private Button saveButton;

    private RecipeDAO recipeDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        titleEditText = findViewById(R.id.titleEditText);
        ingredientsEditText = findViewById(R.id.ingredientsEditText);
        instructionsEditText = findViewById(R.id.instructionsEditText);
        saveButton = findViewById(R.id.saveButton);

        recipeDAO = new RecipeDAO(this);

        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String ingredients = ingredientsEditText.getText().toString().trim();
            String instructions = instructionsEditText.getText().toString().trim();

            if (title.isEmpty() || ingredients.isEmpty() || instructions.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cria receita com o construtor correto (6 parâmetros)
            Recipe recipe = new Recipe(0, title, ingredients, instructions, null, false);

            // Insere no SQLite (offline)
            long insertedId = recipeDAO.insertRecipe(recipe);
            recipe.id = (int) insertedId;

            // Sincroniza no Firebase (se conectado)
            if (isConnected()) {
                DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference("recipes");
                String key = firebaseRef.push().getKey();
                if (key != null) {
                    // Cria objeto para Firebase (sem id local)
                    Recipe firebaseRecipe = new Recipe(0, recipe.title, recipe.ingredients, recipe.instructions, null, true);
                    firebaseRef.child(key).setValue(firebaseRecipe).addOnSuccessListener(unused -> {
                        // Atualiza a receita local com a chave do Firebase e marca como sincronizada
                        recipeDAO.updateFirebaseKeyAndSync(recipe.id, key);
                        runOnUiThread(() -> Toast.makeText(this, "Receita sincronizada no Firebase!", Toast.LENGTH_SHORT).show());
                        setResult(RESULT_OK);
                        finish();
                    }).addOnFailureListener(e -> {
                        runOnUiThread(() -> Toast.makeText(this, "Erro ao sincronizar no Firebase", Toast.LENGTH_SHORT).show());
                        setResult(RESULT_OK);
                        finish();
                    });
                }
            } else {
                Toast.makeText(this, "Receita salva localmente. Será sincronizada quando online.", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private boolean isConnected() {
        android.net.ConnectivityManager cm = (android.net.ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm != null) {
            android.net.NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }
}
