package com.layani.pulsa.integration;

import com.layani.pulsa.integration.utils.HttpLoggingInterceptor;
import okhttp3.OkHttpClient;
import org.slerp.core.Domain;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;


public class RetrofitClient {
    public static Retrofit retrofit(String url, Domain payload){
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        interceptor.setPayload(payload);
        httpClient.addInterceptor(interceptor);
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(httpClient.build())
                .build();
    }
}
