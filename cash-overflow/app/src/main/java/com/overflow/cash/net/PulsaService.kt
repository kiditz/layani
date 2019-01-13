package com.overflow.cash.net

import com.overflow.libs.core.Data
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.QueryMap

interface PulsaService {
    @Headers("Content-Type:application/json")
    @GET("/pulsa/product_layani/products")
    fun getProducts(@QueryMap input: Data): Single<Data>

    @Headers("Content-Type:application/json")
    @GET("/pulsa/product_layani/categories")
    fun getCategories(): Single<Data>
}

