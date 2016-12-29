package com.example.pantera.questionnaire_depression.model;

/**
 * Created by Pantera on 2016-12-27.
 */

public class Doctor {
    private int id;
    private String name;
    private String surname;
    private User user;
    private String phone;

    public String getPhone() {
        return phone;
    }

    public Doctor setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public int getId() {
        return id;
    }

    public Doctor setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Doctor setName(String name) {
        this.name = name;
        return this;
    }

    public String getSurname() {
        return surname;
    }

    public Doctor setSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Doctor setUser(User user) {
        this.user = user;
        return this;
    }
}
