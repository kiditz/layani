package com.overflow.cash.pager

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.overflow.cash.fragment.ProductFragment
import java.util.*

/**
 * Standard Fragment for displaying ViewPager
 * @author kiditz on 28/02/18.
 */

class ViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    private val fragments = ArrayList<Fragment>()
    private val titles = ArrayList<String>()

    fun addFragment(fragment: Fragment, title: String) {
        this.fragments.add(fragment)
        this.titles.add(title)
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }

    fun replace(position: Int, fragment: Fragment) {
        this.fragments[position] = fragment
        this.notifyDataSetChanged()
    }

    fun getFragments(): List<Fragment> {
        return fragments
    }

    fun getTitles(): List<String> {
        return titles
    }

    fun getTitle(position: Int): String {
        return getTitles()[position]
    }

    fun clear() {
        this.fragments.clear()
        this.titles.clear()
    }
}
