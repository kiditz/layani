package com.overflow.cash.fragment

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.overflow.cash.R
import com.overflow.cash.activity.Constant
import com.overflow.cash.adapter.PulsaPaketProductAdapter
import com.overflow.cash.mvp.pulsa.*
import com.overflow.cash.net.API
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.*
import com.overflow.libs.core.Data
import kotlinx.android.synthetic.main.fragment_pulsa_products_by_provider.*
import javax.inject.Inject

class PulsaProductsByProviderListFragment : BaseFragment(), LoadPulsaProductByProviderContract.View, SendOrderPulsaContract.View, LoadProviderByCategoryContract.View{
    @Inject
    lateinit var loadPulsaProductByProviderPresenter: LoadPulsaProductByProviderPresenter
    @Inject
    lateinit var loadProviderByCategoryPresenter: LoadProviderByCategoryPresenter
    @Inject
    lateinit var sendOrderPulsaPresenter: SendOrderPulsaPresenter
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    lateinit var adapter:PulsaPaketProductAdapter
    var currentPage = API.MIN_PAGE
    var categoryId = -1L
    var categoryName = ""
    var productCode:String = ""
    var phoneNumber:String = ""
    var hint:String=""
    var providerList = listOf<Data>()
    var providerId = -1L
    var showPhoneNumber = true
    lateinit var spAdapter:ArrayAdapter<String>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pulsa_products_by_provider, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(showPhoneNumber){
            ed_phone_number.hint = hint
            ed_phone_number.visibility = View.VISIBLE
        }else{
            ed_phone_number.visibility = View.GONE
        }
        this.loadPulsaProductByProviderPresenter.attach(this)
        this.loadProviderByCategoryPresenter.attach(this)
        this.sendOrderPulsaPresenter.attach(this)
        hideMessage()
        adapter = PulsaPaketProductAdapter()
        this.spAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item)
        this.sp_provider?.adapter = spAdapter
        this.loadProviderByCategoryPresenter.loadProvider(categoryId)

        val manager  = LinearLayoutManager(context)
        recycler?.layoutManager =  manager
        recycler?.isNestedScrollingEnabled = false
        recycler?.setHasFixedSize(true)
        recycler?.itemAnimator = DefaultItemAnimator()

        recycler.adapter = adapter
        recycler?.addOnScrollListener(object : AbstractRecyclerPagination(manager){
            override val isLoading: Boolean
                get() = loadPulsaProductByProviderPresenter.loading
            override val isLastPage: Boolean
                get() = loadPulsaProductByProviderPresenter.lastPage
            override val totalItemCount: Int
                get() = loadPulsaProductByProviderPresenter.getSize()

            override fun loadMoreItems() {
                currentPage += 1
                loadPulsaProductByProviderPresenter.loadProduct(currentPage, providerId)
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
        if(showPhoneNumber) {
            if (ed_phone_number.text.length < 8) {
                showErrorMessage("${ed_phone_number.hint} ${getString(R.string.invalid)}")
                return
            }
        }
        val data = Data()
        data["code"] = productCode
        if(!showPhoneNumber){
            data["msisdn"] = Constant.STRIP
        }else{
            data["msisdn"] = ed_phone_number.text.toString()
        }
        showProgress(true)
        sendOrderPulsaPresenter.sendOrder(data)
    }

    @SuppressLint("SetTextI18n")
    private fun initLoadProduct(){
        adapter.onDoneClick = {item, _ ->
            btn_buy_now.isEnabled = true
            this.productCode = item.getString("code")
            this.tv_total_sell_price.text = "${getString(R.string.pay)} ${rupiah(item.getDouble("sell_price"))}"
        }

        sp_provider?.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val provider = providerList[position]
                providerId = provider.getLong("id")
                currentPage = API.MIN_PAGE
                loadPulsaProductByProviderPresenter.loadProduct(currentPage, providerId)
            }

        }

        btn_buy_now.setOnClickListener{
            if(this.productCode.isEmpty()){
                activity!!.snack(getString(R.string.please_choose_product)).show()
                return@setOnClickListener
            }
            doOrder()
        }
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



    override fun onProviderLoaded(providerList: List<Data>) {
        val provider = providerList.map { it.getString("name") }
        this.providerList = providerList
        this.spAdapter.addAll(provider)
    }
    override fun onProductLoaded(productList: List<Data>) {
        hideMessage()
        if(currentPage == API.MIN_PAGE){
            adapter.clearValues()
        }
        adapter.addValues(productList)
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

    companion object {
        const val ARG_CATEGORY = "category"
        @JvmStatic
        fun newInstance(category: String, hint:String, showPhoneNumber:Boolean=true) =
                PulsaProductsByProviderListFragment().apply {
                    arguments = Data(category).toBundle()
                    categoryId = arguments!!.getLong("id")
                    categoryName = arguments!!.getString("name")
                    this.showPhoneNumber = showPhoneNumber
                    this.hint = hint
                }
    }

}