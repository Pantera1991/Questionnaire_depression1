package com.example.pantera.questionnaire_depression.controller;

import android.util.Log;

import com.example.pantera.questionnaire_depression.api.RestClient;
import com.example.pantera.questionnaire_depression.fragment.QuestionnaireFragment;
import com.example.pantera.questionnaire_depression.model.Answer;
import com.example.pantera.questionnaire_depression.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Pantera on 2017-05-24.
 */

public class QuestionnaireController {
    private QuestionnaireFragment questionnaireFragment;
    private RestClient restClient;
    private SessionManager sessionManager;

    public QuestionnaireController(RestClient restClient, SessionManager sessionManager) {
        this.restClient = restClient;
        this.sessionManager = sessionManager;
    }

    public void onInit(QuestionnaireFragment questionnaireFragment){
        this.questionnaireFragment = questionnaireFragment;
    }

    public void onStop(){
        this.questionnaireFragment = null;
    }

    public void loadQuestionnaire(){
        questionnaireFragment.showProgress(true);
        int id = sessionManager.getIdPatient();
        Call<List<Answer>> call = restClient.get().getAnswers(id);

        call.enqueue(new Callback<List<Answer>>() {
            @Override
            public void onResponse(Call<List<Answer>> call, Response<List<Answer>> response) {
                Log.d("status", String.valueOf(response.code()));
                if(response.isSuccessful()){
                    questionnaireFragment.successLoadQuestionnaire(response.body());
                    questionnaireFragment.setupView();
                }else {
                    questionnaireFragment.showError("Nie można załądować listy ankiet");
                }
                questionnaireFragment.showProgress(false);
            }

            @Override
            public void onFailure(Call<List<Answer>> call, Throwable t) {
                questionnaireFragment.showError(t.getLocalizedMessage());
                //ServerConnectionLost.returnToLoginActivity(getActivity());
                questionnaireFragment.showProgress(false);

            }
        });
    }
}
