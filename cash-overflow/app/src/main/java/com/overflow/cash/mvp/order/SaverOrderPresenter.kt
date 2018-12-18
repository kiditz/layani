package com.overflow.cash.mvp.order

import android.content.Context
import android.content.SharedPreferences
import com.overflow.cash.activity.Constant
import com.overflow.cash.net.API
import com.overflow.cash.net.OrderService
import com.overflow.cash.net.RxUtils
import com.overflow.cash.realm.OrderItemRealm
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import io.reactivex.disposables.CompositeDisposable

class SaverOrderPresenter(
        private val context: Context,
        private val orderItemRealm: OrderItemRealm,
        private val preferences: SharedPreferences,
        private val translations: Translations,
        private val orderService: OrderService,
        private val disposable: CompositeDisposable
) : SaveOrderContract.Presenter {

    lateinit var view: SaveOrderContract.View
    fun deleteAllItems() {
        orderItemRealm.deleteItems()
    }
    override fun saveOrder(data: Data) {
        val outlet = Data(preferences.getString("outlet", "{}"))
        data["outlet_id"] = outlet.getLong("id")
        data["user_id"] = outlet.getLong("user_id")
        if (API.isConnected(context)) {
            this.disposable.add(this.orderService.addOrder(data).retry(3).compose(RxUtils.applySingleAsync()).subscribe({
                if (API.ok(it)) {
                    this.view.onOrderCreated(API.payload(it))
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

    override fun attach(view: SaveOrderContract.View) {
        this.view = view
    }

    override fun detach() {
        this.disposable.clear()
    }
}