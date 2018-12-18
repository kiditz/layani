package com.overflow.cash.mvp.discount

import android.support.v7.widget.RecyclerView
import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class LoadDiscountByQuantityContract {
    interface View : BaseView {
        fun onDiscountLoaded(data: Data, holder:RecyclerView.ViewHolder)
        fun onDiscountNotLoaded(data: Data, holder:RecyclerView.ViewHolder)
    }

    interface Presenter : BasePresenter<View> {
        fun loadDiscount(quantity:Long,productId:Long, holder:RecyclerView.ViewHolder)
    }
}