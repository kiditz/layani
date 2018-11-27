package com.overflow.cash

import android.accounts.Account
import android.accounts.AccountAuthenticatorActivity
import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import com.overflow.cash.account.AccountGeneral
import com.overflow.cash.mvp.login.LoginContract
import com.overflow.cash.mvp.login.LoginPresenter
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.moveTo
import com.overflow.cash.utils.snack
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_login.*
import timber.log.Timber
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
        btnCreateStore?.setOnClickListener {
            moveTo(CreateStoreActivity::class.java)
        }
        btnLogin?.setOnClickListener {
            val data = Data()
            btnLogin?.isEnabled = true
            progressBar?.visibility = View.VISIBLE
            data["username"]= edUsername.text.toString()
            data["password"]= edPassword.text.toString()
            presenter.login(data)
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
        btnLogin?.isEnabled = true
        progressBar?.visibility = View.GONE
        val authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN)
        val account = Account(edUsername.text.toString(), intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE))
        accountManager.addAccountExplicitly(account, BuildConfig.auth_password, intent.extras)
        accountManager.setAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, authtoken)
        setAccountAuthenticatorResult(intent.extras)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun showError(error: Throwable) {
        btnLogin?.isEnabled = true
        progressBar?.visibility = View.GONE
        networkExHandler.errorHandle(this, error)
    }

    override fun showNoOk(res: String) {
        snack(res).show()
    }

    override fun showEmpty() {
    }

    override fun showNotConnected(res: String) {
        btnLogin?.isEnabled = true
        progressBar?.visibility = View.GONE
        snack(res).show()
    }
}
