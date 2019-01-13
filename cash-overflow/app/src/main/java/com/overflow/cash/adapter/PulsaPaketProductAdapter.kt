package com.overflow.cash.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.overflow.cash.R
import com.overflow.cash.fragment.dummy.DummyContent.DummyItem
import com.overflow.cash.utils.rupiah
import com.overflow.libs.core.Data
import kotlinx.android.synthetic.main.adapter_pulsa_paket_product.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 */
class PulsaPaketProductAdapter() : RecyclerView.Adapter<PulsaPaketProductAdapter.ViewHolder>() {
    lateinit var context: Context
    private val values: MutableList<Data> = mutableListOf()
    var onDoneClick: ((Data, PulsaPaketProductAdapter.ViewHolder) -> Unit)? = null
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
                .inflate(R.layout.adapter_pulsa_paket_product, parent, false)
        return ViewHolder(view)
    }
    var tempHolder:ViewHolder?=null
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = values[position]
        val name = item.getString("name")
        val code = item.getString("code")
        val sellPrice = rupiah(item.getDouble("sell_price"))
        holder.productCode.text = code
        holder.productName.text = name
        if(name.length < 40){
            holder.productNameDetail.visibility = View.GONE
        }else{
            holder.productNameDetail.visibility = View.VISIBLE
            holder.productNameDetail.text = name
        }
        holder.itemView.setBackgroundResource(android.R.color.white)
        holder.itemView.setOnClickListener {
            holder.itemView.setBackgroundResource(R.color.grey)
            if(tempHolder != null){
                tempHolder?.itemView?.setBackgroundResource(android.R.color.white)
                this.tempHolder = holder
            }else{
                this.tempHolder = holder
            }
            this.onDoneClick?.invoke(item, holder)
        }
        holder.sellPrice.text = sellPrice

    }



    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val productCode: TextView = view.tv_product_code
        val productName: TextView = view.tv_product_name
        val productNameDetail: TextView = view.tv_product_name_detail
        val sellPrice: TextView = view.tv_sell_price
    }
}
