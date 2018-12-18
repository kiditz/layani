package com.overflow.cash.mvp.order

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class LoadOrderItemContract {
    interface View : BaseView {
        fun onOrderItemsLoaded(itemList:List<Data>)
    }

    interface Presenter : BasePresenter<View> {
        fun loadItem(orderCode: String)
    }
}