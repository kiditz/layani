package com.overflow.cash.net

import com.overflow.libs.core.Data
import io.reactivex.Single
import retrofit2.http.*

interface PulsaService {
    @Headers("Content-Type:application/json")
    @GET("/pulsa/product_layani/products")
    fun getProducts(@QueryMap input: Data): Single<Data>

    @Headers("Content-Type:application/json")
    @GET("/pulsa/product_layani/categories")
    fun getCategories(): Single<Data>

    @Headers("Content-Type:application/json")
    @GET("/pulsa/product_layani/providers")
    fun getProviders(@Query("category_id") categoryId:Long): Single<Data>

    @Headers("Content-Type:application/json")
    @GET("/pulsa/product_layani/products_by_provider")
    fun getProductsByProvider(@QueryMap input: Data): Single<Data>

    @Headers("Content-Type:application/json")
    @POST("/pulsa/order_pulsa/add")
    fun sendOrder(@Body data:Data): Single<Data>

}

