package com.overflow.cash.activity.pulsa

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import com.overflow.cash.R
import com.overflow.cash.activity.BaseActivity
import com.overflow.cash.mvp.pulsa.SendOrderPulsaContract
import com.overflow.cash.mvp.pulsa.SendOrderPulsaPresenter
import com.overflow.cash.utils.MessageButtonHandle
import com.overflow.cash.utils.rupiah
import com.overflow.cash.utils.showMessage
import com.overflow.libs.core.Data
import kotlinx.android.synthetic.main.activity_pulsa_pay_the_payment.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class PayThePaymentActivity : BaseActivity(), SendOrderPulsaContract.View {
    val format = SimpleDateFormat("yyyyMM", Locale.getDefault())
    val toFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    @Inject
    lateinit var sendOrderPulsaPresenter: SendOrderPulsaPresenter
    lateinit var data: Data
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pulsa_pay_the_payment)
        sendOrderPulsaPresenter.attach(this)
        val bundle = intent.extras
        data = Data(bundle.getString("data"))
        tv_customer_name.text = data.getString("customerName")
        tv_bill_amount.text = rupiah(data.getDouble("billAmount"))
        tv_adm_cost.text = rupiah(data.getDouble("admCost"))
        tv_post_paid_amount.text = rupiah(data.getDouble("postPaidAmount"))
        tv_num_of_trx.text = data.getString("numOfTrx")
        tv_post_paid_amount.text = rupiah(data.getDouble("postPaidAmount"))
        tv_customer_id.text = data.getString("msisdn")
        try {
            tv_month.text = toFormat.format(format.parse(data.getString("postPaidMonth")))
        } catch (e: Exception) {
            tv_month.text = data.getString("postPaidMonth")
        }
        btn_pay.setOnClickListener {
            doOrder(data.getString("productCode").replace("LCEK", "LBYR"))
        }
    }


    private fun doOrder(productCode: String) {
        this.showMessage(getString(R.string.pay), getString(R.string.are_you_sure_transaction), object : MessageButtonHandle() {
            override fun ok(dialog: DialogInterface, which: Int) {
                super.ok(dialog, which)
                sendOrder(productCode)
            }
        }).show()

    }

    override fun onOrderSended(result: Data) {
        showProgress(false)
        showSuccessMessage(getString(R.string.payment_in_progress))
    }


    override fun showError(error: Throwable) {
    }

    override fun showNoOk(res: String) {
        showProgress(false)
        showErrorMessage(res)
    }

    override fun showEmpty() {
    }

    override fun showNotConnected(res: String) {
        showProgress(false)
        showErrorMessage(res)
    }

    private fun sendOrder(productCode: String) {
        val input = Data()
        input["id"] = data.getLong("id")
        input["code"] = productCode
        input["msisdn"] = tv_customer_id.text.toString()
        showProgress(true)
        sendOrderPulsaPresenter.sendOrder(input)
    }
    private fun showProgress(isShowed:Boolean=false){
        if(isShowed){
            this.progress.visibility = View.VISIBLE
        }else{
            this.progress.visibility = View.GONE
        }
    }
}