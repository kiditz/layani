package com.overflow.cash.net

import com.overflow.libs.core.Data
import io.reactivex.Single
import retrofit2.http.*

interface OrderService {
    @Headers("Content-Type:application/json")
    @POST("/cash/order/add")
    fun addOrder(@Body data:Data): Single<Data>

    @Headers("Content-Type:application/json")
    @POST("/cash/order/refund")
    fun refund(@Body data:Data): Single<Data>

    @Headers("Content-Type:application/json")
    @GET("/cash/order/items")
    fun getOrderItems(@Query("order_code") orderId:String):Single<Data>

    @Headers("Content-Type:application/json")
    @GET("/cash/discount/find")
    fun findDiscount(@Query("product_id") productId:Long, @Query("discount_when") quantity:Long):Single<Data>

    @Headers("Content-Type:application/json")
    @GET("/cash/account_receiveable/list")
    fun getAccountReceiveable(@QueryMap data: Data):Single<Data>

    @Headers("Content-Type:application/json")
    @GET("/cash/account_receiveable/detail/list")
    fun getAccountReceiveableDetail(@QueryMap data: Data):Single<Data>

    @Headers("Content-Type:application/json")
    @PUT("/cash/account_receiveable/pay")
    fun payAccountReceiveable(@Body data:Data): Single<Data>

    @Headers("Content-Type:application/json")
    @GET("/cash/order/chart")
    fun getOrderChart(@QueryMap data: Data):Single<Data>

    @Headers("Content-Type:application/json")
    @GET("/cash/order/profit_chart")
    fun getProfitChart(@QueryMap data: Data):Single<Data>

    @Headers("Content-Type:application/json")
    @GET("/cash/order/income_chart")
    fun getIncomeChart(@QueryMap data: Data):Single<Data>

    @Headers("Content-Type:application/json")
    @GET("/cash/order/dashboard_header")
    fun getDashboardHeader(@QueryMap data: Data):Single<Data>

    @GET("/cash/order/list")
    fun getOrderList(@QueryMap data: Data):Single<Data>

    @Headers("Content-Type:application/json")
    @GET("/cash/order/top_product")
    fun getTopProduct(@QueryMap data: Data):Single<Data>

    @Headers("Content-Type:application/json")
    @GET("/cash/account_receiveable/in_age")
    fun getAccountReceiveableInAge(@QueryMap data: Data):Single<Data>

    @Headers("Content-Type:application/json")
    @GET("/cash/account_receiveable/out_of_age")
    fun getAccountReceiveableOutOfAge(@QueryMap data: Data):Single<Data>



}
