package com.overflow.cash.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.overflow.cash.activity.MenuActivity
import com.overflow.cash.R
import com.overflow.cash.mvp.chart.TopProductChartContract
import com.overflow.cash.mvp.chart.TopProductChartPresenter
import com.overflow.libs.core.Data
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.fragment_pie_chart.*
import javax.inject.Inject


class TopProductFragment: Fragment(), TopProductChartContract.View{

    @Inject
    lateinit var presenter:TopProductChartPresenter
    lateinit var menuActivity: MenuActivity
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
        this.menuActivity = activity as MenuActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pieChart?.setUsePercentValues(false)
        pieChart?.description?.isEnabled = false
        pieChart?.setExtraOffsets(5f, 10f, 5f,5f )
        pieChart?.centerText = generateSpanableString()

        pieChart?.dragDecelerationFrictionCoef = 0.95f
        pieChart?.isDrawHoleEnabled = true
        pieChart?.holeRadius = 58f
        pieChart?.transparentCircleRadius = 61f
        pieChart?.rotationAngle = 0f
        pieChart?.isRotationEnabled = true
        pieChart?.isHighlightPerTapEnabled = true
        //dataSet.valueTextColor =
        pieChart?.setEntryLabelColor(ContextCompat.getColor(activity!!, R.color.textDefault))
        pieChart?.setEntryLabelTextSize(12f)
        val legend = pieChart?.legend
        legend?.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend?.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        legend?.orientation = Legend.LegendOrientation.VERTICAL
        legend?.setDrawInside(false)
        legend?.xEntrySpace = 7f
        legend?.yEntrySpace = 0f
        legend?.yOffset = 0f
        this.presenter.attach(this)
    }

    private fun generateSpanableString(): SpannableString {
        val text = "Produk\nLaku Keras"
        val spannable = SpannableString(text)
        spannable.setSpan(RelativeSizeSpan(1.7f), 0, 6, 0)
        spannable.setSpan(StyleSpan(Typeface.NORMAL), 6, text.length - 7, 0)
        spannable.setSpan(ForegroundColorSpan(Color.GRAY), 6, text.length - 6, 0)
        spannable.setSpan(StyleSpan(Typeface.ITALIC), text.length - 6, text.length, 0)
        spannable.setSpan(ForegroundColorSpan(ColorTemplate.getHoloBlue()), text.length - 6, text.length, 0)
        return spannable
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pie_chart, container, false)
    }
    override fun onChartLoaded(products:List<Data>) {

        setDataChart(products)
    }

    private fun setDataChart(products:List<Data>){
        try {
            val entries = mutableListOf<PieEntry>()
            products.forEach {
                val entry = PieEntry(it.getDouble("quantity").toFloat(), it.getString("name").toUpperCase())
                entries.add(entry)
            }
            val dataSet = PieDataSet(entries, "")
            dataSet.setDrawIcons(false)
            dataSet.selectionShift = 5f
            val colors = mutableListOf<Int>()
            for (c in ColorTemplate.VORDIPLOM_COLORS)
                colors.add(c)
            for (c in ColorTemplate.JOYFUL_COLORS)
                colors.add(c)
            for (c in ColorTemplate.COLORFUL_COLORS)
                colors.add(c)
            for (c in ColorTemplate.LIBERTY_COLORS)
                colors.add(c)
            for (c in ColorTemplate.PASTEL_COLORS)
                colors.add(c)
            colors.add(ColorTemplate.getHoloBlue())
            dataSet.colors = colors
            dataSet.setValueFormatter { value, _, _, _ ->
                return@setValueFormatter "${value.toInt()}"
            }
            val data = PieData(dataSet)
            data.setValueTextColor(ContextCompat.getColor(activity!!, R.color.textDefault))
            data.setValueTextSize(11f)
            pieChart?.data = data
            pieChart?.highlightValue(null)
            pieChart.notifyDataSetChanged()
            pieChart?.invalidate()
            pieChart?.animateY(1400, Easing.EaseInOutQuad)
        }catch (e:Exception){

        }
    }

    override fun showError(error: Throwable) {
    }

    override fun showNoOk(res: String) {
    }

    override fun showEmpty() {

        val menu = menuActivity.nav_view.menu
        menu.findItem(R.id.nav_report).isVisible = false
        menuActivity.onNavigationItemSelected(menu.findItem(R.id.nav_product))
    }

    override fun showNotConnected(res: String) {
    }

}