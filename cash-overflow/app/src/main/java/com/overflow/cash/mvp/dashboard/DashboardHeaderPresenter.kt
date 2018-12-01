package com.overflow.cash.mvp.dashboard

import android.content.Context
import android.content.SharedPreferences
import com.overflow.cash.Constant
import com.overflow.cash.net.API
import com.overflow.cash.net.OrderService
import com.overflow.cash.net.RxUtils
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import io.reactivex.disposables.CompositeDisposable

class DashboardHeaderPresenter(private val context: Context, private val preferences: SharedPreferences, private val translations: Translations, private val orderService:OrderService, private val disposable: CompositeDisposable) : DashboardHeaderContract.Presenter {
    lateinit var view : DashboardHeaderContract.View
    lateinit var merchant:Data

    override fun showHeader() {
        val data=Data()
        data["merchant_id"] = this.merchant.getLong("id")
        if(API.isConnected(context)){
            this.disposable.add(this.orderService.getDashboardHeader(data).retry(3).compose(RxUtils.applySingleAsync()).subscribe({ response ->
                if(API.ok(response)){
                    this.view.onHeaderLoaded(API.payload(response))
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

    override fun attach(view: DashboardHeaderContract.View) {
        this.view = view
        this.merchant = Data(preferences.getString("merchant", "{}"))
        this.showHeader()
    }

    override fun detach() {
        this.disposable.clear()
    }
}