package com.example.pantera.questionnaire_depression.controller;

import com.example.pantera.questionnaire_depression.api.WikiClient;
import com.example.pantera.questionnaire_depression.fragment.InformationFragment;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Pantera on 2017-05-26.
 */

public class InformationController {

    private WikiClient wikiClient;
    private InformationFragment informationFragment;

    public InformationController(WikiClient wikiClient) {
        this.wikiClient = wikiClient;
    }

    public void onInit(InformationFragment informationFragment) {
        this.informationFragment = informationFragment;
    }

    public void onStop() {
        informationFragment = null;
    }

    public void initWiki() {
        informationFragment.showProgress(true);

        wikiClient.get().getWikiDefinition().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    if (informationFragment != null) {
                        String jsonResponse = null;
                        try {
                            jsonResponse = response.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        informationFragment.successLoadWiki(jsonResponse);
                        informationFragment.showProgress(false);
                    }

                } else {
                    if (informationFragment != null) {
                        informationFragment.showError(response.message());
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (informationFragment != null) {
                    informationFragment.showError(t.getLocalizedMessage());
                }
            }
        });
    }
}
