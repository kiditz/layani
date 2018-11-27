package com.overflow.cash.mvp.order

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class CashboxContract {
    interface View : BaseView {
        fun onCashboxLoaded(item: List<Data>)
    }

    interface Presenter : BasePresenter<View> {
        fun loadCashBox()
    }
}