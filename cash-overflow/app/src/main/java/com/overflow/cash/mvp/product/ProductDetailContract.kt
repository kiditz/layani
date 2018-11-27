package com.overflow.cash.mvp.product

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class ProductDetailContract {
    interface View : BaseView {
        fun onStockCreated(data:Data)
        fun onDiscountCreated(data:Data)
    }

    interface Presenter : BasePresenter<View> {
        fun addStock(data: Data)
        fun addDiscount(data: Data)
    }
}