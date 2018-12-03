package com.overflow.cash.mvp.product

import com.overflow.cash.adapter.CategoryListAdapter
import com.overflow.libs.BasePresenter
import com.overflow.libs.BaseView
import com.overflow.libs.core.Data

class EditAndRemoveCategoryContract {
    interface View : BaseView {
        fun onCategoryEdited(data: Data, holder: CategoryListAdapter.ViewHolder)
        fun onCategoryAdded(data: Data, holder: CategoryListAdapter.ViewHolder)
        fun onCategoryRemoved(data: Data, holder: CategoryListAdapter.ViewHolder)
        fun onEditRemoveCategoryNotOk(res: String, holder: CategoryListAdapter.ViewHolder)
        fun onEditRemoveCategoryError(error: Throwable, holder: CategoryListAdapter.ViewHolder)
    }

    interface Presenter : BasePresenter<EditAndRemoveCategoryContract.View> {
        fun editCategory(data: Data, holder: CategoryListAdapter.ViewHolder)
        fun addCategory(data: Data, holder: CategoryListAdapter.ViewHolder)
        fun deleteCategory(id: Long, holder: CategoryListAdapter.ViewHolder)
    }
}