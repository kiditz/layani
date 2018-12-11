package com.overflow.cash.mvp.product

import android.content.Context
import android.content.SharedPreferences
import com.overflow.cash.activity.Constant
import com.overflow.cash.net.API
import com.overflow.cash.net.ProductService
import com.overflow.cash.net.RxUtils
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import io.reactivex.disposables.CompositeDisposable

class DeleteCategoryPresenter(private var context: Context, private var translations: Translations, private var disposable: CompositeDisposable, private var productService: ProductService, val preferences: SharedPreferences): DeleteCategoryContract.Presenter {


    lateinit var view: DeleteCategoryContract.View

    override fun attach(view: DeleteCategoryContract.View) {
        this.view = view
    }



    override fun detach() {
        disposable.clear()
    }

    override fun deleteCategory(categoryId: Long) {
        if(API.isConnected(context)){
            this.disposable.add(this.productService.deleteCategory(categoryId).retry(3).compose(RxUtils.applySingleAsync()).subscribe({ response ->
                if(API.ok(response)){
                    this.view.onCategoryDeleted(API.payload(response))
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

}