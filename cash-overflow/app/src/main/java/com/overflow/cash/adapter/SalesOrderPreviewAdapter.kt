package com.overflow.cash.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.overflow.cash.R
import com.overflow.cash.activity.Constant
import com.overflow.cash.fragment.dummy.DummyContent.DummyItem
import com.overflow.cash.mvp.discount.LoadDiscountByQuantityContract
import com.overflow.cash.mvp.discount.LoadDiscountByQuantityPresenter
import com.overflow.cash.net.ImageService
import com.overflow.cash.realm.OrderItemRealm
import com.overflow.cash.utils.parseRupiah
import com.overflow.cash.utils.rupiah
import com.overflow.libs.core.Data
import kotlinx.android.synthetic.main.adapter_sales_items.view.*
import timber.log.Timber

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 */
class SalesOrderPreviewAdapter(private val imageService: ImageService, private val orderService:OrderItemRealm, private val presenter: LoadDiscountByQuantityPresenter) : RecyclerView.Adapter<SalesOrderPreviewAdapter.ViewHolder>(), LoadDiscountByQuantityContract.View {
    lateinit var context: Context
    val values: MutableList<Data> = mutableListOf()
    fun addValues(payloads: List<Data>) {
        values.addAll(payloads)
        notifyDataSetChanged()
    }

    fun clearValues() {
        values.clear()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context = parent.context
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_sales_items, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        val sellPrice = rupiah(item.getDouble("sellPrice"))
        val qty = item.getLong("qty")
        val unit = item.getString("unit")
        val subTotal = rupiah(item.getDouble("subTotal"))
        val hasDiscount = item.getBoolean("hasDiscount")
        val productId = item.getLong("productId")
        holder.productName.text = item.getString("productName")

        holder.subTotal.text = subTotal
        val documentId = item.getLong("documentId")
        holder.sellPrice.text = "$sellPrice x $qty $unit"
        if (hasDiscount) {
            presenter.loadDiscount(qty, productId, holder)
        }
        imageService.loadDocument(holder.imgProduct, documentId, item.getString("productName"))
    }


    override fun getItemCount(): Int = values.size

    @SuppressLint("SetTextI18n")
    override fun onDiscountLoaded(data: Data, holder: RecyclerView.ViewHolder) {
        val method = data.getInt("method")
        val type = data.getString("discount_type")
        val amount = data.getDouble("amount")
        if (holder is SalesOrderPreviewAdapter.ViewHolder) {
            holder.freeProduct.visibility = View.VISIBLE
            if (method == Constant.DiscountMethod.BY_N_GET_ONE) {

                holder.freeProduct.text = data.getString("name")
            }else{
                holder.freeProduct.text = data.getString("name")
                if (type == Constant.DiscountType.PERCENTAGE) {
                    val calculateDiscount = (amount / 100)
                    holder.sellPrice.text = "${holder.sellPrice.text} - ${amount.toInt()}%"
                    holder.subTotal.text = rupiah(parseRupiah(holder.subTotal.text) - calculateDiscount )
                }else{
                    holder.sellPrice.text = "${holder.sellPrice.text} - ${rupiah(amount)}"
                    holder.subTotal.text = rupiah( parseRupiah(holder.subTotal.text) - amount)
                }
            }

            val item = this.values[holder.adapterPosition]
            item["subTotal"] = parseRupiah(holder.subTotal.text)
            item["discountId"] = data.getLong("id")
            orderService.setItem(item)
        }
    }

    override fun onDiscountNotLoaded(data: Data, holder: RecyclerView.ViewHolder) {
        Timber.i("Discount Not Loaded : %s", data)
        if (holder is SalesOrderPreviewAdapter.ViewHolder) {
            holder.freeProduct.visibility = View.GONE
        }
    }

    //Nothing todo
    override fun showEmpty() {}

    override fun showError(error: Throwable) {}

    override fun showNotConnected(res: String) {}

    override fun showNoOk(res: String) {}

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.tv_product_name
        val freeProduct: TextView = view.tv_free_product
        val sellPrice: TextView = view.tv_sell_price
        val imgProduct: ImageView = view.img_product
        val subTotal: TextView = view.tv_sub_total
    }
}
