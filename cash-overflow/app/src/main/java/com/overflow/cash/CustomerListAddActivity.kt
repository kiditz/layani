package com.overflow.cash

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.jakewharton.rxbinding2.widget.RxTextView
import com.overflow.cash.adapter.AddCustomerListAdapter
import com.overflow.cash.mvp.customer.CustomerChooserContract
import com.overflow.cash.mvp.customer.CustomerChooserPresenter
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.AbstractRecyclerPagination
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_customer_list.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CustomerListAddActivity : BaseActivity(), CustomerChooserContract.View {
    @Inject
    lateinit var presenter: CustomerChooserPresenter
    @Inject
    lateinit var translations: Translations
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    lateinit var adapter: AddCustomerListAdapter

    private var currentPage: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_list)
        this.adapter = AddCustomerListAdapter(translations)
        customerLoader()
        this.presenter.attach(this)
    }

    private fun customerLoader() {
        val manager = LinearLayoutManager(this)
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

        presenter.loadCustomer(currentPage, Constant.TEXT_EMPTY)

        //Handle add new customer
        this.adapter.onDoneClick = { data, holder ->
            editCustomer(data, holder, true)
        }

        this.adapter.onDeleteClick = { data, holder ->
            this.editCustomer(data, holder, false)
        }
    }

    private fun editCustomer(data: Data, holder: AddCustomerListAdapter.ViewHolder, active: Boolean = true) {
        val customer = Data()
        if (data.containsKey("id")) {
            customer["id"] = data.getLong("id")
        } else {
            customer["id"] = null
        }
        customer["name"] = holder.name.text.toString()
        customer["email"] = holder.email.text.toString()
        customer["phone_number"] = holder.phoneNumber.text.toString()
        if (!active) {
            customer["active"] = false
        }
        this.presenter.editCustomer(customer)
    }

    override fun onCustomerLoaded(customerList: List<Data>) {
        hideMessage()
        if (currentPage == 1) {
            this.adapter.clearValues()
        }
        this.adapter.addValues(customerList)
    }

    override fun showError(error: Throwable) {
        showMessage(getString(R.string.system_err))
    }

    override fun showNoOk(res: String) {
        showMessage(res)
    }

    override fun showEmpty() {
        adapter.clearValues()
        addCustomerAdapter()
    }

    private fun addCustomerAdapter() {
        val customer = Data()
        customer["name"] = Constant.TEXT_EMPTY
        customer["email"] = Constant.TEXT_EMPTY
        customer["phone_number"] = Constant.TEXT_EMPTY
        adapter.addValue(customer)
    }


    override fun showNotConnected(res: String) {
        showMessage(res)
    }


    override fun onCustomerEdited(customer: Data, holder: RecyclerView.ViewHolder?) {
        presenter.loadCustomer(currentPage, Constant.TEXT_EMPTY)
        showMessageHeader(this.translations.get(Constant.TranslationsKey.CUSTOMER_EDITED_SUCCESSFULLY).replace("{0}", customer.getString("name")))
        refresh?.isRefreshing = false
        if (holder != null && holder is AddCustomerListAdapter.ViewHolder){
            holder.progressBar.visibility = View.GONE
        }
    }

    /**
     * Set isRefreshing to false when message show up
     * */
    override fun showMessage(title: String, message: String) {
        super.showMessage(title, message)
        refresh?.isRefreshing = false

    }

    /**
     * Set isRefreshing to false when message hide
     * */
    override fun hideMessage() {
        super.hideMessage()
        refresh?.isRefreshing = false
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.action_add -> {
                addCustomerAdapter()
                false
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showMessageHeader(text: String) {
        tv_success_message.text = text
        tv_success_message.visibility = View.VISIBLE
        RxTextView.textChanges(tv_success_message).debounce(5, TimeUnit.SECONDS).subscribe {
            runOnUiThread {
                tv_success_message.visibility = View.GONE
            }
        }
    }

    override fun onCustomerEditShowNoOk(res: String) {
        showMessageHeader(res)
    }
}
