package com.example.pantera.questionnaire_depression.controller;

import android.text.TextUtils;
import android.util.Log;

import com.example.pantera.questionnaire_depression.LoginActivity;
import com.example.pantera.questionnaire_depression.api.RestClient;
import com.example.pantera.questionnaire_depression.model.DateResponse;
import com.example.pantera.questionnaire_depression.model.Patient;
import com.example.pantera.questionnaire_depression.utils.SessionManager;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Pantera on 2017-05-22.
 */

public class LoginController {

    private static String TAG = LoginController.class.getSimpleName();
    private LoginActivity loginActivity;
    private SessionManager sessionManager;
    private RestClient restClient;
    private Call<Patient> patientCall;

    public LoginController(RestClient restClient, SessionManager sessionManager) {
        this.restClient = restClient;
        this.sessionManager = sessionManager;
    }

    public void onInit(LoginActivity loginActivity){
        this.loginActivity = loginActivity;
    }

    public void onStop(){
        loginActivity = null;
    }

    public void login(final String login, final String password){

        if(patientCall == null){
            loginActivity.showProgress(true);
            patientCall = restClient.get().login(login, password);
            patientCall.enqueue(new Callback<Patient>() {
                @Override
                public void onResponse(Call<Patient> call, Response<Patient> response) {
                    patientCall = null;
                    loginActivity.showProgress(false);
                    if(response.isSuccessful()){
                        String setCookie = response.headers().get("Set-Cookie");
                        String stringPattern = "([A-Z]*\\=[0-9A-Z]*)";
                        Pattern word = Pattern.compile(stringPattern);
                        Matcher matcher = word.matcher(setCookie);
                        String cookieValue = "";
                        if (matcher.find()) {
                            cookieValue = matcher.group(1);
                        }
                        Patient patient = response.body();

                        sessionManager.createLoginSession(patient, cookieValue);
                        saveRefreshTokenFireBase(patient.getUser().getId());
                        saveLastDateSendQuestion(patient.getId());
                        loginActivity.loginOk(response.body());
                    }else{
                        loginActivity.showError(response.code());
                    }
                }

                @Override
                public void onFailure(Call<Patient> call, Throwable t) {
                    patientCall = null;
                    if(loginActivity != null){
                        loginActivity.showProgress(false);
                        loginActivity.showError(t.getLocalizedMessage());
                        Log.d(TAG,t.getLocalizedMessage());
                    }
                }
            });
        }
    }


    public void saveLastDateSendQuestion(int patientId){
        Call<DateResponse> dateCall = restClient.get().getDateLastSendAnswer(patientId);
        dateCall.enqueue(new Callback<DateResponse>() {
            @Override
            public void onResponse(Call<DateResponse> call, Response<DateResponse> response) {
                if(response.isSuccessful()){
                    String dateNotify = response.body().getDate();
                    sessionManager.saveLastSendQuestion(dateNotify);
                }else {
                    sessionManager.saveLastSendQuestion("");
                }
            }

            @Override
            public void onFailure(Call<DateResponse> call, Throwable t) {
                sessionManager.saveLastSendQuestion("");
            }
        });
    }

    private void saveRefreshTokenFireBase(int id){
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG+"SEND TOKEN", refreshedToken == null ? "nie ma tokena google": refreshedToken);
        //int id = sessionManager.getUserDetails().getUser().getId();
        if (!TextUtils.isEmpty(refreshedToken)) {
            Call<ResponseBody> call = restClient.get().updateFirebaseToken(id, refreshedToken);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()){

                        Log.d(TAG+"SEND TOKEN", String.valueOf(response.code()));
                    }else {
                        Log.d(TAG+"SEND TOKEN", String.valueOf(response.code()));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d(TAG+"SEND TOKEN", t.getMessage());
                }
            });
        }
    }

    public void updateUserData() {
        int idUser = sessionManager.getIdUser();
        if(idUser != -1){
            Call<Patient> call = restClient.get().getDetalisPatient(idUser);
            call.enqueue(new Callback<Patient>() {
                @Override
                public void onResponse(Call<Patient> call, Response<Patient> response) {
                    if (response.isSuccessful()) {
                        Patient p = response.body();
                        sessionManager.createLoginSession(p, sessionManager.getCookieValue());
                        //checkLogin();
                    }
                }

                @Override
                public void onFailure(Call<Patient> call, Throwable t) {
                    //ServerConnectionLost.returnToLoginActivity((Activity) mContext);
                    Log.d("SESSION", t.toString());
                }
            });
        }
    }

}
