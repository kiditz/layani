package com.overflow.cash.mvp.chart

import android.content.Context
import android.content.SharedPreferences
import com.overflow.cash.Constant
import com.overflow.cash.net.API
import com.overflow.cash.net.OrderService
import com.overflow.cash.net.RxUtils
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import io.reactivex.disposables.CompositeDisposable

class OrderChartPresenter(private val context: Context,private val preferences: SharedPreferences, private val translations: Translations, private val orderService:OrderService, private val disposable: CompositeDisposable) : OrderChartContract.Presenter {
    lateinit var view : OrderChartContract.View
    lateinit var outlet:Data

    override fun showChart(period: String) {
        val data=Data()
        data["outlet_id"] = this.outlet.getLong("id")
        data["period"] = period
        if(API.isConnected(context)){
            this.disposable.add(this.orderService.getOrderChart(data).retry(3).compose(RxUtils.applySingleAsync()).subscribe({ response ->
                if(API.ok(response)){
                    this.view.onChartLoaded(API.payload(response))
                }else{
                    this.view.showNoOk(translations.get(API.getError(response)))
                }
            }, {error ->
                this.view.showError(error)
            }))
        }else{
            this.view.showNotConnected(this.translations.get(Constant.TranslationsKey.NO_INTERNET))
        }
    }

    override fun attach(view: OrderChartContract.View) {
        this.view = view
        this.outlet = Data(preferences.getString("outlet", "{}"))
    }

    override fun detach() {
        this.disposable.clear()
    }
}