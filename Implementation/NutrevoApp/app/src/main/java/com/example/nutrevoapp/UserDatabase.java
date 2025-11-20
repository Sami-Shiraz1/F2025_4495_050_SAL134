package com.example.nutrevoapp;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.nutrevoapp.DataUser;

public class UserDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "user_data.db";
    private static final String TABLE_USERS = "user_details";
    private static final int DB_VERSION = 1;

    private static final String COL_ID = "_id";
    private static final String COL_NAME = "FullName";
    private static final String COL_USERNAME = "Username";
    private static final String COL_EMAIL = "Email";
    private static final String COL_PASSWORD = "Password";

    private static final String CREATE_TABLE_QUERY =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NAME + " TEXT, " +
                    COL_USERNAME + " TEXT, " +
                    COL_EMAIL + " TEXT, " +
                    COL_PASSWORD + " TEXT);";

    private static final String DROP_TABLE_QUERY =
            "DROP TABLE IF EXISTS " + TABLE_USERS;

    private final Context context;

    public UserDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_QUERY);
            Toast.makeText(context, "Database table successfully created!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Error creating table: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_QUERY);
        onCreate(db);
        Toast.makeText(context, "Database upgraded successfully.", Toast.LENGTH_SHORT).show();
    }

    public long insertUser(DataUser user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_NAME, user.getName());
        values.put(COL_USERNAME, user.getUsername());
        values.put(COL_EMAIL, user.getEmail());
        values.put(COL_PASSWORD, user.getPassword());

        return db.insert(TABLE_USERS, null, values);
    }

    public boolean validateLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);

        boolean isValid = false;

        if (cursor.getCount() == 0) {
            Toast.makeText(context, "No user records found.", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                String dbUser = cursor.getString(2);
                String dbPass = cursor.getString(4);

                if (dbUser.equals(username) && dbPass.equals(password)) {
                    isValid = true;
                    break;
                }
            }
        }
        cursor.close();
        return isValid;
    }

    public long insertInDatabase(DataUser dataUser) {
        return 0;
    }

    public Boolean check(String u, String p) {
        return null;
    }
}
