package com.overflow.cash.mvp.receiveable

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import com.overflow.cash.Constant
import com.overflow.cash.net.API
import com.overflow.cash.net.OrderService
import com.overflow.cash.net.RxUtils
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import io.reactivex.disposables.CompositeDisposable

class AccountReceiveablePresenter(private val context:Context, private val preferences: SharedPreferences, private val translations: Translations, private val orderService: OrderService, private val disposable: CompositeDisposable) :AccountReceiveableContract.Presenter{


    lateinit var view:AccountReceiveableContract.View
    var lastPage:Boolean = true
    var loading:Boolean = true
    var outlet:Data = Data()
    init {
         outlet = Data(preferences.getString("outlet", "{}"))
    }
    override fun attach(view: AccountReceiveableContract.View) {
        this.view = view
        loadReceiveable(API.MIN_PAGE, Constant.TEXT_EMPTY)
    }

    override fun detach() {
        disposable.clear()
    }

    fun getSize():Int{
        return preferences.getInt(Constant.MAX_PAGE, API.SIZE)
    }

    override fun loadReceiveable(page:Int, name:String) {

        val input = Data()
        input["page"] = API.MIN_PAGE
        input["size"] = getSize()
        input["outlet_id"] = outlet.getLong("id")
        input["name"] = name
        if(API.isConnected(context)){
            lastPage = false
            loading = true
            this.disposable.add(this.orderService.getAccountReceiveable(input).retry(3).compose(RxUtils.applySingleAsync()).subscribe({ response ->
                if(API.ok(response)){
                    if (response.containsKey("total_pages")) {
                        val total = response.getInt("total_pages")
                        if (page == total) {
                            this.lastPage = true
                        }
                    }
                    val payloads = API.payloads(response)
                    if(payloads.isEmpty()){
                        this.view.showEmpty()
                    }else{
                        this.view.onReceiveableLoaded(payloads)
                    }

                }else{
                    this.view.showNoOk(translations.get(API.getError(response)))
                }
                loading = false
            }, {error ->
                this.view.showError(error)
                loading = false
            }))
        }else{
            this.view.showNotConnected(this.translations.get(Constant.TranslationsKey.NO_INTERNET))
        }
    }



}