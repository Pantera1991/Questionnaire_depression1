package com.example.pantera.questionnaire_depression.controller;

import android.util.Log;

import com.example.pantera.questionnaire_depression.QuestionActivity;
import com.example.pantera.questionnaire_depression.api.RestClient;
import com.example.pantera.questionnaire_depression.model.Question;
import com.example.pantera.questionnaire_depression.model.Questionnaire;
import com.example.pantera.questionnaire_depression.utils.SessionManager;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Pantera on 2017-05-24.
 */

public class QuestionController {
    private SessionManager sessionManager;
    private RestClient restClient;
    private QuestionActivity questionActivity;

    public QuestionController(RestClient restClient, SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.restClient = restClient;
    }

    public void onInit(QuestionActivity questionActivity) {
        this.questionActivity = questionActivity;
    }

    public void onStop() {
        questionActivity = null;
    }

    public void loadQuestions() {
        questionActivity.showProgress(true);
        Call<List<Question>> call = restClient.get().questions("becka");

        call.enqueue(new Callback<List<Question>>() {

            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                Log.d("status question", String.valueOf(response.code()));
                if (questionActivity != null) {
                    if (response.isSuccessful()) {
                        questionActivity.loadQuestionsSuccess(response.body());
                        questionActivity.showProgress(false);
                    } else {
                        questionActivity.showProgress(false);
                        questionActivity.showError("Wystaąpił problem z załadowaniem pytań");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                if (questionActivity != null) {
                    questionActivity.showProgress(false);
                    Log.d("questions error ", t.getMessage());
                    //ServerConnectionLost.returnToLoginActivity(questionActivity);
                }
            }
        });
    }

    public void sendQuestionnaire(List<Integer> listAnswers, final float points) {
        questionActivity.showProgressDialog();
        String patientId = String.valueOf(sessionManager.getUserDetails().getId());

        Questionnaire questionnaire = new Questionnaire(patientId, listAnswers);

        Call<ResponseBody> call = restClient.get().sendAnswer(questionnaire);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (questionActivity != null) {
                        questionActivity.hideProgressDialog();
                        try {
                            int answerId = Integer.parseInt(response.body().string());
                            questionActivity.successSendQuestionnaire(answerId, points);
                        } catch (IOException e) {
                            questionActivity.showError(response.message());
                        }
                    }

                } else {
                    if (questionActivity != null) {
                        questionActivity.hideProgressDialog();
                        questionActivity.showError("Nie można wysłać ankiety");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (questionActivity != null) {
                    questionActivity.hideProgressDialog();
                    questionActivity.showError("Wystaąpił problem z załadowaniem pytań");
                }
            }
        });

    }
}
