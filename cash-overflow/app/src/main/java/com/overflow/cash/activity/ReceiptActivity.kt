package com.overflow.cash.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.overflow.cash.R
import com.overflow.cash.fragment.ReceiptFragment
import com.overflow.cash.utils.home
import com.overflow.cash.utils.moveTo
import com.overflow.cash.utils.replaceContent
import com.overflow.cash.utils.shouldRequestPermissions
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class ReceiptActivity : AppCompatActivity(), HasSupportFragmentInjector {


    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    lateinit var receiptFragment: ReceiptFragment
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        shouldRequestPermissions(Constant.REQUEST_PERMISSION_CODE)
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.container_activity)
        receiptFragment = ReceiptFragment.newInstance(intent.getStringExtra(Constant.ARG_SALES))
        replaceContent(R.id.container, receiptFragment)
    }



    override fun onBackPressed() {
        if(intent.getBooleanExtra("just_back", false)){
            super.onBackPressed()
            return
        }
        moveTo(MenuActivity::class.java, intent.extras)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_receipt, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item!!.itemId){
            R.id.action_download -> receiptFragment.screenShoot()
            R.id.action_share -> receiptFragment.share()
            else -> home(item)
        }

    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }
}
