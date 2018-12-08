package com.overflow.cash

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.overflow.cash.mvp.receiveable.AccountReceiveablePaymentContract
import com.overflow.cash.mvp.receiveable.AccountReceiveablePaymentPresenter
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.moveTo
import com.overflow.cash.utils.parseRupiah
import com.overflow.cash.utils.rupiah
import com.overflow.cash.utils.snack
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_account_receiveable_payed.*
import kotlinx.android.synthetic.main.dialog_cashbox_chooser.view.*
import timber.log.Timber
import javax.inject.Inject


class PaymentAccountReceiveableActivity:AppCompatActivity(), AccountReceiveablePaymentContract.View {

    lateinit var order: Data
    private var cashboxId:Long? = null
    @Inject
    lateinit var presenter:AccountReceiveablePaymentPresenter
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    @Inject
    lateinit var translations: Translations
    private var dialog:AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_receiveable_payed)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        this.order = Data(intent.getStringExtra("sales"))
        Timber.d("Order %s", order)
        Timber.d("Extras %s", intent.extras)
        this.tv_paid.text = rupiah(order.getDouble("total_credit"))
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


        RxTextView.textChanges(tv_result).map { text -> !text.isEmpty() && parseRupiah(text) > 0}.subscribe {
            this.btn_done.isEnabled = it
            this.btn_done.background = if(!it){
                this.btn_done.setTextColor(ContextCompat.getColor(this, android.R.color.tab_indicator_text))
                null
            }else{
                this.btn_done.setTextColor(ContextCompat.getColor(this, R.color.textLight))
                ContextCompat.getDrawable(this, R.drawable.btn_default)
            }
        }
        presenter.attach(this)
        this.btn_done.setOnClickListener {
            payDialog()
        }


    }

    @SuppressLint("SetTextI18n")
    private fun addDigit(value:Int){
        val currentVal = tv_result.text.replace(Regex("[^0-9]"), "")
        this.tv_result.text = rupiah("$currentVal$value".toDouble())
    }

    private fun clearValues(){
        this.tv_result.text = Constant.TEXT_EMPTY
        addDigit(0)
    }

    private fun getButtonIds() = arrayOf(btn_clear, btn_0, btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9)

    override fun onCashboxLoaded(item: List<Data>) {
        val cashboxList = item.map { it.getString("name") }
        val cashBoxAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cashboxList)
        sp_cashbox.adapter = cashBoxAdapter
        sp_cashbox.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                cashboxId = item[position].getLong("id")
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

    }

    private fun payDialog(){
        val cashboxView = LayoutInflater.from(this).inflate(R.layout.dialog_cashbox_chooser, null, false)
        cashboxView.tv_total_payment.text = tv_result.text
        val cashBack = parseRupiah(tv_result.text) - parseRupiah(tv_paid.text)
        cashboxView.tv_cashback.text = rupiah(Math.abs(cashBack))
        cashboxView.tv_pay_type.text = if(cashBack >= 0){
            getString(R.string.cashback)
        }else{
            getString(R.string.paid)
        }
        val builder = AlertDialog.Builder(this).setView(cashboxView)
        dialog = builder.create()
        dialog?.show()


        RxView.clicks(cashboxView.btnPay).subscribe {
            runOnUiThread {
                dialog?.setCancelable(false)
                cashboxView.payProgressBar.visibility = View.VISIBLE
                val data = Data()
                data["order_id"] = order.getLong("order_id")
                data["cash_box_id"] = cashboxId
                data["payment_amount"] = parseRupiah(tv_result.text)
                presenter.payAccount(data)
            }
        }
    }



    override fun onPaymentSuccess(data: Data) {
        dialog?.dismiss()
        if(data.getString("status") == Constant.TransactionStatus.SUCCESS){
            val bundle = Bundle()
            var message = translations.get(Constant.TranslationsKey.ACCOUNT_RECEIVEABLE_CREATED_SUCCESSFULY)
            message = message.replace("{0}", order.getString("customer_name"))
            message = message.replace("{1}", order.getString("order_code"))
            bundle.putString(Constant.SUCCESS_MESSAGE, message)
            bundle.putInt(Constant.GOTO, R.id.nav_accounts_receiveable)
            moveTo(MenuActivity::class.java, bundle)
        }else{
            val bundle = Bundle()
            var message = translations.get(Constant.TranslationsKey.ACCOUNT_RECEIVEABLE_SAVED_SUCCESSFULY)
            message = message.replace("{0}", order.getString("customer_name"))
            message = message.replace("{1}", rupiah(data.getDouble("total_credit")))
            bundle.putString(Constant.SUCCESS_MESSAGE, message)
            bundle.putInt(Constant.GOTO, R.id.nav_accounts_receiveable)
            order["total_payment"] = data.getDouble("total_payment")
            order["total_credit"] = data.getDouble("total_credit")
            order["receiveable_date"] = data.getLong("receiveable_date")
            //bundle.putBoolean("show_message", true)
            bundle.putString("sales", order.toString())
            moveTo(ReceiptActivity::class.java, bundle)
        }
        finish()
    }


    override fun showError(error: Throwable) {
        dialog?.dismiss()
        networkExHandler.errorHandle(this, error)
    }

    override fun showNoOk(res: String) {
        dialog?.dismiss()
        snack(res).show()
    }

    override fun showEmpty() {
        dialog?.dismiss()
    }

    override fun showNotConnected(res: String) {
        snack(res).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
    }

}