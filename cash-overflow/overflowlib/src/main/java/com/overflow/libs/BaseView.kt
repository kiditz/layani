package com.overflow.libs

import com.overflow.libs.core.Data

interface BaseView  {
    fun showError(error:Throwable)
    fun showNoOk(res: String)
    fun showEmpty()
    fun showNotConnected(res: String)
}