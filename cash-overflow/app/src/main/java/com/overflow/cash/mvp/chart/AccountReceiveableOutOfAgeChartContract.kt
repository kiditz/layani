package com.overflow.cash.mvp.chart

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class AccountReceiveableOutOfAgeChartContract {
    interface View : BaseView {
        fun onChartLoaded(data:Data)
    }

    interface Presenter : BasePresenter<View> {
        fun showChart()
    }

}