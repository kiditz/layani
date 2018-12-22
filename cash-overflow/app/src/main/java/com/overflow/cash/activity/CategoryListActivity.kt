package com.overflow.cash.activity

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.jakewharton.rxbinding2.widget.RxTextView
import com.overflow.cash.R
import com.overflow.cash.adapter.CategoryListAdapter
import com.overflow.cash.mvp.product.*
import com.overflow.cash.net.API
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.AbstractRecyclerPagination
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.success_message.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CategoryListActivity : BaseActivity(), LoadCategoryContract.View, EditCategoryContract.View, AddCategoryContract.View, DeleteCategoryContract.View {
    @Inject
    lateinit var loadCategoryPresenter: LoadCategoryPresenter
    @Inject
    lateinit var editCategoryPresenter: EditCategoryPresenter
    @Inject
    lateinit var addCategoryPresenter: AddCategoryPresenter
    @Inject
    lateinit var deleteCategoryPresenter: DeleteCategoryPresenter
    @Inject
    lateinit var translations: Translations
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    lateinit var adapter: CategoryListAdapter
    var currentHolder:CategoryListAdapter.ViewHolder?=null
    private var currentPage: Int = API.MIN_PAGE
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
                get() = loadCategoryPresenter.loading
            override val isLastPage: Boolean
                get() = loadCategoryPresenter.lastPage
            override val totalItemCount: Int
                get() = loadCategoryPresenter.getSize()

            override fun loadMoreItems() {
                currentPage += 1
            }
        })
        this.loadCategoryPresenter.attach(this)
        this.editCategoryPresenter.attach(this)
        this.addCategoryPresenter.attach(this)
        this.deleteCategoryPresenter.attach(this)

        loadCategoryPresenter.loadCategory(currentPage, Constant.TEXT_EMPTY)
        refresh?.setOnRefreshListener {
            currentPage = 1
            loadCategoryPresenter.loadCategory(currentPage, Constant.TEXT_EMPTY)
        }

        this.adapter.onRemoveClicked = { data, holder ->
            this.showProgress(holder)
            this.currentHolder = holder
            if (data.containsKey("category_id") && data.getLong("category_id") > 0){
                this.deleteCategoryPresenter.deleteCategory(data.getLong("category_id"))
            }else{
                adapter.removeItems(holder.adapterPosition)
            }
        }

        this.adapter.onSaveClicked = { data, holder ->
            this.showProgress(holder)
            this.currentHolder = holder
            data["name"] = holder.name.text.toString()
            if (data.containsKey("category_id") && data.getLong("category_id") > 0){
                data["id"] = data["category_id"]
                this.editCategoryPresenter.editCategory(data)
            }else{
                this.addCategoryPresenter.addCategory(data)
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
        //showMessageInBlankLayout(getString(R.string.no_category_data), "")
        hideMessage()
    }


    override fun showNotConnected(res: String) {
        refresh?.isRefreshing = false
        showMessage(getString(R.string.no_internet))
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

    override fun onCategoryDeleted(data: Data) {
        if(currentHolder != null){
            dismissProgressMessage(translations.get(Constant.TranslationsKey.CATEGORY_REMOVED_SUCCESSFULLY), currentHolder!!)
            loadCategoryPresenter.loadCategory(currentPage, Constant.TEXT_EMPTY)
        }
    }

    override fun onCategorySaved(data: Data) {
        if(currentHolder != null){
            currentPage = 1
            loadCategoryPresenter.loadCategory(currentPage, Constant.TEXT_EMPTY)
            dismissProgressMessage(translations.get(Constant.TranslationsKey.CATEGORY_CREATED_SUCCESSFULLY).replace("{0}", data.getString("name")), currentHolder!!)
        }
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
        loadCategoryPresenter.detach()
        editCategoryPresenter.detach()
        addCategoryPresenter.detach()
        deleteCategoryPresenter.detach()
    }
}
