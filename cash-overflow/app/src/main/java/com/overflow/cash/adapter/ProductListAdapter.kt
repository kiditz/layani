package com.overflow.cash.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.jakewharton.rxbinding2.view.RxView
import com.overflow.cash.ProductDetailActivity
import com.overflow.cash.R
import com.overflow.cash.fragment.dummy.DummyContent.DummyItem
import com.overflow.cash.net.ImageService
import com.overflow.cash.utils.moveTo
import com.overflow.cash.utils.rupiah
import com.overflow.libs.core.Data
import com.overflow.libs.core.StreamUtils
import kotlinx.android.synthetic.main.adapter_product.view.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 */
class ProductListAdapter(private val imageService: ImageService) : RecyclerView.Adapter<ProductListAdapter.ViewHolder>() {
    lateinit var context: Context
    private val values: MutableList<Data> = mutableListOf()

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
                .inflate(R.layout.adapter_product, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = values[position]
        val sellPrice = context.rupiah(item.getDouble("sell_price"))
        val purchasePrice = context.rupiah(item.getDouble("purchase_price"))
        if(item.getBoolean("use_stock")){
            if(item["stock"] != null){
                val stock = item.getLong("stock").toInt().toString()
                val unit  = item.getString("unit")
                holder.qty.text = "$stock $unit"
            }
        }
        holder.productCode.text = item.getString("product_code")
        holder.productName.text = item.getString("product_name")
        holder.sellPrice.text =  if(item.getDouble("purchase_price") > 0.0){
            "$sellPrice - $purchasePrice"
        }else{
            sellPrice
        }
        val documentId = item.getLong("document_id")
        imageService.loadDocument(holder.imgProduct, documentId , item.getString("product_name"))
        RxView.clicks(holder.itemView).debounce(200, TimeUnit.MILLISECONDS).subscribe({
            val bundle = item.toBundle()
            if(holder.imgProduct.drawable != null){
                val drawAble = holder.imgProduct.drawable as BitmapDrawable
                bundle.putByteArray("image",StreamUtils.bitmapToByte(drawAble.bitmap))
            }
            context.moveTo(ProductDetailActivity::class.java, bundle)
        }, {
            Timber.e(it)
        })
    }



    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val productCode: TextView = view.tvProductCode
        val productName: TextView = view.tvProductName
        val qty: TextView = view.tvQty
        val sellPrice: TextView = view.tvSellPrice
        val imgProduct: ImageView = view.imgProduct

        override fun toString(): String {
            return super.toString() + " '" + productCode.text + "'"
        }
    }
}
