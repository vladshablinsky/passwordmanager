package io.vladshablinsky.passwordmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by vlad on 5/10/16.
 */
public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "sheets.db";

    public static final String TABLE_SHEETS = "sheets";
    public static final String COLUMN_SHEET_ID = "sheet_id";
    public static final String COLUMN_SHEET_NAME = "sheet_name";
    public static final String COLUMN_SHEET_PASS = "sheet_pass";

    public static final String TABLE_ENTRIES = "entries";
    public static final String COLUMN_ENTRY_ID = "entry_id";
    public static final String COLUMN_ENTRY_NAME = "entry_name";
    public static final String COLUMN_ENTRY_SHEET_ID = "entry_sheet_id";

    private static final String SQL_CREATE_TABLE_SHEETS = "CREATE TABLE " + TABLE_SHEETS + " (" +
            COLUMN_SHEET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_SHEET_NAME + " TEXT NOT NULL, " +
            COLUMN_SHEET_PASS + " TEXT NOT NULL " +
            ");";

    private static final String SQL_CREATE_TABLE_ENTRIES = "CREATE TABLE " + TABLE_ENTRIES + " (" +
            COLUMN_ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ENTRY_NAME + " TEXT NOT NULL, " +
            COLUMN_ENTRY_SHEET_ID + " INTEGER NOT NULL " +
            ");";


    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_SHEETS);
        db.execSQL(SQL_CREATE_TABLE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHEETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRIES);

        onCreate(db);
    }
}
