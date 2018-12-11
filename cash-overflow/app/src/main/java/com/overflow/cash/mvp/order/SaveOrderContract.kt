package com.overflow.cash.mvp.order

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class SaveOrderContract {
    interface View : BaseView {
        fun onOrderCreated(data:Data)
    }

    interface Presenter : BasePresenter<View> {
        fun saveOrder(data:Data)
    }
}