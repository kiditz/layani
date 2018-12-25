package com.overflow.cash.mvp.cashbox

import android.content.Context
import android.content.SharedPreferences
import com.overflow.cash.activity.Constant
import com.overflow.cash.net.API
import com.overflow.cash.net.CashBoxService
import com.overflow.cash.net.RxUtils
import com.overflow.libs.core.Data
import com.overflow.libs.core.DateUtil
import com.overflow.libs.core.Translations
import io.reactivex.disposables.CompositeDisposable
import java.util.*

class LoadCashboxHistoryPresenter(private val context: Context, private val preferences: SharedPreferences, private val translations: Translations, private val service: CashBoxService, private val disposable: CompositeDisposable) : LoadCashboxHistoryContract.Presenter {

    lateinit var view: LoadCashboxHistoryContract.View

    var lastPage:Boolean = true
    var loading:Boolean = true

    override fun attach(view: LoadCashboxHistoryContract.View) {
        this.view = view
    }

    override fun loadCashBoxSummary(page:Int, cashboxSummaryId:Long) {
        val input = Data()
        input["page"] = page
        input["size"] = getSize()
        input["cash_box_summary_id"] = cashboxSummaryId
        if(API.isConnected(context)){
            lastPage = false
            loading = true
            this.disposable.add(this.service.getCashboxHistory(input).retry(3).compose(RxUtils.applySingleAsync()).subscribe({ response ->
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
                        this.view.onCashboxLoaded(payloads)
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

    fun getSize():Int = preferences.getInt(Constant.MAX_PAGE, API.SIZE)

    override fun detach() {
        disposable.clear()
    }
}