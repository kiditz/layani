package com.overflow.cash.mvp.product

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class CategoryListContract {
    interface View : BaseView {
        fun onCategoryLoaded(categoryList:List<Data>)
    }

    interface Presenter : BasePresenter<CategoryListContract.View> {
        fun loadCategory(data:Data)
        fun deleteAllOrderItems()
    }
}