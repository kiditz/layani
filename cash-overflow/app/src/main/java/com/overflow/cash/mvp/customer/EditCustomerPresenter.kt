package com.overflow.cash.mvp.customer

import android.content.Context
import android.content.SharedPreferences
import com.overflow.cash.activity.Constant
import com.overflow.cash.net.API
import com.overflow.cash.net.CustomerService
import com.overflow.cash.net.RxUtils
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import io.reactivex.disposables.CompositeDisposable

class EditCustomerPresenter(private val context:Context, private val preferences: SharedPreferences, private val translations: Translations, private val customerService: CustomerService, private val disposable: CompositeDisposable) :EditCustomerContract.Presenter{

    lateinit var view:EditCustomerContract.View
    var lastPage:Boolean = true
    var loading:Boolean = true
    var outlet:Data = Data()
    init {
         outlet = Data(preferences.getString("outlet", "{}"))
    }

    override fun attach(view: EditCustomerContract.View) {
        this.view = view
    }

    override fun detach() {
        disposable.clear()
    }

    fun getSize():Int= preferences.getInt(Constant.MAX_PAGE, API.SIZE)


    override fun editCustomer(customer: Data) {
        customer["outlet_id"] = outlet.getLong("id")
        if(API.isConnected(context)){
            this.disposable.add(this.customerService.editCustomer(customer).retry(3).compose(RxUtils.applySingleAsync()).subscribe({ response ->
                if(API.ok(response)){
                    this.view.onCustomerEdited(API.payload(response))
                }else{
                    this.view.onCustomerEditShowNoOk(translations.get(API.getError(response)))
                }
            }, {error ->
                this.view.showError(error)
            }))
        }else{
            this.view.showNotConnected(this.translations.get(Constant.TranslationsKey.NO_INTERNET))
        }
    }

}