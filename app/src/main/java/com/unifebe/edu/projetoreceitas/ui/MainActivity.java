package com.unifebe.edu.projetoreceitas.ui;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unifebe.edu.projetoreceitas.R;
import com.unifebe.edu.projetoreceitas.model.Recipe;
import com.unifebe.edu.projetoreceitas.adapter.RecipeAdapter;
import com.unifebe.edu.projetoreceitas.DAO.RecipeDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity principal do app que exibe a lista de receitas.
 * Permite adicionar receitas, buscar receitas, excluir e visualizar detalhes.
 * Sincroniza receitas localmente com Firebase quando conectado.
 */
public class MainActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeListener {

    private RecyclerView recipeRecyclerView;    // Lista de receitas
    private Button addRecipeButton;              // Botão para adicionar nova receita
    private RecipeAdapter adapter;               // Adapter para o RecyclerView
    private ArrayList<Recipe> recipeList = new ArrayList<>(); // Dados locais carregados do banco
    private RecipeDAO dao;                       // Acesso ao banco SQLite local

    private static final int ADD_RECIPE_REQUEST = 100;  // Código para startActivityForResult

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dao = new RecipeDAO(this);

        // Inicializa RecyclerView e seu adapter
        recipeRecyclerView = findViewById(R.id.recipeRecyclerView);
        addRecipeButton = findViewById(R.id.addRecipeButton);

        adapter = new RecipeAdapter(recipeList, this);
        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recipeRecyclerView.setAdapter(adapter);

        // Ao clicar para adicionar, abre a activity de adicionar receita
        addRecipeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddRecipeActivity.class);
            startActivityForResult(intent, ADD_RECIPE_REQUEST);
        });

        // Botão para abrir tela de busca de receitas externas
        Button btnBuscarReceitas = findViewById(R.id.btnBuscarReceitas);
        btnBuscarReceitas.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BuscarReceitasActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecipesFromDb(); // Atualiza lista da interface com dados locais
        if (isConnected()) {
            syncWithFirebase();  // Envia receitas locais não sincronizadas para o Firebase
            fetchFromFirebase(); // Baixa receitas do Firebase para o banco local
        }
    }

    // Recebe resultado da activity de adicionar receita para atualizar a lista
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_RECIPE_REQUEST && resultCode == RESULT_OK) {
            loadRecipesFromDb();
        }
    }

    /**
     * Carrega todas receitas do banco SQLite e atualiza adapter da RecyclerView
     */
    private void loadRecipesFromDb() {
        recipeList.clear();
        recipeList.addAll(dao.getAllRecipes());
        adapter.notifyDataSetChanged();
    }

    /**
     * Verifica se há conexão com internet
     * @return true se conectado, false caso contrário
     */
    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    /**
     * Sincroniza receitas locais que ainda não foram enviadas para o Firebase
     */
    private void syncWithFirebase() {
        List<Recipe> unsynced = dao.getUnsyncedRecipes();
        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference("recipes");

        for (Recipe recipe : unsynced) {
            String key = recipe.firebaseKey;
            if (key == null || key.isEmpty()) {
                key = firebaseRef.push().getKey();
            }

            if (key != null) {
                String finalKey = key;
                recipe.firebaseKey = finalKey;

                Recipe finalRecipe = recipe;
                firebaseRef.child(finalKey).setValue(finalRecipe)
                        .addOnSuccessListener(unused -> {
                            dao.updateFirebaseKeyAndSync(finalRecipe.id, finalKey);
                            Toast.makeText(MainActivity.this, "Receita sincronizada: " + finalRecipe.title, Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(MainActivity.this, "Erro ao sincronizar: " + finalRecipe.title, Toast.LENGTH_SHORT).show();
                        });
            }
        }
    }

    /**
     * Método chamado ao clicar no botão de excluir em uma receita da lista
     * Exibe diálogo de confirmação, exclui do Firebase se conectado, e do banco local sempre.
     */
    @Override
    public void onDeleteClick(Recipe recipe) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir receita")
                .setMessage("Deseja realmente excluir a receita \"" + recipe.title + "\"?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    if (isConnected() && recipe.firebaseKey != null && !recipe.firebaseKey.isEmpty()) {
                        DatabaseReference firebaseRef = FirebaseDatabase.getInstance()
                                .getReference("recipes").child(recipe.firebaseKey);
                        firebaseRef.removeValue().addOnSuccessListener(unused -> {
                            dao.deleteRecipe(recipe.id);
                            loadRecipesFromDb();
                            Toast.makeText(MainActivity.this, "Receita excluída do Firebase e localmente", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(MainActivity.this, "Erro ao excluir no Firebase", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        dao.deleteRecipe(recipe.id);
                        loadRecipesFromDb();
                        Toast.makeText(MainActivity.this, "Receita excluída localmente", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }

    /**
     * Busca receitas do Firebase e insere no banco local caso não existam ainda
     */
    private void fetchFromFirebase() {
        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference("recipes");

        firebaseRef.get().addOnSuccessListener(snapshot -> {
            for (DataSnapshot recipeSnap : snapshot.getChildren()) {
                Recipe recipe = recipeSnap.getValue(Recipe.class);
                if (recipe != null && !recipeAlreadyExists(recipe)) {
                    dao.insertRecipe(recipe); // Salva localmente
                }
            }
            loadRecipesFromDb(); // Atualiza a lista na interface
        }).addOnFailureListener(e -> {
            Toast.makeText(MainActivity.this, "Erro ao buscar dados do Firebase", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Verifica se a receita já existe localmente (por título e ingredientes)
     * @param recipe Receita a verificar
     * @return true se já existe, false caso contrário
     */
    private boolean recipeAlreadyExists(Recipe recipe) {
        List<Recipe> localRecipes = dao.getAllRecipes();
        for (Recipe local : localRecipes) {
            if (local.title.equalsIgnoreCase(recipe.title) &&
                    local.ingredients.equalsIgnoreCase(recipe.ingredients)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Quando o usuário clica em "ver" receita na lista, abre a Activity de detalhes
     * Passa os dados da receita via Intent extras
     */
    @Override
    public void onViewClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_TITLE, recipe.title);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_INGREDIENTS, recipe.ingredients);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_INSTRUCTIONS, recipe.instructions);
        startActivity(intent);
    }
}
