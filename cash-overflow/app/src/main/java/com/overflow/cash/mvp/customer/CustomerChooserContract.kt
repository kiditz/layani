package com.overflow.cash.mvp.customer

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class CustomerChooserContract {
    interface View : BaseView {
        fun onCustomerLoaded(customerList:List<Data>)
        fun onCustomerEdited(customer:Data)
    }

    interface Presenter : BasePresenter<View> {
        fun loadCustomer(page: Int, name: String)
        fun editCustomer(customer:Data)
    }
}