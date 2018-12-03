package com.overflow.cash.mvp.product

import android.content.Context
import android.content.SharedPreferences
import com.overflow.cash.Constant
import com.overflow.cash.adapter.CategoryListAdapter
import com.overflow.cash.net.API
import com.overflow.cash.net.ProductService
import com.overflow.cash.net.RxUtils
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import io.reactivex.disposables.CompositeDisposable

class EditAndRemoveCategoryPresenter(
        private var context: Context,
        private var translations: Translations,
        private var disposable: CompositeDisposable,
        private val preferences: SharedPreferences,
        private val productService: ProductService
): EditAndRemoveCategoryContract.Presenter {
    lateinit var view: EditAndRemoveCategoryContract.View
    override fun attach(view: EditAndRemoveCategoryContract.View) {
        this.view = view
    }
    var merchant:Data = Data()
    init {
        merchant = Data(this.preferences.getString("merchant", "{}"))
    }
    override fun detach() {
        disposable.clear()
    }

    override fun editCategory(data: Data, holder:CategoryListAdapter.ViewHolder) {
        if(API.isConnected(context)){
            this.disposable.add(this.productService.editCategory(data).retry(3).compose(RxUtils.applySingleAsync()).subscribe({ response ->
                if(API.ok(response)){
                    this.view.onCategoryEdited(API.payload(response), holder)
                }else{
                    this.view.showNoOk(translations.get(API.getError(response)))
                }
            }, {error ->
                this.view.showError(error)
            }))
        }else{
            this.view.showNotConnected(this.translations.get(Constant.TranslationsKey.NO_INTERNET))
        }
    }

    override fun deleteCategory(id: Long, holder: CategoryListAdapter.ViewHolder) {
        if(API.isConnected(context)){
            this.disposable.add(this.productService.deleteCategory(id).retry(3).compose(RxUtils.applySingleAsync()).subscribe({ response ->
                if(API.ok(response)){
                    val payload = API.payload(response)

                    this.view.onCategoryRemoved(payload, holder)
                }else{
                    this.view.onEditRemoveCategoryNotOk(translations.get(API.getError(response)), holder)
                }
            }, {error ->
                this.view.onEditRemoveCategoryError(error, holder)
            }))
        }else{
            this.view.showNotConnected(this.translations.get(Constant.TranslationsKey.NO_INTERNET))
        }
    }

    override fun addCategory(data: Data, holder: CategoryListAdapter.ViewHolder) {
        data["merchant_id"] = this.merchant.getLong("id")
        if(API.isConnected(context)){
            this.disposable.add(this.productService.addCategory(data).retry(3).compose(RxUtils.applySingleAsync()).subscribe({ response ->
                if(API.ok(response)){
                    val payload = API.payload(response)

                    this.view.onCategoryAdded(payload, holder)
                }else{
                    this.view.onEditRemoveCategoryNotOk(translations.get(API.getError(response)), holder)
                }
            }, {error ->
                this.view.onEditRemoveCategoryError(error, holder)
            }))
        }else{
            this.view.showNotConnected(this.translations.get(Constant.TranslationsKey.NO_INTERNET))
        }
    }
}