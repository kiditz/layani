package com.overflow.cash.net

import android.accounts.AccountManager
import android.content.Context
import com.overflow.cash.BuildConfig
import com.overflow.cash.R
import com.overflow.cash.account.AccountGeneral
import com.overflow.libs.core.Data
import com.overflow.libs.core.OauthCredentialGenerator
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class TokenInterceptor(private val context: Context, private val accountManager: AccountManager, private val accountService: AccountService) : Interceptor {

    @Synchronized
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response? {
        if (accountManager.getAccountsByType(context.getString(R.string.account_type)).isEmpty()) {
            return null
        }
        val account = accountManager.getAccountsByType(context.getString(R.string.account_type))[0]
        var authToken = accountManager.peekAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS)
        val response = chain.proceed(chain.request().newBuilder().addHeader("Authorization", "Bearer $authToken").build())
        if(response.code() == 400 || response.code() == 401){
            val input = Data()
            input["grant_type"] = "client_credentials"
            val authHeader = OauthCredentialGenerator.generateCredentials(BuildConfig.auth_user, BuildConfig.auth_password)
            val login = accountService.login("Basic $authHeader", input).execute().body()
            authToken = login!!.getString("access_token")
            accountManager.setAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, authToken)
            return chain.proceed(chain.request().newBuilder().addHeader("Authorization", "Bearer $authToken").build())
        }
        return response
    }
}
