package com.unifebe.edu.projetoreceitas.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.unifebe.edu.projetoreceitas.model.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por gerenciar o banco de dados local SQLite da aplicação.
 * Armazena receitas e gerencia sincronização com o Firebase.
 */
public class RecipeDAO extends SQLiteOpenHelper {

    private static final String DB_NAME = "recipes_db";   // Nome do banco de dados
    private static final int DB_VERSION = 1;              // Versão do banco (usar para upgrades)
    private static final String TABLE_NAME = "recipes";   // Nome da tabela de receitas

    // Colunas da tabela
    private static final String COL_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_INGREDIENTS = "ingredients";
    private static final String COL_INSTRUCTIONS = "instructions";
    private static final String COL_FIREBASE_KEY = "firebaseKey";
    private static final String COL_SYNCED = "synced"; // 0 = não sincronizado, 1 = sincronizado

    public RecipeDAO(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Cria a tabela de receitas no banco de dados na primeira execução.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " TEXT, "
                + COL_INGREDIENTS + " TEXT, "
                + COL_INSTRUCTIONS + " TEXT, "
                + COL_FIREBASE_KEY + " TEXT, "
                + COL_SYNCED + " INTEGER DEFAULT 0)";
        db.execSQL(createTable);
    }

    /**
     * Atualiza o banco de dados se a versão for alterada.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Remove a tabela antiga e cria novamente
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Insere uma nova receita no banco de dados local.
     * @param recipe Objeto Recipe contendo os dados
     * @return ID da nova receita inserida
     */
    public long insertRecipe(Recipe recipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, recipe.title);
        values.put(COL_INGREDIENTS, recipe.ingredients);
        values.put(COL_INSTRUCTIONS, recipe.instructions);
        values.put(COL_FIREBASE_KEY, recipe.firebaseKey);
        values.put(COL_SYNCED, recipe.synced ? 1 : 0);
        return db.insert(TABLE_NAME, null, values);
    }

    /**
     * Retorna todas as receitas armazenadas localmente.
     * @return Lista de objetos Recipe
     */
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COL_TITLE + " ASC");

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE));
                String ingredients = cursor.getString(cursor.getColumnIndexOrThrow(COL_INGREDIENTS));
                String instructions = cursor.getString(cursor.getColumnIndexOrThrow(COL_INSTRUCTIONS));
                String firebaseKey = cursor.getString(cursor.getColumnIndexOrThrow(COL_FIREBASE_KEY));
                int syncedInt = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SYNCED));
                boolean synced = syncedInt == 1;

                Recipe recipe = new Recipe(id, title, ingredients, instructions, firebaseKey, synced);
                recipes.add(recipe);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return recipes;
    }

    /**
     * Exclui uma receita do banco de dados local com base no ID.
     * @param id ID da receita
     * @return número de linhas afetadas
     */
    public int deleteRecipe(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    /**
     * Retorna todas as receitas que ainda não foram sincronizadas com o Firebase.
     * @return Lista de receitas não sincronizadas
     */
    public List<Recipe> getUnsyncedRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, null, COL_SYNCED + "=0", null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE));
                String ingredients = cursor.getString(cursor.getColumnIndexOrThrow(COL_INGREDIENTS));
                String instructions = cursor.getString(cursor.getColumnIndexOrThrow(COL_INSTRUCTIONS));
                String firebaseKey = cursor.getString(cursor.getColumnIndexOrThrow(COL_FIREBASE_KEY));
                boolean synced = false;

                Recipe recipe = new Recipe(id, title, ingredients, instructions, firebaseKey, synced);
                recipes.add(recipe);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return recipes;
    }

    /**
     * Atualiza a chave do Firebase e marca a receita como sincronizada.
     * @param id ID da receita
     * @param firebaseKey Chave gerada no Firebase
     */
    public void updateFirebaseKeyAndSync(int id, String firebaseKey) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FIREBASE_KEY, firebaseKey);
        values.put(COL_SYNCED, 1);
        db.update(TABLE_NAME, values, COL_ID + "=?", new String[]{String.valueOf(id)});
    }
}
