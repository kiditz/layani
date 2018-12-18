package com.overflow.cash.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.overflow.cash.R
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.*


fun String.toSpanned(): Spanned {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        return Html.fromHtml(this)
    }
}

fun Context.toast(message: String): Toast {
    return Toast.makeText(this, message, Toast.LENGTH_SHORT)
}

fun Activity.toast(message: String, duration: Int) {
    Toast.makeText(this, message, duration).show()
}

fun Activity.snack(message: String): Snackbar {
    return Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
}

fun Context.showMessage(title: CharSequence, message: CharSequence, button: MessageButton): AlertDialog.Builder {
    return showMessage(title, message, true, button)
}

fun Context.showMessage(title: CharSequence, message: CharSequence, showCancel: Boolean=true, button: MessageButton): AlertDialog.Builder {
    return showMessage(null, title, message, showCancel, button)
}



fun Context.showMessage(view: View?, title: CharSequence, message: CharSequence, showCancel: Boolean=true, button: MessageButton?): AlertDialog.Builder {
    val builder = AlertDialog.Builder(this)
    if (!TextUtils.isEmpty(title)) {
        builder.setTitle(title)
    }
    if (!TextUtils.isEmpty(message)) {
        builder.setMessage(message)
    }
    if (view != null) {
        builder.setView(view)
    }
    builder.setPositiveButton(R.string.yes) { dialog, which ->
        button?.ok(dialog, which)
    }
    if (showCancel) {
        builder.setNegativeButton(R.string.cancel) { dialog, which ->
            button?.cancel(dialog, which)
        }
    }

    return builder
}

abstract class MessageButton {
    abstract fun ok(dialog: DialogInterface, which: Int)

    abstract fun cancel(dialog: DialogInterface, which: Int)
}

open class MessageButtonHandle : MessageButton() {
    override fun ok(dialog: DialogInterface, which: Int) {

    }

    override fun cancel(dialog: DialogInterface, which: Int) {

    }
}


fun rupiah(value:Double, showPrefix:Boolean=true):String{
    val format = DecimalFormat.getCurrencyInstance(Locale("in", "ID")) as DecimalFormat
    format.isDecimalSeparatorAlwaysShown = false
    val symbols = DecimalFormatSymbols()
    if(showPrefix){
        symbols.currencySymbol = "Rp"
        format.negativePrefix = "Rp-"
    }
    format.negativeSuffix = ""
    format.decimalFormatSymbols = symbols
    return format.format(value)
}



fun parseRupiah(value:CharSequence):Double{
    var input = value.toString()
    return input.replace(Regex("[^\\d]"), "").toDouble()
}