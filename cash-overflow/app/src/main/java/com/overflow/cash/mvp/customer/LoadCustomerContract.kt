package com.overflow.cash.mvp.customer

import android.support.v7.widget.RecyclerView
import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class LoadCustomerContract {
    interface View : BaseView {
        fun onCustomerLoaded(customerList:List<Data>)
    }

    interface Presenter : BasePresenter<View> {
        fun loadCustomer(page: Int, name: String)
    }
}