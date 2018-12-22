package com.overflow.cash.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.overflow.cash.R
import com.overflow.cash.activity.Constant
import com.overflow.cash.activity.PaymentOtherActivity
import com.overflow.cash.activity.ReceiptActivity
import com.overflow.cash.mvp.discount.LoadDiscountByBillAmountContract
import com.overflow.cash.mvp.discount.LoadDiscountByBillAmountPresenter
import com.overflow.cash.mvp.order.SaveOrderContract
import com.overflow.cash.mvp.order.SaveOrderPresenter
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.realm.OrderItemRealm
import com.overflow.cash.utils.moveTo
import com.overflow.cash.utils.parseRupiah
import com.overflow.cash.utils.round
import com.overflow.cash.utils.rupiah
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import kotlinx.android.synthetic.main.fragment_payment.*
import javax.inject.Inject

/**
 * @author Rifky Aditya Bastara
 * */
class PaymentTransactionFragment : BaseFragment(), SaveOrderContract.View, LoadDiscountByBillAmountContract.View {

    @Inject
    lateinit var translations: Translations
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    @Inject
    lateinit var saveOrderPresenter: SaveOrderPresenter
    @Inject
    lateinit var loadDiscountPresenter: LoadDiscountByBillAmountPresenter
    @Inject
    lateinit var orderItemRealm: OrderItemRealm
    private var discountId:Long=0
    private var discountAmount:Double=0.0
    private var discountType:String=Constant.TEXT_EMPTY
    private var paymentMethod = Constant.PaymentMethod.CASH
    var totalAmount = 0.0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_payment, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        saveOrderPresenter.attach(this)
        loadDiscountPresenter.attach(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        totalAmount = arguments!!.getDouble("amount", 0.0)

        this.loadDiscountPresenter.loadDiscount(totalAmount)
        tv_bill_amount.text = rupiah(totalAmount)


        //By Default Payment Type is cash
        initButtonPaymentType(Constant.PaymentMethod.CASH)

        //Change Payment Type to card
        btn_card.setOnClickListener {
            initButtonPaymentType(Constant.PaymentMethod.CARD)
        }

        //Change Payment Type to cash
        btn_cash.setOnClickListener {
            initButtonPaymentType(Constant.PaymentMethod.CASH)
        }


        btn_other.setOnClickListener {
            arguments!!.putString("payment_method", paymentMethod)
            if(discountAmount > 0){
                arguments!!.putLong("discount_id", discountId)
                arguments!!.putDouble("amount", parseRupiah(tv_total_amount.text))
            }
            context!!.moveTo(PaymentOtherActivity::class.java, arguments!!)
        }

        initRound()
        btn_the_right_money.setOnClickListener {
            val cashBack = 0.0
            showDialogPayment(totalAmount, cashBack)
        }


        btn_suggestion_round_3.setOnClickListener {
            val totalPayment = parseRupiah(btn_suggestion_round_3.text)
            val cashBack = totalPayment - totalAmount
            showDialogPayment(totalPayment, cashBack)
        }

        btn_suggestion_round_4.setOnClickListener {
            val totalPayment = parseRupiah(btn_suggestion_round_4.text)
            val cashBack = totalPayment - totalAmount
            showDialogPayment(totalPayment, cashBack)
        }

        btn_suggestion_round_5.setOnClickListener {
            val totalPayment = parseRupiah(btn_suggestion_round_5.text)
            val cashBack = totalPayment - totalAmount
            showDialogPayment(totalPayment, cashBack)
        }
    }
    //Initialize button color
    private fun initButtonPaymentType(paymentType:String){
        val bgCurrent = R.drawable.btn_accent
        val textCurrent = ContextCompat.getColor(context!!, android.R.color.tab_indicator_text)
        val textLight = ContextCompat.getColor(context!!, R.color.textLight)
        val bgAccent = ContextCompat.getColor(context!!, R.color.colorAccent)
        if(paymentType == Constant.PaymentMethod.CARD){
            this.btn_card.setBackgroundColor(bgAccent)
            this.btn_card.setTextColor(textLight)
            this.btn_cash.setBackgroundResource(bgCurrent)
            this.btn_cash.setTextColor(textCurrent)
        }else{
            this.btn_card.setBackgroundResource(bgCurrent)
            this.btn_card.setTextColor(textCurrent)
            this.btn_cash.setBackgroundColor(bgAccent)
            this.btn_cash.setTextColor(textLight)
        }
        this.paymentMethod = paymentType
    }

    /**
     * Show dialog preview before paid transaction
     * */
    private fun showDialogPayment(totalPayment:Double, cashBack:Double){
        val payFragment = DialogPaymentMakeSure.newInstance(totalPayment, cashBack, getString(R.string.are_you_sure_transaction))
        payFragment.onDoneClick = {
            doOrder(totalPayment)
        }
        payFragment.show(activity!!.supportFragmentManager, Constant.PaymentMethod.CASH)
    }

    //Call api to save order
    private fun doOrder(totalPayment:Double){
        val totalAmount = parseRupiah(tv_total_amount.text)
        if(totalPayment < totalAmount){
            hideProgress()
            showErrorMessage(translations.get(Constant.TranslationsKey.INVALID_TOTAL_AMOUNT))
            return
        }
        arguments?.apply {
            showProgress()
            val order = Data()
            if(this.getLong("customer_id") > 0){
                order["customer_id"] = this.getLong("customer_id", -1)
            }else{
                order["customer_id"] = null
            }

            if(this.containsKey("order_id")){
                order["order_id"] = this.getLong("order_id")
            }
            order["description"] = this.getString("description", " ")
            order["total_amount"] = totalAmount
            order["total_payment"] = totalPayment
            if(discountAmount > .0){
                order["discount_id"] = discountId
            }
            order["payment_method"] = paymentMethod
            if(this.containsKey("items")){
                val itemsStr = this.getString("items")
                val itemData = Data(itemsStr)
                val orderItems = itemData.getList("items")
                order["items"] = orderItems
            }
            saveOrderPresenter.saveOrder(order)
        }
    }

    // Called when discount loaded
    override fun onDiscountLoaded(data: Data) {

        this.l_discount_calculation.visibility = View.VISIBLE
        this.discountId = data.getLong("id")
        this.discountAmount = data.getDouble("amount")
        this.discountType = data.getString("discount_type")
        if(discountType == Constant.DiscountType.FIXED_PRICE){
            this.tv_discount.text = rupiah(discountAmount)
            this.totalAmount = parseRupiah(tv_bill_amount.text) - discountAmount
            this.tv_total_amount.text = rupiah(totalAmount)
        }else{
            val calculateDiscount = (discountAmount / 100) * parseRupiah(tv_bill_amount.text)
            this.totalAmount = parseRupiah(tv_bill_amount.text) - calculateDiscount
            this.tv_discount.text = "${discountAmount.toInt()}%"
            this.tv_total_amount.text = rupiah(totalAmount)
        }
        initRound()
    }

    override fun onDiscountNotLoaded(data: Data) {
        this.tv_discount.text = "N/A"
        this.l_discount_calculation.visibility = View.GONE
        this.tv_total_amount.text = rupiah(parseRupiah(tv_bill_amount.text) - discountAmount)
    }

    // Called when order has been created
    override fun onOrderCreated(data: Data) {
        hideProgress()
        orderItemRealm.deleteItems()
        val bundle = Bundle()
        bundle.putString(Constant.ARG_SALES, data.toString())
        val message = translations.get(Constant.TranslationsKey.SALES_CREATED_SUCCESSFULY).replace("{0}", data.getString("order_code"))
        bundle.putString(Constant.SUCCESS_MESSAGE, message)
        bundle.putInt(Constant.GOTO, R.id.nav_new_transaction)
        context?.moveTo(ReceiptActivity::class.java, bundle)
    }

    override fun showError(error: Throwable) {
        hideProgress()
        networkExHandler.errorHandle(activity!!, error)
    }

    // Show the error message in the header when order cannot be created or
    // Somthing went wrong with the server
    override fun showNoOk(res: String) {
        hideProgress()
        showErrorMessage(res)

    }

    //Nothing todo here
    override fun showEmpty() {
    }

    private fun initRound(){
        btn_suggestion_round_3.text = if(totalAmount > 100.0){
            rupiah(round(totalAmount, -3))
        }else{
            rupiah(totalAmount)
        }
        btn_suggestion_round_4.text = if(totalAmount > 10000.0){
            rupiah(round(totalAmount, -4))
        }else{
            rupiah(totalAmount)
        }
        btn_suggestion_round_5.text = if(totalAmount > 100000.0){
            rupiah(round(totalAmount, -5))
        }else{
            rupiah(totalAmount)
        }
    }

    override fun showNotConnected(res: String) {
        hideProgress()
        showErrorMessage(res)
    }

    private fun hideProgress(){
        progress.visibility = View.GONE
        btn_the_right_money.isEnabled = true
        btn_suggestion_round_3.isEnabled = true
        btn_suggestion_round_4.isEnabled = true
        btn_suggestion_round_5.isEnabled = true
    }

    private fun showProgress(){
        this.progress.visibility = View.VISIBLE
        btn_the_right_money.isEnabled = false
        btn_suggestion_round_3.isEnabled = false
        btn_suggestion_round_4.isEnabled = false
        btn_suggestion_round_5.isEnabled = false
    }

    override fun onDetach() {
        super.onDetach()
        this.saveOrderPresenter.detach()
    }


}