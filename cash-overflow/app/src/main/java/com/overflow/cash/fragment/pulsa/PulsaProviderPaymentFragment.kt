package com.overflow.cash.fragment.pulsa

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.overflow.cash.R
import com.overflow.cash.activity.pulsa.CheckPaymentActivity
import com.overflow.cash.adapter.ProviderPaymentAdapter
import com.overflow.cash.fragment.BaseFragment
import com.overflow.cash.mvp.pulsa.LoadProviderByCategoryContract
import com.overflow.cash.mvp.pulsa.LoadProviderByCategoryPresenter
import com.overflow.cash.net.API
import com.overflow.cash.net.ImageService
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.decoration.MarginItemDecoration
import com.overflow.cash.utils.moveTo
import com.overflow.libs.core.Data
import kotlinx.android.synthetic.main.fragment_pulsa_provider_payment.*
import timber.log.Timber
import javax.inject.Inject

class PulsaProviderPaymentFragment : BaseFragment(), LoadProviderByCategoryContract.View{
    @Inject
    lateinit var loadProviderByCategoryPresenter: LoadProviderByCategoryPresenter

    @Inject
    lateinit var networkExHandler: NetworkExHandler
    @Inject
    lateinit var imageService:ImageService
    lateinit var adapter:ProviderPaymentAdapter
    var currentPage = API.MIN_PAGE
    var categoryId = -1L
    var categoryName = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pulsa_provider_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.i("Args : %s", arguments)
        this.categoryId = arguments!!.getLong("id")
        this.categoryName = arguments!!.getString("name")
        loadProviderByCategoryPresenter.attach(this)
        loadProviderByCategoryPresenter.loadProvider(categoryId)
        hideMessage()
        this.adapter = ProviderPaymentAdapter(imageService)
        val manager  = GridLayoutManager(context, 2)
        val spaceInPixel = resources.getDimensionPixelSize(R.dimen.grid_margin)
        recycler?.addItemDecoration(MarginItemDecoration(spaceInPixel))
        recycler?.layoutManager =  manager
        recycler?.isNestedScrollingEnabled = false
        recycler?.setHasFixedSize(true)
        recycler?.itemAnimator = DefaultItemAnimator()
        recycler.adapter = adapter
        adapter.onItemClick = {data ->
            activity!!.moveTo(CheckPaymentActivity::class.java, data.toBundle())
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
        Timber.d("Provider List : %s", providerList)
        adapter.clearValues()
        adapter.addValues(providerList)
    }
    private fun showProgress(isShowed:Boolean=false){
        if(isShowed){
            this.progress.visibility = View.VISIBLE
        }else{
            this.progress.visibility = View.GONE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle) =
                PulsaProviderPaymentFragment().apply {
                    arguments = bundle
                }
    }

}