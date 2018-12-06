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

class AddProductPresenter(private var context: Context, private var translations: Translations, private var disposable: CompositeDisposable, private var productService: ProductService, val preferences: SharedPreferences): AddProductContract.Presenter {


    lateinit var view: AddProductContract.View

    override fun attach(view: AddProductContract.View) {
        this.view = view
    }



    override fun detach() {
        disposable.clear()
    }

    override fun addCategory(data: Data) {
        if(API.isConnected(context)){
            this.disposable.add(this.productService.addCategory(data).retry(3).compose(RxUtils.applySingleAsync()).subscribe({ response ->
                if(API.ok(response)){
                    this.view.onCategoryCreated(API.payload(response))
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

    override fun loadCategory(data:Data) {
        if(API.isConnected(context)){

            this.disposable.add(this.productService.getCategory(data).compose(RxUtils.applySingleAsync()).subscribe({ response ->
                if(API.ok(response)){
                    val payloads = API.payloads(response)
                    if(payloads.isEmpty()){
                        this.view.showEmpty()
                    }
                    this.view.onCategoryLoaded(payloads)
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

    override fun addProduct(data: Data) {
        val merchant = Data(preferences.getString("merchant", "{}"))
        data["merchant_id"] = merchant.getLong("id")
        if(API.isConnected(context)){
            this.disposable.add(this.productService.addProduct(data).retry(3).compose(RxUtils.applySingleAsync()).subscribe({ response ->
                if(API.ok(response)){
                    this.view.onProductCreated(API.payload(response))
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

    override fun editProduct(data: Data) {
        if(API.isConnected(context)){
            this.disposable.add(this.productService.editProduct(data).retry(3).compose(RxUtils.applySingleAsync()).subscribe({ response ->
                if(API.ok(response)){
                    this.view.onProductCreated(API.payload(response))
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

    override fun findCategory(categoryId: Long?) {
        if(categoryId == null){
            return
        }

        if(API.isConnected(context)) {
            this.disposable.add(this.productService.findCategory(categoryId).compose(RxUtils.applySingleAsync()).subscribe({ response ->
                if (API.ok(response)) {
                    this.view.onCategorySelected(API.payload(response))
                } else {
                    this.view.showNoOk(translations.get(API.getError(response)))
                }
            }, { error ->
                this.view.showError(error)
            }))
        }else{
            this.view.showNotConnected(this.translations.get(Constant.TranslationsKey.NO_INTERNET))
        }
    }

}