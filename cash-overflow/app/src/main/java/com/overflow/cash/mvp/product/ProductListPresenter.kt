package com.overflow.cash.mvp.product

import android.content.Context
import android.content.SharedPreferences
import com.overflow.cash.Constant
import com.overflow.cash.net.API
import com.overflow.cash.net.ProductService
import com.overflow.cash.net.RxUtils
import com.overflow.cash.realm.OrderRealm
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class ProductListPresenter(private var context: Context, private var translations: Translations, private var disposable: CompositeDisposable, private var productService: ProductService, private var preferences: SharedPreferences): ProductListContract.Presenter {

    lateinit var view: ProductListContract.View
    var lastPage:Boolean = true
    var loading:Boolean = true

    override fun attach(view: ProductListContract.View) {
        this.view = view
    }

    override fun detach() {
        disposable.clear()
    }

    override fun loadProduct(page:Int, categoryId:Long, query:String, order:String) {
        val merchant = Data(preferences.getString("merchant", "{}"))
        val input = Data()
        input["query"] = query
        input["page"] = page
        input["size"] = getSize()
        input["category_id"] = categoryId
        input["order"] = order
        input["merchant_id"] = merchant["id"]
        Timber.d("Page :%s", page)
        if(API.isConnected(context)){
            lastPage = false
            loading = true
            this.disposable.add(this.productService.getProduct(input).retry(3).compose(RxUtils.applySingleAsync()).subscribe({ response ->
                if(API.ok(response)){
                    if (response.containsKey("total_pages")) {
                        val total = response.getInt("total_pages")
                        if (page == total) {
                            this.lastPage = true
                        }
                    }
                    val payloads = API.payloads(response)
                    if(payloads.isEmpty()){
                        this.view.showEmpty()
                    }else{
                        this.view.onProductLoaded(payloads)
                    }

                }else{
                    this.view.showNoOk(translations.get(API.getError(response)))
                }
                loading = false
            }, {error ->
                this.view.showError(error)
                loading = false
            }))
        }else{
            loading = false
            this.view.showNotConnected(this.translations.get(Constant.TranslationsKey.NO_INTERNET))
        }
    }

    fun getSize():Int{
        return preferences.getInt(Constant.MAX_PAGE, API.SIZE)
    }



}