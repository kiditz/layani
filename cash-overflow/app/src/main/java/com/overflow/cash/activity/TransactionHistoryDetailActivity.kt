package com.overflow.cash.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.overflow.cash.R
import com.overflow.cash.fragment.OrderItemsFragment
import com.overflow.cash.mvp.order.DeleteOrderContract
import com.overflow.cash.mvp.order.DeleteOrderPresenter
import com.overflow.cash.mvp.order.RefundContract
import com.overflow.cash.mvp.order.RefundPresenter
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.*
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_transaction_history_detail.*
import kotlinx.android.synthetic.main.dialog_refund.view.*
import javax.inject.Inject

class TransactionHistoryDetailActivity : BaseActivity(), HasSupportFragmentInjector, RefundContract.View, DeleteOrderContract.View {
    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    @Inject
    lateinit var translations: Translations
    @Inject
    lateinit var presenter: RefundPresenter

    @Inject
    lateinit var deleteOrderPresenter: DeleteOrderPresenter

    @Inject
    lateinit var preferences:SharedPreferences
    lateinit var order:Data
    private var canPay = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.presenter.attach(this)
        this.deleteOrderPresenter.attach(this)
        shouldRequestPermissions(Constant.REQUEST_PERMISSION_CODE)
        setContentView(R.layout.activity_transaction_history_detail)

        this.order = Data(intent.getStringExtra(Constant.ARG_SALES))
        this.tv_total_amount.text = rupiah(order.getDouble("total_amount"))
        // Initialize header
        initHeader(order)

        this.canPay = order.getString("status") == Constant.TransactionStatus.CREATED
        this.btn_cancel.isEnabled = order.getString("status") == Constant.TransactionStatus.SUCCESS
        this.btn_cancel.setOnClickListener {
            showRefundDialog()
        }

        val orderItems = OrderItemsFragment.newInstance(order.toBundle())
        orderItems.onItemsLoaded = { orderList ->
            this.btn_receipt.setOnClickListener{
                order["order_items"] = orderList
                val bundle = Bundle()
                bundle.putString(Constant.ARG_SALES, order.toString())
                bundle.putBoolean("just_back", true)
                val message = translations.get(Constant.TranslationsKey.SALES_CREATED_SUCCESSFULY).replace("{0}", order.getString("order_code"))
                bundle.putString(Constant.SUCCESS_MESSAGE, message)
                bundle.putInt(Constant.GOTO, R.id.nav_new_transaction)
                moveTo(ReceiptActivity::class.java, bundle)
            }
        }
        replaceContent(R.id.container, orderItems)
    }

    private fun initHeader(order:Data){
        val orderCode = order.getString("order_code")
        val customerName = if(!order.containsKeyAndNotNull("customer_name")){
            "N/A"
        }else{
            order.getString("customer_name")
        }
        val paymentMethod = order.getString("payment_method")
        tv_order_code.text = orderCode
        tv_customer_name.text = customerName
        tv_payment_method.text = translations.get(paymentMethod.toLowerCase())
    }

    private fun showProgress(show:Boolean=true){
        if(show){
            progress.visibility = View.VISIBLE
        }else{
            progress.visibility = View.GONE
        }
    }

    private fun showRefundDialog(){
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_refund, null, false)
        view.tv_total_refund.text = rupiah(order.getDouble("total_amount"))

        val dialog = AlertDialog.Builder(this).setView(view).create()
        dialog.setTitle(R.string.are_you_sure_refund)
        view.btn_pay.setOnClickListener {
            dialog.dismiss()
            val data = Data()
            data["order_id"] = order.getLong("id")
            data["description"] = view.ed_refund_reason.text.toString()
            presenter.refund(data)
            showProgress(true)
        }
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_transaction_history_detail, menu)
        menu?.findItem(R.id.action_pay)?.isVisible = this.canPay
        menu?.findItem(R.id.action_delete_transaction)?.isVisible = this.canPay
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item!!.itemId){
            R.id.action_pay -> {
                val bundle = Bundle()
                if(order.containsKeyAndNotNull("customer_id"))
                    bundle.putLong("customer_id", order.getLong("customer_id"))
                else
                    bundle.putLong("customer_id", -1L)
                bundle.putDouble("amount", order.getDouble("total_amount"))
                bundle.putLong("order_id", order.getLong("id"))
                moveTo(PaymentTransactionDispatcherActivity::class.java, bundle)
                false
            }

            R.id.action_delete_transaction -> {
                this.showMessage(getString(R.string.delete), getString(R.string.are_you_sure_remove).replace("{0}", order.getString("order_code")), object:MessageButtonHandle(){
                    override fun ok(dialog: DialogInterface, which: Int) {
                        super.ok(dialog, which)
                        deleteOrderPresenter.delete(order.getLong("id"))
                    }
                }).show()
                false
            }
            else -> home(item)
        }
    }

    override fun onBackPressed() {
        if(intent.getBooleanExtra("back", false)){
            moveTo(MenuActivity::class.java, intent.extras)
        }else{
            super.onBackPressed()
        }
    }


    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }

    override fun onRefundSuccess(data: Data) {
        showProgress(false)
        val bundle = Bundle()
        bundle.putString(Constant.SUCCESS_MESSAGE, translations.get(Constant.TranslationsKey.REFUND_SAVED_SUCCESSFULLY).replace("{0}", "#${order.getString("order_code")}"))
        bundle.putInt(Constant.GOTO, R.id.nav_transaction_history)
        moveTo(MenuActivity::class.java, bundle)
    }

    override fun onDeleteOrderSuccess(data: Data) {
        showProgress(false)
        val bundle = Bundle()
        bundle.putString(Constant.SUCCESS_MESSAGE, translations.get(Constant.TranslationsKey.DELETE_ORDER_SUCCESSFULLY).replace("{0}", "#${order.getString("order_code")}"))
        bundle.putInt(Constant.GOTO, R.id.nav_new_transaction)
        moveTo(MenuActivity::class.java, bundle)
    }

    override fun showError(error: Throwable) {
        showProgress(false)
        networkExHandler.errorHandle(this, error)
    }

    override fun showNoOk(res: String) {
        showProgress(false)
        snack(res).show()
    }

    override fun showEmpty() {
        showProgress(false)
        //toast(order.getString("description")).show()
        showMessage(order.getString("description"))
    }

    override fun showNotConnected(res: String) {
        showProgress(false)
        snack(res).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        this.presenter.detach()
        this.deleteOrderPresenter.detach()
    }
}
