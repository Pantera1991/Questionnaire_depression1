package com.example.pantera.questionnaire_depression.controller;

import android.util.Log;

import com.example.pantera.questionnaire_depression.InfoAboutDoctorActivity;
import com.example.pantera.questionnaire_depression.api.RestClient;
import com.example.pantera.questionnaire_depression.model.Doctor;
import com.example.pantera.questionnaire_depression.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Pantera on 2017-05-24.
 */

public class DoctorController {
    private RestClient restClient;
    private SessionManager sessionManager;
    private InfoAboutDoctorActivity infoAboutDoctorActivity;

    public DoctorController(RestClient restClient, SessionManager sessionManager) {
        this.restClient = restClient;
        this.sessionManager = sessionManager;
    }

    public void onInit(InfoAboutDoctorActivity infoAboutDoctorActivity){
        this.infoAboutDoctorActivity = infoAboutDoctorActivity;
    }

    public void onStop(){
        infoAboutDoctorActivity = null;
    }

    public void loadDetails(){
        int patientId = sessionManager.getIdPatient();
        Call<Doctor> call = restClient.get().getInformationAboutDoctor(patientId);

        call.enqueue(new Callback<Doctor>() {
            @Override
            public void onResponse(Call<Doctor> call, Response<Doctor> response) {
                if(response.isSuccessful()){
                    Doctor doctor = response.body();
                    infoAboutDoctorActivity.successLoadDetails(doctor);
                }else {
                    infoAboutDoctorActivity.showError("Wystapił błąd nie udało sie załadować szczegółów");
                }
                infoAboutDoctorActivity.showProgress(false);
            }

            @Override
            public void onFailure(Call<Doctor> call, Throwable t) {
                Log.d("callback",call.request().toString());
                Log.d("informationError",t.getMessage());
                infoAboutDoctorActivity.showProgress(false);
                infoAboutDoctorActivity.showError(t.getLocalizedMessage());
            }
        });
    }
}
