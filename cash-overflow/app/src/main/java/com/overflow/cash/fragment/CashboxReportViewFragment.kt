package com.overflow.cash.fragment

import android.accounts.AccountManager
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
import com.overflow.cash.R
import com.overflow.cash.account.AccountGeneral
import com.overflow.cash.activity.Constant
import com.overflow.cash.utils.shouldRequestPermissions
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_cash_report_view.*
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class CashboxReportViewFragment:BaseFragment(){
    @Inject
    lateinit var preferences: SharedPreferences
    @Inject
    lateinit var translations: Translations
    @Inject
    lateinit var accountManager: AccountManager
    lateinit var order: Data
    private var listDialog:Array<String> = arrayOf()
    lateinit var outlet:Data

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cash_report_view, container, false)
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.shouldRequestPermissions(Constant.REQUEST_PERMISSION_CODE)
        this.outlet = Data(preferences.getString("outlet", "{}"))
        listDialog = resources.getStringArray(R.array.share_receipt_list)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw()
        }
        val summaryId = arguments!!.getLong(ARG_CASH_BOX_SUMMARY_ID)
        if (accountManager.getAccountsByType(getString(R.string.account_type)).isEmpty()) {
            return
        }
        val account = accountManager.getAccountsByType(getString(R.string.account_type)).first()
        val authToken = accountManager.peekAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS)
        val params = mapOf<String, String>("Authorization" to "Bearer $authToken")
        val url = if(arguments!!.getInt(ARG_CASH_BOX_TYPE) == CASH_BOX_TYPE_DETAIL){
            "${BuildConfig.base_url}/cash/receipt/cash/view?id=$summaryId&user_id=${outlet.getLong("user_id")}&lang_code=${preferences.getString("lang_code", "id")}"
        }else{
            "${BuildConfig.base_url}/cash/receipt/cash/print?id=$summaryId&user_id=${outlet.getLong("user_id")}&lang_code=${preferences.getString("lang_code", "id")}"
        }

        webView.loadUrl(url, params)
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
        val message = "${getString(R.string.receipt)} ${outlet.getString("name")} #${order.getString("order_code")}"
        intent.putExtra(Intent.EXTRA_TEXT, message)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/*"
        startActivity(Intent.createChooser(intent, "${getString(R.string.share)} ${getString(R.string.receipt)}"))
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
        const val ARG_CASH_BOX_SUMMARY_ID = "cash_box_summary_id"
        const val ARG_CASH_BOX_TYPE = "cash_box_type"
        const val CASH_BOX_TYPE_DETAIL = 0
        const val CASH_BOX_TYPE_RECEIPT = 1
        @JvmStatic
        fun newInstance(cashboxSummaryId: Long, cashboxType:Int = CASH_BOX_TYPE_DETAIL) =
                CashboxReportViewFragment().apply {
                    arguments = Bundle().apply {
                        putLong(ARG_CASH_BOX_SUMMARY_ID, cashboxSummaryId)
                        putInt(ARG_CASH_BOX_TYPE, cashboxType)
                    }
                }
    }
}