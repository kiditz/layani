package com.overflow.cash.mvp.cashbox

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class SaveCashboxHistoryContract {
    interface View : BaseView {
        fun onCashboxSaved(data: Data)
    }

    interface Presenter : BasePresenter<View> {
        fun saveCashboxHistory(data:Data)
    }
}