package com.overflow.cash.mvp.menu

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class FirebaseTokenContract {

    interface View : BaseView {
        fun onTokenSaved(data: Data)
    }

    interface Presenter : BasePresenter<View> {
        fun saveToken(token:String)
    }
}