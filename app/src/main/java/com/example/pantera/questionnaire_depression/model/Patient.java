package com.example.pantera.questionnaire_depression.model;

import java.io.Serializable;

/**
 * Created by Pantera on 2016-12-22.
 */

public class Patient implements Serializable{
    private int id;
    private String name;
    private String surname;
    private User user;
    private boolean startQuestionnaire;
    private int classified;

    public int getClassified() {
        return classified;
    }

    public Patient setClassified(int classified) {
        this.classified = classified;
        return this;
    }

    public boolean isStartQuestionnaire() {
        return startQuestionnaire;
    }

    public Patient setStartQuestionnaire(boolean startQuestionnaire) {
        this.startQuestionnaire = startQuestionnaire;
        return this;
    }

    public int getId() {
        return id;
    }

    public Patient setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Patient setName(String name) {
        this.name = name;
        return this;
    }

    public String getSurname() {
        return surname;
    }

    public Patient setSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Patient setUser(User user) {
        this.user = user;
        return this;
    }
}
