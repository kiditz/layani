package com.overflow.cash.mvp.cashbox

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class LoadCashboxSummaryContract {
    interface View : BaseView {
        fun onCashboxLoaded(item: List<Data>)
    }

    interface Presenter : BasePresenter<View> {
        fun loadCashBoxSummary(page:Int=1)
    }
}