package com.overflow.cash.fragment

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.jakewharton.rxbinding2.widget.RxTextView
import com.overflow.cash.R
import com.overflow.cash.activity.Constant
import com.overflow.cash.activity.MenuActivity
import com.overflow.cash.activity.ReceiptTransactionWithRefundActivity
import com.overflow.cash.adapter.TransactionHistoryAdapter
import com.overflow.cash.mvp.order.LoadOrderContract
import com.overflow.cash.mvp.order.LoadOrderPresenter
import com.overflow.cash.mvp.receiveable.AccountReceiveableDetailContract
import com.overflow.cash.mvp.receiveable.AccountReceiveableDetailPresenter
import com.overflow.cash.net.API
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.AbstractRecyclerPagination
import com.overflow.cash.utils.currentLocale
import com.overflow.cash.utils.moveTo
import com.overflow.libs.core.Data
import com.overflow.libs.core.Group
import com.overflow.libs.core.Translations
import kotlinx.android.synthetic.main.fragment_blank.*
import kotlinx.android.synthetic.main.fragment_transaction_history.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TransactionHistoryFragment : BaseFragment(), LoadOrderContract.View, AccountReceiveableDetailContract.View {
    @Inject
    lateinit var presenter: LoadOrderPresenter
    @Inject
    lateinit var itemsPresenter: AccountReceiveableDetailPresenter
    @Inject
    lateinit var translations: Translations
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    lateinit var adapter: TransactionHistoryAdapter
    private var currentPage: Int = API.MIN_PAGE
    lateinit var menuActivity: MenuActivity
    private var position = -1
    lateinit var format: SimpleDateFormat
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.menuActivity = activity as MenuActivity
        this.format = SimpleDateFormat("dd MMMM yyyy",this.context!!.currentLocale())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transaction_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.presenter.attach(this)
        this.itemsPresenter.attach(this)
        this.adapter = TransactionHistoryAdapter(translations, format)
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
                currentPage = currentPage + 1
                presenter.loadOrder(currentPage, Constant.TEXT_EMPTY)
            }
        })
        refresh?.setOnRefreshListener {
            currentPage = 1
            presenter.loadOrder(currentPage, Constant.TEXT_EMPTY)
        }


        RxTextView.textChanges(ed_search).skipInitialValue().subscribe {
            activity!!.runOnUiThread {
                currentPage = 1
                presenter.loadOrder(currentPage, it.toString())
            }
        }
        this.adapter.onItemClick = {data, viewHolder ->
            this.position = viewHolder.adapterPosition
            itemsPresenter.loadOrderItems(data.getString("order_code"))
        }
    }


    override fun onOrderLoaded(orderList: List<Data>) {
        recycler?.visibility = View.VISIBLE
        blank_layout?.visibility = View.GONE
        refresh?.isRefreshing = false
        if (currentPage == 1) {
            this.adapter.clearValues()
        }
        // Grouping the list by order_at
        val groupBy = orderList.groupBy { format.format(Date(it.getLong("order_at"))) }

        groupBy.keys.forEach {
            // Check if header has been exists on adapter values
            if(!hashKey(adapter.values, it)){
                val itemHeader = Group()
                itemHeader.type = Group.HEADER
                itemHeader["order_at"] = it
                adapter.values.add(itemHeader)
            }

            groupBy[it]?.forEach {
                val itemData = Group()
                itemData.putAll(it.map)
                itemData.type = Group.GENERAL
                adapter.values.add(itemData)
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun hashKey(payloads:List<Group>, key:String):Boolean{
        for (payload in payloads){
            if(payload["order_at"] == key)
                return true
        }
        return false
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
        showMessage(getString(R.string.transaction_not_found), "")
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
    }

    override fun showNotConnected(res: String) {
        showMessage(translations.get(Constant.TranslationsKey.NO_INTERNET), Constant.TEXT_EMPTY)
    }

    override fun onDetailLoaded(receiveables: List<Data>) {
        TODO("not implemented")
    }

    override fun onOrderItemsLoaded(items: List<Data>) {
        //Check adapter position
        if(position >= 0){
            val order = adapter.values[position]
            order["order_items"] = items
            val bundle = Bundle()
            bundle.putString(Constant.ARG_SALES, order.toString())
            activity!!.moveTo(ReceiptTransactionWithRefundActivity::class.java, bundle)

        }
    }
}
