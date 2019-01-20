package com.overflow.cash.fragment.pulsa

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.RxTextView
import com.overflow.cash.R
import com.overflow.cash.activity.Constant
import com.overflow.cash.adapter.PulsaProductAdapter
import com.overflow.cash.fragment.BaseFragment
import com.overflow.cash.mvp.pulsa.LoadPulsaProductContract
import com.overflow.cash.mvp.pulsa.LoadPulsaProductPresenter
import com.overflow.cash.mvp.pulsa.SendOrderPulsaContract
import com.overflow.cash.mvp.pulsa.SendOrderPulsaPresenter
import com.overflow.cash.net.API
import com.overflow.cash.net.ImageService
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.*
import com.overflow.cash.utils.decoration.MarginItemDecoration
import com.overflow.libs.core.Data
import kotlinx.android.synthetic.main.fragment_pulsa_products.*
import timber.log.Timber
import javax.inject.Inject

class PulsaProductListFragment : BaseFragment(), LoadPulsaProductContract.View ,SendOrderPulsaContract.View{
    @Inject
    lateinit var loadPulsaProductPresenter: LoadPulsaProductPresenter
    @Inject
    lateinit var sendOrderPulsaPresenter: SendOrderPulsaPresenter
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    @Inject
    lateinit var imageService:ImageService
    lateinit var adapter:PulsaProductAdapter

    var currentPage = API.MIN_PAGE
    var categoryId = -1L
    var categoryName = ""
    var productCode:String = ""
    var phoneNumber:String = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pulsa_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.loadPulsaProductPresenter.attach(this)
        this.sendOrderPulsaPresenter.attach(this)
        hideMessage()
        adapter = PulsaProductAdapter()

        val manager  = GridLayoutManager(context, 2)
        recycler?.layoutManager =  manager
        recycler?.isNestedScrollingEnabled = false
        recycler?.setHasFixedSize(true)
        recycler?.itemAnimator = DefaultItemAnimator()
        val spaceInPixel = resources.getDimensionPixelSize(R.dimen.grid_margin)
        recycler?.addItemDecoration(MarginItemDecoration(spaceInPixel))
        recycler.adapter = adapter
        recycler?.addOnScrollListener(object : AbstractRecyclerPagination(manager){
            override val isLoading: Boolean
                get() = loadPulsaProductPresenter.loading
            override val isLastPage: Boolean
                get() = loadPulsaProductPresenter.lastPage
            override val totalItemCount: Int
                get() = loadPulsaProductPresenter.getSize()

            override fun loadMoreItems() {
                currentPage += 1
                loadPulsaProductPresenter.loadProduct(currentPage, categoryId, phoneNumber)
            }
        })

        initLoadProduct()
    }

    private fun doOrder(){
        activity!!.showMessage(categoryName, getString(R.string.are_you_sure_topup).replace("{0}", productCode), object:MessageButtonHandle(){
            override fun ok(dialog: DialogInterface, which: Int) {
                super.ok(dialog, which)
                dialog.dismiss()
                sendOrder()
            }
        }).show()

    }

    private fun sendOrder(){
        if(ed_phone_number.text.length < 8){
            showErrorMessage("${ed_phone_number.hint} ${getString(R.string.invalid)}")
            return
        }
        val data = Data()
        data["code"] = productCode
        data["msisdn"] = ed_phone_number.text.toString()
        showProgress(true)
        sendOrderPulsaPresenter.sendOrder(data)
    }

    @SuppressLint("SetTextI18n")
    private fun initLoadProduct(){
        RxTextView.textChanges(ed_phone_number).filter { it.isNotEmpty() && it.length >= 4}.filter { Patterns.PHONE.matcher(it).matches() }.subscribe( {
            this.phoneNumber = it.toString()

            currentPage = API.MIN_PAGE
            this.loadPulsaProductPresenter.loadProduct(currentPage, categoryId, this.phoneNumber)
        }, {
            Timber.i(it)
        })

        adapter.onDoneClick = {item, _ ->
            this.productCode = item.getString("code")
            btn_buy_now.isEnabled = true
            this.tv_total_sell_price.text = "${getString(R.string.pay)} ${rupiah(item.getDouble("sell_price"))}"
        }

        btn_buy_now.setOnClickListener{
            if(this.productCode.isEmpty()){
                activity!!.snack(getString(R.string.please_choose_product)).show()
                return@setOnClickListener
            }
            doOrder()
        }
    }

    override fun onProductLoaded(productList: List<Data>) {
        hideMessage()
        if(currentPage == API.MIN_PAGE){
            adapter.clearValues()
        }
        val firstProduct = productList[0]
        val providerId = firstProduct.getLong("provider_id")
        provider_image?.let {
            activity!!.runOnUiThread{
                imageService.loadProviderImage(it, providerId, firstProduct["provider"].toString())
            }

        }
        adapter.addValues(productList)
    }

    override fun showError(error: Throwable) {
        showProgress(false)
        this.networkExHandler.errorHandle(activity!!, error)
    }

    override fun showNoOk(res: String) {
        showProgress(false)
        showErrorMessage(res)
    }

    override fun showEmpty() {
        showMessageInBlankLayout(categoryName, getString(R.string.no_product_title))
    }

    override fun showNotConnected(res: String) {
        showProgress(false)
        showErrorMessage(res)
    }

    companion object {
        const val ARG_CATEGORY = "category"
        @JvmStatic
        fun newInstance(category: String) =
                PulsaProductListFragment().apply {
                    arguments = Data(category).toBundle()
                    categoryId = arguments!!.getLong("id")
                    categoryName = arguments!!.getString("name")
                }
    }

    override fun onOrderSended(result: Data) {
        adapter.clearValues()
        adapter.notifyDataSetChanged()
        ed_phone_number.setText(Constant.TEXT_EMPTY)
        showProgress(false)
        btn_buy_now.isEnabled = false
        activity!!.showMessage(categoryName, getString(R.string.transaction_in_progress).replace("{0}", productCode), object :MessageButtonHandle(){

        }).show()
    }

    private fun showProgress(isShowed:Boolean=false){
        if(isShowed){
            this.progress.visibility = View.VISIBLE
        }else{
            this.progress.visibility = View.GONE
        }
    }
//
    override fun onDestroy() {
        super.onDestroy()
        loadPulsaProductPresenter.detach()
        sendOrderPulsaPresenter.detach()
    }

}