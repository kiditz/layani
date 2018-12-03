package com.overflow.cash

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.overflow.cash.utils.home
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.fragment_blank.*
import kotlinx.android.synthetic.main.fragment_blank.view.*

open class BaseActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    open fun showMessage(title: String, message: String=Constant.TEXT_EMPTY) {
        blank_layout?.visibility = View.VISIBLE
        blank_layout?.tv_description?.text = message
        blank_layout?.tv_title?.text = title
    }

    open fun hideMessage(){
        blank_layout?.visibility = View.GONE
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return home(item!!)
    }
}