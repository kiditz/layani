package com.overflow.cash.activity

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.overflow.cash.R
import com.overflow.cash.account.AccountGeneral
import com.overflow.cash.account.AccountSyncAdapter
import com.overflow.cash.mvp.register.RegisterContract
import com.overflow.cash.mvp.register.RegisterPresenter
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.*
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.functions.Function3
import kotlinx.android.synthetic.main.activity_register.*
import javax.inject.Inject

class RegisterActivity : AppCompatActivity(), RegisterContract.View {

    @Inject
    lateinit var translations: Translations
    @Inject
    lateinit var registerPresenter: RegisterPresenter
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    @Inject
    lateinit var accountManager: AccountManager
    @Inject
    lateinit var preferences: SharedPreferences
    lateinit var store: Data

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        this.store = Data(intent?.extras?.getString("store"))
        this.registerPresenter.attach(this)
        validate()
        btnRegister.setOnClickListener {
            btnRegister.isEnabled = false
            progress_bar.visibility = View.VISIBLE
            val data = Data()
            data["password"] = ed_password.text.toString()
            data["username"] = ed_username.text.toString()
            data["fullname"] = edStoreOwnerName.text.toString()
            data["store"] = store
            this.registerPresenter.addStore(data)
        }
    }

    private fun validate(){
        val usernameObserve = this.validateNotEmpty(ed_username, usernameWrapper, translations.get(Constant.TranslationsKey.REQUIRED_VALUE_USERNAME))
        val passwordObserve = this.validateLengthGreaterThan(ed_password, password_wrapper,8 - 1,  translations.get(Constant.TranslationsKey.INVALID_PASSWORD_LENGTH))
        val ownerNameObserve = this.validateNotEmpty(edStoreOwnerName, storeOwnerNameWrapper, translations.get(Constant.TranslationsKey.REQUIRED_VALUE_STORE_OWNER_NAME))
        Observable.combineLatest(usernameObserve, passwordObserve, ownerNameObserve, Function3{username:Boolean, password:Boolean, ownerName:Boolean -> ownerName && username && password}).subscribe { isValid ->
            btnRegister.isEnabled = isValid
        }
    }

    override fun showError(error: Throwable) {
        progress_bar.visibility = View.GONE
        btnRegister.isEnabled = true
        networkExHandler.errorHandle(this, error)
    }

    override fun showNoOk(res: String) {
        progress_bar.visibility = View.GONE
        btnRegister.isEnabled = true
        snack(res).show()
    }

    override fun onAccountCreated(data: Data) {
        progress_bar.visibility = View.GONE
        btnRegister.isEnabled = true
        val bundle = Bundle()
        bundle.putBoolean(Constant.CREATE_ACCOUNT_SUCCESS, true)
        moveTo(LoginActivity::class.java, bundle)

    }

    override fun showEmpty() {
        TODO("not implemented")
    }

    override fun showNotConnected(res: String) {
        progress_bar.visibility = View.GONE
        btnRegister.isEnabled = true
        snack(res).show()
    }

    override fun onLoginSuccess(intent: Intent) {
        val username = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
        val authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN)
        val password = intent.getStringExtra(AccountManager.KEY_PASSWORD)
        val account = Account(username, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE))
        accountManager.addAccountExplicitly(account, password, intent.extras)
        accountManager.setAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, authtoken)
        AccountSyncAdapter.syncAccount(account, this)
        moveTo(MenuActivity::class.java, intent.extras)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return home(item)
    }
    override fun onDestroy() {
        super.onDestroy()
        registerPresenter.detach()
    }
}
