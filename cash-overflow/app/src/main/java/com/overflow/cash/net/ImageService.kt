package com.overflow.cash.net

import android.accounts.AccountManager
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.overflow.cash.BuildConfig
import com.overflow.cash.R
import com.overflow.cash.account.AccountGeneral
import com.overflow.cash.dagger.GlideApp
import com.overflow.cash.utils.drawText
import timber.log.Timber

class ImageService(private val context: Context, private val accountManager: AccountManager) {

    fun loadDocument(img: ImageView, documentId: Long?, defaultText: String = "FL") {
        val account = accountManager.getAccountsByType(context.getString(R.string.account_type)).first()
        val accessToken = accountManager.peekAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS)
        val resourceUrl = BuildConfig.base_url + "/cash/document/find?id=" + documentId
        val glideUrl = GlideUrl(resourceUrl, LazyHeaders.Builder().addHeader("Authorization", "Bearer $accessToken").build())
        val bmp = getDefaultImage(defaultText)
        if(documentId != null && documentId > 0L){
            Timber.d("Load document with id %s", documentId)
            GlideApp.with(context).load(glideUrl).placeholder(bmp).dontAnimate().dontTransform().error(bmp).into(img)
        }else{
            img.setImageBitmap(bmp.bitmap)
        }
    }

    private fun getDefaultImage(text: String): BitmapDrawable {
        val color = ContextCompat.getColor(context, R.color.colorPrimary)
        return BitmapDrawable(context.resources, context.drawText(getAbbreviation(text), 140, 120, 16f, backgroundColor = color))
    }

    private fun getAbbreviation(text:String):String{
        if(text.contains(" ")){
            val split = text.split(" ")
            val builder = StringBuilder()
            split.mapIndexed { index, s ->
                if(index <= 2){
                    builder.append(s.substring(0, 1).toUpperCase())
                }
            }
            return builder.toString()
        }
        return text.substring(0, 1).toUpperCase()
    }
}