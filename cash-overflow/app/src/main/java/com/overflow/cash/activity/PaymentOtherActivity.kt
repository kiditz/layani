package com.overflow.cash.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.getDrawable
import android.view.MenuItem
import android.view.View
import com.jakewharton.rxbinding2.widget.RxTextView
import com.overflow.cash.R
import com.overflow.cash.fragment.DialogPaymentMakeSure
import com.overflow.cash.mvp.order.SaveOrderContract
import com.overflow.cash.mvp.order.SaveOrderPresenter
import com.overflow.cash.utils.home
import com.overflow.cash.utils.moveTo
import com.overflow.cash.utils.parseRupiah
import com.overflow.cash.utils.rupiah
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_payment_other.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class PaymentOtherActivity : BaseActivity(),  SaveOrderContract.View {

    private var customerId: Long? = null
    @Inject
    lateinit var translations: Translations

    @Inject
    lateinit var presenter: SaveOrderPresenter
    private var paymentMethod = Constant.PaymentMethod.CASH
    private var amount=0.0
    private var orderItems:List<Data>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_other)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }
        //Set amount
        this.amount = intent.getDoubleExtra("amount", -1.0)
        //Check if customer id is greater than zero
        if(intent.getLongExtra("customer_id", -1) > 0L){
            this.customerId = intent.getLongExtra("customer_id", 0)
        }
        if(intent.getStringExtra("payment_method") != null){
            this.paymentMethod = intent.getStringExtra("payment_method")
        }
        this.supportActionBar!!.title = "${getString(R.string.total_amount)} ${rupiah(amount)}"

        // Set Items for transaction
        if(intent.hasExtra("items")){
            val itemsStr = intent.getStringExtra("items")
            val itemData = Data(itemsStr)
            this.orderItems = itemData.getList("items")
        }

        //Make numpad can be write into tv_result
        writeValueNumpad()
        this.presenter.attach(this)
        this.tv_result.text = rupiah(Constant.ZERO.toDouble())

        RxTextView.textChanges(tv_result).map { it.isNotBlank() && parseRupiah(it) >= amount }.debounce(100, TimeUnit.MILLISECONDS).subscribe {
            runOnUiThread{
                btn_done.isEnabled = it
            }
        }

        btn_done.setOnClickListener {
            var message = "${getString(R.string.total_payment)} : ${tv_result.text}\n"
            val cashBack = parseRupiah(tv_result.text) - amount
            message += showDialogPayment(cashBack)

        }

    }

    /**
     * Show dialog preview before paid transaction
     * */
    private fun showDialogPayment(cashBack:Double){
        val totalPayment = parseRupiah(tv_result.text)
        val payFragment = DialogPaymentMakeSure.newInstance(totalPayment, cashBack, getString(R.string.are_you_sure_transaction))
        payFragment.onDoneClick = {
            doOrder()
        }
        payFragment.show(supportFragmentManager, Constant.PaymentMethod.CASH)
    }

    @SuppressLint("ResourceType")
    private fun changeBtnStyle(enabled:Boolean){
        if(enabled){
            btn_done.background = getDrawable(this, R.drawable.btn_default)
            btn_done.setTextColor(ContextCompat.getColor(this, R.color.textLight))
        }else{
            btn_done.background = null
            btn_done.setTextColor(ContextCompat.getColor(this, android.R.color.tab_indicator_text))
        }
    }



    private fun writeValueNumpad() {
        getButtonIds().forEach {
            it.setOnClickListener {
                when (it.id) {
                    R.id.btn_clear -> clearValues()
                    R.id.btn_0 -> addDigit(0)
                    R.id.btn_1 -> addDigit(1)
                    R.id.btn_2 -> addDigit(2)
                    R.id.btn_3 -> addDigit(3)
                    R.id.btn_4 -> addDigit(4)
                    R.id.btn_5 -> addDigit(5)
                    R.id.btn_6 -> addDigit(6)
                    R.id.btn_7 -> addDigit(7)
                    R.id.btn_8 -> addDigit(8)
                    R.id.btn_9 -> addDigit(9)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun addDigit(value: Int) {
        val currentVal = tv_result.text.toString().replace(Regex("[^0-9]"), "")
        this.tv_result.text = rupiah("$currentVal$value".toDouble())
    }

    private fun clearValues() {
        this.tv_result.text = Constant.TEXT_EMPTY
        addDigit(0)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return home(item)
    }

    private fun getButtonIds() = arrayOf(btn_clear, btn_0, btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9)


    override fun onOrderCreated(data: Data) {
        progress.visibility = View.GONE
        presenter.deleteAllItems()
        val bundle = Bundle()
        bundle.putString(Constant.ARG_SALES, data.toString())
        val message = translations.get(Constant.TranslationsKey.SALES_CREATED_SUCCESSFULY).replace("{0}", data.getString("order_code"))
        bundle.putString(Constant.SUCCESS_MESSAGE, message)
        bundle.putInt(Constant.GOTO, R.id.nav_new_transaction)
        moveTo(ReceiptActivity::class.java, bundle)
    }

    private fun doOrder(){
        btn_done.isEnabled = false
        progress.visibility = View.VISIBLE
        val order = Data()
        order["customer_id"] = this.customerId
        order["total_amount"] = amount
        order["description"] = intent.getStringExtra("description")
        if(intent.hasExtra("discount_id")){
            order["discount_amount"] = intent.getDoubleExtra("discount_amount", -1.0)
            order["discount_name"] = intent.getStringExtra("discount_name")
        }
        if(intent.hasExtra("order_id")){
            order["order_id"] = intent.getLongExtra("order_id", -1)
        }
        order["total_payment"] = tv_result.text.replace(Regex("[^0-9]"), "").toDouble()
        order["payment_method"] = this.paymentMethod
        if(orderItems != null){
            order["items"] = orderItems
        }

        changeBtnStyle(false)
        presenter.saveOrder(order)
    }

    override fun showError(error: Throwable) {
        showErrorMessage(getString(R.string.system_err))
    }

    override fun showNoOk(res: String) {
        showErrorMessage(res)
    }

    override fun showEmpty() {

    }

    override fun showNotConnected(res: String) {
        showErrorMessage(res)
    }

    override fun onDestroy() {
        super.onDestroy()
        btn_done?.isEnabled = false
        presenter.detach()
    }

    override fun onBackPressed() {
        if(progress.visibility == View.GONE){
            super.onBackPressed()
        }
    }

}