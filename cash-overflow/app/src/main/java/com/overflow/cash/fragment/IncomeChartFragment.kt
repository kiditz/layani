package com.overflow.cash.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.overflow.cash.R
import com.overflow.cash.mvp.chart.IncomeChartContract
import com.overflow.cash.mvp.chart.IncomeChartPresenter
import com.overflow.libs.core.Data
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_line_chart.*
import org.json.JSONObject
import javax.inject.Inject

class IncomeChartFragment:Fragment(), IncomeChartContract.View {
    @Inject
    lateinit var presenter:IncomeChartPresenter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_line_chart, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)


    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        orderChart?.setBackgroundColor(Color.WHITE)
        orderChart?.setDrawGridBackground(false)
        orderChart?.setTouchEnabled(true)
        orderChart?.setScaleEnabled(true)
        orderChart?.isDragEnabled = true
        orderChart?.setPinchZoom(true)
        orderChart?.description?.isEnabled = false
        this.presenter.attach(this)
        this.arguments?.let {
            this.presenter.showChart(it.getString(ARG_PERIOD))
        }

    }

    override fun onChartLoaded(data: Data) {
        try {
            orderChart?.data?.clearValues()
            val payload = JSONObject(data.toString())
            val labels = payload.getJSONArray("chart_label")
            val linesData = payload.getJSONArray("lines_data").getJSONArray(0)

            val xAxis = orderChart?.xAxis
            xAxis?.setDrawGridLines(false)

            xAxis?.setValueFormatter { value, _ ->
                return@setValueFormatter labels.getString(value.toInt())
            }

            val yAxis = orderChart?.axisLeft
            yAxis?.valueFormatter = LargeValueFormatter()

            xAxis?.setAvoidFirstLastClipping(true)
            //Disable Duplicate X Axis
            xAxis?.granularity = 1f
            xAxis?.position = XAxis.XAxisPosition.BOTTOM

            val dataSet = mutableListOf<Entry>()

            for (index in 0 until labels.length()){
                val i = index.toFloat()
                dataSet.add(Entry(i, linesData.getInt(index).toFloat()))

            }

            val allLinesDataSet = LineDataSet(dataSet, "")

            allLinesDataSet.valueFormatter = LargeValueFormatter()
            setColor(allLinesDataSet, Color.BLUE)
            val lines= mutableListOf<LineDataSet>()
            lines.add(allLinesDataSet)
            orderChart?.legend?.isEnabled = false
            orderChart?.data = LineData(lines.toList())
            orderChart?.axisRight?.setDrawLabels(false)
            orderChart?.legend?.position = Legend.LegendPosition.ABOVE_CHART_CENTER
            orderChart?.notifyDataSetChanged()
            orderChart?.invalidate()
        }catch (e:Exception){

        }
    }

    private fun setColor(dataSet:LineDataSet, color:Int){
        dataSet.fillColor = color
        dataSet.setDrawFilled(true)
        dataSet.setCircleColor(color)
        dataSet.color = color
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
                IncomeChartFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PERIOD, period)
                    }
                }
    }

}