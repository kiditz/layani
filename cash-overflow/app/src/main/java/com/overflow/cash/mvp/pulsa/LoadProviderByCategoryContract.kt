package com.overflow.cash.mvp.pulsa

import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class LoadProviderByCategoryContract {
    interface View : BaseView {
        fun onProviderLoaded(providerList:List<Data>)
    }

    interface Presenter : BasePresenter<LoadProviderByCategoryContract.View> {
        fun loadProvider(categoryId:Long)
    }
}