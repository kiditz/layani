package com.overflow.cash.net

import com.overflow.libs.core.Data
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationService {
    @Headers("Content-Type:application/json")
    @POST("/cash/notification/token/add")
    fun saveToken(@Body input: Data): Single<Data>
}