package com.overflow.cash.mvp.order

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class SummaryOrderContract {
    interface View : BaseView {
        fun onSummaryLoaded(summary: Data)
    }

    interface Presenter : BasePresenter<View> {
        fun loadSummary(summaryId:Long=-1, date:String)
    }
}