package com.overflow.cash.mvp.order

import com.overflow.cash.adapter.SalesListAdapter
import com.overflow.cash.model.OrderItem
import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class OrderContract {
    interface View : BaseView {
        fun onOrderIntemCreated(item:OrderItem?, holder: SalesListAdapter.ViewHolder)
    }

    interface Presenter : BasePresenter<View> {
        fun addOrderItem(data:Data, holder: SalesListAdapter.ViewHolder)
    }


}