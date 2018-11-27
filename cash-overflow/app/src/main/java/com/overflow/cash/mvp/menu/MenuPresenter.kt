package com.overflow.cash.mvp.menu

import android.accounts.AccountManager
import android.content.Context
import com.overflow.cash.R
import io.reactivex.disposables.CompositeDisposable

class MenuPresenter (private val context: Context, private val accountManager: AccountManager, private val disposable: CompositeDisposable): MenuContract.Presenter {
    lateinit var view:MenuContract.View
    override fun attach(view: MenuContract.View) {
        this.view = view

    }

    override fun detach() {
        disposable.clear()
    }
}