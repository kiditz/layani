package com.overflow.cash.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import com.overflow.cash.R
import com.overflow.cash.fragment.PaymentTransactionFragment
import com.overflow.cash.utils.replaceContent
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import timber.log.Timber
import javax.inject.Inject

class PaymentTransactionDispatcherActivity : BaseActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.container_frame_layout)
        try {
            val fragment = PaymentTransactionFragment()
            fragment.arguments = intent.extras
            replaceContent(R.id.container, fragment)
        }catch (e:Exception){
            Timber.e(e)
        }

    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }

}
