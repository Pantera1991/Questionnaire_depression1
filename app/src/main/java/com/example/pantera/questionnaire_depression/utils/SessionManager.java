package com.example.pantera.questionnaire_depression.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.pantera.questionnaire_depression.LoginActivity;
import com.example.pantera.questionnaire_depression.MainActivity;
import com.example.pantera.questionnaire_depression.api.RestClient;
import com.example.pantera.questionnaire_depression.model.DateResponse;
import com.example.pantera.questionnaire_depression.model.Patient;
import com.example.pantera.questionnaire_depression.model.User;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Pantera on 2016-12-22.
 */

public class SessionManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
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
        int PRIVATE_MODE = 0;
        pref = this.mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public SharedPreferences getPref(){
        return this.pref;
    }

    public void updateValue(String key, int value){
        editor.putInt(key, value);
        editor.commit();
    }

    public void updateValue(String key, String value){
        editor.putString(key, value);
        editor.commit();
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
        editor.putInt(KEY_CLASSIFIED, patient.getClassified());

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        int id = patient.getUser().getId();
        RestClient restClient = new RestClient(mContext);
        try {
            Call<ResponseBody> call = restClient.get().updateFirebaseToken(id, refreshedToken);
            Response<ResponseBody> respnse = call.execute();
            Log.d("SEND TOKEN", String.valueOf(respnse.code()));

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("WYSLANIE TOKENA", e.toString());
        }

        Call<DateResponse> dateCall = restClient.get().getDateLastSendAnswer(patient.getId());

        try {
            Response<DateResponse> response = dateCall.execute();
            if(response.code() == 200){
                String dateNotify = response.body().getDate();
                editor.putString(KEY_LAST_SEND_QUESTION, dateNotify);
            }
        } catch (IOException e) {
            e.printStackTrace();
            editor.putString(KEY_LAST_SEND_QUESTION, "");
        }

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

    public void updateUserData(){
        RestClient restClient = new RestClient(mContext);
        Call<Patient> call = restClient.get().getDetalisPatient(pref.getInt(KEY_USER_ID, 0));
        call.enqueue(new Callback<Patient>() {
            @Override
            public void onResponse(Call<Patient> call, Response<Patient> response) {
                if(response.code() == 200){
                    Patient p = response.body();
                    createLoginSession(p, pref.getString(KEY_COOKIE, ""));
                    checkLogin();
                }
            }

            @Override
            public void onFailure(Call<Patient> call, Throwable t) {
                //ServerConnectionLost.returnToLoginActivity((Activity) mContext);
                Log.d("SESSION",t.toString());
            }
        });
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
        patient.setClassified(pref.getInt(KEY_CLASSIFIED, 2));
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


    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false) && pref.getString(KEY_COOKIE, null) != null;
    }
}
