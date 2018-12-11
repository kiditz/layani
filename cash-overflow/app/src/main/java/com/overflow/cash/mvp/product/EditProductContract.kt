package com.overflow.cash.mvp.product

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class EditProductContract {
    interface View : BaseView {
        fun onProductSaved(product:Data)
    }

    interface Presenter : BasePresenter<EditProductContract.View> {
        fun editProduct(data:Data)
    }
}