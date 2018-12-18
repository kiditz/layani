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

class LoadOrderItemPresenter(private val context:Context, private val preferences: SharedPreferences, private val translations: Translations, private val orderService: OrderService, private val disposable: CompositeDisposable) :LoadOrderItemContract.Presenter{

    lateinit var view:LoadOrderItemContract.View

    override fun attach(view: LoadOrderItemContract.View) {
        this.view = view
    }

    override fun detach() {
        disposable.clear()
    }

    fun getSize():Int= preferences.getInt(Constant.MAX_PAGE, API.SIZE)


    override fun loadItem(orderCode:String) {
        if(API.isConnected(context)){
            this.disposable.add(this.orderService.getOrderItems(orderCode).retry(3).compose(RxUtils.applySingleAsync()).subscribe({
                if(API.ok(it)){
                    val payloads = API.payloads(it)
                    if(payloads.isEmpty()){
                        this.view.showEmpty()
                    }else{
                        this.view.onOrderItemsLoaded(payloads)
                    }

                }else{
                    this.view.showNoOk(translations.get(API.getError(it)))
                }
            }, {error ->
                this.view.showError(error)
            }))
        }else{
            this.view.showNotConnected(this.translations.get(Constant.TranslationsKey.NO_INTERNET))
        }
    }


}