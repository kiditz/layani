package com.overflow.cash.net

import com.overflow.libs.core.Data
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.*

interface DiscountService {
    @Headers("Content-Type:application/json")
    @GET("/cash/discount/by_quantity")
    fun findDiscountByBillAmount(@QueryMap input: Data): Single<Data>
    
}
