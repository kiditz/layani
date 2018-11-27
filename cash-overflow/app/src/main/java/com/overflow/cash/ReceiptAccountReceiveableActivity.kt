package com.overflow.cash

import android.annotation.SuppressLint
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
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import com.overflow.cash.utils.home
import com.overflow.cash.utils.moveTo
import com.overflow.cash.utils.rupiah
import com.overflow.cash.utils.shouldRequestPermissions
import com.overflow.libs.core.Data
import com.overflow.libs.core.StreamUtils
import com.overflow.libs.core.Translations
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_receipt.*
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ReceiptAccountReceiveableActivity : AppCompatActivity() {
    @Inject
    lateinit var preferences: SharedPreferences
    @Inject
    lateinit var translations: Translations
    lateinit var order:Data
    private var source:String = ""
    private var listDialog:Array<String> = arrayOf()

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        shouldRequestPermissions(Constant.REQUEST_PERMISSION_CODE)
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        listDialog = resources.getStringArray(R.array.share_receipt_list)
        //Add Content scroll for draw web view to image
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw()
        }

        this.order = Data(intent.getStringExtra("sales"))

        val stream = assets.open("receipt/index.html")
        this.source = StreamUtils.copyStreamToString(stream)
        val merchant = Data(preferences.getString("merchant", "{}"))
        if(order.containsKey("customer_name")){
            val customerName = addCustomInfo("Pelanggan", order.getString("customer_name"))
            val receiveableDate = addCustomInfo("Jatuh Tempo", format.format(Date(order.getLong("receiveable_date"))))
            source = source.replace("{{customer}}", customerName)
            source = source.replace("{{tgl_jatuh_tempo}}", receiveableDate)
        }else{
            source = source.replace("{{customer}}", Constant.TEXT_EMPTY)
            source = source.replace("{{tgl_jatuh_tempo}}", Constant.TEXT_EMPTY)
        }
        source = source.replace("{{title}}",merchant.getString("name"))
        source = source.replace("{{order.code}}",order.getString("order_code"))
        source = source.replace("{{items}}", loadItems(order.getList("order_items")))
        source = source.replace("{{order.total_amount}}", rupiah(order.getDouble("total_amount")))
        source = source.replace("{{order.total_payment}}", rupiah(order.getDouble("total_payment")))

        if(order.getString("payment_method") == Constant.PaymentMethod.CASH){
            if(order.getDouble("cashback") > 0){
                source = source.replace("{{cashback_title}}", "Kembali")
                source = source.replace("{{order.cashback}}", rupiah(order.getDouble("cashback")))
            }else{
                source = source.replace("{{cashback_title}}", "")
                source = source.replace("{{order.cashback}}", "")
            }
        }else{
            source = source.replace("{{cashback_title}}", "Hutang")
            source = source.replace("{{order.cashback}}", rupiah(order.getData("account_receiveable").getDouble("total_credit")))
        }

        source = source.replace("{{order.create_at}}", format.format(Date(order.getLong("order_at"))))
        webView.loadData(source, "text/html;charset=utf-8", "utf-8")
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
            builder.append(rupiah(it.getDouble("sub_total")))
            builder.append("</td>")
            builder.append("</tr>")
        }
        return builder.toString()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(intent.getBooleanExtra("show_menu", true)){
            menuInflater.inflate(R.menu.menu_receipt_account_receiveable, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId){
            R.id.action_download -> screenShoot()
            R.id.action_share -> share()
            R.id.action_paid -> moveTo(PaymentAccountReceiveableActivity::class.java, intent.extras)
            else ->  home(item!!)
        }

    }

    private fun share():Boolean{
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tagihan")
        //shareIntent.putExtra(Intent.EXTRA_TEXT, "Tagihan transaksi dari $")
        shareIntent.putExtra(Intent.EXTRA_STREAM, shareAsImage())
        startActivity(Intent.createChooser(shareIntent, "Bagikan Tagihan"))
        return false
    }

    private fun screenShoot():Boolean{
        val uri = shareAsImage()
        Timber.i("Uri %s", uri)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "image/png")
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
        return true
    }

    private fun shareAsImage(): Uri {
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
        return FileProvider.getUriForFile(this,BuildConfig.APPLICATION_ID + ".fileprovider", dest)
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



    override fun onBackPressed() {
        val bundle = Bundle()
        bundle.putString(Constant.SUCCESS_MESSAGE, translations.get(Constant.TranslationsKey.ACCOUNT_RECEIVEABLE_CREATED_SUCCESSFULY).replace("{0}", order.getString("customer_name")))
        bundle.putInt(Constant.GOTO, R.id.nav_accounts_receiveable)
        moveTo(MenuActivity::class.java, bundle)
    }

}
