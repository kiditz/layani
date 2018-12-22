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

class DeleteOrderPresenter(private val context: Context, private val preferences: SharedPreferences, private val translations: Translations, private val orderService: OrderService, private val disposable: CompositeDisposable) : DeleteOrderContract.Presenter {




    lateinit var view: DeleteOrderContract.View
    var outlet: Data = Data()

    init {
        outlet = Data(preferences.getString("outlet", "{}"))
    }

    override fun attach(view: DeleteOrderContract.View) {
        this.view = view
    }
    override fun delete(orderId: Long) {
        if (API.isConnected(context)) {
            this.disposable.add(this.orderService.delete(orderId).retry(3).compose(RxUtils.applySingleAsync()).subscribe({
                if (API.ok(it)) {
                    this.view.onDeleteOrderSuccess(API.payload(it))
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

    override fun detach() {
        disposable.clear()
    }


}