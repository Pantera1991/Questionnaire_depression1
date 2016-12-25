package com.example.pantera.questionnaire_depression.model;

import java.io.Serializable;

/**
 * Created by Pantera on 2016-12-22.
 */

public class User implements Serializable{
    private int id;
    private String username;
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public User setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public int getId() {
        return id;
    }

    public User setId(int id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }
}
