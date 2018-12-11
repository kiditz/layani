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

class EditCategoryPresenter(private var context: Context, private var translations: Translations, private var disposable: CompositeDisposable, private var productService: ProductService, val preferences: SharedPreferences): EditCategoryContract.Presenter {


    lateinit var view: EditCategoryContract.View
    var outlet:Data = Data()
    init {
        outlet = Data(this.preferences.getString("outlet", "{}"))
    }
    override fun attach(view: EditCategoryContract.View) {
        this.view = view
    }



    override fun detach() {
        disposable.clear()
    }

    override fun editCategory(data: Data) {
        data["outlet_id"] = outlet.getLong("id")
        if(API.isConnected(context)){
            this.disposable.add(this.productService.editCategory(data).retry(3).compose(RxUtils.applySingleAsync()).subscribe({ response ->
                if(API.ok(response)){
                    this.view.onCategorySaved(API.payload(response))
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