package com.overflow.cash.mvp.product

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class AddProductContract {
    interface View : BaseView {
        fun onProductSaved(product:Data)
    }

    interface Presenter : BasePresenter<AddProductContract.View> {
        fun addProduct(data:Data)
    }
}