package io.vladshablinsky.passwordmanager;

import java.io.Serializable;

/**
 * Created by vlad on 5/10/16.
 */
public class Sheet implements Serializable {
    private long id;
    private String name;
    private String pass;
    private String description;

    Sheet() {
    }


    public Sheet(long id, String name, String pass, String description) {
        this.id = id;
        this.name = name;
        this.pass = pass;
        this.description = description;
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
}
