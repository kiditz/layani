package com.overflow.cash.mvp.register

import android.content.Intent
import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class RegisterContract {
    interface View : BaseView {
        fun onAccountCreated(data:Data)
        fun onLoginSuccess(intent: Intent)
    }

    interface Presenter : BasePresenter<View> {
        fun addStore(data:Data)
        fun login(input:Data)
    }
}