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
import com.jakewharton.rxbinding2.widget.RxTextView
import com.overflow.cash.mvp.receiveable.AccountReceiveablePaymentContract
import com.overflow.cash.mvp.receiveable.AccountReceiveablePaymentPresenter
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.*
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
        Timber.i("Order %s", order)
        Timber.i("Extras %s", intent.extras)
        this.tv_paid.text = rupiah(order.getData("account_receiveable").getDouble("total_credit"))
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
        this.btn_done.setOnClickListener {
            presenter.attach(this)
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
        val cashboxView = LayoutInflater.from(this).inflate(R.layout.dialog_cashbox_chooser, null, false)

        val cashboxList = item.map { it.getString("name") }
        val cashBoxAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cashboxList)
        cashboxView.sp_cashbox.adapter = cashBoxAdapter

        val builder = AlertDialog.Builder(this).setTitle("Simpan Ke").setView(cashboxView)
        dialog = builder.create()
        dialog?.show()
        cashboxView.sp_cashbox.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                cashboxId = item[position].getLong("id")
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        cashboxView.btnPay.setOnClickListener {
            dialog?.setCancelable(false)
            cashboxView.payProgressBar.visibility = View.VISIBLE
            val data = Data()
            data["order_id"] = order.getLong("order_id")
            data["cash_box_id"] = cashboxId
            data["payment_amount"] = tv_result.text.replace(Regex("[^0-9]"), "").toDouble()
            presenter.payAccount(data)
        }

    }



    override fun onPaymentSuccess(data: Data) {
        dialog?.dismiss()
        if(data.getString("status") == Constant.TransactionStatus.SUCCESS){
            val bundle = Bundle()
            bundle.putString(Constant.SUCCESS_MESSAGE, translations.get(Constant.TranslationsKey.ACCOUNT_RECEIVEABLE_CREATED_SUCCESSFULY).replace("{0}", order.getString("customer_name")))
            bundle.putInt(Constant.GOTO, R.id.nav_accounts_receiveable)
            moveTo(MenuActivity::class.java, bundle)
        }else{
            val bundle = Bundle()
            bundle.putString(Constant.SUCCESS_MESSAGE, translations.get(Constant.TranslationsKey.ACCOUNT_RECEIVEABLE_CREATED_SUCCESSFULY).replace("{0}", order.getString("customer_name")))
            bundle.putInt(Constant.GOTO, R.id.nav_accounts_receiveable)
            order["total_payment"] = data.getDouble("total_payment")
            order["account_receiveable"] = data
            bundle.putBoolean("show_menu", false)
            bundle.putString("sales", order.toString())
            moveTo(ReceiptAccountReceiveableActivity::class.java, bundle)
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