package io.vladshablinsky.passwordmanager.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.hash.Hashing;

import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import io.vladshablinsky.passwordmanager.Database.DBHandler;
import io.vladshablinsky.passwordmanager.Database.EntryDAO;
import io.vladshablinsky.passwordmanager.Entities.Entry;
import io.vladshablinsky.passwordmanager.Entities.Sheet;

/**
 * Created by vlad on 5/10/16.
 */
public class SheetDAO {

    // Database fields
    public static final String SHEET_EMPTY_PASS = "empty";
    private SQLiteDatabase mDatabase;
    private DBHandler mDbHandler;
    private Context mContext;
    private String[] mAllColumns = {
            DBHandler.COLUMN_SHEET_ID,
            DBHandler.COLUMN_SHEET_NAME,
            DBHandler.COLUMN_SHEET_PASS,
            DBHandler.COLUMN_SHEET_DESC
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

    public Sheet createSheet(String name, String pass, String desc) {
        ContentValues values = new ContentValues();
        values.put(DBHandler.COLUMN_SHEET_NAME, name);
        values.put(DBHandler.COLUMN_SHEET_PASS, Hashing.sha1().hashString(pass, Charset.defaultCharset()).toString());
        values.put(DBHandler.COLUMN_SHEET_DESC, desc);

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
        cursor.close();
        return newSheet;

    }

    public void deleteSheet(Sheet sheet) {
        long id = sheet.getId();
        // Remove all entries of this sheet
        EntryDAO entryDao = new EntryDAO(mContext, SHEET_EMPTY_PASS);
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
        sheet.setDescription(cursor.getString(3));
        return sheet;
    }
}
