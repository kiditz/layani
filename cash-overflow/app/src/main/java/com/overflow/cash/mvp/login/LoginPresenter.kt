package com.overflow.cash.mvp.login

import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.overflow.cash.BuildConfig
import com.overflow.cash.R
import com.overflow.cash.net.API
import com.overflow.cash.net.AccountService
import com.overflow.cash.net.MerchantService
import com.overflow.cash.net.RxUtils
import com.overflow.libs.core.Data
import com.overflow.libs.core.OauthCredentialGenerator
import com.overflow.libs.core.Translations
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class LoginPresenter(private var context: Context, private var translations: Translations, private var disposable: CompositeDisposable, private var accountService: AccountService, private var merchantService: MerchantService, private var preferences: SharedPreferences, private var accountManager: AccountManager): LoginContract.Presenter {


    lateinit var view: LoginContract.View
    override fun attach(view: LoginContract.View) {
        this.view = view
    }



    override fun detach() {
        this.disposable.clear()
    }

    override fun login(input:Data) {
        val authHeader = OauthCredentialGenerator.generateCredentials(BuildConfig.auth_user, BuildConfig.auth_password)
        input["grant_type"] = "client_credentials"
        this.accountService.login("Basic $authHeader", input).enqueue(object:Callback<Data>{
            override fun onFailure(call: Call<Data>?, t: Throwable?) {
                view.showError(t!!)
            }
            override fun onResponse(call: Call<Data>?, response: Response<Data>?) {
                val resp = response!!.body()
                val accessToken = resp!!.getString("access_token")
                //preferences.edit().putString("accessToken", accessToken).apply()
                val bundle = Bundle()
                bundle.putString(AccountManager.KEY_ACCOUNT_NAME, input.getString("username"))
                bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, context.getString(R.string.account_type))
                bundle.putString(AccountManager.KEY_AUTHTOKEN, accessToken)
                bundle.putString(AccountManager.KEY_PASSWORD, BuildConfig.auth_password)
                val intent = Intent()
                intent.putExtras(bundle)
                val username = input.getString("username")
                val password = input.getString("password")
                findMerchant(username, password, accessToken, intent)
            }
        })
    }

    fun findMerchant(username: String, password: String, accessToken:String,intent: Intent) {
        val data = Data()
        data["username"] = username
        data["password"] = password
        disposable.add(accountService.findMerchant("Bearer $accessToken", data).compose(RxUtils.applySingleAsync()).subscribe({
            if(API.ok(it)){
                preferences.edit().putString("merchant", API.payload(it).toString()).apply()
                view.onLoginSuccess(intent)
            }else{
                view.showNoOk(translations.get(API.getError(it)))
            }
        }, {
            view.showError(it)
        }))
    }

}