package com.example.pantera.questionnaire_depression.model;

/**
 * Created by Pantera on 2016-12-24.
 */

public class GroupOfQuestion {
    private String name;
    private String title;

    public String getTitle() {
        return title;
    }

    public GroupOfQuestion setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getName() {
        return name;
    }

    public GroupOfQuestion setName(String name) {
        this.name = name;
        return this;
    }
}
