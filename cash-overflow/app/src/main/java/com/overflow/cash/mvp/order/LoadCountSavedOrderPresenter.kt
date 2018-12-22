package com.overflow.cash.mvp.order

import android.content.Context
import android.content.SharedPreferences
import com.overflow.cash.activity.Constant
import com.overflow.cash.net.API
import com.overflow.cash.net.OrderService
import com.overflow.cash.net.RxUtils
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import io.reactivex.disposables.CompositeDisposable

class LoadCountSavedOrderPresenter(private val context: Context, private val preferences: SharedPreferences, private val translations: Translations, private val service: OrderService, private val disposable: CompositeDisposable):LoadCountSavedOrderContract.Presenter {
    lateinit var view: LoadCountSavedOrderContract.View
    override fun loadSavedOrder() {
        val outlet = Data(preferences.getString("outlet", "{}"))
        if(API.isConnected(context)){
            this.disposable.add(this.service.countOrderSaved(outlet.getLong("id")).retry(3).compose(RxUtils.applySingleAsync()).subscribe({ response ->
                if(API.ok(response)){
                    val payload = API.payload(response)
                    this.view.onSavedOrderLoaded(payload)
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

    override fun attach(view: LoadCountSavedOrderContract.View) {
        this.view = view
        this.loadSavedOrder()
    }

    override fun detach() {
        disposable.clear()
    }

}