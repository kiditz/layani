package com.overflow.cash.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.overflow.cash.Constant
import com.overflow.cash.R
import com.overflow.cash.fragment.dummy.DummyContent.DummyItem
import com.overflow.cash.mvp.order.PreviewSalesContract
import com.overflow.cash.net.ImageService
import com.overflow.cash.utils.rupiah
import com.overflow.libs.core.Data
import kotlinx.android.synthetic.main.adapter_sales_preview.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 */
class PreviewSalesAdapter(private val imageService: ImageService, private val presenter: PreviewSalesContract.Presenter) : RecyclerView.Adapter<PreviewSalesAdapter.ViewHolder>() {
    lateinit var context: Context
    val values: MutableList<Data> = mutableListOf()
    fun addValues(payloads:List<Data>) {
        values.addAll(payloads)
        notifyDataSetChanged()
    }

    fun clearValues(){
        values.clear()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context = parent.context
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_sales_preview, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        val sellPrice = context.rupiah(item.getDouble("sellPrice"))
        val qty = item.getLong("qty")
        val unit = item.getString("unit")
        val subTotal = context.rupiah(item.getDouble("subTotal"))
        val productId = item.getLong("productId")
        holder.productName.text = item.getString("productName")
        holder.sellPrice.text = "$sellPrice x $qty $unit"
        holder.subTotal.text = subTotal
        val documentId = item.getLong("documentId")
        val countDiscount = item.getLong("countDiscount")
        if(countDiscount > 0){
            //this.presenter.loadDiscount(productId, qty, holder, position)
            val discountAmount = item.getDouble("discountAmount")
            val discountType = item.getString("discountType")
            if(discountType == Constant.DiscountType.PERCENTAGE){
                holder.sellPrice.text = "$sellPrice x $qty $unit - ${discountAmount.toInt()}%"
            }else{
                holder.sellPrice.text = "$sellPrice x $qty $unit - ${context.rupiah(discountAmount)}"
            }
        }
        imageService.loadDocument(holder.imgProduct, documentId , item.getString("productName"))
    }



    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.tv_product_name
        val discount: TextView = view.tvDiscount
        val sellPrice: TextView = view.tv_sell_price
        val imgProduct: ImageView = view.img_product
        val subTotal: TextView = view.tvSubTotal
    }
}
