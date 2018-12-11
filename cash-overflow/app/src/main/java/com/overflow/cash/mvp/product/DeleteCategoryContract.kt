package com.overflow.cash.mvp.product

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class DeleteCategoryContract {
    interface View : BaseView {
        fun onCategoryDeleted(data: Data)
    }

    interface Presenter : BasePresenter<View> {
        fun deleteCategory(categoryId:Long)
    }
}