package com.unifebe.edu.projetoreceitas.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unifebe.edu.projetoreceitas.R;
import com.unifebe.edu.projetoreceitas.model.Recipe;
import com.unifebe.edu.projetoreceitas.DAO.RecipeDAO;

/**
 * Activity responsável pela interface de cadastro de receitas.
 * Permite inserir uma receita localmente no banco SQLite e sincronizar com Firebase quando conectado.
 */
public class AddRecipeActivity extends AppCompatActivity {

    private EditText titleEditText, ingredientsEditText, instructionsEditText;
    private Button saveButton;

    private RecipeDAO recipeDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        // Inicializa componentes da interface
        titleEditText = findViewById(R.id.titleEditText);
        ingredientsEditText = findViewById(R.id.ingredientsEditText);
        instructionsEditText = findViewById(R.id.instructionsEditText);
        saveButton = findViewById(R.id.saveButton);

        // Inicializa DAO para acesso SQLite
        recipeDAO = new RecipeDAO(this);

        // Configura ação do botão salvar
        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String ingredients = ingredientsEditText.getText().toString().trim();
            String instructions = instructionsEditText.getText().toString().trim();

            // Validação básica dos campos
            if (title.isEmpty() || ingredients.isEmpty() || instructions.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cria um objeto Recipe com id 0 (novo), chave firebase null, e não sincronizado
            Recipe recipe = new Recipe(0, title, ingredients, instructions, null, false);

            // Insere a receita no banco local SQLite
            long insertedId = recipeDAO.insertRecipe(recipe);
            recipe.id = (int) insertedId;

            // Verifica conexão com internet para sincronizar no Firebase
            if (isConnected()) {
                DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference("recipes");
                String key = firebaseRef.push().getKey();

                if (key != null) {
                    // Prepara objeto para Firebase (id local zero, synced true)
                    Recipe firebaseRecipe = new Recipe(0, recipe.title, recipe.ingredients, recipe.instructions, null, true);

                    // Insere no Firebase e escuta sucesso ou falha
                    firebaseRef.child(key).setValue(firebaseRecipe)
                            .addOnSuccessListener(unused -> {
                                // Atualiza localmente com chave Firebase e flag sincronizada
                                recipeDAO.updateFirebaseKeyAndSync(recipe.id, key);
                                runOnUiThread(() -> Toast.makeText(this, "Receita sincronizada no Firebase!", Toast.LENGTH_SHORT).show());
                                setResult(RESULT_OK);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                runOnUiThread(() -> Toast.makeText(this, "Erro ao sincronizar no Firebase", Toast.LENGTH_SHORT).show());
                                setResult(RESULT_OK);
                                finish();
                            });
                }
            } else {
                // Sem conexão, salva local e avisa o usuário que sincronizará depois
                Toast.makeText(this, "Receita salva localmente. Será sincronizada quando online.", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    /**
     * Verifica se o dispositivo está conectado à internet.
     * @return true se conectado, false caso contrário.
     */
    private boolean isConnected() {
        android.net.ConnectivityManager cm = (android.net.ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm != null) {
            android.net.NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }
}
