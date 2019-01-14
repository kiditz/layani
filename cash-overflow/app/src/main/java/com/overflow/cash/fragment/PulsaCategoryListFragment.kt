package com.overflow.cash.fragment

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.overflow.cash.R
import com.overflow.cash.mvp.pulsa.LoadPulsaCategoryContract
import com.overflow.cash.mvp.pulsa.LoadPulsaCategoryPresenter
import com.overflow.cash.pager.ViewPagerAdapter
import com.overflow.libs.core.Data
import kotlinx.android.synthetic.main.fragment_pulsa_category.*

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
            if (it.getString("name").equals("Pulsa Isi Ulang", ignoreCase = true)) {
                adapter.addFragment(PulsaProductListFragment.newInstance(it.toString()), it.getString("name"))
            } else if (it.getString("name").equals("Paket Internet", ignoreCase = true)
                    || it.getString("name").equals("Paket Sms", ignoreCase = true)
                    || it.getString("name").equals("Paket Telepon", ignoreCase = true)
                    || it.getString("name").equals("Pulsa Transfer", ignoreCase = true)) {
                adapter.addFragment(PulsaPaketProductListFragment.newInstance(it.toString()), it.getString("name"))
            }else if (it.getString("name").equals("Voucher Game", ignoreCase = true)) {
                adapter.addFragment(PulsaProductsByProviderListFragment.newInstance(it.toString(), getString(R.string.customer_no), false), it.getString("name"))
            }else if (it.getString("name").equals("Saldo Gojek", ignoreCase = true)) {
                adapter.addFragment(PulsaProductsByProviderListFragment.newInstance(it.toString(), getString(R.string.phone_number_gojek)), it.getString("name"))
            }else if (it.getString("name").equals("Saldo Grab", ignoreCase = true)) {
                adapter.addFragment(PulsaProductsByProviderListFragment.newInstance(it.toString(), getString(R.string.phone_number_grab)), it.getString("name"))
            } else if (it.getString("name").equals("Token Listrik", ignoreCase = true)) {
                adapter.addFragment(PulsaProductsByProviderListFragment.newInstance(it.toString(), getString(R.string.no_meter)), it.getString("name"))
            }else if (it.getString("name").equals("eMoney-eToll", ignoreCase = true)) {
                adapter.addFragment(PulsaProductsByProviderListFragment.newInstance(it.toString(), getString(R.string.customer_no)), it.getString("name"))
            }
            else {
                adapter.addFragment(BlankFragment.newInstance(it.getString("name")), it.getString("name"))
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


}