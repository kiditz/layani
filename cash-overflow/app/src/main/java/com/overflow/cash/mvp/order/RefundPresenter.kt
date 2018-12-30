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

class RefundPresenter(private val context: Context, private val preferences: SharedPreferences, private val translations: Translations, private val orderService: OrderService, private val disposable: CompositeDisposable) : RefundContract.Presenter {


    lateinit var view: RefundContract.View
    var lastPage: Boolean = true
    var loading: Boolean = true
    var outlet: Data = Data()

    init {
        outlet = Data(preferences.getString("outlet", "{}"))
    }

    override fun attach(view: RefundContract.View) {
        this.view = view
    }

    override fun detach() {
        disposable.clear()
    }

    fun getSize(): Int = preferences.getInt(Constant.MAX_PAGE, API.SIZE)

    override fun refund(data:Data) {
        if (API.isConnected(context)) {
            data["user_id"] = outlet.getDouble("user_id")
            this.disposable.add(this.orderService.refund(data).retry(3).compose(RxUtils.applySingleAsync()).subscribe({
                if (API.ok(it)) {
                    this.view.onRefundSuccess(API.payload(it))
                } else {
                    this.view.showNoOk(translations.get(API.getError(it)))
                }
            }, { error ->
                this.view.showError(error)
            }))
        } else {
            this.view.showNotConnected(this.translations.get(Constant.TranslationsKey.NO_INTERNET))
        }
    }
}