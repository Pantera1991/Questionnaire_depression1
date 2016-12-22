package com.example.pantera.questionnaire_depression.api;

import android.content.Context;
import android.util.Log;

import com.example.pantera.questionnaire_depression.SessionManager;
import com.example.pantera.questionnaire_depression.utils.Constants;
import com.facebook.stetho.okhttp3.StethoInterceptor;

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
        Log.d("token: ",cookie);
        final String[] splitCookie = cookie.split("=");
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        //.header(splitCookie[0], splitCookie[1])
                        .header("Accept", "application/json")
                        .method(original.method(), original.body());

                if(!"null".equals(cookie)){
                    requestBuilder.addHeader(splitCookie[0], splitCookie[1]);
                }

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        OkHttpClient client = httpClient
                .addInterceptor(new StethoInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return  retrofit.create(RestApi.class);
    }
}
