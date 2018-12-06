package com.overflow.cash.net

import com.overflow.libs.core.Data
import io.reactivex.Single
import retrofit2.http.*

interface ProductService {
    @GET("/cash/category/list")
    fun getCategory(@QueryMap data:Data): Single<Data>

    @GET("/cash/category/find")
    fun findCategory(@Query("id") id:Long): Single<Data>

    @Headers("Content-Type:application/json")
    @POST("/cash/category/add")
    fun addCategory(@Body data:Data): Single<Data>

    @Headers("Content-Type:application/json")
    @PUT("/cash/category/edit")
    fun editCategory(@Body data:Data): Single<Data>

    @Headers("Content-Type:application/json")
    @DELETE("/cash/category/delete")
    fun deleteCategory(@Query("id") id:Long): Single<Data>

    @Headers("Content-Type:application/json")
    @POST("/cash/product/add")
    fun addProduct(@Body data:Data): Single<Data>

    @Headers("Content-Type:application/json")
    @PUT("/cash/product/edit")
    fun editProduct(@Body data:Data): Single<Data>

    @Headers("Content-Type:application/json")
    @PUT("/cash/product/edit_by_code")
    fun editProductByCode(@Body data:Data): Single<Data>

    @GET("/cash/product/list")
    fun getProduct(@QueryMap data:Data): Single<Data>

    @Headers("Content-Type:application/json")
    @POST("/cash/stock/add")
    fun addStock(@Body data:Data): Single<Data>

    @Headers("Content-Type:application/json")
    @POST("/cash/discount/add")
    fun addDiscount(@Body data:Data): Single<Data>
}