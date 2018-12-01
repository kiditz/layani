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

class TopProductChartPresenter(private val context: Context, private val preferences: SharedPreferences, private val translations: Translations, private val orderService:OrderService, private val disposable: CompositeDisposable) : TopProductChartContract.Presenter {
    lateinit var view : TopProductChartContract.View
    lateinit var merchant:Data

    override fun showChart() {
        val data=Data()
        data["merchant_id"] = this.merchant.getLong("id")
        data["page"] = 1L
        data["size"] = 5L
        if(API.isConnected(context)){
            this.disposable.add(this.orderService.getTopProduct(data).retry(3).compose(RxUtils.applySingleAsync()).subscribe({ response ->
                if(API.ok(response)){
                    this.view.onChartLoaded(API.payloads(response))

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

    override fun attach(view: TopProductChartContract.View) {
        this.view = view
        this.merchant = Data(preferences.getString("merchant", "{}"))
        this.showChart()
    }

    override fun detach() {
        this.disposable.clear()
    }
}