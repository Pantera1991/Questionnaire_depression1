package com.example.pantera.questionnaire_depression.api;

import android.content.Context;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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
        //SessionManager sessionManager = new SessionManager(mContext);
        //final String cookie = sessionManager.getCookieValue();

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addNetworkInterceptor(httpLoggingInterceptor);
//        httpClient.interceptors().add(new Interceptor() {
//            @Override
//            public Response intercept(Interceptor.Chain chain) throws IOException {
//                Request original = chain.request();
//
//                // Request customization: add request headers
//                Request.Builder requestBuilder = original.newBuilder()
//                        .header("Accept", "application/json")
//                        .method(original.method(), original.body());
//
//                if(cookie != null){
//                    final String[] splitCookie = cookie.split("=");
//                    requestBuilder.addHeader(splitCookie[0], splitCookie[1]);
//                }
//
//                Request request = requestBuilder.build();
//                return chain.proceed(request);
//            }
//        });

        OkHttpClient client = httpClient
                .build();

        String url = "http://10.0.0.18:3000/";
        //String url = "http://192.168.1.101:3000/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return  retrofit.create(RestApi.class);
    }
}
