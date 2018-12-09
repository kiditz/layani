package com.overflow.cash.net

import com.overflow.libs.core.Data
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.*

interface OutletService {
    @Headers("Content-Type:application/json")
    @GET("/cash/outlet/find")
    fun findOutlet(@QueryMap input: Data): Single<Data>
    
}
