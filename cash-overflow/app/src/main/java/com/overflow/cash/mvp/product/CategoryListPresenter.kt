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

class CategoryListPresenter(
        private var context: Context,
        private var translations: Translations,
        private var disposable: CompositeDisposable,
        private val productService: ProductService,
        private val preferences:SharedPreferences,
        private val orderRealm: OrderRealm
): CategoryListContract.Presenter {
    lateinit var view: CategoryListContract.View
    var lastPage:Boolean = true
    var loading:Boolean = true
    var outlet:Data = Data()
    init {
        outlet = Data(this.preferences.getString("outlet", "{}"))
    }
    override fun attach(view: CategoryListContract.View) {
        this.view = view
    }

    override fun detach() {
        disposable.clear()
    }

    override fun loadCategory(page:Int, name:String) {
        val data = Data()
        data["outlet_id"] = this.outlet.getLong("id")
        if(page > 0){
            data["size"] = getSize()
            data["page"] = page
        }
        data["name"] = name
        if(API.isConnected(context)){
            this.disposable.add(this.productService.getCategory(data).retry(3).compose(RxUtils.applySingleAsync()).subscribe({ response ->
                if(API.ok(response)){
                    val payloads = API.payloads(response)
                    if (response.containsKey("total_pages")) {
                        val total = response.getInt("total_pages")
                        if (page == total) {
                            this.lastPage = true
                        }
                    }
                    if(payloads.isEmpty()){
                        this.view.showEmpty()
                    }else{
                        this.view.onCategoryLoaded(payloads)
                    }

                }else{
                    this.view.showNoOk(translations.get(API.getError(response)))
                }
                loading = false
            }, {error ->
                loading = false
                this.view.showError(error)
            }))
        }else{
            loading = false
            this.view.showNotConnected(this.translations.get(Constant.TranslationsKey.NO_INTERNET))
        }
    }

    override fun deleteAllOrderItems(){
        orderRealm.removeAllItems()
    }

    fun getSize():Int= preferences.getInt(Constant.MAX_PAGE, API.SIZE)
}