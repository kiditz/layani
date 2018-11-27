package com.overflow.cash.mvp.product

import android.content.Context
import android.content.SharedPreferences
import com.overflow.cash.Constant
import com.overflow.cash.net.API
import com.overflow.cash.net.ProductService
import com.overflow.cash.net.RxUtils
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import io.reactivex.disposables.CompositeDisposable

class ProductDetailPresenter(private var context: Context, private var translations: Translations, private var disposable: CompositeDisposable, private var productService: ProductService, private var preferences: SharedPreferences):ProductDetailContract.Presenter {


    private lateinit var view: ProductDetailContract.View
    override fun addStock(data: Data) {
        if(API.isConnected(context)){
            this.disposable.add(this.productService.addStock(data).compose(RxUtils.applySingleAsync()).subscribe({ response ->
                if(API.ok(response)){
                    this.view.onStockCreated(API.payload(response))
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
    override fun addDiscount(data: Data) {
        if(API.isConnected(context)){
            this.disposable.add(this.productService.addDiscount(data).compose(RxUtils.applySingleAsync()).subscribe({ response ->
                if(API.ok(response)){
                    this.view.onDiscountCreated(API.payload(response))
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

    override fun attach(view: ProductDetailContract.View) {
        this.view = view
    }

    override fun detach() {
        disposable.clear()
    }

}