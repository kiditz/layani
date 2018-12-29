package com.overflow.cash.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.overflow.cash.R
import com.overflow.cash.activity.Constant
import com.overflow.cash.utils.rupiah
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import kotlinx.android.synthetic.main.adapter_cashbox_history.view.*

/**
 * [RecyclerView.Adapter] that can display a [Data] and called by class [com.overflow.cash.activity.CustomerListAddActivity]
 */
class CashboxHistoryAdapter(private val translations: Translations) : RecyclerView.Adapter<CashboxHistoryAdapter.ViewHolder>() {
    lateinit var context: Context
    val values: MutableList<Data> = mutableListOf()
    var onDeleteClick: ((Data, ViewHolder) -> Unit)? = null
    var onDoneClick: ((Data, ViewHolder) -> Unit)? = null

    fun addValue(payload:Data) {
        values.add(payload)
        notifyDataSetChanged()
    }

    fun addValues(payloads:List<Data>) {
        values.addAll(payloads)
        notifyDataSetChanged()
    }

    fun clearValues(){
        values.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context = parent.context
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_cashbox_history, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.amount.text = rupiah(item.getDouble("amount"))
        holder.description.text = item.getString("remark")
        var status = ""
        var textColor = Color.BLACK
        if(item.getInt("ref_id") == 1){
            status = translations.get(Constant.CashboxType.CASH_BANK_IN)
            textColor = ContextCompat.getColor(context, android.R.color.holo_green_light)
        }else{
            status = translations.get(Constant.CashboxType.CASH_BANK_OUT)
            textColor = ContextCompat.getColor(context, android.R.color.holo_red_light)
        }
        holder.status.text = status
        holder.status.setTextColor(textColor)



    }





    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val amount: TextView = view.tv_amount
        val status: TextView = view.tv_status
        val description: TextView = view.tv_description
    }
}
