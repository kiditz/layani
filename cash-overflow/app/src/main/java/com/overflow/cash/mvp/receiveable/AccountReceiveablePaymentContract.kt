package com.overflow.cash.mvp.receiveable

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class AccountReceiveablePaymentContract {
    interface View : BaseView {
        fun onCashboxLoaded(item: List<Data>)
        fun onPaymentSuccess(data:Data)
    }
    interface Presenter : BasePresenter<AccountReceiveablePaymentContract.View> {
        fun loadCashBox()
        fun payAccount(data:Data)
    }
}