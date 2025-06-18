package com.unifebe.edu.projetoreceitas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;

public class UserDAO extends SQLiteOpenHelper {

    private static final String DB_NAME = "users_db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "users";

    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";

    public UserDAO(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " ("
                + COL_EMAIL + " TEXT PRIMARY KEY, "
                + COL_PASSWORD + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EMAIL, user.email);
        values.put(COL_PASSWORD, user.password);
        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COL_EMAIL + "=?", new String[]{email}, null, null, null);
        if (cursor.moveToFirst()) {
            String password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD));
            cursor.close();
            return new User(email, password);
        }
        cursor.close();
        return null;
    }
}
