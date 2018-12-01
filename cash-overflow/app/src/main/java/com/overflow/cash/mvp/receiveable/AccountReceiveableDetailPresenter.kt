package com.overflow.cash.mvp.receiveable

import android.content.Context
import android.content.SharedPreferences
import com.overflow.cash.Constant
import com.overflow.cash.net.API
import com.overflow.cash.net.OrderService
import com.overflow.cash.net.RxUtils
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import io.reactivex.disposables.CompositeDisposable

class AccountReceiveableDetailPresenter(private val context:Context, private val preferences: SharedPreferences, private val translations: Translations, private val orderService: OrderService, private val disposable: CompositeDisposable) :AccountReceiveableDetailContract.Presenter {


    lateinit var view:AccountReceiveableDetailContract.View
    var lastPage:Boolean = true
    var loading:Boolean = true

    fun getSize():Int{
        return preferences.getInt(Constant.MAX_PAGE, API.SIZE)
    }

    override fun loadOrderItems(orderId: Long) {
        if(API.isConnected(context)){
            lastPage = false
            loading = true
            this.disposable.add(this.orderService.getOrderItems(orderId).retry(3).compose(RxUtils.applySingleAsync()).subscribe({ response ->
                if(API.ok(response)){
                    val payloads = API.payloads(response)
                    if(payloads.isEmpty()){
                        this.view.showEmpty()
                    }else{
                        this.view.onOrderItemsLoaded(payloads)
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

    override fun loadDetail(page: Int, customerId: Long) {

        val input = Data()
        input["page"] = API.MIN_PAGE
        input["size"] = getSize()
        input["customer_id"] = customerId
        if(API.isConnected(context)){
            lastPage = false
            loading = true
            this.disposable.add(this.orderService.getAccountReceiveableDetail(input).compose(RxUtils.applySingleAsync()).subscribe({ response ->
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
                        this.view.onDetailLoaded(payloads)
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

    override fun attach(view: AccountReceiveableDetailContract.View) {
        this.view = view
    }

    override fun detach() {
        disposable.clear()
    }

}