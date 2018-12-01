package com.overflow.cash.net

import android.accounts.AccountManager
import android.content.Context
import com.overflow.cash.R
import com.overflow.cash.account.AccountGeneral
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class TokenInterceptor(private val context: Context, private val accountManager: AccountManager) : Interceptor {

    @Synchronized
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response? {
        if (accountManager.getAccountsByType(context.getString(R.string.account_type)).isEmpty()) {
            return null
        }
        val account = accountManager.getAccountsByType(context.getString(R.string.account_type))[0]
        var authToken = accountManager.peekAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS)
        return chain.proceed(chain.request().newBuilder().addHeader("Authorization", "Bearer $authToken").build())
    }
}
