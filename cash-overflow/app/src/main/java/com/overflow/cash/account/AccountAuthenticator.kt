package com.overflow.cash.account

import android.accounts.*
import android.accounts.AccountManager.KEY_BOOLEAN_RESULT
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.overflow.cash.BuildConfig
import com.overflow.cash.activity.LoginActivity
import com.overflow.cash.net.AccountService
import com.overflow.libs.core.Data
import com.overflow.libs.core.OauthCredentialGenerator
import timber.log.Timber

class AccountAuthenticator(private val context: Context) : AbstractAccountAuthenticator(context) {
    private lateinit var accountService: AccountService
    private var oauthToken: String? = null

    fun setAccountService(accountService: AccountService) {
        this.accountService = accountService
    }



    override fun editProperties(response: AccountAuthenticatorResponse, accountType: String): Bundle? {
        return null
    }

    override fun addAccount(response: AccountAuthenticatorResponse, accountType: String, authTokenType: String, requiredFeatures: Array<String>, options: Bundle): Bundle {
        val intent = Intent(context, LoginActivity::class.java)
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType)
        intent.putExtra(AccountGeneral.ARG_AUTH_TYPE, authTokenType)
        intent.putExtra(AccountGeneral.ARG_IS_ADDING_NEW_ACCOUNT, true)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
        val bundle = Bundle()
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return bundle
    }

    @Throws(NetworkErrorException::class)
    override fun confirmCredentials(response: AccountAuthenticatorResponse, account: Account, options: Bundle): Bundle? {

        return null
    }

    override fun getAuthToken(response: AccountAuthenticatorResponse, account: Account, authTokenType: String, options: Bundle): Bundle {
        if (authTokenType != AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY && authTokenType != AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS) {
            val result = Bundle()
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType")
            return result
        }
        val am = AccountManager.get(context)
        this.oauthToken = am.peekAuthToken(account, authTokenType)
        Timber.i("Oauth Token Peeked: %s", oauthToken)
        if (TextUtils.isEmpty(oauthToken)) {
            val password = am.getPassword(account)
            if (password != null) {
                try {
                    this.oauthToken = doLogin()!!.getString("access_token")
                }catch (e:Exception){
                    this.oauthToken = "no token here"
                }
            }
        }
        val result = Bundle()
        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
        result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
        result.putString(AccountManager.KEY_AUTHTOKEN, oauthToken)
        return result
    }

    override fun getAuthTokenLabel(authTokenType: String): String {
        return AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS_LABEL
    }

    override fun updateCredentials(response: AccountAuthenticatorResponse, account: Account, authTokenType: String, options: Bundle): Bundle? {
        return null
    }

    override fun hasFeatures(response: AccountAuthenticatorResponse, account: Account, features: Array<String>): Bundle {
        val result = Bundle()
        result.putBoolean(KEY_BOOLEAN_RESULT, false)
        return result
    }

    private fun doLogin(): Data? {
        val input = Data()
        input["grant_type"] = "client_credentials"
        val authHeader = OauthCredentialGenerator.generateCredentials(BuildConfig.auth_user, BuildConfig.auth_password)
        return accountService.login("Basic $authHeader", input).execute().body()
    }

    @Throws(NetworkErrorException::class)
    override fun getAccountRemovalAllowed(response: AccountAuthenticatorResponse, account: Account): Bundle {
        val result = Bundle()
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, true)
        return result
    }
}
