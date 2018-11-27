package com.overflow.cash.mvp.order

import com.overflow.cash.adapter.PreviewSalesAdapter
import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class PreviewSalesContract {
    interface View : BaseView {
        fun onOrderLoaded(item:MutableList<Data>)
        fun onDiscountLoaded(discount:Data, holder:PreviewSalesAdapter.ViewHolder, position: Int)
    }

    interface Presenter : BasePresenter<View> {
        fun loadOrder()
        fun calculateTotalAmount():Double?
        fun loadDiscount(productId:Long, quantity:Long, holder:PreviewSalesAdapter.ViewHolder, position:Int)
        fun deleteAllItems()
    }


}