package com.overflow.cash

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.overflow.cash.account.AccountGeneral
import com.overflow.cash.account.AccountSyncAdapter
import com.overflow.cash.mvp.register.RegisterContract
import com.overflow.cash.mvp.register.RegisterPresenter
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.home
import com.overflow.cash.utils.moveTo
import com.overflow.cash.utils.snack
import com.overflow.cash.utils.validateNotEmpty
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
            progressBar.visibility = View.VISIBLE
            val data = Data()
            data["password"] = edPassword.text.toString()
            data["username"] = edUsername.text.toString()
            data["fullname"] = edStoreOwnerName.text.toString()
            data["store"] = store
            this.registerPresenter.addStore(data)
        }
    }

    private fun validate(){
        val usernameObserve = this.validateNotEmpty(edUsername, usernameWrapper, translations.get(Constant.TranslationsKey.REQUIRED_VALUE_USERNAME))
        val passwordObserve = this.validateNotEmpty(edPassword, passwordWrapper, translations.get(Constant.TranslationsKey.REQUIRED_VALUE_PASSWORD))
        val ownerNameObserve = this.validateNotEmpty(edStoreOwnerName, storeOwnerNameWrapper, translations.get(Constant.TranslationsKey.REQUIRED_VALUE_STORE_OWNER_NAME))
        Observable.combineLatest(usernameObserve, passwordObserve, ownerNameObserve, Function3{username:Boolean, password:Boolean, ownerName:Boolean -> ownerName && username && password}).subscribe { isValid ->
            btnRegister.isEnabled = isValid
        }
    }

    override fun showError(error: Throwable) {
        progressBar.visibility = View.GONE
        btnRegister.isEnabled = true
        networkExHandler.errorHandle(this, error)
    }

    override fun showNoOk(res: String) {
        progressBar.visibility = View.GONE
        btnRegister.isEnabled = true
        snack(res).show()
    }

    override fun onAccountCreated(data: Data) {
        this.preferences.edit().putString("merchant", data.toString()).apply()
        progressBar.visibility = View.GONE
        btnRegister.isEnabled = true
        registerPresenter.login(data)
    }

    override fun showEmpty() {
        TODO("not implemented")
    }

    override fun showNotConnected(res: String) {
        progressBar.visibility = View.GONE
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
