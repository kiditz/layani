package com.overflow.cash.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.overflow.cash.R
import com.overflow.cash.fragment.CashboxHistoryFragment
import com.overflow.cash.fragment.DialogOrderSummary
import com.overflow.cash.mvp.cashbox.SaveCashboxSummaryContract
import com.overflow.cash.mvp.cashbox.SaveCashboxSummaryPresenter
import com.overflow.cash.mvp.order.SummaryOrderContract
import com.overflow.cash.mvp.order.SummaryOrderPresenter
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.moveTo
import com.overflow.cash.utils.replaceContent
import com.overflow.libs.core.Data
import com.overflow.libs.core.DateUtil
import com.overflow.libs.core.Translations
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_cashbox_history_dispatcher.*
import javax.inject.Inject

class CashboxHistoryDispatcherActivity : BaseActivity(), HasSupportFragmentInjector, SummaryOrderContract.View, SaveCashboxSummaryContract.View {
    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var presenter: SummaryOrderPresenter
    @Inject
    lateinit var saveCashboxSummaryPresenter: SaveCashboxSummaryPresenter
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    @Inject
    lateinit var translations: Translations

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cashbox_history_dispatcher)
        val cashboxSummaryId = intent.getLongExtra(CashboxHistoryFragment.ARG_CASH_BOX_SUMMARY_ID, -1)
        val status = intent.getStringExtra("status")
        tv_fullname.text = intent.getStringExtra("fullname")
        tv_datetime.text = DateUtil.printDateTime(intent.getLongExtra("start_at", System.currentTimeMillis()))

        presenter.attach(this)
        saveCashboxSummaryPresenter.attach(this)
        if (status == Constant.CashboxStatus.CLOSE) {
            btn_fill_cash_summary.visibility = View.GONE
        } else {
            btn_fill_cash_summary.visibility = View.VISIBLE
            replaceContent(R.id.container, CashboxHistoryFragment.newInstance(cashboxSummaryId))
        }
        btn_fill_cash_summary.setOnClickListener {
            this.presenter.loadSummary()
        }

    }


    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }

    override fun showError(error: Throwable) {
        progress.visibility = View.GONE
        networkExHandler.errorHandle(this, error)
    }

    override fun showNoOk(res: String) {
        progress.visibility = View.GONE
        showErrorMessage(res)
    }

    override fun showEmpty() {
        progress.visibility = View.GONE
    }

    override fun showNotConnected(res: String) {
        progress.visibility = View.GONE
        showErrorMessage(res)
    }

    override fun onSummaryLoaded(summary: Data) {
        summary["id"] = intent.getLongExtra("id", -1L)
        val dialog = DialogOrderSummary()
        dialog.arguments = summary.toBundle()
        dialog.onDoneClick = {
            progress.visibility = View.VISIBLE
            this.saveCashboxSummaryPresenter.saveCashboxHistory(it)
            dialog.dismiss()

        }
        dialog.show(supportFragmentManager, "Cash Dialog")
    }


    override fun onCashboxSaved(data: Data) {
        val bundle = Bundle()
        bundle.putString(Constant.SUCCESS_MESSAGE, translations.get(Constant.TranslationsKey.RECAP_CASH_SAVED_SUCCESSFULLY))
        bundle.putInt(Constant.GOTO, R.id.nav_cash_summary)
        moveTo(MenuActivity::class.java, bundle)
    }

}