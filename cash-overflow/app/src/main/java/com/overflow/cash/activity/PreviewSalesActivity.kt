package com.overflow.cash.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.widget.ArrayAdapter
import com.overflow.cash.R
import com.overflow.cash.adapter.PreviewSalesAdapter
import com.overflow.cash.mvp.order.SaveOrderContract
import com.overflow.cash.mvp.order.SaverOrderPresenter
import com.overflow.cash.mvp.receiveable.AccountReceiveablePaymentContract
import com.overflow.cash.net.ImageService
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.realm.OrderRealm
import com.overflow.cash.utils.*
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_preview_sales.*
import javax.inject.Inject

class PreviewSalesActivity : BaseActivity(), SaveOrderContract.View {


    @Inject
    lateinit var imageService: ImageService

    @Inject
    lateinit var translations:Translations
    @Inject
    lateinit var orderRealm: OrderRealm
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    @Inject
    lateinit var saveOrderPresenter: SaverOrderPresenter
    lateinit var adapter:PreviewSalesAdapter
    lateinit var cashBoxAdapter:ArrayAdapter<String>
    private var customerId:Long? = null

    var disposable:CompositeDisposable = CompositeDisposable()
    lateinit var outlet:Data
    @Inject
    lateinit var preferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_sales)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        this.saveOrderPresenter.attach(this)
        this.outlet = Data(preferences.getString("outlet", "{}"))
        this.adapter = PreviewSalesAdapter(imageService)
        onOrderLoaded(orderRealm.loadOrder())
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
            val amount = this.adapter.values.map { it.getDouble("subTotal") }.sum()
            this.supportActionBar?.title = this.rupiah(amount)
        }

        val manager = LinearLayoutManager(this)
        recycler?.layoutManager =  manager
        recycler?.isNestedScrollingEnabled = false
        recycler?.setHasFixedSize(true)
        recycler?.itemAnimator = DefaultItemAnimator()
        recycler?.adapter = adapter

        cashBoxAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, mutableListOf<String>())
        this.btn_pay.setOnClickListener {
            payOrder()
        }

        this.btn_save.setOnClickListener {
            saveOrder()
        }
    }


    private fun onOrderLoaded(item: MutableList<Data>) {
        adapter.clearValues()
        adapter.addValues(item)
    }

    private fun saveOrder(){
        val order = Data()
        order["cash_box_id"] = null
        order["customer_id"] = this.customerId
        order["status"] = Constant.TransactionStatus.CREATED
        val itemData = Data(generateOrder()!!.getString("items"))
        val orderItems = itemData.getList("items")
        order["items"] = orderItems
        this.saveOrderPresenter.saveOrder(order)
    }

    private fun payOrder(){
        moveTo(PaymentTransactionActivity::class.java, generateOrder())
    }

    private fun generateOrder():Bundle?{
        val amount = this.adapter.values.map { it.getDouble("subTotal") }.sum()
        if (adapter.values.isEmpty()){
            return null
        }

        val items:List<Data> = this.adapter.values.map {
            val dataItem = Data()
            dataItem["product_id"] = it["productId"]
            dataItem["qty"] = it["qty"]
            dataItem["sub_total"] = it["subTotal"]
            dataItem["unit"] = it["unit"]
            dataItem["product_name"] = it["productName"]
            dataItem["use_stock"] = it["useStock"]
            dataItem["discount_amount"] = it["discountAmount"]
            dataItem["discount_type"] = it["discountType"]
            dataItem
        }.toList()
        val bundle = Bundle()
        bundle.putDouble("amount", amount)
        bundle.putString("items", Data().put("items", items).toString())
        return bundle
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == Constant.REQUEST_CODE_VIEW_CUSTOMER && resultCode == Activity.RESULT_OK){
            this.customerId = data?.getLongExtra("id", -1L)
        }
    }

    override fun onOrderCreated(data: Data) {
        val bundle = Bundle()
        bundle.putInt(Constant.GOTO, R.id.nav_transaction)
        bundle.putString(Constant.SUCCESS_MESSAGE, translations.get(Constant.TranslationsKey.SALES_CREATED_SUCCESSFULY).replace("{0}", data.getString("order_code")))
        moveTo(MenuActivity::class.java, bundle)
    }

    override fun showError(error: Throwable) {
        networkExHandler.errorHandle(this, error)
    }

    override fun showNoOk(res: String) {
        showErrorMessage(res)
    }

    override fun showEmpty() {

    }

    override fun showNotConnected(res: String) {
        snack(res, Snackbar.LENGTH_INDEFINITE).setAction(R.string.try_again) {
            saveOrder()
        }.show()
    }
}


