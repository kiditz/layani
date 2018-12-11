package com.overflow.cash.mvp.customer

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class EditCustomerContract {
    interface View : BaseView {
        fun onCustomerEdited(customer:Data)
        fun onCustomerEditShowNoOk(res: String)
    }

    interface Presenter : BasePresenter<View> {
        fun editCustomer(customer:Data)
    }
}