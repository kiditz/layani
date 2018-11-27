package com.overflow.cash.net

import android.accounts.AccountManager
import android.app.Activity
import com.overflow.cash.Constant
import com.overflow.cash.R
import com.overflow.cash.account.AccountSyncAdapter
import com.overflow.cash.utils.snack
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import retrofit2.HttpException
import timber.log.Timber
import java.lang.NullPointerException
import java.net.ConnectException
import java.net.SocketTimeoutException


class NetworkExHandler(internal var translations: Translations) {

    fun errorHandle(activity: Activity, error: Throwable) {
        try {
            Timber.e(error)
            if (error is HttpException) {
                val errString: String = error.response().errorBody()?.string().toString()
                val errorBody = Data(errString)
                if (error.response().code() == 401 || error.response().code() == 400) {
                    if (errorBody.getString("error") == "invalid_token") {
                        //val accountManager = AccountManager.get(activity.baseContext)
                        //val account = accountManager.getAccountsByType(activity.getString(R.string.account_type)).first()
                        //Timber.e("Synchronize account: %s", account.name)
                        //AccountSyncAdapter.syncAccount(account, activity)
                        return
                    }
                    val errorMessage = translations.get(errorBody.getString("error_description"))
                    activity.snack(errorMessage).show()
                } else {
                    activity.snack(errString).show()
                }
            } else if (error is ConnectException) {
                activity.snack(translations.get(Constant.TranslationsKey.NO_INTERNET)).show()
            }
            else if( error is SocketTimeoutException){
                activity.snack(translations.get(Constant.TranslationsKey.CONNECTION_TIMEOUT)).show()
            }
            else {
                Timber.e(error)
                activity.snack(translations.get(Constant.TranslationsKey.SYSTEM_ERROR)).show()
            }
        } catch (e: Exception) {
            if(e !is NullPointerException){
                Timber.e(e)
                activity.snack(translations.get(Constant.TranslationsKey.SYSTEM_ERROR)).show()
            }

        }
    }
}
