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

class FindCategoryPresenter(
        private var context: Context,
        private var translations: Translations,
        private var disposable: CompositeDisposable,
        private val productService: ProductService,
        private val preferences:SharedPreferences
): FindCategoryContract.Presenter {
    lateinit var view: FindCategoryContract.View
    var lastPage:Boolean = true
    var loading:Boolean = true
    var outlet:Data = Data()
    init {
        outlet = Data(this.preferences.getString("outlet", "{}"))
    }
    override fun attach(view: FindCategoryContract.View) {
        this.view = view
    }

    override fun detach() {
        disposable.clear()
    }

    override fun findCategory(categoryId:Long) {
        if(API.isConnected(context)){
            this.disposable.add(this.productService.findCategory(categoryId).retry(3).compose(RxUtils.applySingleAsync()).subscribe({ response ->
                if(API.ok(response)){
                    this.view.onCategorySelected(API.payload(response))
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

    fun getSize():Int= preferences.getInt(Constant.MAX_PAGE, API.SIZE)
}