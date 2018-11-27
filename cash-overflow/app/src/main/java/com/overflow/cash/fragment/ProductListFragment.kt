package com.overflow.cash.fragment


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.PopupMenu
import com.jakewharton.rxbinding2.widget.RxTextView
import com.overflow.cash.SaveProductActivity
import com.overflow.cash.Constant
import com.overflow.cash.R
import com.overflow.cash.mvp.product.CategoryListContract
import com.overflow.cash.mvp.product.CategoryListPresenter
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.pager.ViewPagerAdapter
import com.overflow.cash.utils.moveTo
import com.overflow.cash.utils.snack
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_product_list.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ProductListFragment : Fragment(), CategoryListContract.View, ViewPager.OnPageChangeListener{

    private lateinit var adapter: ViewPagerAdapter
    @Inject
    lateinit var translations: Translations
    @Inject
    lateinit var presenter: CategoryListPresenter
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    private var categoryList = mutableListOf<Data>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_product_list, container, false)
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        this.adapter = ViewPagerAdapter(activity!!.supportFragmentManager)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.presenter.attach(this)
        btnShowSearch?.setOnClickListener {

        }
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        this.viewPager?.adapter = adapter
        viewPager?.addOnPageChangeListener(this)
        tabLayout.setupWithViewPager(viewPager)


    }


    override fun onCategoryLoaded(categoryList: List<Data>) {
        try {
            var productFragment = ProductFragment.newInstance(-1)
            adapter.addFragment(productFragment, getString(R.string.all_product))
            this.categoryList.add(Data())
            categoryList.map {
                productFragment = ProductFragment.newInstance(it.getLong("category_id"))
                adapter.addFragment(productFragment, it.getString("category_name"))
            }
            this.categoryList.addAll(categoryList)
            adapter.notifyDataSetChanged()
            handleAddProduct(viewPager.currentItem)
            handleChangeEvent(viewPager.currentItem)
            handlePopUpMenu(viewPager.currentItem)
        }catch (e:Exception){
            Timber.e(e)
        }
    }

    private fun handleAddProduct(position: Int) {
        val category = this.categoryList[position]
        fabAddProduct.setOnClickListener { activity!!.moveTo(SaveProductActivity::class.java, category.toBundle()) }
    }

    private fun handleChangeEvent(position: Int) {
        RxTextView.textChangeEvents(edSearch).debounce(500, TimeUnit.MILLISECONDS).distinctUntilChanged().subscribe({
            (adapter.getItem(position) as ProductFragment).searchProduct(it.text().toString())
        }, {
            Timber.e(it)
        })
    }

    private fun handlePopUpMenu(position: Int) {
        val productFragment = (adapter.getItem(position) as ProductFragment)
        btnSort.setOnClickListener {
            val menu = PopupMenu(context, btnSort)
            menu.inflate(R.menu.sort_menu)
            menu.setOnMenuItemClickListener{
                when(it.itemId){
                    R.id.action_sort_by_name ->{
                        productFragment.order(Constant.Sort.BY_NAME)
                    }
                    R.id.action_sort_by_least_stock ->{
                        productFragment.order(Constant.Sort.BY_LEAST_STOCK)
                    }
                    R.id.action_sort_by_most_stock ->{
                        productFragment.order(Constant.Sort.BY_MOST_STOCK)
                    }
                    R.id.action_sort_by_sell_price ->{
                        productFragment.order(Constant.Sort.BY_SELL_PRICE)
                    }
                    R.id.action_sort_by_purchase_price ->{
                        productFragment.order(Constant.Sort.BY_PURCHASE_PRICE)
                    }
                }
                false
            }
            menu.show()
        }
    }

    override fun showError(error: Throwable) {
        activity?.let {
            networkExHandler.errorHandle(it, error)
        }

    }

    override fun showNoOk(res: String) {
        activity?.let {
            it.snack(res).show()
        }
    }

    override fun showEmpty() {
        adapter.addFragment(BlankFragment.newInstance(getString(R.string.no_product_title), getString(R.string.no_product_description)), "")
        adapter.notifyDataSetChanged()
        fabAddProduct.setOnClickListener { activity!!.moveTo(SaveProductActivity::class.java) }
    }

    override fun showNotConnected(res: String) {
        activity?.let {
            it.snack(res).show()
        }
    }


    override fun onPageScrollStateChanged(p0: Int) {
    }

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
    }

    override fun onPageSelected(position: Int) {
        handleAddProduct(position)
        handleChangeEvent(position)
        handlePopUpMenu(position)
    }

}
