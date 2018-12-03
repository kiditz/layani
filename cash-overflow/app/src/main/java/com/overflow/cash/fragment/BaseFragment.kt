package com.overflow.cash.fragment

import android.content.Context
import android.support.v4.app.Fragment
import android.view.View
import com.overflow.cash.Constant
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_blank.*
import kotlinx.android.synthetic.main.fragment_blank.view.*

open class BaseFragment:Fragment() {
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    open fun showMessage(title: String, message: String=Constant.TEXT_EMPTY) {
        blank_layout?.visibility = View.VISIBLE
        blank_layout?.tv_description?.text = message
        blank_layout?.tv_title?.text = title
    }

    open fun hideMessage(){
        blank_layout?.visibility = View.GONE
    }
}