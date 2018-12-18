package com.overflow.cash.mvp.discount

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.widget.RecyclerView
import com.overflow.cash.activity.Constant
import com.overflow.cash.net.API
import com.overflow.cash.net.DiscountService
import com.overflow.cash.net.RxUtils
import com.overflow.libs.core.Data
import com.overflow.libs.core.DateUtil
import com.overflow.libs.core.Translations
import io.reactivex.disposables.CompositeDisposable
import java.util.*

/**
 * @author Rifky Aditya Bastara
 * @since 15 December 2018 22:49
 * */
class LoadDiscountByQuantityPresenter(
        private val context: Context,
        private val preferences: SharedPreferences,
        private val translations: Translations,
        private val discountService: DiscountService,
        private val disposable: CompositeDisposable
) : LoadDiscountByQuantityContract.Presenter {

    lateinit var view: LoadDiscountByQuantityContract.View
    val outlet = Data(preferences.getString("outlet", "{}"))

    override fun loadDiscount(quantity:Long,productId:Long, holder: RecyclerView.ViewHolder) {
        val data = Data()
        data["quantity"] = quantity
        data["date"] = DateUtil.printDefaultDate(Date())
        data["outlet_id"] = outlet.getLong("id")
        data["product_id"]= productId
        if (API.isConnected(context)) {
            this.disposable.add(this.discountService.findDiscountByQuantity(data).retry(3).compose(RxUtils.applySingleAsync()).subscribe({
                if (API.ok(it)) {
                    val payload = API.payload(it)
                    this.view.onDiscountLoaded(payload, holder)
                } else {
                    this.view.onDiscountNotLoaded(it, holder)
                }
            }, { error ->
                this.view.showError(error)
            }))
        } else {
            this.view.showNotConnected(this.translations.get(Constant.TranslationsKey.NO_INTERNET))
        }
    }

    override fun attach(view: LoadDiscountByQuantityContract.View) {
        this.view = view
    }

    override fun detach() {
        this.disposable.clear()
    }
}