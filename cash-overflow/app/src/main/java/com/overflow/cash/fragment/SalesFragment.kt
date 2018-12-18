package com.overflow.cash.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.overflow.cash.R
import com.overflow.cash.activity.Constant
import com.overflow.cash.activity.MenuActivity
import com.overflow.cash.adapter.SalesListAdapter
import com.overflow.cash.model.OrderItem
import com.overflow.cash.mvp.product.LoadProductContract
import com.overflow.cash.mvp.product.LoadProductPresenter
import com.overflow.cash.net.API
import com.overflow.cash.net.ImageService
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.realm.OrderItemRealm
import com.overflow.cash.utils.AbstractRecyclerPagination
import com.overflow.cash.utils.decoration.MarginItemDecoration
import com.overflow.cash.utils.snack
import com.overflow.libs.core.Data
import kotlinx.android.synthetic.main.dialog_manage_order_item_qty.view.*
import kotlinx.android.synthetic.main.fragment_blank.*
import kotlinx.android.synthetic.main.fragment_sales_recycler.*
import javax.inject.Inject

/**
 * @author Rifky Aditya Bastara
 * Load Product From Rest API Into View
 */
class SalesFragment : BaseFragment(), LoadProductContract.View{
    var currentPage: Int = API.MIN_PAGE
    var categoryId:Long = -1L
    private lateinit var adapter: SalesListAdapter
    @Inject
    lateinit var productListPresenter: LoadProductPresenter
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    @Inject
    lateinit var imageService: ImageService
    @Inject
    lateinit var orderItemRealm: OrderItemRealm
    private var addOrSubtractOrderItemView: View? = null
    private var addOrSubtractOrderItemDialog: AlertDialog? = null
    private var orderBy:String= Constant.Sort.BY_NAME
    private var search:String= Constant.TEXT_EMPTY
    lateinit var menuActivity: MenuActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryId = it.getLong(ARG_CATEGORY_ID)
        }
        this.menuActivity = this.activity as MenuActivity
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        productListPresenter.attach(this)
        currentPage = 1

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sales_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.adapter = SalesListAdapter(imageService)
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
                get() = productListPresenter.loading
            override val isLastPage: Boolean
                get() = productListPresenter.lastPage
            override val totalItemCount: Int
                get() = productListPresenter.getSize()

            override fun loadMoreItems() {
                currentPage += 1
                productListPresenter.loadProduct(currentPage, categoryId, search, orderBy)
            }
        })
        refresh?.setOnRefreshListener {
            loadFirst()
        }
        loadFirst()
        adapter.onItemClick = {data, viewHolder ->
            activity?.runOnUiThread {
                handleAddOrder(data, viewHolder)
            }
        }
        adapter.onItemLongClick = {data, holder ->
            handleEditOrder(data, holder)
        }
    }

    private fun handleEditOrder(data: Data, holder: SalesListAdapter.ViewHolder) {
        this.addOrSubtractOrderItemView = LayoutInflater.from(context).inflate(R.layout.dialog_manage_order_item_qty, null, false)
        var qty = holder.qty.text.replace("[^0-9]".toRegex(), "").trim().toLong()
        this.addOrSubtractOrderItemView?.ed_qty?.setText(qty.toString())
        this.addOrSubtractOrderItemView?.btn_add_qty?.setOnClickListener {

            this.addOrSubtractOrderItemView?.ed_qty?.setText("${qty++ }")
            if(this.addOrSubtractOrderItemView?.ed_qty?.text.toString().toLong() <= 0){
                this.addOrSubtractOrderItemView?.ed_qty?.setText("0")
            }
        }
        this.addOrSubtractOrderItemView?.btn_sub_qty?.setOnClickListener {
            this.addOrSubtractOrderItemView?.ed_qty?.setText("${qty-- }")
            if(this.addOrSubtractOrderItemView?.ed_qty?.text.toString().toLong() <= 0){
                this.addOrSubtractOrderItemView?.ed_qty?.setText("0")
            }
        }
        val builder = AlertDialog.Builder(context!!)
        builder.setView(this.addOrSubtractOrderItemView).setCancelable(false)
        builder.setPositiveButton(R.string.submit){ _, _ ->
            val qtyParse = addOrSubtractOrderItemView?.ed_qty?.text.toString().toLong()
            if(this.addOrSubtractOrderItemView?.ed_qty?.text.toString().toLong() <= 0){
                holder.qty.visibility = View.GONE
                holder.qty.text = "${Constant.ZERO} ${data.getString("unit")}"
                orderItemRealm.deleteItem(data.getLong("product_id"))
            }else{
                qty = qtyParse - 1
                addItem(null, qty, true, holder)
            }
        }

        builder.setNegativeButton(R.string.cancel){dialog, _ ->
            dialog.dismiss()
        }

        this.addOrSubtractOrderItemDialog = builder.create()

        if(holder.qty.visibility == View.VISIBLE){
            this.addOrSubtractOrderItemDialog?.show()
        }

    }

    private fun handleAddOrder(data: Data, viewHolder: SalesListAdapter.ViewHolder) {
        val qty = viewHolder.qty.text.replace("[^0-9]".toRegex(), "").trim().toLong()
        addItem(null, qty, true, viewHolder)

    }

    private fun loadFirst(){
        this.search = Constant.TEXT_EMPTY
        currentPage = 1
        productListPresenter.loadProduct(currentPage, categoryId, Constant.TEXT_EMPTY, orderBy)
    }

    fun order(orderBy:String){
        this.orderBy = orderBy
        currentPage = API.MIN_PAGE
        productListPresenter.loadProduct(currentPage, categoryId, search, orderBy)
    }

    fun searchProduct(search:String){
        this.search = search
        currentPage = API.MIN_PAGE
        productListPresenter.loadProduct(currentPage, categoryId, search, orderBy)
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
        showMessage(res, Constant.TEXT_EMPTY)
    }

    override fun showEmpty() {
        showMessage(getString(R.string.no_product_title), Constant.TEXT_EMPTY)
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
                SalesFragment().apply {
                    arguments = Bundle().apply {
                        putLong(ARG_CATEGORY_ID, categoryId)
                    }
                }
    }


    @SuppressLint("SetTextI18n")
    private fun onOrderIntemCreated(item: OrderItem?, holder: SalesListAdapter.ViewHolder) {
        holder.qty.text = item?.qty.toString() + " " + item?.unit
        holder.qty.visibility = View.VISIBLE
    }


    private fun addItem(discount: Data?, qty:Long, updateQty:Boolean=false, holder: SalesListAdapter.ViewHolder){
        val data = adapter.values[holder.adapterPosition]
        if(updateQty){
            if(data.getBoolean("use_stock")){
                if(data["stock"] != null){
                    if(data.getLong("stock") - (qty + 1)  < 0){
                        activity?.snack(getString(R.string.not_enough_stock))?.show()
                        return
                    }
                }
            }
        }
        val subTotal = data.getDouble("sell_price") * (qty + 1)
        data["sub_total"] = subTotal
        val item = orderItemRealm.addItem(data, updateQty)
        onOrderIntemCreated(item, holder)

    }
}
