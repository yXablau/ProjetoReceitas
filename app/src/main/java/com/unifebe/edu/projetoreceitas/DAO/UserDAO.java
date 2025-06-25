package com.unifebe.edu.projetoreceitas.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;

import com.unifebe.edu.projetoreceitas.model.User;

/**
 * Classe responsável por manipular o banco de dados local SQLite para autenticação de usuários.
 * Contém métodos para criação, inserção e consulta de usuários.
 */
public class UserDAO extends SQLiteOpenHelper {

    private static final String DB_NAME = "users_db";      // Nome do banco de dados
    private static final int DB_VERSION = 1;               // Versão do banco
    private static final String TABLE_NAME = "users";      // Nome da tabela de usuários

    // Colunas da tabela
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";

    /**
     * Construtor que inicializa o banco de dados com o nome e versão definidos.
     */
    public UserDAO(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Cria a tabela de usuários no banco de dados local.
     * Chamada automaticamente na primeira execução do app.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " ("
                + COL_EMAIL + " TEXT PRIMARY KEY, "
                + COL_PASSWORD + " TEXT)";
        db.execSQL(createTable);
    }

    /**
     * Atualiza o banco de dados caso a versão seja alterada.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Insere um novo usuário ou substitui um existente com o mesmo e-mail.
     * @param user Objeto do tipo User com e-mail e senha.
     */
    public void insertUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EMAIL, user.email);
        values.put(COL_PASSWORD, user.password);

        // Insere ou substitui se já existir (evita erro de chave primária duplicada)
        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Busca um usuário pelo e-mail.
     * @param email E-mail do usuário a ser buscado.
     * @return Objeto User se encontrado, ou null se não existir.
     */
    public User getUserByEmail(String email) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                COL_EMAIL + "=?",
                new String[]{email},
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            String password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD));
            cursor.close();
            return new User(email, password);
        }

        cursor.close();
        return null;
    }
}
