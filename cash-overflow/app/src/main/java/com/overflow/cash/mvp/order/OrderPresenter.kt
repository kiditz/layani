package com.overflow.cash.mvp.order

import com.overflow.cash.adapter.SalesListAdapter
import com.overflow.cash.realm.OrderRealm
import com.overflow.libs.core.Data

class OrderPresenter(private val orderRealm: OrderRealm) :OrderContract.Presenter{
    override fun addOrderItem(data: Data, holder: SalesListAdapter.ViewHolder) {
        val orderItem = orderRealm.addItem(data)
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