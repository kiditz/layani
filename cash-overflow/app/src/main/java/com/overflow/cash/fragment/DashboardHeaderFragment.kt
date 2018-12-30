package com.overflow.cash.fragment

import android.annotation.SuppressLint
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

    @SuppressLint("SetTextI18n")
    override fun onHeaderLoaded(data: Data) {
        if(data.getDouble("sales_increase_percentage") < 0){
            iv_sales.setImageResource(R.drawable.ic_arrow_down)
            tv_sales_percentage?.text = Math.round(data.getDouble("sales_increase_percentage") * -1.0).toString() + "%"
        }else{
            tv_sales_percentage?.text = Math.round(data.getDouble("sales_increase_percentage")).toString() + "%"
            iv_sales.setImageResource(R.drawable.ic_arrow_up)
        }
        if(data.getDouble("trx_increase_percentage") < 0){
            iv_trx.setImageResource(R.drawable.ic_arrow_down)
            tv_trx_percentage?.text = Math.round(data.getDouble("trx_increase_percentage") * -1.0).toString() + "%"
        }else{
            tv_trx_percentage?.text = Math.round(data.getDouble("trx_increase_percentage")).toString() + "%"
            iv_trx.setImageResource(R.drawable.ic_arrow_up)
        }
        tv_sales?.text = rupiah(data.getDouble("sales"))
        tv_trx?.text = Math.round(data.getDouble("trx")).toString()
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