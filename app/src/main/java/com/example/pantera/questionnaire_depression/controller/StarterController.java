package com.example.pantera.questionnaire_depression.controller;

import android.util.Log;

import com.example.pantera.questionnaire_depression.StarterActivity;
import com.example.pantera.questionnaire_depression.api.RestClient;
import com.example.pantera.questionnaire_depression.model.Question;
import com.example.pantera.questionnaire_depression.model.Questionnaire;
import com.example.pantera.questionnaire_depression.utils.ServerConnectionLost;
import com.example.pantera.questionnaire_depression.utils.SessionManager;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Pantera on 2017-05-23.
 */

public class StarterController {
    private SessionManager sessionManager;
    private RestClient restClient;
    private StarterActivity starterActivity;
    private StarterActivity.StartQuestionnaireFragment startQuestionnaireFragment;

    public StarterController(RestClient restClient, SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.restClient = restClient;
    }

    public void onInit(StarterActivity starterActivity) {
        this.starterActivity = starterActivity;
    }

    public void onInit(StarterActivity.StartQuestionnaireFragment startQuestionnaireFragment) {
        this.startQuestionnaireFragment = startQuestionnaireFragment;
    }


    public void onStop() {
        starterActivity = null;
        startQuestionnaireFragment = null;
    }

    //starterActivity
    public void sendStarterTest(final List<Integer> listAnswers) {
        starterActivity.showProgressDialog();
        String patientId = String.valueOf(sessionManager.getUserDetails().getId());
        Questionnaire questionnaire = new Questionnaire(patientId, listAnswers);

        Call<ResponseBody> call = restClient.get().sendAnswer(questionnaire);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(starterActivity != null) {
                    if (response.isSuccessful()) {
                        sessionManager.updateValue(SessionManager.KEY_STARTER_TEST, true);
                        starterActivity.sendOk();
                    } else {
                        starterActivity.showError(response.message());
                    }
                    starterActivity.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (starterActivity != null) {
                    starterActivity.hideProgressDialog();
                    starterActivity.showError(t.getLocalizedMessage());
                }
            }
        });
    }

    //StartQuestionnaireFragment
    public void loadStartQuestions() {
        Call<List<Question>> call = restClient.get().questions("hads");
        call.enqueue(new Callback<List<Question>>() {

            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {

                if (response.isSuccessful()) {
                    if (startQuestionnaireFragment != null) {
                        startQuestionnaireFragment.successLoadData(response.body());
                    }
                } else {
                    if (startQuestionnaireFragment != null) {
                        startQuestionnaireFragment.showError("Wystąpił problem nie można załadować pytań ankiety");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                Log.d("questions error ", t.getMessage());
                ServerConnectionLost.returnToLoginActivity(startQuestionnaireFragment.getActivity());
            }
        });

    }

}
