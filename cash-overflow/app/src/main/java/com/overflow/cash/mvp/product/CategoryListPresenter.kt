package com.overflow.cash.mvp.product

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import com.overflow.cash.Constant
import com.overflow.cash.net.API
import com.overflow.cash.net.ProductService
import com.overflow.cash.net.RxUtils
import com.overflow.cash.realm.OrderRealm
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import io.reactivex.disposables.CompositeDisposable

class CategoryListPresenter(
        private var context: Context,
        private var translations: Translations,
        private var disposable: CompositeDisposable,
        private var productService: ProductService,
        private val preferences:SharedPreferences,
        private val orderRealm: OrderRealm
): CategoryListContract.Presenter {
    lateinit var view: CategoryListContract.View

    override fun attach(view: CategoryListContract.View) {
        this.view = view
        val merchant = Data(preferences.getString("merchant", "{}"))
        val data = Data()
        data["merchant_id"] =merchant.getLong("id")
        data["name"] = Constant.TEXT_EMPTY
        this.loadCategory(data)
    }

    override fun detach() {
        disposable.clear()
    }

    override fun loadCategory(data:Data) {

        if(API.isConnected(context)){
            this.disposable.add(this.productService.getCategory(data).compose(RxUtils.applySingleAsync()).subscribe({ response ->
                if(API.ok(response)){
                    val payloads = API.payloads(response)
                    if(payloads.isEmpty()){
                        this.view.showEmpty()
                    }else{
                        this.view.onCategoryLoaded(payloads)
                    }

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

    override fun deleteAllOrderItems(){
        orderRealm.removeAllItems()
    }
}