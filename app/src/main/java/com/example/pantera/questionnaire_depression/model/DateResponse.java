package com.example.pantera.questionnaire_depression.model;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by Pantera on 2017-01-29.
 */

public class DateResponse {
    private String date;

    public String getDate() {
        return date;
    }

    public DateResponse setDate(String date) {
        this.date = date;
        return this;
    }

    public static DateTime stringToDateTime(String date){
        if(date != null && !date.isEmpty()){
            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
            return formatter.parseDateTime(date);
        }else {
            return null;
        }
    }
}
