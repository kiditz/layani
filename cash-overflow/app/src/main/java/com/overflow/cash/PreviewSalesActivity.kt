package com.overflow.cash

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.jakewharton.rxbinding2.widget.RxTextView
import com.overflow.cash.Constant.TranslationsKey.Companion.REQUIRED_VALUE_CUSTOMER_NAME
import com.overflow.cash.Constant.TranslationsKey.Companion.TOTAL_AMOUNT_GREATER_THAN
import com.overflow.cash.Constant.TranslationsKey.Companion.TOTAL_AMOUNT_LESS_THAN
import com.overflow.cash.adapter.PreviewSalesAdapter
import com.overflow.cash.mvp.order.PreviewSalesContract
import com.overflow.cash.mvp.order.PreviewSalesPresenter
import com.overflow.cash.net.ImageService
import com.overflow.cash.utils.*
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.activity_preview_sales.*
import kotlinx.android.synthetic.main.dialog_order_payment.view.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class PreviewSalesActivity : AppCompatActivity(), PreviewSalesContract.View {

    @Inject
    lateinit var imageService: ImageService
    @Inject
    lateinit var presenter: PreviewSalesPresenter
    @Inject
    lateinit var translations:Translations

    lateinit var adapter:PreviewSalesAdapter
    private var orderPaymentView: View? = null
    private var orderPaymentDialog:AlertDialog? = null
    lateinit var cashBoxAdapter:ArrayAdapter<String>
    private var cashBoxList = mutableListOf<Data>()
    private var cashboxId = -1L
    private var discountId:Long? = null
    private var customerId:Long? = null
    private var paymentMethod = Constant.PaymentMethod.CASH
    var disposable:CompositeDisposable = CompositeDisposable()
    lateinit var merchant:Data
    @Inject
    lateinit var preferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_sales)
        this.merchant = Data(preferences.getString("merchant", "{}"))
        this.adapter = PreviewSalesAdapter(imageService, presenter)

        presenter.attach(this)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
            val amount = this.adapter.values.map { it.getDouble("subTotal") }.sum()
            this.supportActionBar?.title = this.rupiah(amount)
        }

        val manager = LinearLayoutManager(this)
        receycler?.layoutManager =  manager
        receycler?.isNestedScrollingEnabled = false
        receycler?.setHasFixedSize(true)
        receycler?.itemAnimator = DefaultItemAnimator()
        receycler?.adapter = adapter
        cashBoxAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, mutableListOf<String>())

    }


    override fun onOrderLoaded(item: MutableList<Data>) {
        adapter.clearValues()
        adapter.addValues(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_preview_sales, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item!!.itemId){
            R.id.action_done -> {

                val amount = this.adapter.values.map { it.getDouble("subTotal") }.sum()

                if (adapter.values.isEmpty()){
                    return false
                }

                val items:List<Data> = this.adapter.values.map {
                    val dataItem = Data()
                    dataItem["product_id"] = it["productId"]
                    dataItem["qty"] = it["qty"]
                    dataItem["sub_total"] = it["subTotal"]
                    dataItem["unit"] = it["unit"]
                    dataItem["product_name"] = it["productName"]
                    dataItem["use_stock"] = it["useStock"]
                    dataItem
                }.toList()
                val bundle = Bundle()
                bundle.putDouble("amount", amount)
                bundle.putString("items", Data().put("items", items).toString())
                moveTo(PaymentTransactionActivity::class.java, bundle)
                true
            }
            else -> home(item)
        }

    }


    override fun showError(error: Throwable) {
    }

    override fun showNoOk(res: String) {

    }

    override fun showEmpty() {

    }

    override fun showNotConnected(res: String) {
    }



    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
        disposable.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == Constant.REQUEST_CODE_VIEW_CUSTOMER && resultCode == Activity.RESULT_OK){
            this.orderPaymentView?.edCustomer?.setText(data?.getStringExtra("name"))
            this.customerId = data?.getLongExtra("id", -1L)
            this.orderPaymentView?.edCustomer?.error = null
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onDiscountLoaded(discount: Data, holder: PreviewSalesAdapter.ViewHolder, position: Int) {
        val item = this.adapter.values[position]
        this.discountId = discount.getLong("id")
        val discountAmount = discount.getLong("discount")
        holder.discount.text = "$discountAmount%"
        holder.discount.visibility = View.VISIBLE
        val calculateDiscount = discountAmount.toDouble() / 100.0 * item.getDouble("subTotal")
        val priceAfterDiscount = item.getDouble("subTotal") - calculateDiscount
        holder.subTotal.text = rupiah(priceAfterDiscount)
        item["subTotal"] = priceAfterDiscount
        this.adapter.values[position] = item
        val amount = this.adapter.values.map { it.getDouble("subTotal") }.sum()
        this.supportActionBar?.title = this.rupiah(amount)
    }
}


