package com.getbetter.formsegment;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Hannah on 2/13/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper dbhelper;
    private static SQLiteDatabase db;

    private static final String DB_NAME = "FormDatabase";
    private static final int DB_VERSION = 1;

    public static synchronized DBHelper getInstance(Context context) {
        if (dbhelper == null) {
            dbhelper = new DBHelper(context.getApplicationContext());
        }
        return dbhelper;
    }

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        db.execSQL(
            "create table forms" +
                    "(id integer primary key, element integer)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
