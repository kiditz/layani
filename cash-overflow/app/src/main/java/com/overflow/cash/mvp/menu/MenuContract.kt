package com.overflow.cash.mvp.menu

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView

class MenuContract() {

    interface View : BaseView {
        fun onNotLogin()
    }

    interface Presenter : BasePresenter<View> {

    }
}