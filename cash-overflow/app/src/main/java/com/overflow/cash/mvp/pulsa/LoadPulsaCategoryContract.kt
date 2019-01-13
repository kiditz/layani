package com.overflow.cash.mvp.pulsa

import com.overflow.cash.activity.Constant
import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class LoadPulsaCategoryContract {
    interface View : BaseView {
        fun onCategoryLoaded(categoryList:List<Data>)
    }

    interface Presenter : BasePresenter<LoadPulsaCategoryContract.View> {
        fun loadCategory()
    }
}