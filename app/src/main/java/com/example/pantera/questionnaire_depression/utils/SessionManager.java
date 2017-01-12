package com.example.pantera.questionnaire_depression.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.pantera.questionnaire_depression.LoginActivity;
import com.example.pantera.questionnaire_depression.MainActivity;
import com.example.pantera.questionnaire_depression.model.Patient;
import com.example.pantera.questionnaire_depression.model.User;

/**
 * Created by Pantera on 2016-12-22.
 */

public class SessionManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context mContext;
    private static final String PREF_NAME = "QuestionnairePref";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String KEY_NAME = "name";
    private static final String KEY_SURNAME = "surname";
    private static final String KEY_ID = "id";
    private static final String KEY_COOKIE = "cookie";
    private static final String KEY_USER_USERNAME = "user.username";
    private static final String KEY_USER_ENABLED = "user.enabled";
    private static final String KEY_USER_ID = "user.id";
    private static final String KEY_STARTER_TEST = "starttest";

    // Constructor
    public SessionManager(Context mContext) {
        this.mContext = mContext;
        int PRIVATE_MODE = 0;
        pref = this.mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void createLoginSession(Patient patient, String cookie) {

        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_COOKIE, cookie);
        editor.putInt(KEY_ID, patient.getId());
        editor.putString(KEY_NAME, patient.getName());
        editor.putString(KEY_SURNAME, patient.getSurname());
        editor.putInt(KEY_USER_ID, patient.getUser().getId());
        editor.putString(KEY_USER_USERNAME, patient.getUser().getUsername());
        editor.putBoolean(KEY_USER_ENABLED, patient.getUser().isEnabled());
        editor.putBoolean(KEY_STARTER_TEST, patient.isStartQuestionnaire());

        // commit changes
        editor.commit();
    }

    public void checkLogin() {
        // Check login status
        if (this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(mContext, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);
        }

    }

    public String getCookieValue() {
        return pref.getString(KEY_COOKIE, null);
    }

    public Patient getUserDetails() {
        Patient patient = new Patient();
        User user = new User();
        patient.setId(pref.getInt(KEY_ID, 0));
        patient.setName(pref.getString(KEY_NAME, null));
        patient.setSurname(pref.getString(KEY_SURNAME, null));
        patient.setStartQuestionnaire(pref.getBoolean(KEY_STARTER_TEST, false));
        user.setId(pref.getInt(KEY_USER_ID, 0));
        user.setUsername(pref.getString(KEY_USER_USERNAME, null));
        user.setEnabled(pref.getBoolean(KEY_USER_ENABLED, false));
        patient.setUser(user);
        return patient;
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();

        Intent i = new Intent(mContext, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
    }


    private boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false) && pref.getString(KEY_COOKIE, null) != null;
    }
}
