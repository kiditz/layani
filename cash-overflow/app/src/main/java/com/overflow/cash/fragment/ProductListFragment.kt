package com.overflow.cash.fragment


import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import com.jakewharton.rxbinding2.widget.RxTextView
import com.overflow.cash.CategoryListActivity
import com.overflow.cash.Constant
import com.overflow.cash.R
import com.overflow.cash.SaveProductActivity
import com.overflow.cash.mvp.product.CategoryListContract
import com.overflow.cash.mvp.product.CategoryListPresenter
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.pager.ViewPagerAdapter
import com.overflow.cash.utils.moveTo
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_customer.*
import kotlinx.android.synthetic.main.fragment_product_list.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ProductListFragment : BaseFragment(), CategoryListContract.View, ViewPager.OnPageChangeListener{

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



    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        this.adapter = ViewPagerAdapter(activity!!.supportFragmentManager)
        this.presenter.attach(this)
    }

    override fun onResume() {
        super.onResume()
        this.presenter.loadCategory(-1)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_edit_category?.setOnClickListener {
            activity?.moveTo(CategoryListActivity::class.java)
        }
        tab_layout.tabMode = TabLayout.MODE_SCROLLABLE
        this.view_pager?.adapter = adapter
        view_pager?.addOnPageChangeListener(this)
        tab_layout.setupWithViewPager(view_pager)

    }


    override fun onCategoryLoaded(categoryList: List<Data>) {
        adapter.clear()
        try {
            hideMessage()
            var productFragment = ProductFragment.newInstance(-1)
            adapter.addFragment(productFragment, getString(R.string.all_product))
            this.categoryList.add(Data())
            //Use Property because for bugfix
            categoryList.map {
                productFragment = ProductFragment.newInstance(it.getLong("category_id"))
                adapter.addFragment(productFragment, it.getString("category_name"))
            }
            this.categoryList.addAll(categoryList)
            adapter.notifyDataSetChanged()
            handleAddProduct(view_pager.currentItem)
            handleChangeEvent(view_pager.currentItem)
            handlePopUpMenu(view_pager.currentItem)
        }catch (e:UninitializedPropertyAccessException){
            Timber.e(e)
        }
    }

    private fun handleAddProduct(position: Int) {
        val category = this.categoryList[position]
        fab_add_product.setOnClickListener { activity!!.moveTo(SaveProductActivity::class.java, category.toBundle()) }
    }

    private fun handleChangeEvent(position: Int) {
        (adapter.getItem(position) as ProductFragment).searchProduct(Constant.TEXT_EMPTY)
        RxTextView.textChangeEvents(ed_search).skipInitialValue().debounce(300, TimeUnit.MILLISECONDS).distinctUntilChanged().subscribe({
            (adapter.getItem(position) as ProductFragment).searchProduct(it.text().toString())
        }, {
            Timber.e(it)
        })
    }


    private fun handlePopUpMenu(position: Int) {
        val productFragment = (adapter.getItem(position) as ProductFragment)
        btn_sort.setOnClickListener {
            val menu = PopupMenu(context, btn_sort)
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
        showMessage(res)
    }

    override fun showEmpty() {
        showMessage(getString(R.string.no_product_title), getString(R.string.no_product_description))
    }

    override fun showNotConnected(res: String) {
        showMessage(res)
    }

    override fun showMessage(title: String, message: String) {
        recycler?.visibility = View.GONE
        //adapter.addFragment(BlankFragment.newInstance(getString(R.string.no_product_title), getString(R.string.no_product_description)), "")
        adapter.notifyDataSetChanged()
        fab_add_product.setOnClickListener { activity!!.moveTo(SaveProductActivity::class.java) }
        super.showMessage(title, message)
    }

    override fun onPageScrollStateChanged(p0: Int) {
    }

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
    }

    override fun onPageSelected(position: Int) {
        try {
            handleAddProduct(position)
            handleChangeEvent(position)
            handlePopUpMenu(position)
        }catch (e:UninitializedPropertyAccessException){
            //Ignore property
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

}
