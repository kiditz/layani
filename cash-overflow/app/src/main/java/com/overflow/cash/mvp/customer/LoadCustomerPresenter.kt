package com.overflow.cash.mvp.customer

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.widget.RecyclerView
import com.overflow.cash.activity.Constant
import com.overflow.cash.net.*
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import io.reactivex.disposables.CompositeDisposable

class LoadCustomerPresenter(private val context:Context, private val preferences: SharedPreferences, private val translations: Translations, private val customerService: CustomerService, private val disposable: CompositeDisposable) :LoadCustomerContract.Presenter{

    lateinit var view:LoadCustomerContract.View
    var lastPage:Boolean = true
    var loading:Boolean = true
    var outlet:Data = Data()
    init {
         outlet = Data(preferences.getString("outlet", "{}"))
    }
    override fun attach(view: LoadCustomerContract.View) {
        this.view = view
        loadCustomer(API.MIN_PAGE, Constant.TEXT_EMPTY)
    }

    override fun detach() {
        disposable.clear()
    }

    fun getSize():Int= preferences.getInt(Constant.MAX_PAGE, API.SIZE)


    override fun loadCustomer(page:Int, name:String) {
        val input = Data()
        input["page"] = API.MIN_PAGE
        input["size"] = getSize()
        input["outlet_id"] = outlet.getLong("id")
        input["name"] = name
        if(API.isConnected(context)){
            lastPage = false
            loading = true
            this.disposable.add(this.customerService.getCustomers(input).retry(3).compose(RxUtils.applySingleAsync()).subscribe({ response ->
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
                        this.view.onCustomerLoaded(payloads)
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