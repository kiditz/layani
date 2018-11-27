package com.overflow.cash.net

import com.overflow.libs.core.Data
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.*

interface MerchantService {
    @Headers("Content-Type:application/json")
    @GET("/cash/merchant/find")
    fun findMerchant(@QueryMap input: Data): Single<Data>
    
}
