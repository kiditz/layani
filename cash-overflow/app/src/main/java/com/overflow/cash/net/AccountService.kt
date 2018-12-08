package com.overflow.cash.net

import com.overflow.libs.core.Data
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.*

interface AccountService {
    @POST("/uua/oauth/token")
    fun login(@Header("Authorization") authentication: String, @QueryMap input: Data): Call<Data>
    @POST("/uua/oauth/token")
    fun loginAsync(@Header("Authorization") authentication: String, @QueryMap input: Data): Single<Data>
    @Headers("Content-Type:application/json")
    @POST("/cash/merchant/add")
    fun register(@Body input: Data): Single<Data>

    @Headers("Content-Type:application/json")
    @GET("/cash/merchant/find")
    fun findMerchant(@Header("Authorization") authentication: String, @QueryMap input: Data): Single<Data>
    
}
