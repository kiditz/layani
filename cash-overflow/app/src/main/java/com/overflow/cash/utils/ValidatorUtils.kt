package com.overflow.cash.utils

import android.content.Context
import android.support.design.widget.TextInputLayout
import android.widget.EditText
import android.widget.TextView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.overflow.cash.R
import io.reactivex.Observable
import timber.log.Timber

fun Context.validateNotEmpty(textView: TextView, textInputLayout: TextInputLayout, message:String="", resId:Int= R.style.AppTheme_TextInputLayout_ErrorRed, skipCount:Long=1L):Observable<Boolean>{
    val emptyObserve = RxTextView.textChanges(textView).skip(skipCount).map { text -> !text.isEmpty() }
    emptyObserve.subscribe { valid ->
        textInputLayout.error = message
        textInputLayout.isErrorEnabled = !valid
        textInputLayout.setErrorTextAppearance(resId)
    }
    return emptyObserve
}

fun Context.validateGreaterThan(textView: TextView, textInputLayout: TextInputLayout, value:Int=0, message:String="", resId:Int= R.style.AppTheme_TextInputLayout_ErrorRed, skipCount:Long=1L):Observable<Boolean>{
    var emptyObserve = RxTextView.textChanges(textView).skip(skipCount).map { text -> text.toString().isNotEmpty() && text.toString().toInt() > value}
    emptyObserve.subscribe ({ valid ->
        textInputLayout.error = message
        textInputLayout.isErrorEnabled = !valid
        textInputLayout.setErrorTextAppearance(resId)
    }, {
        Timber.e(it)
    })
    return emptyObserve
}
fun Context.validateGreaterThanOrEquals(textView: TextView, textInputLayout: TextInputLayout, value:Double=0.0, message:String="", resId:Int= R.style.AppTheme_TextInputLayout_ErrorRed, skipCount:Long=1L):Observable<Boolean>{
    var emptyObserve = RxTextView.textChanges(textView).skip(skipCount).map { text -> text.toString().isNotEmpty() && text.toString().toDouble() >= value}
    emptyObserve.subscribe ({ valid ->
        textInputLayout.error = message
        textInputLayout.isErrorEnabled = !valid
        textInputLayout.setErrorTextAppearance(resId)
    }, {
        Timber.e(it)
    })
    return emptyObserve
}
fun Context.validateLessThan(textView: TextView, textInputLayout: TextInputLayout, value:Double=0.0, message:String="", resId:Int= R.style.AppTheme_TextInputLayout_ErrorRed, skipCount:Long=1L):Observable<Boolean>{
    var emptyObserve = RxTextView.textChanges(textView).skip(skipCount).map { text -> text.toString().isNotEmpty() && text.toString().toDouble() < value}
    emptyObserve.subscribe ({ valid ->
        textInputLayout.error = message
        textInputLayout.isErrorEnabled = !valid
        textInputLayout.setErrorTextAppearance(resId)
    }, {
        Timber.e(it)
    })
    return emptyObserve
}

fun Context.validateLessThan(textView: TextView, textInputLayout: TextInputLayout, value:Int=0, message:String="", resId:Int= R.style.AppTheme_TextInputLayout_ErrorRed, skipCount:Long=1L):Observable<Boolean>{
    var emptyObserve = RxTextView.textChanges(textView).skip(skipCount).map { text -> text.toString().isNotEmpty() && text.toString().toInt() < value}
    emptyObserve.subscribe ({ valid ->
        textInputLayout.error = message
        textInputLayout.isErrorEnabled = !valid
        textInputLayout.setErrorTextAppearance(resId)
    }, {
        Timber.e(it)
    })
    return emptyObserve
}

fun Context.validateLengthLessThan(textView: TextView, textInputLayout: TextInputLayout, value:Int=0, message:String="", resId:Int= R.style.AppTheme_TextInputLayout_ErrorRed, skipCount:Long=1L):Observable<Boolean>{
    var emptyObserve = RxTextView.textChanges(textView).skip(skipCount).map { text -> text.length < value}
    emptyObserve.subscribe ({ valid ->
        textInputLayout.error = message
        textInputLayout.isErrorEnabled = !valid
        textInputLayout.setErrorTextAppearance(resId)
    }, {
        Timber.e(it)
    })
    return emptyObserve
}

fun Context.validateLengthGreaterThan(textView: TextView, textInputLayout: TextInputLayout, value:Int=0, message:String="", resId:Int= R.style.AppTheme_TextInputLayout_ErrorRed, skipCount:Long=1L):Observable<Boolean>{
    var emptyObserve = RxTextView.textChanges(textView).skip(skipCount).map { text -> text.length > value}
    emptyObserve.subscribe ({ valid ->
        textInputLayout.error = message
        textInputLayout.isErrorEnabled = !valid
        textInputLayout.setErrorTextAppearance(resId)
    }, {
        Timber.e(it)
    })
    return emptyObserve
}

fun Context.validateLessThanOrEquals(textView: TextView, textInputLayout: TextInputLayout, value:Double=0.0, message:String="", resId:Int= R.style.AppTheme_TextInputLayout_ErrorRed, skipCount:Long=1L):Observable<Boolean>{
    var emptyObserve = RxTextView.textChanges(textView).skip(skipCount).map { text -> text.toString().isNotEmpty() && text.toString().toDouble() <= value}
    emptyObserve.subscribe ({ valid ->
        textInputLayout.error = message
        textInputLayout.isErrorEnabled = !valid
        textInputLayout.setErrorTextAppearance(resId)
    }, {
        Timber.e(it)
    })
    return emptyObserve
}