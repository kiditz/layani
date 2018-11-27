package com.overflow.cash.dagger

import android.content.Context
import android.util.Log
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.overflow.cash.R


@GlideModule
class DefaultGlideModule : AppGlideModule(){
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        builder.setLogLevel(Log.ERROR)
        builder.setDefaultRequestOptions(RequestOptions())
    }
}
