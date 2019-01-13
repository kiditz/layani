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
import kotlinx.android.synthetic.main.adapter_pulsa_product.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 */
class PulsaProductAdapter() : RecyclerView.Adapter<PulsaProductAdapter.ViewHolder>() {
    lateinit var context: Context
    private val values: MutableList<Data> = mutableListOf()
    var onDoneClick: ((Data, PulsaProductAdapter.ViewHolder) -> Unit)? = null
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
                .inflate(R.layout.adapter_pulsa_product, parent, false)
        return ViewHolder(view)
    }
    var tempHolder:ViewHolder?=null
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = values[position]
        val name = item.getString("name")
        val code = item.getString("code")
        val nominal = rupiah(item.getDouble("nominal"))
        val sellPrice = rupiah(item.getDouble("sell_price"))
        holder.productCode.text = code
        holder.nominal.text = nominal
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
        if(nominal != sellPrice){
            holder.sellPrice.text = "${context.getString(R.string.pay)} $sellPrice"
        }

    }



    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val productCode: TextView = view.tv_product_code
        val nominal: TextView = view.tv_nominal
        val sellPrice: TextView = view.tv_sell_price
    }
}
