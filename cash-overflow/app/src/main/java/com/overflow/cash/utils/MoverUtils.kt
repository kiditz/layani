package com.overflow.cash.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import com.overflow.cash.R
import java.util.*


fun Activity.snack(message: String, duration: Int): Snackbar {
    return Snackbar.make(findViewById(android.R.id.content), message, duration)
}

fun Context.moveTo(destination: Class<out Activity>, bundle: Bundle?): Boolean{
    val i = Intent(this, destination)
    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    if (bundle != null)
        i.putExtras(bundle)
    startActivity(i)
    return true
}

fun FragmentActivity.replaceContent(targetFragment: Fragment): FragmentActivity{
    return replaceContent(R.id.menu_content, targetFragment)
}

fun FragmentActivity.replaceContent(container:Int, targetFragment: Fragment): FragmentActivity{
    val transaction = this.supportFragmentManager.beginTransaction()
    transaction.replace(container, targetFragment)
    //transaction.addToBackStack(null)
    transaction.commit()
    return this
}

fun Context.moveTo(destination: Class<out Activity>): Boolean {
    moveTo(destination, null)
    return true
}



fun Activity.home(menuItem: MenuItem?): Boolean{
    if(menuItem == null){
        return false
    }

    if (menuItem.itemId == android.R.id.home) {
        onBackPressed()
        return true
    }
    return false
}


//fun randomColor(): Int{
//    val rnd = Random()
//    return Color.argb(255, rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255))
//}

fun Activity.hideKeyboard(){
    val focus = this.currentFocus
    if(focus != null){
        val imm : InputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }
}

fun Fragment.hideKeyboard(){
    val focus = this.view
    if(focus != null){
        val imm : InputMethodManager = this.activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }
}

fun Context.currentLocale():Locale{
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
        resources.configuration.locales.get(0)
    }else{
        resources.configuration.locale
    }


}
