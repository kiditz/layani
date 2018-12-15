package com.overflow.cash.mvp.order

import android.content.Context
import android.content.SharedPreferences
import com.overflow.cash.activity.Constant
import com.overflow.cash.net.API
import com.overflow.cash.net.CashBoxService
import com.overflow.cash.net.RxUtils
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import io.reactivex.disposables.CompositeDisposable

class LoadCashboxPresenter(private val context: Context, private val preferences: SharedPreferences, private val translations: Translations, private val service: CashBoxService, private val disposable: CompositeDisposable) : LoadCashboxContract.Presenter{
    

    lateinit var view:LoadCashboxContract.View


    override fun attach(view: LoadCashboxContract.View) {
        this.view = view
        this.loadCashBox()
    }

    override fun loadCashBox() {
        val outlet = Data(preferences.getString("outlet", "{}"))
        val input = Data()
        input["page"] = API.MIN_PAGE
        input["size"] = getSize()
        input["outlet_id"] = outlet.getLong("id")
        if(API.isConnected(context)){
            this.disposable.add(this.service.getCashboxs(input).retry(3).compose(RxUtils.applySingleAsync()).subscribe({ response ->
                if(API.ok(response)){
                    val payloads = API.payloads(response)
                    if(payloads.isEmpty()){
                        this.view.showEmpty()
                    }else{
                        this.view.onCashboxLoaded(payloads)
                    }

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

    private fun getSize():Int = preferences.getInt(Constant.MAX_PAGE, API.SIZE)

    override fun detach() {
        disposable.clear()
    }
}