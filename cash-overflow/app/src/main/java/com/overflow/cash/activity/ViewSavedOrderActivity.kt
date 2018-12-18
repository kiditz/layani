package com.overflow.cash.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import com.overflow.cash.R
import com.overflow.cash.fragment.TransactionHistoryFragment
import com.overflow.cash.utils.replaceContent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector

import javax.inject.Inject

class ViewSavedOrderActivity:BaseActivity(), HasSupportFragmentInjector {


    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.container_activity)
        replaceContent(R.id.container, TransactionHistoryFragment.newInstance(false, Constant.TransactionStatus.CREATED))
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }
}