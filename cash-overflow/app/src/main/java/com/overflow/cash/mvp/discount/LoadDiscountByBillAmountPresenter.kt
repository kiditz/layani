package com.overflow.cash.mvp.discount

import android.content.Context
import android.content.SharedPreferences
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
class LoadDiscountByBillAmountPresenter(
        private val context: Context,
        private val preferences: SharedPreferences,
        private val translations: Translations,
        private val discountService: DiscountService,
        private val disposable: CompositeDisposable
) : LoadDiscountByBillAmountContract.Presenter {

    lateinit var view: LoadDiscountByBillAmountContract.View
    val outlet = Data(preferences.getString("outlet", "{}"))

    override fun loadDiscount(billAmount:Double) {
        val data = Data()
        data["bill_amount"] = billAmount
        data["date"] = DateUtil.printDefaultDate(Date())
        data["outlet_id"] = outlet.getLong("id")
        data[""]
        if (API.isConnected(context)) {
            this.disposable.add(this.discountService.findDiscountByBillAmount(data).retry(3).compose(RxUtils.applySingleAsync()).subscribe({
                if (API.ok(it)) {
                    val payload = API.payload(it)
                    this.view.onDiscountLoaded(payload)
                } else {
                    this.view.onDiscountNotLoaded(it)
                }
            }, { error ->
                this.view.showError(error)
            }))
        } else {
            this.view.showNotConnected(this.translations.get(Constant.TranslationsKey.NO_INTERNET))
        }
    }

    override fun attach(view: LoadDiscountByBillAmountContract.View) {
        this.view = view
    }

    override fun detach() {
        this.disposable.clear()
    }
}