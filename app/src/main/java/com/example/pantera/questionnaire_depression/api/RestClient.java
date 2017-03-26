package com.example.pantera.questionnaire_depression.api;

import android.content.Context;

import com.example.pantera.questionnaire_depression.utils.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Pantera on 2016-12-22.
 */

public class RestClient {
    private Context mContext;

    public RestClient(Context mContext) {
        this.mContext = mContext;
    }

    public RestApi get() {
        SessionManager sessionManager = new SessionManager(mContext);
        final String cookie = sessionManager.getCookieValue();


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Accept", "application/json")
                        .method(original.method(), original.body());

                if(cookie != null){
                    final String[] splitCookie = cookie.split("=");
                    requestBuilder.addHeader(splitCookie[0], splitCookie[1]);
                }

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        OkHttpClient client = httpClient
                .build();

        String url = "http://172.17.179.199:3000/";
        //String url = "http://10.0.0.10:3000/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                //.client(client)
                .build();
        return  retrofit.create(RestApi.class);
    }
}
