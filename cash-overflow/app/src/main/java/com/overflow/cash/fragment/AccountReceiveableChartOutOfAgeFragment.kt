package com.overflow.cash.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.overflow.cash.activity.Constant
import com.overflow.cash.R
import com.overflow.cash.mvp.chart.AccountReceiveableOutOfAgeChartContract
import com.overflow.cash.mvp.chart.AccountReceiveableOutOfAgeChartPresenter
import com.overflow.libs.core.Data
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_bar_chart.*
import javax.inject.Inject

class AccountReceiveableChartOutOfAgeFragment:Fragment(), AccountReceiveableOutOfAgeChartContract.View {
    @Inject
    lateinit var presenter:AccountReceiveableOutOfAgeChartPresenter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bar_chart, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        chart?.setBackgroundColor(Color.WHITE)
        chart?.setDrawGridBackground(false)
        chart?.setTouchEnabled(true)
        chart?.setScaleEnabled(true)
        chart?.isDragEnabled = true
        chart?.setPinchZoom(false)
        chart?.description?.isEnabled = false
        this.presenter.attach(this)
    }

    override fun onChartLoaded(data: Data) {
        chart?.data?.clearValues()
        val entries = mutableListOf<BarEntry>()
        entries.add(BarEntry(1f, data.getFloat("0-30 Hari")))
        entries.add(BarEntry(2f, data.getFloat("30-60 Hari")))
        entries.add(BarEntry(3f, data.getFloat("60-90 Hari")))
        entries.add(BarEntry(4f, data.getFloat("Lebih Dari 90 Hari")))

        val xAxis = chart.xAxis
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled= true

        xAxis.position = XAxis.XAxisPosition.BOTTOM

        xAxis?.labelRotationAngle = -25f
        xAxis?.setValueFormatter { value, _ ->
            when (value) {
                1f -> return@setValueFormatter "0-30 Hari"
                2f -> return@setValueFormatter "30-60 Hari"
                3f -> return@setValueFormatter "60-90 Hari"
                4f -> return@setValueFormatter "> 90 Hari"
                else -> ""
            }
        }
        xAxis.labelCount = 4


        val barDataSet = BarDataSet(entries, Constant.TEXT_EMPTY)
        barDataSet.valueFormatter = LargeValueFormatter()
        barDataSet.color = Color.RED

        chart?.data = BarData(listOf(barDataSet))
        chart.barData.barWidth = .4f

        chart?.legend?.isEnabled = false
        xAxis?.setDrawGridLines(false)
        chart?.axisRight?.isEnabled = false
        val yAxis = chart?.axisLeft

        yAxis?.valueFormatter = LargeValueFormatter()
        yAxis?.setDrawGridLines(false)
        chart?.axisRight?.setDrawLabels(false)

        chart?.notifyDataSetChanged()
        chart?.invalidate()
        chart?.animateY(1400)

    }

    override fun showError(error: Throwable) {
        
    }

    override fun showNoOk(res: String) {

    }

    override fun showEmpty() {

    }

    override fun showNotConnected(res: String) {

    }


    companion object {
        const val ARG_PERIOD = "period"
        @JvmStatic
        fun newInstance(period: String) =
                AccountReceiveableChartOutOfAgeFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PERIOD, period)
                    }
                }
    }

}