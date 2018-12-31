package com.overflow.cash.fragment


import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.overflow.cash.R
import com.overflow.cash.adapter.OrderItemsAdapter
import com.overflow.cash.mvp.order.LoadOrderItemContract
import com.overflow.cash.mvp.order.LoadOrderItemPresenter
import com.overflow.cash.net.ImageService
import com.overflow.cash.net.NetworkExHandler
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import kotlinx.android.synthetic.main.fragment_product_recycler.*
import javax.inject.Inject

class OrderItemsFragment : BaseFragment(), LoadOrderItemContract.View{

    lateinit var adapter: OrderItemsAdapter
    @Inject
    lateinit var translations: Translations
    @Inject
    lateinit var presenter: LoadOrderItemPresenter
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    @Inject
    lateinit var imageService: ImageService
    var onItemsLoaded: ((List<Data>) -> Unit)? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_order_items, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        this.presenter.attach(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val manager = LinearLayoutManager(context!!)
        adapter = OrderItemsAdapter(imageService)
        recycler?.layoutManager =  manager
        recycler?.isNestedScrollingEnabled = false
        recycler?.setHasFixedSize(true)
        recycler?.itemAnimator = DefaultItemAnimator()
        recycler?.adapter = adapter
        arguments?.let {
            this.presenter.loadItem(it.getString(ARG_ORDER_CODE))
        }
    }

    override fun onOrderItemsLoaded(orderList: List<Data>) {
        this.onItemsLoaded?.invoke(orderList)
        hideMessage()
        adapter.addValues(orderList)
    }

    override fun showEmpty() {
        showMessageInBlankLayout(getString(R.string.transaction_custom_does_not_have_order_items))
        this.onItemsLoaded?.invoke(listOf<Data>())
    }

    override fun showError(error: Throwable) {
        networkExHandler.errorHandle(activity!!, error)
    }

    override fun showNoOk(res: String) {
        showMessageInBlankLayout(res)
    }

    override fun showNotConnected(res: String) {
        showMessageInBlankLayout(res)
    }

    override fun onDetach() {
        super.onDetach()
        this.presenter.detach()
    }

    companion object {
        const val ARG_ORDER_CODE = "order_code"
        @JvmStatic
        fun newInstance(order: Bundle) =
                OrderItemsFragment().apply {
                    arguments = order
                }
    }
}
