package com.overflow.cash.fragment

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.PopupMenu
import com.jakewharton.rxbinding2.widget.RxTextView
import com.overflow.cash.R
import com.overflow.cash.activity.Constant
import com.overflow.cash.activity.TransactionHistoryDetailActivity
import com.overflow.cash.adapter.OrderAdapter
import com.overflow.cash.mvp.order.LoadOrderContract
import com.overflow.cash.mvp.order.LoadOrderPresenter
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

class TransactionHistoryFragment : BaseFragment(), LoadOrderContract.View{
    @Inject
    lateinit var presenter: LoadOrderPresenter
    @Inject
    lateinit var translations: Translations
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    lateinit var adapter: OrderAdapter
    private var currentPage: Int = API.MIN_PAGE
    var excludeStatus: Boolean = true

    var status: String = Constant.TEXT_EMPTY
    lateinit var format: SimpleDateFormat

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        arguments?.let {
            this.excludeStatus = it.getBoolean(ARG_EXCLUDE_STATUS)
            this.status = it.getString(ARG_STATUS, Constant.TEXT_EMPTY)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.format = SimpleDateFormat("dd MMMM yyyy", this.context!!.currentLocale())

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transaction_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.presenter.attach(this)
        this.adapter = OrderAdapter(translations, format)


        val manager = LinearLayoutManager(activity)
        recycler?.layoutManager = manager
        recycler?.isNestedScrollingEnabled = false
        recycler?.setHasFixedSize(true)
        recycler?.itemAnimator = DefaultItemAnimator()
        recycler?.adapter = adapter

        presenter.loadOrder(currentPage, Constant.TEXT_EMPTY, status, excludeStatus)

        recycler?.addOnScrollListener(object : AbstractRecyclerPagination(manager) {
            override val isLoading: Boolean
                get() = presenter.loading
            override val isLastPage: Boolean
                get() = presenter.lastPage
            override val totalItemCount: Int
                get() = presenter.getSize()

            override fun loadMoreItems() {
                currentPage += 1
                presenter.loadOrder(currentPage, Constant.TEXT_EMPTY, status, excludeStatus)
            }
        })
        refresh?.setOnRefreshListener {
            currentPage = 1
            presenter.loadOrder(currentPage, Constant.TEXT_EMPTY, status, excludeStatus)
        }


        RxTextView.textChanges(ed_search).skipInitialValue().subscribe {
            activity!!.runOnUiThread {
                currentPage = 1
                presenter.loadOrder(currentPage, it.toString(), status, excludeStatus)
            }
        }

        this.adapter.onItemClick = { order, _ ->
            val bundle = Bundle()
            bundle.putString(Constant.ARG_SALES, order.toString())
            activity!!.moveTo(TransactionHistoryDetailActivity::class.java, bundle)
        }

        if(!excludeStatus){
            btn_filter.visibility = View.GONE
        }

        handleFilter()
    }

    private fun handleFilter(){
        btn_filter.setOnClickListener {
            val menu = PopupMenu(context, btn_filter)
            menu.inflate(R.menu.menu_filter_transaction_history)
            menu.setOnMenuItemClickListener {
                return@setOnMenuItemClickListener when (it.itemId) {
                    R.id.action_success -> {
                        val status = Constant.TransactionStatus.SUCCESS
                        presenter.loadOrder(currentPage, Constant.TEXT_EMPTY, status, excludeStatus)
                        true
                    }

                    R.id.action_void -> {
                        val status = Constant.TransactionStatus.VOID
                        presenter.loadOrder(currentPage, Constant.TEXT_EMPTY, status)
                        true
                    }

                    R.id.action_in_progress -> {
                        val status = Constant.TransactionStatus.PENDING
                        presenter.loadOrder(currentPage, Constant.TEXT_EMPTY, status)
                        true
                    }
                    else -> false
                }
            }
            menu.show()
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
            if (!hashKey(adapter.values, it)) {
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

    private fun hashKey(payloads: List<Group>, key: String): Boolean {
        for (payload in payloads) {
            if (payload["order_at"] == key)
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



    override fun onDestroy() {
        super.onDestroy()
        this.presenter.detach()
    }


    companion object {
        const val ARG_EXCLUDE_STATUS = "exclude_status"
        const val ARG_STATUS = "status"
        @JvmStatic
        fun newInstance(excludeStatus: Boolean, status:String=Constant.TEXT_EMPTY) =
                TransactionHistoryFragment().apply {
                    arguments = Bundle().apply {
                        putBoolean(ARG_EXCLUDE_STATUS, excludeStatus)
                        putString(ARG_STATUS, status)
                    }
                }

        @JvmStatic
        fun newInstance() = TransactionHistoryFragment()
    }

}
