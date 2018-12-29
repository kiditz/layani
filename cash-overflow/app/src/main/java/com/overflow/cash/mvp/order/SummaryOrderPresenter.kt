package com.overflow.cash.mvp.order

import android.content.Context
import android.content.SharedPreferences
import com.overflow.cash.activity.Constant
import com.overflow.cash.net.API
import com.overflow.cash.net.OrderService
import com.overflow.cash.net.RxUtils
import com.overflow.libs.core.Data
import com.overflow.libs.core.DateUtil
import com.overflow.libs.core.Translations
import io.reactivex.disposables.CompositeDisposable
import java.util.*

class SummaryOrderPresenter(private val context: Context, private val preferences: SharedPreferences, private val translations: Translations, private val service: OrderService, private val disposable: CompositeDisposable):SummaryOrderContract.Presenter {
    lateinit var view: SummaryOrderContract.View
    override fun loadSummary(summaryId:Long, date:String) {
        if(API.isConnected(context)){
            this.disposable.add(this.service.getSummary(summaryId, date).retry(3).compose(RxUtils.applySingleAsync()).subscribe({ response ->
                if(API.ok(response)){
                    val payload = API.payload(response)
                    this.view.onSummaryLoaded(payload)
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

    override fun attach(view: SummaryOrderContract.View) {
        this.view = view
    }

    override fun detach() {
        disposable.clear()
    }

}