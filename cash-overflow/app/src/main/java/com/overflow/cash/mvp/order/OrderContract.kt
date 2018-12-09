package com.overflow.cash.mvp.order

import com.overflow.cash.adapter.SalesListAdapter
import com.overflow.cash.model.OrderItem
import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class OrderContract {
    interface View : BaseView {
        fun onOrderIntemCreated(item:OrderItem?, holder: SalesListAdapter.ViewHolder)
        fun onDiscountLoaded(data:Data, holder: SalesListAdapter.ViewHolder)
        fun onDiscountNotLoaded(res:String, holder: SalesListAdapter.ViewHolder)
    }

    interface Presenter : BasePresenter<View> {
        fun loadDiscount(productId:Long, quantity:Long, holder: SalesListAdapter.ViewHolder)
        fun addOrderItem(data:Data, updateQty:Boolean=false, holder: SalesListAdapter.ViewHolder)
    }


}