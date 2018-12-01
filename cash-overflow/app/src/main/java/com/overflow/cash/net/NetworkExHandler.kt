package com.overflow.cash.net

import android.app.Activity
import com.overflow.cash.Constant
import com.overflow.cash.R
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
                if (error.response().code() == 500) {
                    activity.snack(activity.getString(R.string.system_err)).show()
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
