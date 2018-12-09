package com.overflow.cash.fragment

import android.accounts.AccountManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.RxView
import com.overflow.cash.MenuActivity
import com.overflow.cash.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_setting.*
import javax.inject.Inject

class SettingFragment:Fragment() {
    lateinit var menuActivity:MenuActivity
    @Inject
    lateinit var accountManager: AccountManager
    @Inject
    lateinit var preferences: SharedPreferences
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.menuActivity = activity as MenuActivity
    }
    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        RxView.clicks(lv_sign_out).subscribe {
            if (accountManager.getAccountsByType(getString(R.string.account_type)).isEmpty()) {
                return@subscribe
            }
            val account = accountManager.getAccountsByType(getString(R.string.account_type))[0]
            accountManager.removeAccount(account, null, null)
            preferences.edit().remove("outlet").apply()
            menuActivity.onNotLogin()
        }
    }
}