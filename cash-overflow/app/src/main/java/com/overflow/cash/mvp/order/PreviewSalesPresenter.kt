package com.overflow.cash.mvp.order

import android.content.Context
import android.content.SharedPreferences
import com.overflow.cash.Constant
import com.overflow.cash.adapter.PreviewSalesAdapter
import com.overflow.cash.net.API
import com.overflow.cash.net.CashBoxService
import com.overflow.cash.net.OrderService
import com.overflow.cash.net.RxUtils
import com.overflow.cash.realm.OrderRealm
import com.overflow.libs.core.Translations
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class PreviewSalesPresenter(private val context:Context, private val orderRealm: OrderRealm, private val preferences: SharedPreferences, private val translations: Translations, private val service: CashBoxService, private val orderService: OrderService, private val disposable: CompositeDisposable) :PreviewSalesContract.Presenter{



    override fun deleteAllItems() {
        orderRealm.removeAllItems()
    }


    override fun calculateTotalAmount(): Double? {
        return orderRealm.sumOrderSubTotal()?.toDouble()
    }

    lateinit var view:PreviewSalesContract.View

    override fun loadOrder() {
        val dataList = orderRealm.loadOrder()
        Timber.d("Data List : %s", dataList.toString())
        view.onOrderLoaded(dataList)
    }


    override fun detach() {
        disposable.clear()
    }
    override fun attach(view: PreviewSalesContract.View) {
        this.view = view
        loadOrder()
    }
    private fun getSize():Int{
        return preferences.getInt(Constant.MAX_PAGE, API.SIZE)
    }


    override fun loadDiscount(productId: Long, quantity: Long, holder:PreviewSalesAdapter.ViewHolder, position: Int) {
        if(API.isConnected(context)){
            orderService.findDiscount(productId, quantity).compose(RxUtils.applySingleAsync()).subscribe({
                if(API.ok(it)){
                    val payload = API.payload(it)
                    this.view.onDiscountLoaded(payload, holder, position)
                }else{
                    this.view.showNoOk(translations.get(API.getError(it)))
                }
            }, {
                this.view.showError(it)
            })
        }
    }



}