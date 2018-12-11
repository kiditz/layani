package com.overflow.cash.mvp.receiveable

import android.content.Context
import android.content.SharedPreferences
import com.overflow.cash.activity.Constant
import com.overflow.cash.net.API
import com.overflow.cash.net.CashBoxService
import com.overflow.cash.net.OrderService
import com.overflow.cash.net.RxUtils
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import io.reactivex.disposables.CompositeDisposable

class AccountReceiveablePaymentPresenter(private val context:Context, private val preferences: SharedPreferences, private val translations: Translations, private val service: CashBoxService, private val orderService:OrderService, private val disposable: CompositeDisposable) :AccountReceiveablePaymentContract.Presenter {


    lateinit var view:AccountReceiveablePaymentContract.View

    override fun loadCashBox() {
        val outlet = Data(preferences.getString("outlet", "{}"))
        val input = Data()
        input["page"] = API.MIN_PAGE
        input["size"] = getSize()
        input["outlet_id"] = outlet.getLong("id")
        if(API.isConnected(context)){
            this.disposable.add(this.service.getCashboxs(input).compose(RxUtils.applySingleAsync()).subscribe({ response ->
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

    override fun payAccount(data: Data) {
        val outlet = Data(preferences.getString("outlet", "{}"))
        data["outlet_id"] = outlet.getLong("id")

        if(API.isConnected(context)){
            this.disposable.add(this.orderService.payAccountReceiveable(data).retry(3).compose(RxUtils.applySingleAsync()).subscribe({ response ->
                if(API.ok(response)){
                    val payload = API.payload(response)
                    if(payload.isEmpty()){
                        this.view.showEmpty()
                    }else{
                        this.view.onPaymentSuccess(payload)
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

    override fun attach(view: AccountReceiveablePaymentContract.View) {
        this.view = view
        this.loadCashBox()
    }

    override fun detach() {
        disposable.clear()
    }

    private fun getSize():Int{
        return preferences.getInt(Constant.MAX_PAGE, API.SIZE)
    }

}