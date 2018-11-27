package com.overflow.cash.utils

import android.os.Bundle

import com.overflow.libs.core.CoreException

fun checkArgumentKeys(bundle: Bundle, vararg keys: String) {
    for (key in keys) {
        if (!bundle.containsKey(key)) {
            throw CoreException("required.keys.$keys")
        }
    }
}