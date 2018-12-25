package com.overflow.cash.fragment

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.overflow.cash.R
import com.overflow.cash.activity.CashboxHistoryDispatcherActivity
import com.overflow.cash.adapter.CashboxSummaryAdapter
import com.overflow.cash.mvp.cashbox.LoadCashboxSummaryContract
import com.overflow.cash.mvp.cashbox.LoadCashboxSummaryPresenter
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
import javax.inject.Inject


/**
 * @author Rifky Aditya Bastara
 * @since 22 Desember 2018
 *
 * this class called from the [com.overflow.cash.activity.MenuActivity] Activity
 * to inform the user about their cashbox summary
 */
class CashboxSummaryFragment: BaseFragment(), LoadCashboxSummaryContract.View {
    @Inject
    lateinit var presenter: LoadCashboxSummaryPresenter
    @Inject
    lateinit var translations: Translations
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    lateinit var adapter: CashboxSummaryAdapter
    private lateinit var format: SimpleDateFormat
    private var currentPage: Int = API.MIN_PAGE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.format = SimpleDateFormat("dd MMMM yyyy", this.context!!.currentLocale())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cashbox_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.adapter = CashboxSummaryAdapter(translations, format)
        val manager = LinearLayoutManager(activity)
        recycler?.layoutManager = manager
        recycler?.isNestedScrollingEnabled = false
        recycler?.setHasFixedSize(true)
        recycler?.itemAnimator = DefaultItemAnimator()
        recycler?.adapter = adapter
        this.presenter.attach(this)

        recycler?.addOnScrollListener(object : AbstractRecyclerPagination(manager) {
            override val isLoading: Boolean
                get() = presenter.loading
            override val isLastPage: Boolean
                get() = presenter.lastPage
            override val totalItemCount: Int
                get() = presenter.getSize()

            override fun loadMoreItems() {
                currentPage += 1
                presenter.loadCashBoxSummary(currentPage)
            }
        })
        refresh?.setOnRefreshListener {
            currentPage = 1
            presenter.loadCashBoxSummary(currentPage)
        }

        adapter.onItemClick = {data, _ ->
            val bundle = data.toBundle()
            bundle.putLong(CashboxHistoryFragment.ARG_CASH_BOX_SUMMARY_ID, data.getLong("id"))
            activity!!.moveTo(CashboxHistoryDispatcherActivity::class.java, bundle)
        }
    }
    override fun onCashboxLoaded(item: List<Data>) {
        recycler?.visibility = View.VISIBLE
        blank_layout?.visibility = View.GONE
        refresh?.isRefreshing = false
        if (currentPage == 1) {
            this.adapter.clearValues()
        }

        Group.generate(item, adapter.values, "end_at", format)
        adapter.notifyDataSetChanged()
    }

    override fun showError(error: Throwable) {
        networkExHandler.errorHandle(activity!!, error)
    }

    override fun showNoOk(res: String) {
        showMessageInBlankLayout(res)
    }

    override fun showEmpty() {
        showMessageInBlankLayout(getString(R.string.no_cashbox_found))
    }

    override fun showNotConnected(res: String) {
        showMessageInBlankLayout(res)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
    }

    override fun onDetach() {
        super.onDetach()
        this.presenter.detach()
    }

}