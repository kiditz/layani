package com.overflow.cash.fragment

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.view.*
import com.overflow.cash.R
import com.overflow.cash.activity.Constant
import com.overflow.cash.adapter.ProductListAdapter
import com.overflow.cash.mvp.product.LoadProductContract
import com.overflow.cash.mvp.product.LoadProductPresenter
import com.overflow.cash.net.API
import com.overflow.cash.net.ImageService
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.AbstractRecyclerPagination
import com.overflow.cash.utils.decoration.MarginItemDecoration
import com.overflow.cash.utils.snack
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import kotlinx.android.synthetic.main.fragment_blank.*
import kotlinx.android.synthetic.main.fragment_product_recycler.*
import javax.inject.Inject

/**
 * @author Rifky Aditya Bastara
 * Load Product From Rest API Into View
 */
class ProductFragment : BaseFragment(), LoadProductContract.View {

    var currentPage: Int = API.MIN_PAGE
    var categoryId:Long = -1L
    lateinit var adapter: ProductListAdapter
    @Inject
    lateinit var presenter: LoadProductPresenter
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    @Inject
    lateinit var imageService: ImageService
    @Inject
    lateinit var translations: Translations
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryId = it.getLong(ARG_CATEGORY_ID)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        presenter.attach(this)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_product_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.adapter = ProductListAdapter(imageService)
        val manager  = GridLayoutManager(context, 2)
        recycler?.layoutManager =  manager
        recycler?.isNestedScrollingEnabled = false
        recycler?.setHasFixedSize(true)
        recycler?.itemAnimator = DefaultItemAnimator()
        val spaceInPixel = resources.getDimensionPixelSize(R.dimen.grid_margin)
        recycler?.addItemDecoration(MarginItemDecoration(spaceInPixel))
        recycler?.adapter = adapter
        recycler?.addOnScrollListener(object :AbstractRecyclerPagination(manager){
            override val isLoading: Boolean
                get() = presenter.loading
            override val isLastPage: Boolean
                get() = presenter.lastPage
            override val totalItemCount: Int
                get() = presenter.getSize()

            override fun loadMoreItems() {
                currentPage += 1
                presenter.loadProduct(currentPage, categoryId, Constant.TEXT_EMPTY)
            }
        })
        refresh?.setOnRefreshListener {
            currentPage = 1
            presenter.loadProduct(currentPage, categoryId, Constant.TEXT_EMPTY)
        }
    }

    fun order(orderBy:String){
        currentPage = API.MIN_PAGE
        this.presenter.loadProduct(currentPage, categoryId, Constant.TEXT_EMPTY, orderBy)
    }
    fun searchProduct(search:String){
        currentPage = API.MIN_PAGE
        this.presenter.loadProduct(currentPage, categoryId, search)
    }
    override fun onProductLoaded(productList: List<Data>) {
        dismiss()
        if(currentPage == 1){
            this.adapter.clearValues()
        }
        this.adapter.addValues(productList)
    }

    override fun showError(error: Throwable) {
        dismiss()
        activity?.let {
            networkExHandler.errorHandle(it, error)
        }
    }

    override fun showNoOk(res: String) {
        dismiss()
        activity?.snack(res)?.show()
    }

    override fun showEmpty() {
        recycler?.visibility = View.GONE
        refresh?.isRefreshing = false
        showMessage(getString(R.string.no_product_title), getString(R.string.no_product_description))
    }


    override fun showNotConnected(res: String) {
        dismiss()
        activity?.snack(res)?.show()
    }

    private fun dismiss(){
        refresh?.isRefreshing = false
        recycler?.visibility = View.VISIBLE
        blank_layout?.visibility = View.GONE
    }

    companion object {
        const val ARG_CATEGORY_ID = "category_id"
        @JvmStatic
        fun newInstance(categoryId: Long) =
                ProductFragment().apply {
                    arguments = Bundle().apply {
                        putLong(ARG_CATEGORY_ID, categoryId)
                    }
                }
    }

}
