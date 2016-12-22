package com.example.pantera.questionnaire_depression.api;

import com.example.pantera.questionnaire_depression.model.Patient;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Pantera on 2016-12-22.
 */

public interface RestApi {
    @FormUrlEncoded
    @POST("/login/loginJson")
    Call<Patient> login(@Field("j_username") String username, @Field("j_password") String password);
}
