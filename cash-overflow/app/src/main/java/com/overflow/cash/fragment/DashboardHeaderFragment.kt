package com.overflow.cash.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.overflow.cash.R
import com.overflow.cash.mvp.chart.DashboardHeaderContract
import com.overflow.cash.mvp.chart.DashboardHeaderPresenter
import com.overflow.cash.utils.rupiah
import com.overflow.libs.core.Data
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_dashboard_header.*
import javax.inject.Inject

class DashboardHeaderFragment:Fragment(), DashboardHeaderContract.View {

    @Inject
    lateinit var presenter: DashboardHeaderPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard_header, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.presenter.attach(this)
    }

    override fun onHeaderLoaded(data: Data) {
        tvAccountReceiveable?.text = rupiah(data.getDouble("total_receiveable"))
        tvCash?.text = rupiah(data.getDouble("cashbox_amount"))
        tvProfit?.text = rupiah(data.getDouble("total_profit"))
        tvOmzet.text = rupiah(data.getDouble("total_income"))
    }

    override fun showError(error: Throwable) {

    }

    override fun showNoOk(res: String) {

    }

    override fun showEmpty() {

    }

    override fun showNotConnected(res: String) {
    }

}