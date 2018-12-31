package com.overflow.cash.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import com.overflow.cash.R
import com.overflow.cash.activity.Constant
import com.overflow.cash.activity.CustomerChooserActivity
import com.overflow.cash.activity.ReceiptActivity
import com.overflow.cash.mvp.order.SaveOrderContract
import com.overflow.cash.mvp.order.SaveOrderPresenter
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.moveTo
import com.overflow.cash.utils.parseRupiah
import com.overflow.cash.utils.rupiah
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import kotlinx.android.synthetic.main.fragment_sales_other.*
import javax.inject.Inject

/**
 * @author Rifky Aditya Bastara
 * */
class SalesOtherFragment : BaseFragment(), SaveOrderContract.View {
    @Inject
    lateinit var saveOrderPresenter: SaveOrderPresenter
    @Inject
    lateinit var translations: Translations
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    var customerId:Long? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sales_other, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        saveOrderPresenter.attach(this)
        writeValueNumpad()
        l_customer.setOnClickListener {
            addCustomer()
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

    private fun writeValueNumpad() {
        getButtonIds().forEach {
            it.setOnClickListener {
                when (it.id) {
                    R.id.btn_clear -> clearValues()
                    R.id.btn_backspace -> backSpace()
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

    private fun backSpace() {
        var temp = this.tv_result.text
        if(temp.isNotEmpty()) {
            temp = temp.substring(0, temp.length - 1)
        }
        try {
            this.tv_result.text = rupiah(parseRupiah(temp))
        }catch (e:Exception){
            clearValues()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        inflater?.inflate(R.menu.menu_checked, menu)
        menu?.findItem(R.id.action_check)?.setOnMenuItemClickListener {
            addOrder()
            false
        }
    }
    private fun addOrder(){
        if(tv_result.text.isEmpty()){
            showErrorMessage(translations.get(Constant.TranslationsKey.REQUIRED_VALUE_SALES_AMOUNT))
            return
        }

        if(tv_description.text.isEmpty()){
            showErrorMessage(translations.get(Constant.TranslationsKey.REQUIRED_VALUE_DESCRIPTION))
            return
        }
        val paymentMethod = if(rg_payment_method.checkedRadioButtonId == R.id.rd_cash){
            Constant.PaymentMethod.CASH
        }else{
            Constant.PaymentMethod.CARD
        }
        showProgress()
        val order = Data()
        order["customer_id"] = this.customerId
        order["payment_method"] = paymentMethod
        order["description"] = tv_description.text.toString()
        order["total_amount"] = parseRupiah(tv_result.text)
        order["total_payment"] = parseRupiah(tv_result.text)
        this.saveOrderPresenter.saveOrder(order)
    }

    private fun hideProgress(){
        progress.visibility = View.GONE
    }

    private fun showProgress(){
        progress.visibility = View.VISIBLE
    }
    private fun getButtonIds() = arrayOf(btn_backspace, btn_clear, btn_0, btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9)

    override fun onDetach() {
        super.onDetach()
        saveOrderPresenter.detach()
    }

    override fun onOrderCreated(data: Data) {
        hideProgress()
        val bundle = Bundle()
        bundle.putString(Constant.ARG_SALES, data.toString())
        val message = translations.get(Constant.TranslationsKey.SALES_CREATED_SUCCESSFULY).replace("{0}", data.getString("order_code"))
        bundle.putString(Constant.SUCCESS_MESSAGE, message)
        bundle.putInt(Constant.GOTO, R.id.nav_new_transaction)
        context?.moveTo(ReceiptActivity::class.java, bundle)
    }

    override fun showError(error: Throwable) {
        networkExHandler.errorHandle(activity!!, error)
    }

    override fun showNoOk(res: String) {
        hideProgress()
        showErrorMessage(res)
    }

    override fun showEmpty() {
        //Nothing todo
    }

    override fun showNotConnected(res: String) {
        hideProgress()
        showErrorMessage(res)
    }

    private fun addCustomer(){
        val intent = Intent(activity, CustomerChooserActivity::class.java)
        if(!TextUtils.isEmpty(tv_customer_name.text))
            intent.putExtra("name", tv_customer_name.text.toString())
        startActivityForResult(intent, Constant.REQUEST_CODE_VIEW_CUSTOMER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == Constant.REQUEST_CODE_VIEW_CUSTOMER && resultCode == Activity.RESULT_OK){
            this.customerId = data?.getLongExtra("id", -1L)
            if (data != null) {
                tv_customer_name.text =data.getStringExtra("name")
            }

        }
    }

}