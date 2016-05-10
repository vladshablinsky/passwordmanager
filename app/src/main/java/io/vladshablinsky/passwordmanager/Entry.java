package io.vladshablinsky.passwordmanager;

import java.io.Serializable;

/**
 * Created by vlad on 5/10/16.
 */
public class Entry implements Serializable {
    private long id;
    private long sheetId;
    private String name;
    private Sheet sheet;

    public Sheet getSheet() {
        return sheet;
    }

    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }



    public Entry(int id, int sheetId, String name) {
        this.id = id;
        this.sheetId = sheetId;
        this.name = name;
    }

    public Entry() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getListID() {
        return sheetId;
    }

    public void setSheetId(long sheetId) {
        this.sheetId = sheetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
