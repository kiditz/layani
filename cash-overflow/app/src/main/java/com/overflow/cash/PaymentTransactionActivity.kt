package com.overflow.cash

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.getDrawable
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import com.jakewharton.rxbinding2.widget.RxAdapterView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.overflow.cash.mvp.order.CashboxContract
import com.overflow.cash.mvp.order.CashboxPresenter
import com.overflow.cash.mvp.payment.PaymentTransactionContract
import com.overflow.cash.mvp.payment.PaymentTransactionPresenter
import com.overflow.cash.utils.*
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.dialog_pay.view.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class PaymentTransactionActivity : AppCompatActivity(), CashboxContract.View, PaymentTransactionContract.View {

    private var customerId: Long? = null
    private var cashboxId: Long? = null
    @Inject
    lateinit var cashboxPresenter: CashboxPresenter
    @Inject
    lateinit var translations: Translations
    @Inject
    lateinit var disposable: CompositeDisposable
    @Inject
    lateinit var presenter:PaymentTransactionPresenter
    private var paymentMethod =Constant.PaymentMethod.CASH
    private var amount=0.0
    lateinit var orderItems:List<Data>
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }
        //Set amount
        this.amount = intent.getDoubleExtra("amount", -1.0)
        this.supportActionBar!!.title = "${getString(R.string.pay)} ${this.rupiah(amount)}"

        // Set Items for transaction
        val itemsStr = intent.getStringExtra("items")
        val itemData = Data(itemsStr)
        this.orderItems = itemData.getList("items")

        //Make numpad can be write into tv_result
        writeValueNumpad()

        //Open customer chooser
        ed_customer.setOnClickListener {
            openCustomerActivity()
        }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", currentLocale())
        val calendar = Calendar.getInstance()

        val minCalendar = Calendar.getInstance()
        minCalendar.add(Calendar.DATE, 1)
        ed_due_date.setText(dateFormat.format(calendar.time))
        ed_due_date.setOnClickListener {
            showDueDateDatePicker(calendar, minCalendar, dateFormat).show()
        }
        // Change color customer make it work over lollipop
        ed_customer.tinting(R.color.colorAccent)

        // Change color due date make it work over lollipop
        ed_due_date.tinting(R.color.colorAccent)

        //Load Cashbox Data From Internet
        this.cashboxPresenter.attach(this)
        //Load presenter to view
        this.presenter.attach(this)

        //Validate transaction
        validate(amount)
        ed_due_date.visibility = View.GONE
        rg_payment_method.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rdCash){
                this.paymentMethod = Constant.PaymentMethod.CASH
                ed_due_date.visibility = View.GONE
                validate(amount)
            }else{
                this.paymentMethod = Constant.PaymentMethod.CREDIT
                ed_due_date.visibility = View.VISIBLE
                validate(amount)
            }
        }


        btn_done.setOnClickListener {
            var message = "${getString(R.string.total_payment)} : ${tv_result.text}\n"
            message += if(paymentMethod == Constant.PaymentMethod.CASH){
                val cashBack = parseRupiah(tv_result.text) - amount
                showDialogPayment(rupiah(cashBack))
            }else{
                val paid = amount - parseRupiah(tv_result.text)
                showDialogPayment(rupiah(paid), true)
            }

        }

    }

    /**
     * Show dialog preview before paid transaction
     * */
    private fun showDialogPayment(value:String, isPaid:Boolean=false){
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_pay, null, false)
        view.tv_total_payment.text = tv_result.text
        view.tv_cashback.text = value
        view.tv_pay_type.text = if(isPaid){
            getString(R.string.paid)
        }else{
            getString(R.string.cashback)
        }

        val dialog = AlertDialog.Builder(this).setView(view).create()
        view.btn_pay.setOnClickListener {
            dialog.dismiss()
            saveOrder()
        }
        dialog.show()
    }

    private fun validate(amount:Double){
        disposable.clear()
        ed_customer.error = null
        // Validate when payment method is cash
        if(paymentMethod == Constant.PaymentMethod.CASH){
            // Validate greater than
            val totalAmountObserver = RxTextView.textChanges(tv_result).skipInitialValue().map { text -> text.toString().isNotEmpty() && parseRupiah(text) >= amount}
            disposable.add(totalAmountObserver.subscribe ({
                if(!it){
                    showErrorMessage(translations.get(Constant.TranslationsKey.TOTAL_AMOUNT_GREATER_THAN).replace("{0}", rupiah(amount)))
                    ed_customer.error = Constant.TEXT_EMPTY
                }else{
                    ed_customer.error = null
                    hideErrorMessage()
                }
                btn_done.isEnabled = it

                changeBtnStyle(it)
            }, {
                Timber.e(it)
            }))
        }else{
            // Validate when payment method is credit
            val totalAmountObserver = RxTextView.textChanges(tv_result).map { text -> text.toString().isNotEmpty() && text.replace(Regex("[^0-9]"), "").toDouble() < amount}
            disposable.add(totalAmountObserver.subscribe {
                if(!it){
                    showErrorMessage(translations.get(Constant.TranslationsKey.TOTAL_AMOUNT_LESS_THAN).replace("{0}", rupiah(amount)))
                    ed_customer.error = Constant.TEXT_EMPTY
                }else{
                    hideErrorMessage()
                    ed_customer.error = null
                }
            })

            val customerObserver = RxTextView.textChanges(ed_customer).map { text -> text.toString().isNotEmpty()}
            disposable.add(customerObserver.subscribe {
                ed_customer.error = translations.get(Constant.TranslationsKey.REQUIRED_VALUE_CUSTOMER_NAME)
            })

            Observable.combineLatest(totalAmountObserver, customerObserver, BiFunction { t1:Boolean, t2:Boolean -> t1 && t2 }).subscribe({ valid ->
                btn_done.isEnabled = valid
                changeBtnStyle(valid)
            }, {
                Timber.e(it)
            })

        }
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

    private fun showErrorMessage(message:String){
        changeBtnStyle(true)
        tv_error_message.text = message
        tv_error_message.visibility = View.VISIBLE
    }

    private fun hideErrorMessage(){
        tv_error_message.visibility = View.GONE
        if(progress.visibility == View.VISIBLE){
            progress.visibility = View.GONE
        }
    }
    private fun openCustomerActivity() {
        val intent = Intent(this, CustomerChooserActivity::class.java)
        intent.putExtra("name", this.ed_customer.text.toString())
        //Open customer and get data from it
        startActivityForResult(intent, Constant.REQUEST_CODE_VIEW_CUSTOMER)
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
        val currentVal = tv_result.text.toString().replace(".00", "").replace(Regex("[^0-9]"), "")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_CODE_VIEW_CUSTOMER && resultCode == Activity.RESULT_OK) {
            data?.let {
                // Receive data customer from activity
                ed_customer.setText(it.getStringExtra("name"))
                ed_customer.error = null
                this.customerId = it.getLongExtra("id", -1L)
            }
        }
    }

    private fun showDueDateDatePicker(calendar:Calendar, minCalendar:Calendar, dateFormat:SimpleDateFormat):DatePickerDialog{
        //Calendar
        val datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, y, m, d ->
            calendar.set(Calendar.YEAR, y)
            calendar.set(Calendar.MONTH, m)
            calendar.set(Calendar.DAY_OF_MONTH, d)
            ed_due_date.setText(dateFormat.format(calendar.time))
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePicker.datePicker.minDate = minCalendar.timeInMillis
        return datePicker
    }

    override fun onCashboxLoaded(item: List<Data>) {
        // Transform cashbox data to list string of name
        val cashboxStrList = item.map { it.getString("name") }
        // Initialize cashbox
        val cashBoxAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cashboxStrList)
        sp_cashbox.adapter = cashBoxAdapter
        RxAdapterView.itemSelections(sp_cashbox).subscribeOn(AndroidSchedulers.mainThread()).subscribe {
            cashboxId = item[it].getLong("id")
        }
    }

    override fun onOrderCreated(data: Data) {
        progress.visibility = View.GONE
        presenter.deleteAllItems()
        val bundle = Bundle()
        bundle.putString(Constant.ARG_SALES, data.toString())
        val message = translations.get(Constant.TranslationsKey.SALES_CREATED_SUCCESSFULY).replace("{0}", data.getString("order_code"))
        bundle.putString(Constant.SUCCESS_MESSAGE, message)
        bundle.putInt(Constant.GOTO, R.id.nav_sales)
        moveTo(ReceiptActivity::class.java, bundle)
    }

    private fun saveOrder(){
        btn_done.isEnabled = false
        progress.visibility = View.VISIBLE
        val data = Data()
        data["cash_box_id"] = this.cashboxId
        data["customer_id"] = this.customerId
        data["total_amount"] = amount
        data["total_payment"] = tv_result.text.replace(Regex("[^0-9]"), "").toDouble()
        data["payment_method"] = this.paymentMethod
        if(this.paymentMethod == Constant.PaymentMethod.CREDIT){
            data["due_date"] = ed_due_date.text.toString()
            if (TextUtils.isEmpty(ed_due_date.text)) {
                ed_due_date.error = translations.get(Constant.TranslationsKey.REQUIRED_VALUE_DUE_DATE)
                progress.visibility = View.GONE
                return
            }
        }
        data["items"] = orderItems
        changeBtnStyle(false)
        presenter.saveOrder(data)
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
        disposable.clear()
        btn_done?.isEnabled = false
        presenter.detach()
        cashboxPresenter.detach()
    }

    override fun onBackPressed() {
        if(progress.visibility == View.GONE){
            super.onBackPressed()
        }
    }

}