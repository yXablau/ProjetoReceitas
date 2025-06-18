package com.unifebe.edu.projetoreceitas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class RecipeDAO extends SQLiteOpenHelper {

    private static final String DB_NAME = "recipes_db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "recipes";

    private static final String COL_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_INGREDIENTS = "ingredients";
    private static final String COL_INSTRUCTIONS = "instructions";
    private static final String COL_FIREBASE_KEY = "firebaseKey";
    private static final String COL_SYNCED = "synced"; // 0 = não sincronizado, 1 = sincronizado

    public RecipeDAO(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

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

    public int deleteRecipe(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

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

    public void updateFirebaseKeyAndSync(int id, String firebaseKey) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FIREBASE_KEY, firebaseKey);
        values.put(COL_SYNCED, 1);
        db.update(TABLE_NAME, values, COL_ID + "=?", new String[]{String.valueOf(id)});
    }
}
