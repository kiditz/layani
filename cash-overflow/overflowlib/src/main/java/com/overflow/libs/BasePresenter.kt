package com.overflow.libs

interface BasePresenter<T : BaseView> {
    fun attach(view: T)
    fun detach()
}