package com.unifebe.edu.projetoreceitas;

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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeListener {

    private RecyclerView recipeRecyclerView;
    private Button addRecipeButton;
    private RecipeAdapter adapter;
    private ArrayList<Recipe> recipeList = new ArrayList<>();
    private RecipeDAO dao;

    private static final int ADD_RECIPE_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dao = new RecipeDAO(this);

        recipeRecyclerView = findViewById(R.id.recipeRecyclerView);
        addRecipeButton = findViewById(R.id.addRecipeButton);

        adapter = new RecipeAdapter(recipeList, this);
        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recipeRecyclerView.setAdapter(adapter);

        addRecipeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddRecipeActivity.class);
            startActivityForResult(intent, ADD_RECIPE_REQUEST);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecipesFromDb();
        if (isConnected()) {
            syncWithFirebase();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_RECIPE_REQUEST && resultCode == RESULT_OK) {
            loadRecipesFromDb();
        }
    }

    private void loadRecipesFromDb() {
        recipeList.clear();
        recipeList.addAll(dao.getAllRecipes());
        adapter.notifyDataSetChanged();
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

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
                firebaseRef.child(finalKey).setValue(finalRecipe).addOnSuccessListener(unused -> {
                    dao.updateFirebaseKeyAndSync(finalRecipe.id, finalKey);
                    Toast.makeText(MainActivity.this, "Receita sincronizada: " + finalRecipe.title, Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Erro ao sincronizar: " + finalRecipe.title, Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

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

    @Override
    public void onViewClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_TITLE, recipe.title);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_INGREDIENTS, recipe.ingredients);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_INSTRUCTIONS, recipe.instructions);
        startActivity(intent);
    }
}
