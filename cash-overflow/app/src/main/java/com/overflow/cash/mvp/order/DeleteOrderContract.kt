package com.overflow.cash.mvp.order

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class DeleteOrderContract {
    interface View : BaseView {
        fun onDeleteOrderSuccess(data:Data)
    }

    interface Presenter : BasePresenter<View> {
        fun delete(orderId:Long)
    }
}