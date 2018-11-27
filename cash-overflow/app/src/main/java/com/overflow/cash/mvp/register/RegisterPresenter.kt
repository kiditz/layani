package com.overflow.cash.mvp.register

import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import com.overflow.cash.BuildConfig
import com.overflow.cash.Constant
import com.overflow.cash.R
import com.overflow.cash.net.API
import com.overflow.cash.net.AccountService
import com.overflow.cash.net.RxUtils
import com.overflow.libs.core.Data
import com.overflow.libs.core.OauthCredentialGenerator
import com.overflow.libs.core.Translations
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterPresenter(private var context: Context, private var translations: Translations, private var disposable: CompositeDisposable, private var accountService: AccountService): RegisterContract.Presenter {


    lateinit var view: RegisterContract.View

    override fun attach(view: RegisterContract.View) {
        this.view = view
    }

    override fun addStore(data: Data) {
        if(API.isConnected(context)){
            this.disposable.add(this.accountService.register(data).compose(RxUtils.applySingleAsync()).subscribe({ response ->
                if(API.ok(response)){
                    this.view.onAccountCreated(API.payload(response))
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


    override fun detach() {
        this.disposable.clear()
    }

    override fun login(input:Data) {
        val authHeader = OauthCredentialGenerator.generateCredentials(BuildConfig.auth_user, BuildConfig.auth_password)
        val data = Data()
        data["grant_type"] = "client_credentials"
        this.accountService.login("Basic $authHeader", data).enqueue(object:Callback<Data>{
            override fun onFailure(call: Call<Data>?, t: Throwable?) {
                view.showError(t!!)
            }

            override fun onResponse(call: Call<Data>?, response: Response<Data>?) {
                val resp = response!!.body()
                val accessToken = resp!!.getString("access_token")
                val bundle = input.toBundle()
                bundle.putString(AccountManager.KEY_ACCOUNT_NAME, input.getString("username"))
                bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, context.getString(R.string.account_type))
                bundle.putString(AccountManager.KEY_AUTHTOKEN, accessToken)
                val intent = Intent()
                intent.putExtras(bundle)
                view.onLoginSuccess(intent)
            }
        })
    }

}