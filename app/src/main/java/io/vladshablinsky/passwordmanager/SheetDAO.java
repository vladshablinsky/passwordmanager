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
public class SheetDAO {

    // Database fields
    private SQLiteDatabase mDatabase;
    private DBHandler mDbHandler;
    private Context mContext;
    private String[] mAllColumns = {
            DBHandler.COLUMN_SHEET_ID,
            DBHandler.COLUMN_SHEET_NAME,
            DBHandler.COLUMN_SHEET_PASS
    };

    public SheetDAO(Context context) {
        this.mContext = context;
        mDbHandler = new DBHandler(context);
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

    public Sheet createSheet(String name, String pass) {
        ContentValues values = new ContentValues();
        values.put(DBHandler.COLUMN_SHEET_NAME, name);
        values.put(DBHandler.COLUMN_SHEET_PASS, pass);

        long insertId = mDatabase
                .insert(
                        DBHandler.TABLE_SHEETS,
                        null,
                        values
                );

        Cursor cursor = mDatabase.query(
                DBHandler.TABLE_SHEETS,
                mAllColumns,
                DBHandler.COLUMN_SHEET_ID + " = " + insertId,
                null,
                null,
                null,
                null
        );
        cursor.moveToFirst();
        Sheet newSheet = cursorToSheet(cursor);
        return newSheet;

    }

    public void deleteSheet(Sheet sheet) {
        long id = sheet.getId();
        // delete all entries of this sheet
        EntryDAO entryDao = new EntryDAO(mContext);
        List<Entry> listEntries = entryDao.getEntriesOfSheet(id);
        if (listEntries != null && !listEntries.isEmpty()) {
            for (Entry e : listEntries) {
                entryDao.deleteEntry(e);
            }
        }

        System.out.println("the deleted sheet has the id: " + id);
        mDatabase.delete(DBHandler.TABLE_SHEETS, DBHandler.COLUMN_SHEET_ID
                + " = " + id, null);
    }

    public List<Sheet> getAllSheets() {
        List<Sheet> listSheets = new ArrayList<>();

        Cursor cursor = mDatabase.query(DBHandler.TABLE_SHEETS, mAllColumns,
                null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Sheet sheet = cursorToSheet(cursor);
                listSheets.add(sheet);
                cursor.moveToNext();
            }

            // make sure to close the cursor
            cursor.close();
        }
        return listSheets;
    }

    public Sheet getSheetById(long id) {
        Cursor cursor = mDatabase.query(DBHandler.TABLE_SHEETS, mAllColumns,
                DBHandler.COLUMN_SHEET_ID + " = ?",
                new String[] { String.valueOf(id) }, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        Sheet sheet = cursorToSheet(cursor);
        return sheet;
    }

    protected Sheet cursorToSheet(Cursor cursor) {
        Sheet sheet = new Sheet();
        sheet.setId(cursor.getLong(0));
        sheet.setName(cursor.getString(1));
        sheet.setPass(cursor.getString(2));
        return sheet;
    }
}
