package com.overflow.cash.activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.jakewharton.rxbinding2.widget.RxTextView
import com.overflow.cash.R
import com.overflow.cash.net.RxUtils
import com.overflow.cash.utils.home
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.fragment_blank.*
import kotlinx.android.synthetic.main.fragment_blank.view.*
import kotlinx.android.synthetic.main.success_message.*
import java.util.concurrent.TimeUnit

open class BaseActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

    }

    open fun showBlankMessage(title: String, message: String= Constant.TEXT_EMPTY) {
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

    fun showSuccessMessage(message:String){
        this.tv_success_message?.text = message
        this.tv_success_message?.visibility = View.VISIBLE
        this.tv_success_message?.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
        RxTextView.textChanges(tv_success_message).compose(RxUtils.applyObservableAsync()).debounce(5, TimeUnit.SECONDS).subscribe {
            runOnUiThread {
                tv_success_message?.visibility = View.GONE
            }
        }
    }

    fun showErrorMessage(message:String){
        this.tv_success_message.setBackgroundColor(ContextCompat.getColor(this, R.color.red_light))
        this.tv_success_message?.text = message
        this.tv_success_message?.visibility = View.VISIBLE
        RxTextView.textChanges(tv_success_message).compose(RxUtils.applyObservableAsync()).debounce(5, TimeUnit.SECONDS).subscribe {
            runOnUiThread {
                tv_success_message?.visibility = View.GONE
            }
        }
    }
}