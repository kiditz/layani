package com.overflow.cash.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat

fun Activity.shouldRequestPermissions(requestCode: Int):Boolean{
    val permissions = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_SYNC_SETTINGS,
            Manifest.permission.READ_SYNC_SETTINGS,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA
    )
    if(!hasPermissions(permissions)){
        ActivityCompat.requestPermissions(this, permissions, requestCode)
        return true
    }
    return false
}

fun Context.hasPermissions(permissions: Array<String>):Boolean{
    for (permission in permissions){
        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}