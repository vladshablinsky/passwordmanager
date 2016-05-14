package io.vladshablinsky.passwordmanager;

import java.io.Serializable;

/**
 * Created by vlad on 5/10/16.
 */
public class Entry implements Serializable {
    private long id;
    private String name;
    private String pass;
    private String description;
    private long sheetId;
    private Sheet sheet;

    public Sheet getSheet() {
        return sheet;
    }

    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
        this.sheetId = sheet.getId();
    }

    public Entry(long id, String name, String pass, String description) {
        this.id = id;
        this.name = name;
        this.pass = pass;
        this.description = description;
    }

    public Entry() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public long getSheetId() {
        return sheetId;
    }
}
