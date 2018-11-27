package com.overflow.cash.utils

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import com.overflow.libs.picker.EasyImageFiles
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.util.*
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.EditText
import android.widget.TextView




fun Context.drawText(text:String, width:Int, height:Int, textSize: Float = 12F, backgroundColor: Int = randomColor()): Bitmap {
    val fileToSave = File(EasyImageFiles.tempImageDirectory(this), "$text.png")
    if(fileToSave.exists()){
        return BitmapFactory.decodeFile(fileToSave.absolutePath)
    }
    val textSizeSp = pxToSp(textSize.toInt())
    val fos = FileOutputStream(fileToSave)
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val area = Rect(0, 0, width, height)
    val canvas = Canvas(bitmap)
    val paint = Paint(ANTI_ALIAS_FLAG)
    paint.textSize = textSizeSp.toFloat()
    paint.color = backgroundColor
    canvas.drawRect(area, paint)
    val bounds = RectF(area)
    bounds.right = paint.measureText(text, 0, text.length)
    bounds.bottom = paint.descent() - paint.ascent()
    bounds.left += (area.width() - bounds.right) / 2.0f
    bounds.top += (area.height() - bounds.bottom) / 2.0f
    paint.color = ContextCompat.getColor(this, android.R.color.white)
    canvas.drawText(text, bounds.left, bounds.top - paint.ascent(), paint)
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
    fos.flush()
    fos.close()
    return bitmap
}

fun Context.pxToDp(px: Int): Int {
    val r = this.resources
    return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px.toFloat(), r.displayMetrics))
}

fun Context.pxToSp(px: Int): Int {
    val r = this.resources
    return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, px.toFloat(), r.displayMetrics))
}

fun randomColor(): Int{
    val rnd = Random()
    return Color.argb(255, rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255))
}

fun TextView.tinting(color: Int) {
    val col = ContextCompat.getColor(context, color)
    for (drawable in compoundDrawables) {
        if (drawable != null) {
            drawable.colorFilter = PorterDuffColorFilter(col, PorterDuff.Mode.SRC_IN)
        }
    }
}

fun EditText.tinting(color: Int) {
    val col = ContextCompat.getColor(context, color)
    for (drawable in compoundDrawables) {
        if (drawable != null) {
            drawable.colorFilter = PorterDuffColorFilter(col, PorterDuff.Mode.SRC_IN)
        }
    }
}
