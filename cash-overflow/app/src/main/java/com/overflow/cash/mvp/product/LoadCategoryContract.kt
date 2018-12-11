package com.overflow.cash.mvp.product

import com.overflow.cash.activity.Constant
import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class LoadCategoryContract {
    interface View : BaseView {
        fun onCategoryLoaded( categoryList:List<Data>)
    }

    interface Presenter : BasePresenter<LoadCategoryContract.View> {
        fun loadCategory(page:Int=-1, name:String= Constant.TEXT_EMPTY)
    }
}