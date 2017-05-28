package com.example.pantera.questionnaire_depression.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

/**
 * Created by Pantera on 2017-05-26.
 */

public interface WikiApi {
    @Headers({
            "Content-Type: application/json"
    })
    @GET("/w/api.php?format=json&action=query&prop=extracts|info&inprop=url&exintro=&explaintext=&titles=Zaburzenia_depresyjne")
    Call<ResponseBody> getWikiDefinition();
}
