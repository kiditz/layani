package com.overflow.cash.mvp.product

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class AddCategoryContract {
    interface View : BaseView {
        fun onCategorySaved(data: Data)
    }

    interface Presenter : BasePresenter<View> {
        fun addCategory(data: Data)
    }
}