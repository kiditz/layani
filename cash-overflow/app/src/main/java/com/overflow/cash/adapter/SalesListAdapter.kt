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
import com.overflow.cash.model.OrderItem
import com.overflow.cash.net.ImageService
import com.overflow.cash.utils.rupiah
import com.overflow.libs.core.Data
import io.realm.Realm
import kotlinx.android.synthetic.main.adapter_sales.view.*
import timber.log.Timber

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 */
class SalesListAdapter(private val imageService: ImageService) : RecyclerView.Adapter<SalesListAdapter.ViewHolder>() {
    lateinit var context: Context
    val values: MutableList<Data> = mutableListOf()
    private val realm:Realm = Realm.getDefaultInstance()
    var onItemClick: ((Data, ViewHolder) -> Unit)? = null
    var onItemLongClick: ((Data, ViewHolder) -> Unit)? = null
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
                .inflate(R.layout.adapter_sales, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        val sellPrice = context.rupiah(item.getDouble("sell_price"))
        val unit = item.getString("unit")
        val productName = item.getString("product_name")
        holder.productName.text = productName
        holder.sellPrice.text =  sellPrice
        val documentId = item.getLong("document_id")
        imageService.loadDocument(holder.imgProduct,documentId , productName)
        val orderItem = realm.where(OrderItem::class.java).equalTo("productId", item.getLong("product_id")).findFirst()

        if(orderItem != null){
            holder.qty.text = orderItem.qty.toString() +" " + unit
            if(orderItem.discountAmount > 0){
                if(orderItem.discountType == Constant.DiscountType.PERCENTAGE){
                    holder.discount.text = orderItem.discountAmount.toString() + "%"
                }else{
                    holder.discount.text = context.rupiah(orderItem.discountAmount)
                }
                holder.discount.visibility = View.VISIBLE
            }else{
                holder.discount.visibility = View.GONE
            }
            holder.qty.visibility= View.VISIBLE
        }else{
            holder.qty.visibility= View.GONE
        }

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(item, holder)
        }
        try {
            if(item.getBoolean("use_stock")){
                val stock = item.getLong("stock")
                holder.remainingStock.text = "$stock $unit"
                holder.remainingStock.visibility = View.VISIBLE
            }else{
                holder.remainingStock.visibility = View.GONE
            }
        }catch (e:Exception){
        }

        holder.itemView.setOnLongClickListener{
            try {
                onItemLongClick?.invoke(item, holder)
            }catch (e:Exception){
                //Ignore
                Timber.e(e)
            }
            true
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.tv_product_name
        val sellPrice: TextView = view.tv_sell_price
        val imgProduct: ImageView = view.img_product
        val remainingStock = view.tv_stock
        val qty: TextView = view.tv_order_item_qty
        val discount: TextView = view.tv_discount_amount
    }
}
