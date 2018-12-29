package com.overflow.cash.fragment

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.overflow.cash.R
import com.overflow.cash.activity.SaveCashHistoryActivity
import com.overflow.cash.adapter.CashboxHistoryAdapter
import com.overflow.cash.mvp.cashbox.LoadCashboxHistoryContract
import com.overflow.cash.mvp.cashbox.LoadCashboxHistoryPresenter
import com.overflow.cash.net.API
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.AbstractRecyclerPagination
import com.overflow.cash.utils.currentLocale
import com.overflow.cash.utils.home
import com.overflow.cash.utils.moveTo
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import kotlinx.android.synthetic.main.fragment_blank.*
import kotlinx.android.synthetic.main.fragment_transaction_history.*
import java.text.SimpleDateFormat
import javax.inject.Inject


/**
 * @author Rifky Aditya Bastara
 * @since 22 Desember 2018
 *
 * this class called from the [com.overflow.cash.activity.CashboxHistoryDispatcherActivity] Activity
 * to inform the user about their cash in and cash out
 */
class CashboxHistoryFragment: BaseFragment(), LoadCashboxHistoryContract.View {
    @Inject
    lateinit var presenter: LoadCashboxHistoryPresenter
    @Inject
    lateinit var translations: Translations
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    lateinit var adapter: CashboxHistoryAdapter
    private lateinit var format: SimpleDateFormat
    private var currentPage: Int = API.MIN_PAGE
    var cashboxSummaryId:Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.format = SimpleDateFormat("dd MMMM yyyy", this.context!!.currentLocale())
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        this.arguments?.let {
            this.cashboxSummaryId = it.getLong(ARG_CASH_BOX_SUMMARY_ID)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cashbox_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.adapter = CashboxHistoryAdapter(translations)
        val manager = LinearLayoutManager(activity)
        recycler?.layoutManager = manager
        recycler?.isNestedScrollingEnabled = false
        recycler?.setHasFixedSize(true)
        recycler?.itemAnimator = DefaultItemAnimator()
        recycler?.adapter = adapter
        this.presenter.attach(this)
        currentPage = API.MIN_PAGE
        presenter.loadCashBoxSummary(currentPage, cashboxSummaryId)
        recycler?.addOnScrollListener(object : AbstractRecyclerPagination(manager) {
            override val isLoading: Boolean
                get() = presenter.loading
            override val isLastPage: Boolean
                get() = presenter.lastPage
            override val totalItemCount: Int
                get() = presenter.getSize()

            override fun loadMoreItems() {
                currentPage += API.MIN_PAGE
                presenter.loadCashBoxSummary(currentPage, cashboxSummaryId)
            }
        })
        refresh?.setOnRefreshListener {
            currentPage = API.MIN_PAGE
            presenter.loadCashBoxSummary(currentPage, cashboxSummaryId)
        }
    }
    override fun onCashboxLoaded(item: List<Data>) {
        recycler?.visibility = View.VISIBLE
        blank_layout?.visibility = View.GONE
        refresh?.isRefreshing = false
        if (currentPage == 1) {
            this.adapter.clearValues()
        }
        adapter.addValues(item)
        adapter.notifyDataSetChanged()
    }

    override fun showError(error: Throwable) {
        networkExHandler.errorHandle(activity!!, error)
    }

    override fun showNoOk(res: String) {
        showMessageInBlankLayout(res)
    }

    override fun showEmpty() {
        showMessageInBlankLayout(getString(R.string.no_cashbox_history_found))
    }

    override fun showNotConnected(res: String) {
        showMessageInBlankLayout(res)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        inflater!!.inflate(R.menu.menu_edit, menu)
    }



    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item!!.itemId){
            R.id.action_edit -> {
                activity!!.moveTo(SaveCashHistoryActivity::class.java)
                false
            }
            else -> activity!!.home(item)
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.presenter.detach()
    }
    companion object {
        const val ARG_CASH_BOX_SUMMARY_ID = "cash_box_summary_id"
        @JvmStatic
        fun newInstance(cashboxSummaryId: Long) =
                CashboxHistoryFragment().apply {
                    arguments = Bundle().apply {
                        putLong(ARG_CASH_BOX_SUMMARY_ID, cashboxSummaryId)
                    }
                }
    }

}