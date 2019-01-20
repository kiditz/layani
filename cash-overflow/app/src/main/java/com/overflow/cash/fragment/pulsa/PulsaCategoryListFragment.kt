package com.overflow.cash.fragment.pulsa

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.overflow.cash.R
import com.overflow.cash.fragment.BaseFragment
import com.overflow.cash.mvp.pulsa.LoadPulsaCategoryContract
import com.overflow.cash.mvp.pulsa.LoadPulsaCategoryPresenter
import com.overflow.cash.pager.ViewPagerAdapter
import com.overflow.libs.core.Data
import kotlinx.android.synthetic.main.fragment_pulsa_category.*
import timber.log.Timber
import javax.inject.Inject

class PulsaCategoryListFragment : BaseFragment(), LoadPulsaCategoryContract.View {
    @Inject
    lateinit var loadPulsaCategoryPresenter: LoadPulsaCategoryPresenter
    lateinit var adapter: ViewPagerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pulsa_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.adapter = ViewPagerAdapter(activity!!.supportFragmentManager)
        this.view_pager?.adapter = adapter
        this.view_pager.isSaveFromParentEnabled = false
        tab_layout.tabMode = TabLayout.MODE_SCROLLABLE
        tab_layout.setupWithViewPager(view_pager)
        this.loadPulsaCategoryPresenter.attach(this)

    }

    override fun onCategoryLoaded(categoryList: List<Data>) {
        hideMessage()
        adapter.clear()
        showFragmentByName(categoryList)
        adapter.notifyDataSetChanged()
    }

    private fun showFragmentByName(categoryList: List<Data>) {
        categoryList.forEach {
            Timber.i("Name : %s", it["name"])
            val name = it["name"].toString()
            try {
                val fragment = getMap(it)[name.toUpperCase()]!!
                adapter.addFragment(fragment, name)
            } catch (e: Exception) {
                Timber.e(name)
            }
        }
    }

    override fun showError(error: Throwable) {
    }

    override fun showNoOk(res: String) {
        showMessageInBlankLayout(res)
    }

    override fun showEmpty() {
    }

    override fun showNotConnected(res: String) {
        showMessageInBlankLayout(res)
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.clear()
        loadPulsaCategoryPresenter.detach()
    }

    val map = mutableMapOf<String, Fragment>()

    private fun getMap(it: Data): Map<String, Fragment> {
        map["PAKET INTERNET"] = PulsaPaketProductListFragment.newInstance(it.toString())
        map["PAKET SMS"] = PulsaPaketProductListFragment.newInstance(it.toString())
        map["PAKET TELEPON"] = PulsaPaketProductListFragment.newInstance(it.toString())
        map["PULSA TRANSFER"] = PulsaPaketProductListFragment.newInstance(it.toString())
        map["PLN PRABAYAR"] = PulsaProductListByProviderFragment.newInstance(it.toBundle(), getString(R.string.no_meter))
        map["TAGIHAN"] = PulsaProviderPaymentFragment.newInstance(it.toBundle())
        map["EMONEY-ETOLL"] = PulsaProductListByProviderFragment.newInstance(it.toBundle(), getString(R.string.customer_no))
        map["PULSA ISI ULANG"] = PulsaProductListFragment.newInstance(it.toString())
        map["VOUCHER GAME"] = PulsaProductListByProviderFragment.newInstance(it.toBundle(), getString(R.string.customer_no))
        map["SALDO GOJEK"] = PulsaProductListByProviderFragment.newInstance(it.toBundle(), getString(R.string.phone_number_gojek))
        map["SALDO GRAB"] = PulsaProductListByProviderFragment.newInstance(it.toBundle(), getString(R.string.phone_number_grab))
        return map
    }
}