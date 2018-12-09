package com.overflow.cash

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import com.overflow.cash.adapter.PreviewSalesAdapter
import com.overflow.cash.mvp.order.PreviewSalesContract
import com.overflow.cash.mvp.order.PreviewSalesPresenter
import com.overflow.cash.net.ImageService
import com.overflow.cash.utils.home
import com.overflow.cash.utils.moveTo
import com.overflow.cash.utils.rupiah
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_preview_sales.*
import javax.inject.Inject

class PreviewSalesActivity : AppCompatActivity(), PreviewSalesContract.View {

    @Inject
    lateinit var imageService: ImageService
    @Inject
    lateinit var presenter: PreviewSalesPresenter
    @Inject
    lateinit var translations:Translations

    lateinit var adapter:PreviewSalesAdapter
    lateinit var cashBoxAdapter:ArrayAdapter<String>
    private var customerId:Long? = null
    var disposable:CompositeDisposable = CompositeDisposable()
    lateinit var outlet:Data
    @Inject
    lateinit var preferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_sales)
        this.outlet = Data(preferences.getString("outlet", "{}"))
        this.adapter = PreviewSalesAdapter(imageService, presenter)

        presenter.attach(this)
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
                    dataItem["discount_amount"] = it["discountAmount"]
                    dataItem["discount_type"] = it["discountType"]
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
            this.customerId = data?.getLongExtra("id", -1L)
        }
    }

    override fun onDiscountLoaded(discount: Data, holder: PreviewSalesAdapter.ViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

//    @SuppressLint("SetTextI18n")
//    override fun onDiscountLoaded(discount: Data, holder: PreviewSalesAdapter.ViewHolder, position: Int) {
//        val item = this.adapter.values[position]
//        this.discountId = discount.getLong("id")
//        val discountAmount = discount.getDouble("discount")
//        val discountType = discount.getDouble("discount_type")
//        holder.discount.text = "$discountAmount%"
//        holder.discount.visibility = View.VISIBLE
//        val calculateDiscount = discountAmount / 100.0 * item.getDouble("subTotal")
//        val priceAfterDiscount = item.getDouble("subTotal") - calculateDiscount
//
//        holder.subTotal.text = rupiah(priceAfterDiscount)
//        item["subTotal"] = priceAfterDiscount
//
//        this.adapter.values[position] = item
//        val amount = this.adapter.values.map { it.getDouble("subTotal") }.sum()

//    }
}


