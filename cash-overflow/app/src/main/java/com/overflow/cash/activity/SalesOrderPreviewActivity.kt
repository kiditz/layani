package com.overflow.cash.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import com.overflow.cash.R
import com.overflow.cash.adapter.SalesOrderPreviewAdapter
import com.overflow.cash.mvp.discount.LoadDiscountByQuantityPresenter
import com.overflow.cash.mvp.order.SaveOrderContract
import com.overflow.cash.mvp.order.SaverOrderPresenter
import com.overflow.cash.net.ImageService
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.realm.OrderItemRealm
import com.overflow.cash.utils.moveTo
import com.overflow.cash.utils.rupiah
import com.overflow.cash.utils.snack
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_preview_sales.*
import javax.inject.Inject

class SalesOrderPreviewActivity : BaseActivity(), SaveOrderContract.View {

    @Inject
    lateinit var imageService: ImageService
    @Inject
    lateinit var translations:Translations
    @Inject
    lateinit var orderItemRealm: OrderItemRealm
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    @Inject
    lateinit var saveOrderPresenter: SaverOrderPresenter
    lateinit var adapter:SalesOrderPreviewAdapter
    private var customerId:Long? = null
    private var customerName:String? = null
    var disposable:CompositeDisposable = CompositeDisposable()
    lateinit var outlet:Data
    @Inject
    lateinit var preferences: SharedPreferences
    @Inject
    lateinit var loadDiscountByQuantityPresenter: LoadDiscountByQuantityPresenter
    var totalAmount:Number = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_sales)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        this.saveOrderPresenter.attach(this)
        this.adapter = SalesOrderPreviewAdapter(imageService, orderItemRealm, loadDiscountByQuantityPresenter)
        this.loadDiscountByQuantityPresenter.attach(this.adapter)
        this.outlet = Data(preferences.getString("outlet", "{}"))

        onOrderLoaded(orderItemRealm.loadOrder())
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }

        val manager = LinearLayoutManager(this)
        recycler?.layoutManager =  manager
        recycler?.isNestedScrollingEnabled = false
        recycler?.setHasFixedSize(true)
        recycler?.itemAnimator = DefaultItemAnimator()
        recycler?.adapter = adapter
        this.btn_pay.setOnClickListener {
            payOrder()
        }

        this.btn_save.setOnClickListener {
            saveOrder()
        }

        l_customer.setOnClickListener {
            addCustomer()
        }

        val items = this.orderItemRealm.loadAll()
        totalAmount = items.sum("subTotal")
        this.supportActionBar?.title = rupiah(totalAmount.toDouble())
        items.addChangeListener { t, _ ->
            totalAmount = t.sum("subTotal")
            this.supportActionBar?.title = rupiah(totalAmount.toDouble())
        }

    }


    private fun onOrderLoaded(item: MutableList<Data>) {
        adapter.clearValues()
        adapter.addValues(item)
    }

    private fun saveOrder(){
        progress.visibility = View.VISIBLE
        btn_save.isEnabled = false
        val order = Data()
        order["customer_id"] = this.customerId
        order["total_amount"] = this.totalAmount.toDouble()
        order["status"] = Constant.TransactionStatus.CREATED
        val itemData = Data(generateOrder()!!.getString("items"))
        val orderItems = itemData.getList("items")
        order["items"] = orderItems
        this.saveOrderPresenter.saveOrder(order)
    }

    private fun payOrder(){
        val order = generateOrder()
        if(customerId != null){
            order?.putLong("customer_id", customerId!!)
        }
        moveTo(PaymentTransactionDispatcherActivity::class.java, order)
    }

    private fun generateOrder():Bundle?{
        val amount = this.adapter.values.map { it.getDouble("subTotal") }.sum()
        if (adapter.values.isEmpty()){
            return null
        }

        val items:List<Data> = this.adapter.values.map {
            val dataItem = Data()
            dataItem["product_id"] = it["productId"]
            if (it.containsKey("discountId")){
                dataItem["discount_id"] = it["discountId"]
            }
            dataItem["qty"] = it["qty"]
            dataItem["sub_total"] = it["subTotal"]
            dataItem["unit"] = it["unit"]
            dataItem["product_name"] = it["productName"]
            dataItem["use_stock"] = it["useStock"]
            dataItem["discount_amount"] = it["discountAmount"]
            dataItem["discount_type"] = it["discountType"]
            dataItem["purchase_price_id"] = it["purchasePriceId"]
            dataItem["sell_price_id"] = it["sellPriceId"]
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
            if (data != null) {
                this.customerName = data.getStringExtra("name")
                tv_customer_name.text = customerName
            }

        }
    }

    private fun addCustomer(){
        val intent = Intent(this, CustomerChooserActivity::class.java)
        if(!TextUtils.isEmpty(tv_customer_name.text))
            intent.putExtra("name", tv_customer_name.text.toString())
        startActivityForResult(intent, Constant.REQUEST_CODE_VIEW_CUSTOMER)
    }

    override fun onOrderCreated(data: Data) {
        hideProgress()
        val bundle = Bundle()
        bundle.putInt(Constant.GOTO, R.id.nav_new_transaction)
        bundle.putString(Constant.SUCCESS_MESSAGE, translations.get(Constant.TranslationsKey.SALES_CREATED_SUCCESSFULY).replace("{0}", data.getString("order_code")))
        orderItemRealm.deleteItems()
        moveTo(MenuActivity::class.java, bundle)
    }

    override fun showError(error: Throwable) {
        hideProgress()
        networkExHandler.errorHandle(this, error)
    }

    override fun showNoOk(res: String) {
        hideProgress()
        showErrorMessage(res)
    }

    override fun showEmpty() {
        hideProgress()
    }

    override fun showNotConnected(res: String) {
        snack(res, Snackbar.LENGTH_INDEFINITE).setAction(R.string.try_again) {
            saveOrder()
        }.show()
    }

    private fun hideProgress(){
        progress.visibility = View.GONE
        btn_save.isEnabled = true
    }

}


