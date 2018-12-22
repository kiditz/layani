package com.overflow.cash.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import com.jakewharton.rxbinding2.widget.RxTextView
import com.overflow.cash.R
import com.overflow.cash.activity.Constant
import com.overflow.cash.net.RxUtils
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_blank.*
import kotlinx.android.synthetic.main.fragment_blank.view.*
import kotlinx.android.synthetic.main.success_message.*
import java.util.concurrent.TimeUnit

open class BaseFragment:Fragment() {

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    open fun showMessageInBlankLayout(title: String, message: String= Constant.TEXT_EMPTY) {
        blank_layout?.visibility = View.VISIBLE
        blank_layout?.tv_description?.text = message
        blank_layout?.tv_title?.text = title
    }

    open fun hideMessage(){
        blank_layout?.visibility = View.GONE
    }


    fun showSuccessMessage(message:String){
        this.tv_success_message?.text = message
        this.tv_success_message?.visibility = View.VISIBLE
        this.tv_success_message?.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.colorAccent))
        RxTextView.textChanges(tv_success_message).compose(RxUtils.applyObservableAsync()).debounce(5, TimeUnit.SECONDS).subscribe {
            activity!!.runOnUiThread {
                tv_success_message?.visibility = View.GONE
            }
        }
    }

    fun showErrorMessage(message:String){
        this.tv_success_message.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.red_light))
        this.tv_success_message?.text = message
        this.tv_success_message?.visibility = View.VISIBLE
        RxTextView.textChanges(tv_success_message).compose(RxUtils.applyObservableAsync()).debounce(5, TimeUnit.SECONDS).subscribe {
            activity!!.runOnUiThread {
                tv_success_message?.visibility = View.GONE
            }
        }
    }
}