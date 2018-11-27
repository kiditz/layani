package com.overflow.cash.mvp.login

import android.content.Intent
import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class LoginContract {
    interface View : BaseView {
        fun onLoginSuccess(intent: Intent)
    }

    interface Presenter : BasePresenter<View> {
        fun login(input:Data)
    }
}