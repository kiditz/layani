package com.overflow.cash

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.jaeger.library.StatusBarUtil
import com.overflow.cash.adapter.AccountReceiveableDetailAdapter
import com.overflow.cash.mvp.receiveable.AccountReceiveableDetailContract
import com.overflow.cash.mvp.receiveable.AccountReceiveableDetailPresenter
import com.overflow.cash.net.API
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.AbstractRecyclerPagination
import com.overflow.cash.utils.home
import com.overflow.cash.utils.moveTo
import com.overflow.cash.utils.rupiah
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_account_receiveable_detail.*
import kotlinx.android.synthetic.main.fragment_blank.*
import kotlinx.android.synthetic.main.fragment_blank.view.*
import javax.inject.Inject

class AccountReceiveableDetailActivity : AppCompatActivity(), AccountReceiveableDetailContract.View {
    @Inject
    lateinit var presenter:AccountReceiveableDetailPresenter
    @Inject
    lateinit var translations:Translations
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    lateinit var adapter:AccountReceiveableDetailAdapter
    private var order:Data? = null
    var currentPage= API.MIN_PAGE
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_receiveable_detail)
        StatusBarUtil.setTranslucent(this, 0)
        setSupportActionBar(toolbar)
        this.tvAccountReceiveable.text = rupiah(intent.getDoubleExtra("total_credit", -1.0))
        this.tvOrderCount.text = intent.getLongExtra("total_order", -1L).toString()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = "Piutang ${intent.getStringExtra("name")}"
        this.presenter.attach(this)
        this.adapter = AccountReceiveableDetailAdapter(translations)
        val manager = LinearLayoutManager(this)
        currentPage = 1
        presenter.loadDetail(currentPage, intent.getLongExtra("customer_id", -1L))
        recycler?.layoutManager = manager
        recycler?.itemAnimator = DefaultItemAnimator()
        recycler?.isNestedScrollingEnabled = false
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
                presenter.loadDetail(currentPage, intent.getLongExtra("customer_id", -1L))
            }
        })
        refresh?.setOnRefreshListener {
            currentPage = 1
            presenter.loadDetail(currentPage, intent.getLongExtra("customer_id", -1L))
        }

        this.adapter.onItemClick = {data, _ ->
            this.order = data
            this.presenter.loadOrderItems(data.getLong("order_id"))
        }
    }

    override fun onOrderItemsLoaded(items: List<Data>) {
        order?.let {
            it["customer_name"] = intent.getStringExtra("name")
            it["account_receiveable"] = Data().put("total_credit", it.getDouble("total_credit"))
            it["order_items"] = items
            val bundle = intent.extras
            bundle.putString("sales", it.toString())
            moveTo(ReceiptAccountReceiveableActivity::class.java, bundle)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return home(item!!)
    }
    override fun onDetailLoaded(receiveables: List<Data>) {
        blank_layout?.visibility = View.GONE
        refresh?.visibility = View.VISIBLE
        refresh?.isRefreshing = false
        if (currentPage == 1) {
            this.adapter.clearValues()
        }
        this.adapter.addValues(receiveables)
    }

    override fun showError(error: Throwable) {
        networkExHandler.errorHandle(this, error)
    }

    override fun showNoOk(res: String) {
        refresh?.isRefreshing = false
        showMessage(res, "")
    }

    override fun showEmpty() {
        refresh?.isRefreshing = false
        showMessage(getString(R.string.no_customer_title), "")
    }

    override fun showNotConnected(res: String) {
        refresh?.isRefreshing = false
        showMessage(res, "")
    }

    private fun showMessage(title: String, message: String) {
        blank_layout?.visibility = View.VISIBLE
        blank_layout?.tv_description?.text = message
        blank_layout?.tv_title?.text = title
    }

}