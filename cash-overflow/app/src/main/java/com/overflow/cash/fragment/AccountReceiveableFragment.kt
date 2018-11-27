package com.overflow.cash.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.view.*
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.overflow.cash.AccountReceiveableDetailActivity
import com.overflow.cash.Constant
import com.overflow.cash.MenuActivity
import com.overflow.cash.R
import com.overflow.cash.adapter.AccountReceiveableAdapter
import com.overflow.cash.mvp.receiveable.AccountReceiveableContract
import com.overflow.cash.mvp.receiveable.AccountReceiveablePresenter
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.AbstractRecyclerPagination
import com.overflow.cash.utils.moveTo
import com.overflow.cash.utils.replaceContent
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_account_receiveable.*
import kotlinx.android.synthetic.main.fragment_blank.*
import kotlinx.android.synthetic.main.fragment_blank.view.*
import javax.inject.Inject

class AccountReceiveableFragment : Fragment(), AccountReceiveableContract.View {


    @Inject
    lateinit var presenter: AccountReceiveablePresenter
    @Inject
    lateinit var translations: Translations
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    lateinit var adapter: AccountReceiveableAdapter

    private var currentPage: Int = 1
    lateinit var menuActivity: MenuActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        this.menuActivity = activity as MenuActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_account_receiveable, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.presenter.attach(this)
        this.adapter = AccountReceiveableAdapter(translations)

        val manager = LinearLayoutManager(activity)
        this.refresh?.visibility = View.GONE
        receycler?.layoutManager = manager
        receycler?.isNestedScrollingEnabled = false
        receycler?.setHasFixedSize(true)
        receycler?.itemAnimator = DefaultItemAnimator()
        receycler?.adapter = adapter
        receycler?.addOnScrollListener(object : AbstractRecyclerPagination(manager) {
            override val isLoading: Boolean
                get() = presenter.loading
            override val isLastPage: Boolean
                get() = presenter.lastPage
            override val totalItemCount: Int
                get() = presenter.getSize()

            override fun loadMoreItems() {
                currentPage += 1
                presenter.loadReceiveable(currentPage, Constant.TEXT_EMPTY)
            }
        })
        refresh?.setOnRefreshListener {
            currentPage = 1
            presenter.loadReceiveable(currentPage, Constant.TEXT_EMPTY)
        }

        this.adapter.onItemClick = { data, _ ->
            activity?.moveTo(AccountReceiveableDetailActivity::class.java, data.toBundle())
        }

        menuActivity.search?.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentPage = 1
                presenter.loadReceiveable(currentPage, query!!)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        this.showAccountReceiveableInAge()
        this.btnChooser.setOnClickListener {
            val popup = PopupMenu(context!!, it)
            popup.inflate(R.menu.menu_choose_account_receiveable_graph)
            popup.setOnMenuItemClickListener {
                this.tvGraphTitle.text = it.title
                when(it.itemId){
                    R.id.action_in_age ->{

                        this.showAccountReceiveableInAge()
                        false
                    }
                    else ->{
                        this.showAccountReceiveableOutOfAge()
                        false
                    }
                }
            }
            popup.show()
        }
    }

    override fun onReceiveableLoaded(receiveables: List<Data>) {
        blankLayout?.visibility = View.GONE
        refresh?.visibility = View.VISIBLE
        refresh?.isRefreshing = false
        if (currentPage == 1) {
            this.adapter.clearValues()
        }
        this.adapter.addValues(receiveables)
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
        showMessage(getString(R.string.accounts_receiveable), getString(R.string.no_account_receiveable_description))
    }

    private fun showMessage(title: String, message: String) {
        blankLayout?.visibility = View.VISIBLE
        blankLayout?.tvDescription?.text = message
        blankLayout?.tvTitle?.text = title
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_customer, menu)
        menuActivity.search?.setMenuItem(menu!!.findItem(R.id.action_search))
    }

    override fun showNotConnected(res: String) {
        showMessage(translations.get(Constant.TranslationsKey.NO_INTERNET), Constant.TEXT_EMPTY)
    }

    private fun showAccountReceiveableInAge(){
        activity?.replaceContent(R.id.accountReceiveableGraph, AccountReceiveableChartInAgeFragment())
    }
    private fun showAccountReceiveableOutOfAge(){
        activity?.replaceContent(R.id.accountReceiveableGraph, AccountReceiveableChartOutOfAgeFragment())
    }


}
