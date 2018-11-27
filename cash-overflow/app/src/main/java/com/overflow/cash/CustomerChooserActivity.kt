package com.overflow.cash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.jakewharton.rxbinding2.widget.RxTextView
import com.overflow.cash.adapter.CustomerListAdapter
import com.overflow.cash.mvp.customer.CustomerChooserContract
import com.overflow.cash.mvp.customer.CustomerChooserPresenter
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.AbstractRecyclerPagination
import com.overflow.libs.core.Data
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_customer_chooser.*
import kotlinx.android.synthetic.main.fragment_blank.*
import kotlinx.android.synthetic.main.fragment_blank.view.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CustomerChooserActivity : AppCompatActivity(), CustomerChooserContract.View {
    @Inject
    lateinit var presenter:CustomerChooserPresenter
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    lateinit var adapter:CustomerListAdapter

    private var currentPage:Int = 1
    private var customerId:Long = -1L
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_chooser)

        if(intent.hasExtra("name")){
            edCustomer.setText(intent.getStringExtra("name"))
        }

        this.presenter.attach(this)
        this.adapter = CustomerListAdapter()
        val manager  = LinearLayoutManager(this)
        receycler?.layoutManager =  manager
        receycler?.isNestedScrollingEnabled = false
        receycler?.setHasFixedSize(true)
        receycler?.itemAnimator = DefaultItemAnimator()
        receycler?.adapter = adapter
        receycler?.addOnScrollListener(object : AbstractRecyclerPagination(manager){
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
            val data = Data()
            data["id"] = customerId
            data["name"] = this.edCustomer?.text.toString()
            this.presenter.editCustomer(data)
        }

        this.adapter.onItemClick = {data, _->
            onCustomerEdited(data)
        }
        RxTextView.textChanges(edCustomer).debounce(300, TimeUnit.MILLISECONDS).subscribe {
            currentPage = 1
            presenter.loadCustomer(currentPage, it.toString())
        }
    }

    override fun onCustomerLoaded(customerList: List<Data>) {
        blankLayout?.visibility = View.GONE
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
        showMessage(getString(R.string.no_customer_title), "")
    }
    private fun showMessage(title:String, message:String){
        blankLayout?.visibility = View.VISIBLE
        blankLayout?.tvDescription?.text = message
        blankLayout?.tvTitle?.text = title
    }

    override fun showNotConnected(res: String) {
    }

    override fun onCustomerEdited(customer: Data) {
        val intent = Intent()
        intent.putExtras(customer.toBundle())
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
