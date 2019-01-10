package com.layani.pulsa.integration.model;

import org.slerp.core.Domain;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

import java.util.Map;

public interface HokkyTronik {

    @FormUrlEncoded
    @POST("/api/order")
    Call<Domain> postOrder(@Header ("api-key") String apiKey,@FieldMap Map<String, String> input);
}
