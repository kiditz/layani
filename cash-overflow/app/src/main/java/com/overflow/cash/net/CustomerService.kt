package com.overflow.cash.net

import com.overflow.libs.core.Data
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.*

interface CustomerService {
    @Headers("Content-Type:application/json")
    @GET("/cash/customer/list")
    fun getCustomers(@QueryMap input: Data): Single<Data>

    @Headers("Content-Type:application/json")
    @PUT("/cash/customer/edit")
    fun editCustomer(@Body input: Data): Single<Data>
    
}
