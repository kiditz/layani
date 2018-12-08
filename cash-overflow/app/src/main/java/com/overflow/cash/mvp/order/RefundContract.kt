package com.overflow.cash.mvp.order

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class RefundContract {
    interface View : BaseView {
        fun onRefundSuccess(data:Data)
    }

    interface Presenter : BasePresenter<View> {
        fun refund(data: Data)
    }
}