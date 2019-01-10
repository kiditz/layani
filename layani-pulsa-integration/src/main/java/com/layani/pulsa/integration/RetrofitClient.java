package com.layani.pulsa.integration;

import okhttp3.OkHttpClient;
import org.slerp.core.Domain;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClient {
    public static Retrofit retrofit(String url, Domain payload){
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        //HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        //interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //interceptor.setPayload(payload);
        //httpClient.addInterceptor(interceptor);
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
    }
}
