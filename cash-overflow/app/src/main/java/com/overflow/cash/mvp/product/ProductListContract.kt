package com.overflow.cash.mvp.product

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class ProductListContract {
    interface View : BaseView {
        fun onProductLoaded(productList:List<Data>)
    }

    interface Presenter : BasePresenter<View> {
        fun loadProduct(page: Int, categoryId: Long, query: String, order: String = "product_name asc")
    }
}