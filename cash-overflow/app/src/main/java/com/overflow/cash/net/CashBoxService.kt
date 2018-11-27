package com.overflow.cash.net

import com.overflow.libs.core.Data
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.*

interface CashBoxService {
    @Headers("Content-Type:application/json")
    @GET("/cash/cashbox/list")
    fun getCashboxs(@QueryMap input: Data): Single<Data>
    
}
