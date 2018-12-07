package com.overflow.cash

import android.accounts.Account
import android.accounts.AccountAuthenticatorActivity
import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import com.jakewharton.rxbinding2.widget.RxTextView
import com.overflow.cash.account.AccountGeneral
import com.overflow.cash.mvp.login.LoginContract
import com.overflow.cash.mvp.login.LoginPresenter
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.moveTo
import com.overflow.cash.utils.snack
import com.overflow.cash.utils.validateLengthGreaterThan
import com.overflow.cash.utils.validateNotEmpty
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.activity_login.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LoginActivity : AccountAuthenticatorActivity(), LoginContract.View {
    @Inject
    lateinit var translations: Translations
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    @Inject
    lateinit var accountManager: AccountManager
    @Inject
    lateinit var preferences: SharedPreferences
    @Inject
    lateinit var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_login)
        this.presenter.attach(this)
        if(intent.getBooleanExtra(Constant.CREATE_ACCOUNT_SUCCESS, false)){
            tv_success_message.visibility = View.VISIBLE
            tv_success_message.setText(R.string.create_store_success)
            RxTextView.textChanges(tv_success_message).debounce(2, TimeUnit.SECONDS).subscribe{
                runOnUiThread{
                    tv_success_message.visibility = View.GONE
                }
            }
        }
        btnCreateStore?.setOnClickListener {
            moveTo(CreateStoreActivity::class.java)
        }
        btn_login?.setOnClickListener {
            val data = Data()
            btn_login?.isEnabled = true
            progress_bar?.visibility = View.VISIBLE
            data["username"]= ed_username.text.toString()
            data["password"]= ed_password.text.toString()
            presenter.login(data)
        }

        validateInput()
    }

    private fun validateInput(){
        //Validate username must not empty
        val usernameObserve = this.validateNotEmpty(ed_username, usernameWrapper, translations.get(Constant.TranslationsKey.REQUIRED_VALUE_USERNAME))
        //Validate password length must gt 8
        val passwordObserve = this.validateLengthGreaterThan(ed_password, password_wrapper, 8 - 1,translations.get(Constant.TranslationsKey.INVALID_PASSWORD_LENGTH))
        Observable.combineLatest(usernameObserve, passwordObserve, BiFunction{ username:Boolean, password:Boolean -> username && password}).subscribe { isValid ->
            btn_login.isEnabled = isValid
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        Timber.i("Back Pressed")
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        this.presenter.detach()
    }

    override fun onLoginSuccess(intent: Intent) {
        Timber.i("Login")
        btn_login?.isEnabled = true
        progress_bar?.visibility = View.GONE
        val authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN)
        val account = Account(ed_username.text.toString(), intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE))
        accountManager.addAccountExplicitly(account, BuildConfig.auth_password, intent.extras)
        accountManager.setAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, authtoken)
        setAccountAuthenticatorResult(intent.extras)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun showError(error: Throwable) {
        btn_login?.isEnabled = true
        progress_bar?.visibility = View.GONE
        networkExHandler.errorHandle(this, error)
    }

    override fun showNoOk(res: String) {
        snack(res).show()
        progress_bar?.visibility = View.GONE
    }

    override fun showEmpty() {
        progress_bar?.visibility = View.GONE
    }

    override fun showNotConnected(res: String) {
        btn_login?.isEnabled = true
        progress_bar?.visibility = View.GONE
        snack(res).show()
    }
}
