package com.overflow.cash.mvp.order

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class LoadOrderContract {
    interface View : BaseView {
        fun onOrderLoaded(orderList:List<Data>)
    }

    interface Presenter : BasePresenter<View> {
        fun loadOrder(page: Int, query: String, status: String, exclude:Boolean=true)
    }
}