package com.overflow.cash.mvp.product

import com.overflow.cash.activity.Constant
import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class FindCategoryContract {
    interface View : BaseView {
        fun onCategorySelected(category:Data)
    }

    interface Presenter : BasePresenter<FindCategoryContract.View> {
        fun findCategory(categoryId:Long)
    }
}