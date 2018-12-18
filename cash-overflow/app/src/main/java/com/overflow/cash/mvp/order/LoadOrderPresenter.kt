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
import timber.log.Timber

class LoadOrderPresenter(private val context:Context, private val preferences: SharedPreferences, private val translations: Translations, private val orderService: OrderService, private val disposable: CompositeDisposable) :LoadOrderContract.Presenter{

    lateinit var view:LoadOrderContract.View
    var lastPage:Boolean = true
    var loading:Boolean = true
    var outlet:Data = Data()
    init {
         outlet = Data(preferences.getString("outlet", "{}"))
    }
    override fun attach(view: LoadOrderContract.View) {
        this.view = view
    }

    override fun detach() {
        disposable.clear()
    }

    fun getSize():Int= preferences.getInt(Constant.MAX_PAGE, API.SIZE)


    override fun loadOrder(page:Int, query:String,  status: String, exclude:Boolean) {
        val input = Data()
        if(status.isNotBlank() && status != Constant.TEXT_EMPTY){
            input["status"] = status
        }
        input["exclude"]=exclude
        input["page"] = page
        input["size"] = getSize()
        input["outlet_id"] = outlet.getLong("id")
        input["query"] = query
        Timber.i("Input Load Order : %s", input)
        if(API.isConnected(context)){
            lastPage = false
            loading = true
            this.disposable.add(this.orderService.getOrderList(input).retry(3).compose(RxUtils.applySingleAsync()).subscribe({ response ->
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
                        this.view.onOrderLoaded(payloads)
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