package com.example.pantera.questionnaire_depression.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Pantera on 2016-12-27.
 */

public class Answer implements Serializable {

    private int id;
    private Date date;
    @SerializedName("sumPoints")
    private int sumOfPoints;

    public int getId() {
        return id;
    }

    public Answer setId(int id) {
        this.id = id;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public Answer setDate(Date date) {
        this.date = date;
        return this;
    }

    public int getSumOfPoints() {
        return sumOfPoints;
    }

    public Answer setSumOfPoints(int sumOfPoints) {
        this.sumOfPoints = sumOfPoints;
        return this;
    }
}
