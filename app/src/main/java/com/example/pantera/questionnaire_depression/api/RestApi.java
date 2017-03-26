package com.example.pantera.questionnaire_depression.api;

import com.example.pantera.questionnaire_depression.model.Answer;
import com.example.pantera.questionnaire_depression.model.DateResponse;
import com.example.pantera.questionnaire_depression.model.Doctor;
import com.example.pantera.questionnaire_depression.model.Patient;
import com.example.pantera.questionnaire_depression.model.Question;

import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Pantera on 2016-12-22.
 */

public interface RestApi {
    @Headers({
            "Content-Type: application/x-www-form-urlencoded"
    })
    @FormUrlEncoded
    @POST("/loginJson")
    Call<Patient> login(@Field(value="email", encoded = true) String username, @Field(value="password", encoded = true) String password);

    @Headers({
            "Content-Type: application/json"
    })
    @GET("/questions/questionlist/{type}")
    Call<List<Question>> questions(@Path("type") String type);

    @GET("/patient/answerlist/{id}")
    Call<List<Answer>> getAnswers(@Path("id") int id);

    @GET("/patient/getdoctorbyid/{id}")
    Call<Doctor> getInformationAboutDoctor(@Path("id") int id);

    @GET("/questions/getdatelastsendquestionnaire/{patientId}")
    Call<DateResponse> getDateLastSendAnswer(@Path("patientId") int patientId);
    @Headers({
            "Content-Type: application/json"
    })
    @GET("/w/api.php?format=json&action=query&prop=extracts|info&inprop=url&exintro=&explaintext=&titles=Zaburzenia_depresyjne")
    Call<ResponseBody> getWikiDefinition();

    @Headers({
            "Content-Type: application/json"
    })
    @POST("/patient/sendanswer")
    Call<ResponseBody> sendAnswer(@Body JSONObject answer);

    @PUT("/patient/firebase_token/{idUser}/{token}")
    Call<ResponseBody> updateFirebaseToken(@Path("idUser") int idUser, @Path("token") String token);

    @GET("/patient/detalis/{idUser}")
    Call<Patient> getDetalisPatient(@Path("idUser") int idUser);
}
