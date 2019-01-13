package com.overflow.cash.mvp.pulsa

import android.content.Context
import android.content.SharedPreferences
import com.overflow.cash.activity.Constant
import com.overflow.cash.net.API
import com.overflow.cash.net.PulsaService
import com.overflow.cash.net.RxUtils
import com.overflow.libs.core.Translations
import io.reactivex.disposables.CompositeDisposable

class LoadProviderByCategoryPresenter(
        private var context: Context,
        private var translations: Translations,
        private var disposable: CompositeDisposable,
        private val pulsaService: PulsaService,
        private val preferences:SharedPreferences
): LoadProviderByCategoryContract.Presenter {
    lateinit var view: LoadProviderByCategoryContract.View
//    var outlet:Data = Data()
//    init {
//        outlet = Data(this.preferences.getString("outlet", "{}"))
//    }
    override fun attach(view: LoadProviderByCategoryContract.View) {
        this.view = view
    }

    override fun detach() {
        disposable.clear()
    }

    override fun loadProvider(categoryId: Long) {
        if(API.isConnected(context)){
            this.disposable.add(this.pulsaService.getProviders(categoryId).retry(3).compose(RxUtils.applySingleAsync()).subscribe({ response ->
                if(API.ok(response)){
                    val payloads = API.payloads(response)
                    if(payloads.isEmpty()){
                        this.view.showEmpty()
                    }else{
                        this.view.onProviderLoaded(payloads)
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


    fun getSize():Int= preferences.getInt(Constant.MAX_PAGE, API.SIZE)
}