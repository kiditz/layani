package com.overflow.cash

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import com.overflow.cash.fragment.ReceiptFragment
import com.overflow.cash.mvp.order.RefundContract
import com.overflow.cash.mvp.order.RefundPresenter
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.*
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_receipt_with_refund.*
import kotlinx.android.synthetic.main.dialog_refund.view.*
import javax.inject.Inject

class ReceiptTransactionWithRefundActivity : BaseActivity(), HasSupportFragmentInjector, RefundContract.View {
    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    @Inject
    lateinit var translations: Translations
    @Inject
    lateinit var presenter: RefundPresenter
    @Inject
    lateinit var preferences:SharedPreferences
    lateinit var receiptFragment: ReceiptFragment
    lateinit var order:Data

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        this.presenter.attach(this)
        shouldRequestPermissions(Constant.REQUEST_PERMISSION_CODE)
        setContentView(R.layout.activity_receipt_with_refund)
        this.order = Data(intent.getStringExtra(Constant.ARG_SALES))
        //Handle Action Cancel
        this.btn_cancel.isEnabled = order.getString("status") == Constant.TransactionStatus.SUCCESS
        this.btn_cancel.setOnClickListener {

            showRefundDialog()
        }

        receiptFragment = ReceiptFragment.newInstance(intent.getStringExtra(Constant.ARG_SALES))
        replaceContent(R.id.receipt, receiptFragment)

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
        view.btn_pay.setOnClickListener {
            dialog.dismiss()
            val data = Data()
            data["order_id"] = order.getLong("id")
            presenter.refund(data)
            showProgress(true)
        }
        dialog.show()
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return home(item!!)
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
        bundle.putString(Constant.SUCCESS_MESSAGE, translations.get(Constant.TranslationsKey.REFUND_SAVED_SUCCESSFULLY).replace("{0}", "#${data.getString("order_code")}"))
        bundle.putInt(Constant.GOTO, R.id.nav_transaction_history)
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showNotConnected(res: String) {
        showProgress(false)
        snack(res).show()
    }
}
