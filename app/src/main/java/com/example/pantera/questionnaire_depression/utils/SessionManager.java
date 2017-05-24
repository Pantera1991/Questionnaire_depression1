package com.example.pantera.questionnaire_depression.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.pantera.questionnaire_depression.model.Patient;
import com.example.pantera.questionnaire_depression.model.User;

/**
 * Created by Pantera on 2016-12-22.
 */

public class SessionManager {

    private final SharedPreferences pref;
    private Context mContext;
    public static final String PREF_NAME = "QuestionnairePref";
    public static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_NAME = "name";
    public static final String KEY_SURNAME = "surname";
    public static final String KEY_ID = "id";
    public static final String KEY_COOKIE = "cookie";
    public static final String KEY_USER_USERNAME = "user.username";
    public static final String KEY_USER_ENABLED = "user.enabled";
    public static final String KEY_USER_ID = "user.id";
    public static final String KEY_STARTER_TEST = "starttest";
    public static final String KEY_CLASSIFIED = "classified";
    public static final String KEY_LAST_SEND_QUESTION = "last_send_questionnaire";

    // Constructor
    public SessionManager(Context mContext) {
        this.mContext = mContext;
        pref = this.mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public SessionManager(SharedPreferences pref) {
        this.pref = pref;
    }

    public SharedPreferences getPref() {
        return this.pref;
    }

    public void updateValue(String key, int value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public <T> void updateValue(String key, T value) {
        SharedPreferences.Editor editor = pref.edit();

        if(value instanceof String){
            editor.putString(key, (String) value);
        }else if(value instanceof Boolean){
            editor.putBoolean(key, (Boolean) value);
        }else if(value instanceof Integer){
            editor.putInt(key, (Integer) value);
        }else if(value instanceof Float){
            editor.putFloat(key, (Float) value);
        }else if(value instanceof Long){
            editor.putLong(key ,(Long) value);
        }

        editor.apply();
    }

    public void createLoginSession(Patient patient, String cookie) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_COOKIE, cookie);
        editor.putInt(KEY_ID, patient.getId());
        editor.putString(KEY_NAME, patient.getName());
        editor.putString(KEY_SURNAME, patient.getSurname());
        editor.putInt(KEY_USER_ID, patient.getUser().getId());
        editor.putString(KEY_USER_USERNAME, patient.getUser().getUsername());
        editor.putBoolean(KEY_USER_ENABLED, patient.getUser().isEnabled());
        editor.putBoolean(KEY_STARTER_TEST, patient.isStartQuestionnaire());
        editor.putInt(KEY_CLASSIFIED, patient.getClassified());

        // commit changes
        editor.apply();
    }


    public void saveLastSendQuestion(final String dateNotify) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_LAST_SEND_QUESTION, dateNotify);
        editor.apply();
    }

    public String getDateLastSendQuestion() {
        return pref.getString(KEY_LAST_SEND_QUESTION, null);
    }

    public String getCookieValue() {
        return pref.getString(KEY_COOKIE, null);
    }

    public int getIdUser() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    public int getIdPatient() {
        return pref.getInt(KEY_ID, -1);
    }

    public boolean isStartTest(){
        return pref.getBoolean(KEY_STARTER_TEST, false);
    }

    public Patient getUserDetails() {
        Patient patient = new Patient();
        User user = new User();
        patient.setId(pref.getInt(KEY_ID, 0));
        patient.setName(pref.getString(KEY_NAME, null));
        patient.setSurname(pref.getString(KEY_SURNAME, null));
        patient.setStartQuestionnaire(pref.getBoolean(KEY_STARTER_TEST, false));
        patient.setClassified(pref.getInt(KEY_CLASSIFIED, 2));
        user.setId(pref.getInt(KEY_USER_ID, 0));
        user.setUsername(pref.getString(KEY_USER_USERNAME, null));
        user.setEnabled(pref.getBoolean(KEY_USER_ENABLED, false));
        patient.setUser(user);
        return patient;
    }

    public void logoutUser() {
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }


    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false) && pref.getString(KEY_COOKIE, null) != null;
    }
}
