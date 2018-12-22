package com.overflow.cash.fragment

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.overflow.cash.R
import com.overflow.cash.utils.replaceContent
import kotlinx.android.synthetic.main.fragment_sales_main.*

class SalesMainFragment:BaseFragment(), BottomNavigationView.OnNavigationItemSelectedListener{

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sales_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigation.setOnNavigationItemSelectedListener(this)
        onNavigationItemSelected(navigation.menu.findItem(R.id.navigation_transaction))
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.navigation_transaction ->{
                activity?.replaceContent(R.id.bn_container, SalesListFragment())
                true
            }
            R.id.navigation_topup ->{
                true
            }
            R.id.navigation_purchase ->{
                activity?.replaceContent(R.id.bn_container, CashbankOutFragment())
                true
            }
            else -> false
        }
    }


}