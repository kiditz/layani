package com.overflow.cash.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.FileProvider
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import com.overflow.cash.BuildConfig
import com.overflow.cash.Constant
import com.overflow.cash.R
import com.overflow.cash.utils.currentLocale
import com.overflow.cash.utils.rupiah
import com.overflow.cash.utils.shouldRequestPermissions
import com.overflow.libs.core.Data
import com.overflow.libs.core.StreamUtils
import com.overflow.libs.core.Translations
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_receipt.*
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ReceiptFragment:BaseFragment(){
    @Inject
    lateinit var preferences: SharedPreferences
    @Inject
    lateinit var translations: Translations
    lateinit var order: Data
    private var source:String = ""
    private var listDialog:Array<String> = arrayOf()
    lateinit var merchant:Data

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_receipt, container, false)
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.shouldRequestPermissions(Constant.REQUEST_PERMISSION_CODE)
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm", context!!.currentLocale())
        this.merchant = Data(preferences.getString("merchant", "{}"))
        listDialog = resources.getStringArray(R.array.share_receipt_list)
        //Add Content scroll for draw web view to image
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw()
        }

        this.order = Data(arguments!!.getString(Constant.ARG_SALES))

        val stream = activity?.assets?.open("receipt/index.html")
        this.source = StreamUtils.copyStreamToString(stream)
        val merchant = Data(preferences.getString("merchant", "{}"))
        if(order.containsKey("customer_name")){
            val customerName = addCustomInfo("Pelanggan", order.getString("customer_name"))
            source = source.replace("{{customer}}", customerName)
        }else{
            source = source.replace("{{customer}}", Constant.TEXT_EMPTY)
        }
        if(order.containsNotNull("receiveable_date")){
            val receiveableDate = addCustomInfo("Jatuh Tempo", format.format(Date(order.getLong("receiveable_date"))))
            source = source.replace("{{tgl_jatuh_tempo}}", receiveableDate)
        }else{
            source = source.replace("{{tgl_jatuh_tempo}}", Constant.TEXT_EMPTY)
        }
        source = source.replace("{{title}}",merchant.getString("name"))
        source = source.replace("{{order.code}}","%23${order.getString("order_code")}")
        source = source.replace("{{items}}", loadItems(order.getList("order_items")))
        source = source.replace("{{order.total_amount}}", activity!!.rupiah(order.getDouble("total_amount")))
        source = source.replace("{{order.total_payment}}", activity!!.rupiah(order.getDouble("total_payment")))

        if(order.getString("payment_method") == Constant.PaymentMethod.CASH){
            if(order.getDouble("cashback") > 0){
                source = source.replace("{{cashback_title}}", "Kembali")
                source = source.replace("{{order.cashback}}", activity!!.rupiah(order.getDouble("cashback")))
            }else{
                source = source.replace("{{cashback_title}}", "")
                source = source.replace("{{order.cashback}}", "")
            }
        }else{
            if(order.containsNotNull("total_credit") && order.getDouble("total_credit") > 0){
                source = source.replace("{{cashback_title}}", "Hutang")
                source = source.replace("{{order.cashback}}", activity!!.rupiah(order.getDouble("total_credit")))
            }else{
                source = source.replace("{{cashback_title}}", "Kembali")
                source = source.replace("{{order.cashback}}", activity!!.rupiah(order.getDouble("cashback")))
            }
        }

        source = source.replace("{{order.create_at}}", format.format(Date(order.getLong("order_at"))))
        webView.loadData(source, "text/html;charset=utf-8", "utf-8")
    }

    private fun addCustomInfo(title:String, value:String): String {
        return """
            <tr class="item last">
        <td>${title}</td>
        <td></td>
        <td>${value}</td>
        </tr>
        """.trimIndent()
    }
    private fun loadItems(items: List<Data>): String {
        val builder = StringBuilder()
        items.forEach {
            builder.append("<tr class=\"item\">")
            builder.append("<td>")
            builder.append(it.getString("product_name"))
            builder.append("</td>")
            builder.append("<td>")
            builder.append(it.getString("qty"))
            builder.append("</td>")
            builder.append("<td>")
            builder.append(activity?.rupiah(it.getDouble("sub_total")))
            builder.append("</td>")
            builder.append("</tr>")
        }
        return builder.toString()
    }


    fun screenShoot():Boolean{
        val uri = shareAsImage()
        Timber.i("Uri %s", uri)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "image/png")
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
        return true
    }

    fun share():Boolean{
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, shareAsImage())
        val message = "${getString(R.string.receipt)} ${merchant.getString("name")} #${order.getString("order_code")}"
        intent.putExtra(Intent.EXTRA_TEXT, message)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/*"
        startActivity(Intent.createChooser(intent, "${getString(R.string.share)} ${getString(R.string.receipt)}"));
        return false
    }

    fun shareAsImage(): Uri {
        var dest = File("${Environment.getExternalStorageDirectory()}/${getString(R.string.app_name).replace(" ", "")}/order", "${order.getString("order_code")}.png")
        if(!dest.isDirectory){
            dest.parentFile.mkdirs()
        }
        webView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        webView.layout(0, 0, webView.measuredWidth, webView.measuredHeight)
        webView.isDrawingCacheEnabled = true
        webView.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(webView.measuredWidth, webView.measuredHeight, Bitmap.Config.ARGB_8888)
        webView.isDrawingCacheEnabled = false
        val paint = Paint()
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(bitmap, 0f, bitmap.height.toFloat(), paint)
        webView.draw(canvas)
        val fos = FileOutputStream(dest)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        bitmap.recycle()
        fos.flush()
        fos.close()
        //Re initialize because the first one is unknown file
        dest = File("${Environment.getExternalStorageDirectory()}/${getString(R.string.app_name).replace(" ", "")}/order", "${order.getString("order_code")}.png")
        return FileProvider.getUriForFile(context!!, BuildConfig.APPLICATION_ID + ".fileprovider", dest)
    }

    companion object {
        @JvmStatic
        fun newInstance(sales: String) =
                ReceiptFragment().apply {
                    arguments = Bundle().apply {
                        putString(Constant.ARG_SALES, sales)
                    }
                }
    }
}