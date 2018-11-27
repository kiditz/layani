package com.overflow.cash.mvp.product

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class AddProductContract {
    interface View : BaseView {
        fun onCategoryCreated(category:Data)
        fun onCategoryLoaded(categoryList:List<Data>)
        fun onProductCreated(product:Data)
        fun onCategorySelected(category: Data)
    }

    interface Presenter : BasePresenter<AddProductContract.View> {
        fun addCategory(data:Data)
        fun loadCategory(data:Data)
        fun addProduct(data:Data)
        fun editProduct(data:Data)
        fun findCategory(categoryId:Long?)
    }
}