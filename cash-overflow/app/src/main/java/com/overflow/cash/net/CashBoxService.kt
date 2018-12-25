package com.overflow.cash.net

import com.overflow.libs.core.Data
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.*

interface CashBoxService {
    @Headers("Content-Type:application/json")
    @GET("/cash/cashbox/summary/list")
    fun getCashboxSummary(@QueryMap input: Data): Single<Data>

    @Headers("Content-Type:application/json")
    @GET("/cash/cashbox/history/list")
    fun getCashboxHistory(@QueryMap input: Data): Single<Data>

    @Headers("Content-Type:application/json")
    @PUT("/cash/cashbox/history/edit")
    fun saveCashboxHistory(@Body input: Data): Single<Data>

    @Headers("Content-Type:application/json")
    @PUT("/cash/cashbox/summary/edit")
    fun saveCashboxSummary(@Body input: Data): Single<Data>
}

