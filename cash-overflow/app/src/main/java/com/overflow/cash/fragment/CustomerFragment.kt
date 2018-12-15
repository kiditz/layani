package com.overflow.cash.fragment

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.overflow.cash.activity.Constant
import com.overflow.cash.activity.CustomerListAddActivity
import com.overflow.cash.activity.MenuActivity
import com.overflow.cash.R
import com.overflow.cash.adapter.CustomerChooserAdapter
import com.overflow.cash.mvp.customer.LoadCustomerContract
import com.overflow.cash.mvp.customer.LoadCustomerPresenter
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.AbstractRecyclerPagination
import com.overflow.cash.utils.moveTo
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import kotlinx.android.synthetic.main.fragment_blank.*
import kotlinx.android.synthetic.main.fragment_customer.*
import javax.inject.Inject

class CustomerFragment : BaseFragment(), LoadCustomerContract.View {


    @Inject
    lateinit var presenter: LoadCustomerPresenter
    @Inject
    lateinit var translations: Translations
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    lateinit var adapter: CustomerChooserAdapter

    private var currentPage: Int = 1
    lateinit var menuActivity: MenuActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.menuActivity = activity as MenuActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_customer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.presenter.attach(this)
        this.adapter = CustomerChooserAdapter()
        val manager = LinearLayoutManager(activity)
        recycler?.layoutManager = manager
        recycler?.isNestedScrollingEnabled = false
        recycler?.setHasFixedSize(true)
        recycler?.itemAnimator = DefaultItemAnimator()
        recycler?.adapter = adapter
        recycler?.addOnScrollListener(object : AbstractRecyclerPagination(manager) {
            override val isLoading: Boolean
                get() = presenter.loading
            override val isLastPage: Boolean
                get() = presenter.lastPage
            override val totalItemCount: Int
                get() = presenter.getSize()

            override fun loadMoreItems() {
                currentPage += 1
                presenter.loadCustomer(currentPage, Constant.TEXT_EMPTY)
            }
        })
        refresh?.setOnRefreshListener {
            currentPage = 1
            presenter.loadCustomer(currentPage, Constant.TEXT_EMPTY)
        }

        this.adapter.onItemClick = { data, _ ->
            //Not Implemented
        }

        menuActivity.search?.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentPage = 1
                presenter.loadCustomer(currentPage, query!!)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    override fun onCustomerLoaded(customerList: List<Data>) {
        recycler?.visibility = View.VISIBLE
        blank_layout?.visibility = View.GONE
        refresh?.isRefreshing = false
        if (currentPage == 1) {
            this.adapter.clearValues()
        }
        this.adapter.addValues(customerList)
    }

    override fun showError(error: Throwable) {
        networkExHandler.errorHandle(activity!!, error)
        refresh?.isRefreshing = false
    }

    override fun showNoOk(res: String) {
        showMessage(res, "")
        refresh?.isRefreshing = false
    }

    override fun showEmpty() {
        refresh?.isRefreshing = false
        showMessage(getString(R.string.no_customer_title), "")
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        inflater?.inflate(R.menu.menu_customer, menu)
        menuActivity.search?.setMenuItem(menu!!.findItem(R.id.action_search))
        val itemEditCustomer = menu?.findItem(R.id.action_edit_customer)
        itemEditCustomer?.setOnMenuItemClickListener {
            onOptionsItemSelected(itemEditCustomer)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)
        return when(item!!.itemId){
            R.id.action_edit_customer -> {
                activity!!.moveTo(CustomerListAddActivity::class.java)
                true
            }
            else -> false
        }
    }

    override fun showNotConnected(res: String) {
        showMessage(translations.get(Constant.TranslationsKey.NO_INTERNET), Constant.TEXT_EMPTY)
    }

}
