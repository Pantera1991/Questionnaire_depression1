package com.example.pantera.questionnaire_depression.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Pantera on 2016-12-24.
 */

public class Question implements Serializable{

    private int id;
    private String name;
    private int points;
    @SerializedName("groupofquestion")
    private GroupOfQuestion groupOfQuestion;
    @Expose
    private boolean selectOption;

    public boolean getSelectOption() {
        return selectOption;
    }

    public Question setSelectOption(boolean selectOption) {
        this.selectOption = selectOption;
        return this;
    }

    public String getName() {
        return name;
    }

    public Question setName(String name) {
        this.name = name;
        return this;
    }

    public int getId() {
        return id;
    }

    public Question setId(int id) {
        this.id = id;
        return this;
    }

    public GroupOfQuestion getGroupOfQuestion() {
        return groupOfQuestion;
    }

    public Question setGroupOfQuestion(GroupOfQuestion groupOfQuestion) {
        this.groupOfQuestion = groupOfQuestion;
        return this;
    }

    public int getPoints() {
        return points;
    }

    public Question setPoints(int points) {
        this.points = points;
        return this;
    }
}
