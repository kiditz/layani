package com.overflow.cash.activity

import android.app.DatePickerDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import com.overflow.cash.R
import com.overflow.cash.mvp.product.ProductDetailContract
import com.overflow.cash.mvp.product.ProductDetailPresenter
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.*
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.dialog_add_sub_stock.view.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ProductDetailActivity : AppCompatActivity(), ProductDetailContract.View {


    @Inject
    lateinit var presenter: ProductDetailPresenter
    @Inject
    lateinit var translations: Translations
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    private var stockDialog: AlertDialog? = null
    private var stockView: View? = null
    private var stockMultiplier = 1L
    private var discountType:String = Constant.DiscountType.PERCENTAGE
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        this.presenter.attach(this)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }

        btn_manage_stock?.setOnClickListener {
            stockMultiplier = 1
            showAddStockDialog()
            validateStock()
        }
        intent?.extras?.let {
            initData(it)
        }
    }

    private fun initData(it: Bundle) {
        if (it.containsKey("image")) {
            val image = it.getByteArray("image")
            val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
            product_image.setImageBitmap(bitmap)
        }
        tvProductCode.text = it.getString("product_code")
        tv_product_name.text = it.getString("product_name")
        tv_sell_price.text = rupiah(it.getDouble("sell_price"))
        tv_purchase_price.text = rupiah(it.getDouble("purchase_price"))
        if (it.getBoolean("use_stock")) {
            tvRemainingStock?.text = "${it.getLong("stock").toInt()} ${getString(R.string.pcs)}"
            layoutStock?.visibility = View.VISIBLE
            btn_manage_stock?.visibility = View.VISIBLE
            vertStock?.visibility = View.VISIBLE
        } else {
            layoutStock?.visibility = View.GONE
            btn_manage_stock?.visibility = View.GONE
            vertStock.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_product, menu)
        val itemEdit = menu?.findItem(R.id.action_edit)
        itemEdit?.let {
            it.title = getString(R.string.edit_product)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> moveTo(SaveProductActivity::class.java, intent?.extras)
            R.id.action_delete -> {
                val input = Data()
                input["code"] = intent.getStringExtra("product_code");
                input["active"] = false
                presenter.deleteProduct(input)
            }
            else -> home(item)
        }
        return false
    }

    private fun showAddStockDialog() {
        this.stockView = LayoutInflater.from(this).inflate(R.layout.dialog_add_sub_stock, null, false)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.manage_stock)
        builder.setView(this.stockView)
        this.stockDialog = builder.create()

        this.stockView?.btn_submit?.setOnClickListener {
            handleAddStock()
        }
        stockDialog?.show()
        showStockDatePicker()
        handleStockType()
    }

    private fun handleStockType() {
        this.stockView?.rgStock?.setOnCheckedChangeListener { _, id ->
            this.stockMultiplier = when (id) {
                R.id.stockOut -> -1L
                else -> 1L
            }
            this.stockView?.purchase_price_wrapper?.visibility = when (id) {
                R.id.stockOut -> {
                    val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
                    params.weight = 1f
                    this.stockView?.quantity_wrapper?.layoutParams = params
                    View.GONE
                }
                else -> {
                    val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
                    params.weight = .5f
                    this.stockView?.quantity_wrapper?.layoutParams = params
                    this.stockView?.purchase_price_wrapper?.layoutParams = params
                    View.VISIBLE
                }
            }
        }
    }

    private fun showStockDatePicker() {
        //Calendar
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", currentLocale())
        val calendar = Calendar.getInstance()
        this.stockView?.edDateStockIn?.setText(dateFormat.format(calendar.time))

        this.stockView?.btnDate?.setOnClickListener {
            val datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, y, m, d ->
                calendar.set(Calendar.YEAR, y)
                calendar.set(Calendar.MONTH, m)
                calendar.set(Calendar.DAY_OF_MONTH, d)
                this.stockView?.edDateStockIn?.setText(dateFormat.format(calendar.time))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }
    }

    private fun validateStock() {
        val validatePurchasePrice = this.validateGreaterThan(this.stockView?.ed_purchase_price!!, this.stockView?.purchase_price_wrapper!!, 0, translations.get(Constant.TranslationsKey.INIT_PRICE_MUST_GREATER_THAN_ZERO), skipCount = 0L)
        val validateQuantity = this.validateGreaterThan(this.stockView?.ed_quantity!!, this.stockView?.quantity_wrapper!!, 0, translations.get(Constant.TranslationsKey.STOCK_PRICE_MUST_GREATER_THAN_ZERO), skipCount = 0L)
        Observable.combineLatest(validatePurchasePrice, validateQuantity, BiFunction { initPrice: Boolean, qty: Boolean -> initPrice && qty }).subscribe({
            this.stockView?.btn_submit?.isEnabled = it
        }, {})
    }

    private fun handleAddStock() {
        this.stockView?.progress_bar?.visibility = View.VISIBLE
        this.stockView?.btn_submit?.isEnabled = false
        val data = Data()
        data["product_id"] = intent.extras.getLong("product_id")
        if (stockMultiplier > 0) {
            data["purchase_price"] = this.stockView?.ed_purchase_price?.text.toString().toDouble()
        }
        data["quantity"] = this.stockView?.ed_quantity?.text.toString().toLong() * stockMultiplier
        data["start_date"] = this.stockView?.edDateStockIn?.text.toString().trim()
        data["description"] = this.stockView?.edDescription?.text.toString()
        this.presenter.addStock(data)
    }


    private fun dismiss() {
        this.stockView?.progress_bar?.visibility = View.GONE
        this.stockView?.btn_submit?.isEnabled = true
        this.stockDialog?.dismiss()
    }

    override fun onStockCreated(data: Data) {
        dismiss()
        this.tvRemainingStock?.text = data.getLong("quantity").toString()
        if (data.containsKey("purchase_price")) {
            this.tv_purchase_price?.text = rupiah(data.getDouble("purchase_price"))
        }
        stockDialog?.let {
            it.dismiss()
        }
        snack(translations.get(Constant.TranslationsKey.STOCK_CREATED_SUCCESSFULLY)).show()

    }

    override fun showError(error: Throwable) {
        dismiss()
        networkExHandler.errorHandle(this, error)
    }

    override fun showNoOk(res: String) {
        dismiss()
        snack(res).show()
    }

    override fun showEmpty() {

    }

    override fun showNotConnected(res: String) {
        dismiss()
        snack(res).show()
    }

    override fun onDiscountCreated(data: Data) {
        dismiss()
        snack(translations.get(Constant.TranslationsKey.DISCOUNT_CREATED_SUCCESSFULLY)).show()

    }

    override fun onDeleteProductSuccess(data: Data) {
        val bundle = Bundle()
        val message = translations.get(Constant.TranslationsKey.PRODUCT_REMOVED_SUCCESSFULLY).replace("{0}", data.getString("code"))
        bundle.putString(Constant.SUCCESS_MESSAGE, message)
        bundle.putInt(Constant.GOTO, R.id.nav_product)
        moveTo(MenuActivity::class.java, bundle)
    }
}
