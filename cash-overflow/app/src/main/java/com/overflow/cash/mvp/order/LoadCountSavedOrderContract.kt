package com.overflow.cash.mvp.order

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class LoadCountSavedOrderContract {
    interface View : BaseView {
        fun onSavedOrderLoaded(order: Data)
    }

    interface Presenter : BasePresenter<View> {
        fun loadSavedOrder()
    }
}