package com.unifebe.edu.projetoreceitas;

public class Recipe {
    public int id;
    public String title;
    public String ingredients;
    public String instructions;
    public String firebaseKey; // chave do Firebase, pode ser null se não sincronizado
    public boolean synced;

    // Construtor para novo registro (id gerado pelo SQLite)
    public Recipe(String title, String ingredients, String instructions) {
        this.title = title;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.firebaseKey = null;
        this.synced = false;
    }

    // Construtor completo (para recuperar do DB)
    public Recipe(int id, String title, String ingredients, String instructions, String firebaseKey, boolean synced) {
        this.id = id;
        this.title = title;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.firebaseKey = firebaseKey;
        this.synced = synced;
    }
}
