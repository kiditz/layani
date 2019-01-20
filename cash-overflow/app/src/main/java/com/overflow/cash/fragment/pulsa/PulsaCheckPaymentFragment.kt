package com.overflow.cash.fragment.pulsa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.RxTextView
import com.overflow.cash.R
import com.overflow.cash.activity.Constant
import com.overflow.cash.fragment.BaseFragment
import com.overflow.cash.mvp.pulsa.LoadPulsaProductByProviderContract
import com.overflow.cash.mvp.pulsa.LoadPulsaProductByProviderPresenter
import com.overflow.cash.mvp.pulsa.SendOrderPulsaContract
import com.overflow.cash.mvp.pulsa.SendOrderPulsaPresenter
import com.overflow.cash.net.API
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.MessageButtonHandle
import com.overflow.cash.utils.showMessage
import com.overflow.libs.core.Data
import kotlinx.android.synthetic.main.fragment_pulsa_check_payment.*
import timber.log.Timber
import javax.inject.Inject

class PulsaCheckPaymentFragment : BaseFragment(), LoadPulsaProductByProviderContract.View , SendOrderPulsaContract.View{

    @Inject
    lateinit var loadPulsaProductByProviderPresenter: LoadPulsaProductByProviderPresenter
    @Inject
    lateinit var sendOrderPulsaPresenter: SendOrderPulsaPresenter
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    var providerId: Long = -1L
    var paymentCheckCode = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pulsa_check_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.loadPulsaProductByProviderPresenter.attach(this)
        this.sendOrderPulsaPresenter.attach(this)
        this.providerId = arguments!!.getLong("id")
        this.loadPulsaProductByProviderPresenter.loadProduct(API.MIN_PAGE, providerId)
    }

    override fun onProductLoaded(productList: List<Data>) {
        val check = productList.filter { it.getString("code").startsWith(Constant.PaymentPrefix.CHECK, true) }.map {
            it.getString("code")
        }
        this.paymentCheckCode = check[0]
        val noMeterObserver = RxTextView.textChanges(ed_no_meter).skipInitialValue().map { text -> text.length > 10 }
        noMeterObserver.subscribe({ valid ->
            ed_no_meter_wrapper.error = getString(R.string.invalid_no_meter)
            ed_no_meter_wrapper.isErrorEnabled = !valid
            if (this.paymentCheckCode.isNotEmpty()) {
                btn_check_payment.isEnabled = valid
            }
        }, {
            Timber.e(it)
        })

        btn_check_payment.setOnClickListener {
            sendOrder()
        }
    }


    override fun onOrderSended(result: Data) {
        showProgress(false)
        btn_check_payment.isEnabled = false
        ed_no_meter.setText(Constant.TEXT_EMPTY)
        activity!!.showMessage(getString(R.string.check_payment), getString(R.string.transaction_in_progress).replace("{0}", getString(R.string.check_payment)), false, object: MessageButtonHandle(){
        }).show()
    }

    override fun showError(error: Throwable) {
        networkExHandler.errorHandle(activity!!, error)
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
    private fun sendOrder() {
        val data = Data()
        data["code"] = paymentCheckCode
        data["msisdn"] = ed_no_meter.text.toString()
        showProgress(true)
        sendOrderPulsaPresenter.sendOrder(data)
    }

    private fun showProgress(isShowed:Boolean=false){
        if(isShowed){
            this.progress.visibility = View.VISIBLE
        }else{
            this.progress.visibility = View.GONE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle) =
                PulsaCheckPaymentFragment().apply {
                    arguments = bundle
                }
    }
}