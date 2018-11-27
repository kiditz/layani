package com.overflow.cash.net

import android.content.Context

import android.net.ConnectivityManager
import com.overflow.libs.core.Data


object API {
    const val SIZE = 10
    const val MIN_PAGE = 1

    fun ok(data: Data): Boolean {
        return data.getString("status") != null && data.getString("status") == "OK"
    }

    fun fail(data: Data): Boolean {
        return data.getString("status") != null && data.getString("status") == "FAIL"
    }

    fun payload(data: Data): Data {
        return data.getData("payload")
    }

    fun payloads(data: Data): List<Data> {
        return data.getList("payload")
    }

    fun getError(response: Data): String {
        val message: String = response.getString("message")
        val sb = StringBuilder()
        if (response.containsKey("key")) {
            sb.append(message + "." + response.getString("key"))
        }else{
            sb.append(message)
        }
        return sb.toString()
    }

    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }
}
