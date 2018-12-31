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
import com.overflow.cash.net.ImageService
import com.overflow.cash.utils.rupiah
import com.overflow.libs.core.Data
import kotlinx.android.synthetic.main.adapter_order_items.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 */
class OrderItemsAdapter(private val imageService: ImageService) : RecyclerView.Adapter<OrderItemsAdapter.ViewHolder>() {
    lateinit var context: Context
    val values: MutableList<Data> = mutableListOf()
    var onItemClick: ((Data, ViewHolder) -> Unit)? = null
    //var onItemLongClick: ((Data, ViewHolder) -> Unit)? = null
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
                .inflate(R.layout.adapter_order_items, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        val sellPrice = rupiah(item.getDouble("sell_price"))
        val qty = item.getLong("qty")
        val unit = item.getString("unit")
        val subTotal = rupiah(item.getDouble("sub_total"))
        val productName = item.getString("product_name")
        val discountName = if (item.containsKeyAndNotNull("discount_name")){
            holder.discountName.visibility = View.VISIBLE
            item.getString("discount_name")
        }else{
            holder.discountName.visibility = View.GONE
            Constant.TEXT_EMPTY
        }
        val discountAmount = item.getDouble("discount_amount")
        val documentId = item["document_id"]?.toString()?.toLong()
        holder.productName.text= productName
        holder.subTotal.text = subTotal
        holder.sellPrice.text = "$sellPrice x $qty"
        imageService.loadDocument(holder.imgProduct, documentId, productName)

        holder.discountName.text = discountName
        if(discountAmount > 0){
            holder.sellPrice.text = "${holder.sellPrice.text} - ${rupiah(discountAmount)}"
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.tv_product_name
        val discountName: TextView = view.tv_discount_name
        val sellPrice: TextView = view.tv_sell_price
        val imgProduct: ImageView = view.img_product
        val subTotal: TextView = view.tv_sub_total
    }
}
