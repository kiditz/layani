package com.overflow.cash.mvp.customer

import android.support.v7.widget.RecyclerView
import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class CustomerChooserContract {
    interface View : BaseView {
        fun onCustomerLoaded(customerList:List<Data>)
        fun onCustomerEdited(customer:Data, holder:RecyclerView.ViewHolder?=null)
        fun onCustomerEditShowNoOk(res: String)
    }

    interface Presenter : BasePresenter<View> {
        fun loadCustomer(page: Int, name: String)
        fun editCustomer(customer:Data, holder:RecyclerView.ViewHolder?=null)
    }
}