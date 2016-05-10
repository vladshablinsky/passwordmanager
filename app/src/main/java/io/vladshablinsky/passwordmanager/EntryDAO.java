package io.vladshablinsky.passwordmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vlad on 5/10/16.
 */
public class EntryDAO {

    // Database Fields
    private SQLiteDatabase mDatabase;
    private DBHandler mDbHandler;
    private Context mContext;
    private String[] mAllColumns = {
            DBHandler.COLUMN_ENTRY_ID,
            DBHandler.COLUMN_ENTRY_NAME,
            DBHandler.COLUMN_ENTRY_SHEET_ID
    };

    public EntryDAO(Context context) {
        mDbHandler = new DBHandler(context);
        this.mContext = context;
        try {
            open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void open() throws SQLException {
        mDatabase = mDbHandler.getWritableDatabase();
    }

    public void close() {
        mDbHandler.close();
    }

    public Entry createEntry(String name, long sheetId) {
        ContentValues values = new ContentValues();
        values.put(DBHandler.COLUMN_ENTRY_NAME, name);
        values.put(DBHandler.COLUMN_ENTRY_SHEET_ID, sheetId);
        long insertId = mDatabase
                .insert(DBHandler.TABLE_ENTRIES, null, values);

        Cursor cursor = mDatabase.query(
                DBHandler.TABLE_ENTRIES,
                mAllColumns,
                DBHandler.COLUMN_ENTRY_ID + " = " + insertId,
                null,
                null,
                null,
                null
        );
        cursor.moveToFirst();
        Entry newEntry = cursorToEntry(cursor);
        cursor.close();
        return newEntry;
    }

    public void deleteEntry(Entry entry) {
        long id = entry.getId();
        mDatabase.delete(
                DBHandler.TABLE_ENTRIES,
                DBHandler.COLUMN_ENTRY_ID + " = " + id,
                null
        );
    }

    public List<Entry> getAllEntries() {
        List<Entry> listEntries = new ArrayList<>();

        Cursor cursor = mDatabase.query(
                DBHandler.TABLE_ENTRIES,
                mAllColumns,
                null,
                null,
                null,
                null,
                null
        );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Entry employee = cursorToEntry(cursor);
            listEntries.add(employee);
            cursor.moveToNext();
        }

        // make sure to close the cursor
        cursor.close();
        return listEntries;
    }

    public List<Entry> getEntriesOfSheet(long sheetId) {
        List<Entry> listEntries = new ArrayList<>();

        Cursor cursor = mDatabase.query(
                DBHandler.TABLE_ENTRIES,
                mAllColumns,
                DBHandler.COLUMN_SHEET_ID + " = ?",
                new String[] { String.valueOf(sheetId) },
                null,
                null,
                null
        );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Entry entry = cursorToEntry(cursor);
            listEntries.add(entry);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return listEntries;
    }

    private Entry cursorToEntry(Cursor cursor) {
        Entry entry = new Entry();
        entry.setId(cursor.getLong(0));
        entry.setName(cursor.getString(1));

        long sheetId = cursor.getLong(2);
        SheetDAO dao = new SheetDAO(mContext);
        Sheet sheet = dao.getSheetById(sheetId);

        if (sheet != null) {
            entry.setSheet(sheet);
        }

        return entry;
    }

}
