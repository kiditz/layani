package com.overflow.cash.net

import android.accounts.AccountManager
import android.content.Context
import com.overflow.cash.R
import com.overflow.cash.account.AccountGeneral
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import java.io.IOException

class TokenAuthenticator(private val context: Context, private val accountManager: AccountManager) : Authenticator {

    @Throws(IOException::class)
    override fun authenticate(route: Route, response: Response): Request? {
        Timber.i("Response Code : %s", response.code())
        Timber.i("Response Body : %s", response.body()?.string())
        val account = accountManager.getAccountsByType(context.getString(R.string.account_type)).first()
        Timber.i("Account : %s", account.name)
        if(accountManager.getAccountsByType(context.getString(R.string.account_type)).isEmpty()){
            return null
        }
        var authToken = accountManager.peekAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS)
        return if(response.code() == 400 || response.code() == 401){
            accountManager.invalidateAuthToken(context.getString(R.string.account_type), authToken)
            authToken = accountManager.blockingGetAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, false)
            Timber.i("Auth Token : %s", authToken)
            accountManager.setAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, authToken)
            authToken = accountManager.peekAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS)
            Timber.i("New Auth Token : %s", authToken)
            response.request().newBuilder().addHeader("Authorization", "Bearer $authToken").build()
        }else{
            response.request().newBuilder().addHeader("Authorization", "Bearer $authToken").build()
        }
    }
}
