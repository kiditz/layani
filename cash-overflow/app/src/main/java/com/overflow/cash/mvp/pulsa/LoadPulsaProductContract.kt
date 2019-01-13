package com.overflow.cash.mvp.pulsa

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class LoadPulsaProductContract {
    interface View : BaseView {
        fun onProductLoaded(productList:List<Data>)
    }

    interface Presenter : BasePresenter<View> {
        fun loadProduct(page: Int, categoryId: Long, phoneNumber: String)
    }
}