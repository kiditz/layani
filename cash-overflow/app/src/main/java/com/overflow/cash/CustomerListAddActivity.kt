package com.overflow.cash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.overflow.cash.adapter.AddCustomerListAdapter
import com.overflow.cash.mvp.customer.CustomerChooserContract
import com.overflow.cash.mvp.customer.CustomerChooserPresenter
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.AbstractRecyclerPagination
import com.overflow.libs.core.Data
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_customer_list.*
import javax.inject.Inject

class CustomerListAddActivity : BaseActivity(), CustomerChooserContract.View {
    @Inject
    lateinit var presenter:CustomerChooserPresenter
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    lateinit var adapter:AddCustomerListAdapter

    private var currentPage:Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_list)
        this.adapter = AddCustomerListAdapter()
        customerLoader()
        this.presenter.attach(this)
    }

    private fun customerLoader(){
        val manager  = LinearLayoutManager(this)
        recycler?.layoutManager =  manager
        recycler?.isNestedScrollingEnabled = false
        recycler?.setHasFixedSize(true)
        recycler?.itemAnimator = DefaultItemAnimator()
        recycler?.adapter = adapter
        recycler?.addOnScrollListener(object : AbstractRecyclerPagination(manager){
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
            val customer = Data()
            if(data.containsKey("id")){
                customer["id"] = data.getLong("id")
            }else{
                customer["id"] = null
            }

            customer["name"] = holder.name.text.toString()
            customer["email"] = holder.email.text.toString()
            customer["phone_number"] = holder.phoneNumber.text.toString()
            this.presenter.editCustomer(customer)
        }
    }

    override fun onCustomerLoaded(customerList: List<Data>) {
        hideMessage()
        if(currentPage == 1){
            this.adapter.clearValues()
        }
        this.adapter.addValues(customerList)
    }

    override fun showError(error: Throwable) {
        networkExHandler.errorHandle(this, error)
    }

    override fun showNoOk(res: String) {
        showMessage(res, "")
    }

    override fun showEmpty() {
        //showMessage(getString(R.string.no_customer_title), "")
        adapter.clearValues()
        addCustomerAdapter()
    }

    private fun addCustomerAdapter(){

        val customer = Data()
        customer["name"] = Constant.TEXT_EMPTY
        customer["email"] = Constant.TEXT_EMPTY
        customer["phone_number"] = Constant.TEXT_EMPTY
        adapter.addValue(customer)
    }



    override fun showNotConnected(res: String) {
    }

    override fun onCustomerEdited(customer: Data) {
        val intent = Intent()
        intent.putExtras(customer.toBundle())
        setResult(Activity.RESULT_OK, intent)
        finish()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item!!.itemId){
            R.id.action_add -> {
                addCustomerAdapter()
                false
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
