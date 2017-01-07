package com.example.pantera.questionnaire_depression.api;

import com.example.pantera.questionnaire_depression.model.Answer;
import com.example.pantera.questionnaire_depression.model.Doctor;
import com.example.pantera.questionnaire_depression.model.Patient;
import com.example.pantera.questionnaire_depression.model.Question;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Pantera on 2016-12-22.
 */

public interface RestApi {
    @FormUrlEncoded
    @POST("/login/loginJson")
    Call<Patient> login(@Field("j_username") String username, @Field("j_password") String password);

    @Headers({
            "Content-Type: application/json"
    })
    @GET("/rest/questionsservice/getallquestionsbytype/{type}")
    Call<List<Question>> questions(@Path("type") String type);

    @GET("/rest/answerservice/getanswersbypatient/{id}")
    Call<List<Answer>> getAnswers(@Path("id") int id);

    @GET("/rest/userservice/getdoctorbyid/{id}")
    Call<Doctor> getInformationAboutDoctor(@Path("id") int id);

    @GET("/rest/answerservice/getdatelastsendquestionnaire/{patientId}")
    Call<Date> getDateLastSendAnswer(@Path("patientId") int patientId);
    @Headers({
            "Content-Type: application/json"
    })
    @GET("/w/api.php?format=json&action=query&prop=extracts|info&inprop=url&exintro=&explaintext=&titles=Zaburzenia_depresyjne")
    Call<ResponseBody> getWikiDefinition();

    @Headers({
            "Content-Type: application/json"
    })
    @POST("/rest/answerservice/sendanswer")
    Call<ResponseBody> sendAnswer(@Body JSONObject answer);
}
