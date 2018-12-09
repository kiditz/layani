package com.overflow.cash.mvp.order

import android.content.Context
import com.overflow.cash.adapter.SalesListAdapter
import com.overflow.cash.net.API
import com.overflow.cash.net.OrderService
import com.overflow.cash.net.RxUtils
import com.overflow.cash.realm.OrderRealm
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations

class OrderPresenter(private val context: Context, private val orderRealm: OrderRealm, private val orderService: OrderService, private val translations: Translations) :OrderContract.Presenter{
    override fun loadDiscount(productId: Long, quantity: Long, holder: SalesListAdapter.ViewHolder) {
        if(API.isConnected(context)){
            orderService.findDiscount(productId, quantity).retry(3).compose(RxUtils.applySingleAsync()).subscribe({
                if(API.ok(it)){
                    val payload = API.payload(it)
                    this.view.onDiscountLoaded(payload, holder)
                }else{
                    this.view.onDiscountNotLoaded(translations.get(API.getError(it)), holder)
                }
            }, {
                this.view.showError(it)
            })
        }
    }

    override fun addOrderItem(data: Data, updateQty:Boolean, holder: SalesListAdapter.ViewHolder) {
        val orderItem = orderRealm.addItem(data, updateQty)
        this.view.onOrderIntemCreated(orderItem, holder)
    }

    lateinit var view:OrderContract.View


    override fun attach(view: OrderContract.View) {
        this.view = view
    }

    override fun detach() {

    }

    fun deleteItem(productId:Long){
        orderRealm.deleteItem(productId)
    }

}