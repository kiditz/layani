package com.overflow.cash.fragment


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.*
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.overflow.cash.Constant
import com.overflow.cash.MenuActivity
import com.overflow.cash.PreviewSalesActivity
import com.overflow.cash.R
import com.overflow.cash.model.OrderItem
import com.overflow.cash.mvp.product.CategoryListContract
import com.overflow.cash.mvp.product.CategoryListPresenter
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.pager.ViewPagerAdapter
import com.overflow.cash.utils.moveTo
import com.overflow.cash.utils.snack
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import dagger.android.support.AndroidSupportInjection
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_sales_list.*
import javax.inject.Inject

class SalesListFragment : Fragment(), CategoryListContract.View, ViewPager.OnPageChangeListener {

    private lateinit var merchant: Data
    private lateinit var adapter: ViewPagerAdapter
    @Inject
    lateinit var translations: Translations
    @Inject
    lateinit var presenter: CategoryListPresenter
    @Inject
    lateinit var preferences: SharedPreferences
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    private var categoryList = mutableListOf<Data>()
    private lateinit var menuActivity:MenuActivity
    private lateinit var realm: Realm

    private var allOrders:RealmResults<OrderItem>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sales_list, container, false)
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        setHasOptionsMenu(true)
        this.merchant = Data(preferences.getString("merchant", "{}"))
        this.adapter = ViewPagerAdapter(activity!!.supportFragmentManager)
        this.presenter.attach(this)
        realm = Realm.getDefaultInstance()
        loadCategory()
    }

    private fun loadCategory(){
        val category = Data()
        category["merchant_id"] = merchant.getLong("id")
        category["name"] = ""
        presenter.loadCategory(category)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.menuActivity = activity as MenuActivity
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        this.viewPager?.adapter = adapter
        viewPager?.addOnPageChangeListener(this)
        tabLayout.setupWithViewPager(viewPager)
        showSumQuantity()

    }

    private fun showSumQuantity() {
        this.allOrders = realm.where(OrderItem::class.java).findAll()
        sumQty(allOrders!!)
        allOrders!!.addChangeListener { t, _ ->
            sumQty(t)
        }
    }



    private fun sumQty(results:RealmResults<OrderItem>){
        val sumQty = results.sum("qty")
        if(sumQty.toLong() > 0){
            tvSumQty?.text = sumQty.toString()
            tvSumQty?.visibility = View.VISIBLE
            fabSales?.isEnabled = true
        }else{
            tvSumQty?.visibility = View.GONE
            fabSales?.isEnabled = false
        }

        fabSales?.setOnClickListener {
            activity!!.moveTo(PreviewSalesActivity::class.java)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.action_sales, menu)
        menuActivity.search?.setMenuItem(menu!!.findItem(R.id.action_search))
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.action_delete_transaction -> {
                presenter.deleteAllOrderItems()
                val salesFragment = (adapter.getItem(viewPager.currentItem) as SalesFragment)
                salesFragment.searchProduct(Constant.TEXT_EMPTY)
                return true
            }
        }
        return false
    }

    override fun onCategoryLoaded(categoryList: List<Data>) {
        adapter.clear()
        var salesFragment = SalesFragment.newInstance(-1)
        //Sometimes now working after reload token
        try {
            adapter.addFragment(salesFragment, getString(R.string.all_product))
            this.categoryList.clear()
            this.categoryList.add(Data())
            categoryList.map {
                salesFragment = SalesFragment.newInstance(it.getLong("category_id"))
                adapter.addFragment(salesFragment, it.getString("category_name"))
            }
            this.categoryList.addAll(categoryList)
            adapter.notifyDataSetChanged()
            handleSearchEvent(viewPager.currentItem)
        }catch (e:Exception){
            //Ignored
        }
    }


    override fun showError(error: Throwable) {
        activity?.let {
            networkExHandler.errorHandle(it, error)
        }

    }

    override fun showNoOk(res: String) {
        activity?.snack(res)?.show()
    }

    override fun showEmpty() {
        adapter.addFragment(BlankFragment.newInstance(getString(R.string.no_product_title), getString(R.string.no_product_description)), "")
        adapter.notifyDataSetChanged()
    }

    override fun showNotConnected(res: String) {
        activity?.snack(res)?.show()
    }


    override fun onPageScrollStateChanged(p0: Int) {
    }

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
    }

    override fun onPageSelected(position: Int) {
        handleSearchEvent(position)
    }
    private fun handleSearchEvent(position: Int) {
        val salesFragment = (adapter.getItem(position) as SalesFragment)
        menuActivity.search?.setOnQueryTextListener(object:MaterialSearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                val search = query ?: Constant.TEXT_EMPTY
                salesFragment.searchProduct(search)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }
}
