package com.overflow.cash.mvp.discount

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class LoadDiscountByBillAmountContract {
    interface View : BaseView {
        fun onDiscountLoaded(data: Data)
        fun onDiscountNotLoaded(data: Data)
    }

    interface Presenter : BasePresenter<View> {
        fun loadDiscount(billAmount:Double=0.0)
    }
}