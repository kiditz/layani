package com.overflow.cash

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
import kotlinx.android.synthetic.main.dialog_add_discount.view.*
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
    private var discountDialog: AlertDialog? = null
    private var stockView: View? = null
    private var discountView: View? = null
    private var stockMultiplier = 1L
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        this.presenter.attach(this)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }

        btnManageStock?.setOnClickListener {
            stockMultiplier = 1
            showAddStockDialog()
            validateStock()
        }

        btnDiscount?.setOnClickListener {
            showAddDiscountDialog()
            validateDiscount()
        }
        intent?.extras?.let {
            initData(it)
        }
    }

    private fun initData(it: Bundle) {
        if (it.containsKey("image")) {
            val image = it.getByteArray("image")
            val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
            productImage.setImageBitmap(bitmap)
        }
        tvProductCode.text = it.getString("product_code")
        tvProductName.text = it.getString("product_name")
        tvSellPrice.text = rupiah(it.getDouble("sell_price"))
        tvPurcPrice.text = rupiah(it.getDouble("purchase_price"))
        if (it.getBoolean("use_stock")) {
            tvRemainingStock?.text = "${it.getLong("stock").toInt()} ${getString(R.string.pcs)}"
            layoutStock?.visibility = View.VISIBLE
            btnManageStock?.visibility = View.VISIBLE
            vertStock?.visibility = View.VISIBLE
        } else {
            layoutStock?.visibility = View.GONE
            btnManageStock?.visibility = View.GONE
            vertStock.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        val itemEdit = menu?.findItem(R.id.action_edit)
        itemEdit?.let {
            it.title = getString(R.string.edit_product)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> moveTo(SaveProductActivity::class.java, intent?.extras)
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

        this.stockView?.btnSubmit?.setOnClickListener {
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
            this.stockView?.purchasePriceWrapper?.visibility = when (id) {
                R.id.stockOut -> {
                    val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
                    params.weight = 1f
                    this.stockView?.quantityWrapper?.layoutParams = params
                    View.GONE
                }
                else -> {
                    val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
                    params.weight = .5f
                    this.stockView?.quantityWrapper?.layoutParams = params
                    this.stockView?.purchasePriceWrapper?.layoutParams = params
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

    private fun showAddDiscountDialog() {
        this.discountView = LayoutInflater.from(this).inflate(R.layout.dialog_add_discount, null, false)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.discount)
        builder.setView(this.discountView)

        this.discountDialog = builder.create()
        this.discountView?.btnSubmitDiscount?.setOnClickListener {
            handleAddDiscount()
        }
        this.discountDialog?.show()
    }

    private fun validateDiscount() {
        val validateDiscountPercent = this.validateGreaterThan(this.discountView?.edDiscount!!, this.discountView?.discountWrapper!!, 0, translations.get(Constant.TranslationsKey.DISCOUNT_MUST_GREATER_THAN_ZERO), skipCount = 0)
        val validateDiscountWhen = this.validateGreaterThan(this.discountView?.edDiscountWhen!!, this.discountView?.discountWhenWrapper!!, 0, translations.get(Constant.TranslationsKey.DISCOUNT_WHEN_MUST_GREATER_THAN_ZERO), skipCount = 0)
        Observable.combineLatest(validateDiscountPercent, validateDiscountWhen, BiFunction { discountAmount: Boolean, discountWhen: Boolean -> discountAmount && discountWhen }).subscribe({
            this.discountView?.btnSubmitDiscount?.isEnabled = it
        }, {})
    }

    private fun validateStock() {
        val validatePurchasePrice = this.validateGreaterThan(this.stockView?.edPurchasePrice!!, this.stockView?.purchasePriceWrapper!!, 0, translations.get(Constant.TranslationsKey.INIT_PRICE_MUST_GREATER_THAN_ZERO), skipCount = 0L)
        val validateQuantity = this.validateGreaterThan(this.stockView?.edQuantity!!, this.stockView?.quantityWrapper!!, 0, translations.get(Constant.TranslationsKey.STOCK_PRICE_MUST_GREATER_THAN_ZERO), skipCount = 0L)
        Observable.combineLatest(validatePurchasePrice, validateQuantity, BiFunction { initPrice: Boolean, qty: Boolean -> initPrice && qty }).subscribe({
            this.stockView?.btnSubmit?.isEnabled = it
        }, {})
    }

    private fun handleAddStock() {
        this.stockView?.progress_bar?.visibility = View.VISIBLE
        this.stockView?.btnSubmit?.isEnabled = false
        val data = Data()
        data["product_id"] = intent.extras.getLong("product_id")
        if (stockMultiplier > 0) {
            data["purchase_price"] = this.stockView?.edPurchasePrice?.text.toString().toDouble()
        }
        data["quantity"] = this.stockView?.edQuantity?.text.toString().toLong() * stockMultiplier
        data["start_date"] = this.stockView?.edDateStockIn?.text.toString().trim()
        data["description"] = this.stockView?.edDescription?.text.toString()
        this.presenter.addStock(data)
    }

    private fun handleAddDiscount() {
        showDiscountProgress()
        val data = Data()
        data["discount"] = discountView?.edDiscount?.text.toString().toLong()
        data["discount_when"] = discountView?.edDiscountWhen?.text.toString().toLong()
        data["product_id"] = intent.extras.getLong("product_id")
        this.presenter.addDiscount(data)

    }

    private fun showDiscountProgress() {
        this.discountView?.progressBarDiscount?.visibility = View.VISIBLE
        this.discountView?.btnSubmitDiscount?.isEnabled = false
    }

    private fun dismiss() {
        this.stockView?.progress_bar?.visibility = View.GONE
        this.stockView?.btnSubmit?.isEnabled = true
        this.discountView?.progressBarDiscount?.visibility = View.GONE
        this.discountView?.btnSubmitDiscount?.isEnabled = true
        this.stockDialog?.dismiss()
        this.discountDialog?.dismiss()
    }

    override fun onStockCreated(data: Data) {
        dismiss()
        this.tvRemainingStock?.text = data.getLong("quantity").toString()
        if (data.containsKey("purchase_price")) {
            this.tvPurcPrice?.text = rupiah(data.getDouble("purchase_price"))
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
}
