package com.overflow.cash.mvp.menu

import android.accounts.AccountManager
import android.content.Context
import android.content.SharedPreferences
import com.overflow.cash.BuildConfig
import com.overflow.cash.activity.Constant
import com.overflow.cash.net.API
import com.overflow.cash.net.NotificationService
import com.overflow.cash.net.OrderService
import com.overflow.cash.net.RxUtils
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import io.reactivex.disposables.CompositeDisposable

class FirebaseTokenPresenter (private val context:Context, private val preferences: SharedPreferences, private val translations: Translations, private val notificationService: NotificationService, private val disposable: CompositeDisposable): FirebaseTokenContract.Presenter {


    lateinit var view:FirebaseTokenContract.View

    override fun attach(view: FirebaseTokenContract.View) {
        this.view = view
    }

    override fun saveToken(token: String) {
        val outlet = Data(preferences.getString("outlet", "{}"))
        val data = Data()
        data["client_id"] = BuildConfig.auth_user
        data["user_id"] = outlet.getLong("user_id")
        data["token"] = token
        if (API.isConnected(context)) {
            this.disposable.add(this.notificationService.saveToken(data).retry(3).compose(RxUtils.applySingleAsync()).subscribe({
                if (API.ok(it)) {
                    this.view.onTokenSaved(API.payload(it))
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

    override fun detach() {
        disposable.clear()
    }
}