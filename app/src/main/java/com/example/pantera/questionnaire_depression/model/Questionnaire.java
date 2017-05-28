package com.example.pantera.questionnaire_depression.model;

import java.util.List;

/**
 * Created by Pantera on 2017-05-25.
 */

public class Questionnaire {
    private String patientID;
    private List<Integer> answers;

    public Questionnaire(String patientID, List<Integer> answers) {
        this.patientID = patientID;
        this.answers = answers;
    }
}
