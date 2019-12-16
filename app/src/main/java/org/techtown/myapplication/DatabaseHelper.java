package org.techtown.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static String NAME = "book_nick.db";
    public static int VERSION = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists book("
                + " _id integer PRIMARY KEY autoincrement, "
                + " book_nation text, "
                + " book_name text, "
                + " book_char text, "
                + " book_nick text, "
                + " title_server text, "
                + " download_status text, "
                + " imgResource text, "
                + "UNIQUE(book_name, book_char) "
                + ")";

        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > 1) {
            db.execSQL("DROP TABLE IF EXISTS book");
        }
    }
}
