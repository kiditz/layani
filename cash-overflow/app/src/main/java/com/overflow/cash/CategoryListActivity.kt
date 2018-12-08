package com.overflow.cash

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.jakewharton.rxbinding2.widget.RxTextView
import com.overflow.cash.adapter.CategoryListAdapter
import com.overflow.cash.mvp.product.CategoryListContract
import com.overflow.cash.mvp.product.CategoryListPresenter
import com.overflow.cash.mvp.product.EditAndRemoveCategoryContract
import com.overflow.cash.mvp.product.EditAndRemoveCategoryPresenter
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.AbstractRecyclerPagination
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import kotlinx.android.synthetic.main.activity_category.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CategoryListActivity : BaseActivity(), CategoryListContract.View, EditAndRemoveCategoryContract.View {
    @Inject
    lateinit var presenter: CategoryListPresenter
    @Inject
    lateinit var editPresenter: EditAndRemoveCategoryPresenter
    @Inject
    lateinit var translations: Translations
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    lateinit var adapter: CategoryListAdapter

    private var currentPage: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        this.adapter = CategoryListAdapter()

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
            }
        })
        this.presenter.attach(this)
        this.editPresenter.attach(this)
        presenter.loadCategory(currentPage, Constant.TEXT_EMPTY)
        refresh?.setOnRefreshListener {
            currentPage = 1
            presenter.loadCategory(currentPage, Constant.TEXT_EMPTY)
        }

        this.adapter.onRemoveClicked = { data, holder ->
            this.showProgress(holder)
            if (data.containsKey("category_id") && data.getLong("category_id") > 0){
                this.editPresenter.deleteCategory(data.getLong("category_id"), holder)
            }else{
                adapter.removeItems(holder.adapterPosition)
            }
        }

        this.adapter.onSaveClicked = { data, holder ->
            this.showProgress(holder)
            data["name"] = holder.name.text.toString()
            if (data.containsKey("category_id") && data.getLong("category_id") > 0){
                data["id"] = data["category_id"]
                this.editPresenter.editCategory(data, holder)
            }else{
                this.editPresenter.addCategory(data, holder)
            }
        }

    }


    override fun showError(error: Throwable) {
        networkExHandler.errorHandle(this, error)
        refresh?.isRefreshing = false
    }

    override fun showNoOk(res: String) {
        showMessage(res, "")
        refresh?.isRefreshing = false
    }

    override fun showEmpty() {
        refresh?.isRefreshing = false
        //showMessage(getString(R.string.no_category_data), "")
        hideMessage()
    }


    override fun showNotConnected(res: String) {
        refresh?.isRefreshing = false
        showMessage(translations.get(Constant.TranslationsKey.NO_INTERNET), Constant.TEXT_EMPTY)
    }

    override fun onCategoryLoaded(categoryList: List<Data>) {
        recycler?.visibility = View.VISIBLE
        refresh?.isRefreshing = false
        if (currentPage == 1) {
            this.adapter.clearValues()
        }
        hideMessage()
        adapter.addValues(categoryList)
    }

    override fun showMessage(title: String, message: String) {
        super.showMessage(title, message)
        recycler?.visibility = View.GONE
    }

    /**
     * When Category Edited successfuly progress bar must be dismissed and show the message header
     * */
    override fun onCategoryEdited(data: Data, holder:CategoryListAdapter.ViewHolder) {
        presenter.loadCategory(currentPage, Constant.TEXT_EMPTY)
        dismissProgressMessage(translations.get(Constant.TranslationsKey.CATEGORY_EDITED_SUCCESSFULLY).replace("{0}", data.getString("name")), holder)
    }

    override fun onCategoryAdded(data: Data, holder: CategoryListAdapter.ViewHolder) {
        presenter.loadCategory(currentPage, Constant.TEXT_EMPTY)
        dismissProgressMessage(translations.get(Constant.TranslationsKey.CATEGORY_CREATED_SUCCESSFULLY).replace("{0}", data.getString("name")), holder)
    }


    /**
     * When Remove success progress bar must be dismissed and remove adapter position too!!
     * */
    override fun onCategoryRemoved(data: Data, holder:CategoryListAdapter.ViewHolder) {
        dismissProgressMessage(translations.get(Constant.TranslationsKey.CATEGORY_REMOVED_SUCCESSFULLY), holder)
        presenter.loadCategory(currentPage, Constant.TEXT_EMPTY)
    }

    /**
     * When Remove error progress bar must be dismissed!!
     * */
    override fun onEditRemoveCategoryError(error: Throwable, holder: CategoryListAdapter.ViewHolder) {
        dismissProgressMessage(getString(R.string.system_err), holder)
    }

    /**
     * When Not Ok error progress bar must be dismissed!!
     * */
    override fun onEditRemoveCategoryNotOk(res: String, holder: CategoryListAdapter.ViewHolder) {
        dismissProgressMessage(res, holder)
    }

    /**
     * Dismiss progressbar and show message
     * */
    private fun dismissProgressMessage(text:String,holder:CategoryListAdapter.ViewHolder){
        refresh?.isRefreshing = false
        holder.progressBar.visibility = View.GONE
        this.showMessageHeader(text)
    }

    private fun showMessageHeader(text:String){
        tv_success_message.text = text
        tv_success_message.visibility = View.VISIBLE
        RxTextView.textChanges(tv_success_message).debounce(5, TimeUnit.SECONDS).subscribe {
            runOnUiThread {
                tv_success_message.visibility = View.GONE
            }
        }
    }

    private fun showProgress(holder:CategoryListAdapter.ViewHolder){
        holder.progressBar.visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        return when(item!!.itemId){
            R.id.action_add ->{
                val data = Data().put("name", Constant.TEXT_EMPTY)
                adapter.addValue(data!!)
                false
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
        editPresenter.detach()
    }
}
