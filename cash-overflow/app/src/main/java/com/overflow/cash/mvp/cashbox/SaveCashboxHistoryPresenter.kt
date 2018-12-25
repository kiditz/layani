package com.overflow.cash.mvp.cashbox

import android.content.Context
import android.content.SharedPreferences
import com.overflow.cash.activity.Constant
import com.overflow.cash.net.API
import com.overflow.cash.net.CashBoxService
import com.overflow.cash.net.RxUtils
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import io.reactivex.disposables.CompositeDisposable

class SaveCashboxHistoryPresenter(private val context: Context, private val preferences: SharedPreferences, private val translations: Translations, private val service: CashBoxService, private val disposable: CompositeDisposable) : SaveCashboxHistoryContract.Presenter {

    lateinit var view: SaveCashboxHistoryContract.View


    override fun attach(view: SaveCashboxHistoryContract.View) {
        this.view = view
    }

    override fun detach() {
        this.disposable.clear()
    }

    override fun saveCashboxHistory(data: Data) {
        val outlet = Data(preferences.getString("outlet", "{}"))
        data["outlet_id"] = outlet.getLong("id")
        data["user_id"] = outlet.getLong("user_id")
        if (API.isConnected(context)) {
            this.disposable.add(this.service.saveCashboxHistory(data).retry(3).compose(RxUtils.applySingleAsync()).subscribe({
                if (API.ok(it)) {
                    this.view.onCashboxSaved(API.payload(it))
                } else {
                    this.view.showNoOk(translations.get(API.getError(it)))
                }
            }, { error ->
                this.view.showError(error)
            }))
        } else {
            this.view.showNotConnected(this.translations.get(Constant.TranslationsKey.NO_INTERNET))
        }
    }
}