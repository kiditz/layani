package com.overflow.cash.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.widget.RxTextView
import com.overflow.cash.R
import com.overflow.cash.adapter.CustomerChooserAdapter
import com.overflow.cash.mvp.customer.LoadCustomerContract
import com.overflow.cash.mvp.customer.LoadCustomerPresenter
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.AbstractRecyclerPagination
import com.overflow.cash.utils.moveTo
import com.overflow.libs.core.Data
import kotlinx.android.synthetic.main.activity_customer_chooser.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CustomerChooserActivity : BaseActivity(), LoadCustomerContract.View {
    @Inject
    lateinit var presenter:LoadCustomerPresenter
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    lateinit var adapter:CustomerChooserAdapter

    private var currentPage:Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_chooser)

        if(intent.hasExtra("name")){
            edCustomer.setText(intent.getStringExtra("name"))
        }

        this.presenter.attach(this)
        this.adapter = CustomerChooserAdapter()
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

        this.btnSaveCustomer?.setOnClickListener {

            moveTo(CustomerListAddActivity::class.java)
        }
        // On customer choosed
        this.adapter.onItemClick = {data, _ ->
            val intent = Intent()
            intent.putExtras(data.toBundle())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        RxTextView.textChanges(edCustomer).skipInitialValue().debounce(500, TimeUnit.MILLISECONDS).subscribe {
            currentPage = 1
            presenter.loadCustomer(currentPage, it.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.loadCustomer(currentPage, edCustomer.text.toString())
    }

    override fun onCustomerLoaded(customerList: List<Data>) {
        hideMessage()
        if(currentPage == 1){
            this.adapter.clearValues()
        }
        this.adapter.addValues(customerList)
        if(adapter.itemCount > 0){
            this.btnSaveCustomer.setImageResource(R.drawable.ic_edit)
        }else{
            this.btnSaveCustomer.setImageResource(R.drawable.ic_plus)
        }
    }

    override fun showError(error: Throwable) {
        networkExHandler.errorHandle(this, error)
    }

    override fun showNoOk(res: String) {
        showMessage(res, "")
    }

    override fun showEmpty() {
        showMessage(getString(R.string.no_customer_title), "")
    }

    override fun showNotConnected(res: String) {
        showMessage(res)
    }

    override fun showMessage(title: String, message: String) {
        super.showMessage(title, message)
        refresh?.isRefreshing = false
    }


    override fun hideMessage() {
        super.hideMessage()
        refresh?.isRefreshing = false
    }

}
