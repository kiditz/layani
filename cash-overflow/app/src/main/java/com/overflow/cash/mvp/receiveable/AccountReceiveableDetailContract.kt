package com.overflow.cash.mvp.receiveable

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class AccountReceiveableDetailContract {
    interface View : BaseView {
        fun onDetailLoaded(receiveables:List<Data>)
        fun onOrderItemsLoaded(items:List<Data>)
    }

    interface Presenter : BasePresenter<View> {

        fun loadDetail(page: Int, customerId:Long)

        fun loadOrderItems(orderCode:String)
    }
}