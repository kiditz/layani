package com.overflow.cash.mvp.receiveable

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class AccountReceiveableContract {
    interface View : BaseView {
        fun onReceiveableLoaded(receiveables:List<Data>)
    }

    interface Presenter : BasePresenter<View> {

        fun loadReceiveable(page: Int, name: String)
    }
}