package com.overflow.cash.fragment


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.*
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.overflow.cash.R
import com.overflow.cash.activity.Constant
import com.overflow.cash.activity.MenuActivity
import com.overflow.cash.activity.PreviewSalesActivity
import com.overflow.cash.activity.ScannerActivity
import com.overflow.cash.model.OrderItem
import com.overflow.cash.mvp.product.LoadCategoryContract
import com.overflow.cash.mvp.product.LoadCategoryPresenter
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.pager.ViewPagerAdapter
import com.overflow.cash.realm.OrderRealm
import com.overflow.cash.utils.moveTo
import com.overflow.cash.utils.snack
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import dagger.android.support.AndroidSupportInjection
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.cart_layout.view.*
import kotlinx.android.synthetic.main.fragment_sales_list.*
import javax.inject.Inject


class SalesListFragment : BaseFragment(), LoadCategoryContract.View, ViewPager.OnPageChangeListener {

    private lateinit var outlet: Data
    private lateinit var adapter: ViewPagerAdapter
    @Inject
    lateinit var translations: Translations
    @Inject
    lateinit var presenter: LoadCategoryPresenter
    @Inject
    lateinit var preferences: SharedPreferences
    @Inject
    lateinit var orderRealm:OrderRealm
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    private var categoryList = mutableListOf<Data>()
    private lateinit var menuActivity: MenuActivity
    private lateinit var realm: Realm

    private var allOrders:RealmResults<OrderItem>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sales_list, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        setHasOptionsMenu(true)
        this.outlet = Data(preferences.getString("outlet", "{}"))
        this.adapter = ViewPagerAdapter(activity!!.supportFragmentManager)
        realm = Realm.getDefaultInstance()

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.menuActivity = activity as MenuActivity
        tab_layout.tabMode = TabLayout.MODE_SCROLLABLE
        this.view_pager?.adapter = adapter
        view_pager?.addOnPageChangeListener(this)
        tab_layout.setupWithViewPager(view_pager)
        this.presenter.attach(this)
        this.presenter.loadCategory(-1)
    }

    private fun showSumQuantity(menuItem: MenuItem) {
        this.allOrders = realm.where(OrderItem::class.java).findAll()
        sumQty(menuItem, allOrders!!)
        allOrders!!.addChangeListener { t, _ ->
            sumQty(menuItem, t)
        }
    }



    private fun sumQty(menuItem: MenuItem, results:RealmResults<OrderItem>){
        val sumQty = results.sum("qty")

        if(sumQty.toLong() > 0){
            menuItem.actionView.cart_badge?.text = sumQty.toString()
            menuItem.actionView.cart_badge?.visibility = View.VISIBLE
        }else{
            menuItem.actionView.cart_badge?.visibility = View.GONE
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_sales, menu)
        val menuItem = menu!!.findItem(R.id.action_cart)
        menuItem.actionView.btn_preview_sales.setOnClickListener {
            //Pindah ke activity preview sales
            menuActivity.moveTo(PreviewSalesActivity::class.java)
        }
        showSumQuantity(menuItem!!)
        menuActivity.search?.setMenuItem(menu.findItem(R.id.action_search))
        btn_scan.setOnClickListener {
            handleScanAction()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item!!.itemId){
            R.id.action_delete_transaction -> {
                orderRealm.removeAllItems()
                menuActivity.goTo(R.id.nav_transaction)
                true
            }
            R.id.action_cart -> {
                activity!!.moveTo(PreviewSalesActivity::class.java)
                true
            }
            else -> false
        }
    }

    override fun onCategoryLoaded(categoryList: List<Data>) {
        adapter.clear()

        //Sometimes not cause sales fragment not attached into the context working after reload token
        try {
            var salesFragment = SalesFragment.newInstance(-1)
            hideMessage()
            adapter.addFragment(salesFragment, getString(R.string.all_product))
            this.categoryList.clear()
            this.categoryList.add(Data())
            categoryList.map {
                salesFragment = SalesFragment.newInstance(it.getLong("category_id"))
                adapter.addFragment(salesFragment, it.getString("category_name"))
            }
            this.categoryList.addAll(categoryList)
            adapter.notifyDataSetChanged()
            handleSearchEvent(view_pager.currentItem)
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
        hideMessage()
        adapter.clear()
        val salesFragment = SalesFragment.newInstance(-1)
        adapter.addFragment(salesFragment, getString(R.string.all_product))
        adapter.notifyDataSetChanged()
        this.categoryList.add(Data())
        handleSearchEvent(view_pager.currentItem)
    }

    override fun showNotConnected(res: String) {
        showMessage(res)
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

    private fun handleScanAction() {
        val intent = Intent(activity, ScannerActivity::class.java)
        startActivityForResult(intent, Constant.REQUEST_CODE_SCANNER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_CODE_SCANNER && resultCode == Activity.RESULT_OK) {
            view_pager.setCurrentItem(0, true)
            val salesFragment = (adapter.getItem(view_pager.currentItem) as SalesFragment)
            salesFragment.searchProduct(data!!.getStringExtra("barcode"))
        }
    }

}
