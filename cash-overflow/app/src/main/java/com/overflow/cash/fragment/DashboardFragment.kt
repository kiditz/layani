package com.overflow.cash.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.PopupMenu
import com.overflow.cash.R
import com.overflow.cash.utils.replaceContent
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment:BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        choosePeriodForOrder()
        choosePeriodForProfit()
        choosePeriodForIncome()
        setOrderChart()
        setProfitChart()
        setIncomeChart()
        showHeader()
        showTopProductChart()
    }

    private fun showHeader(){
        activity?.replaceContent(R.id.headerContainer, DashboardHeaderFragment())
    }
    private fun setOrderChart(){
        this.btnChoosePeriod.setOnClickListener {
            val menu = PopupMenu(context, it)
            menu.inflate(R.menu.menu_choose_period)
            menu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.action_week ->{
                        choosePeriodForOrder("week")
                        false
                    }
                    R.id.action_month ->{
                        choosePeriodForOrder("month")
                        false
                    }
                    R.id.action_year ->{
                        choosePeriodForOrder("year")
                        false
                    }
                    else -> false
                }
            }
            menu.show()
        }
    }

    private fun setProfitChart(){
        this.btnChoosePeriodForProfit.setOnClickListener {
            val menu = PopupMenu(context, it)
            menu.inflate(R.menu.menu_choose_period)
            menu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.action_week ->{
                        choosePeriodForProfit("week")
                        false
                    }
                    R.id.action_month ->{
                        choosePeriodForProfit("month")
                        false
                    }
                    R.id.action_year ->{
                        choosePeriodForProfit("year")
                        false
                    }
                    else -> false
                }
            }
            menu.show()
        }
    }

    private fun setIncomeChart(){
        this.btnChoosePeriodForIncome.setOnClickListener {
            val menu = PopupMenu(context, it)
            menu.inflate(R.menu.menu_choose_period)
            menu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.action_week ->{
                        choosePeriodForIncome("week")
                        false
                    }
                    R.id.action_month ->{
                        choosePeriodForIncome("month")
                        false
                    }
                    R.id.action_year ->{
                        choosePeriodForIncome("year")
                        false
                    }
                    else -> false
                }
            }
            menu.show()
        }
    }


    private fun choosePeriodForOrder(period:String="week"){
        activity?.replaceContent(R.id.orderChartContainer, OrderChartFragment.newInstance(period))
    }

    private fun choosePeriodForProfit(period:String="week"){
        activity?.replaceContent(R.id.profitChartContainer, ProfitChartFragment.newInstance(period))
    }

    private fun choosePeriodForIncome(period:String="week"){
        activity?.replaceContent(R.id.incomeChartContainer, IncomeChartFragment.newInstance(period))
    }

    private fun showTopProductChart(){
        activity?.replaceContent(R.id.topProductContainer, TopProductFragment())
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
    }
}