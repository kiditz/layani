package com.overflow.cash.mvp.pulsa

import com.overflow.cash.activity.Constant
import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class SendOrderPulsaContract {
    interface View : BaseView {
        fun onOrderSended(result:Data)
    }

    interface Presenter : BasePresenter<SendOrderPulsaContract.View> {
        fun sendOrder(data:Data)
    }
}