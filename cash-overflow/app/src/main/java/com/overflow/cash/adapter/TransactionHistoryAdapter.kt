package com.overflow.cash.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.overflow.cash.activity.Constant
import com.overflow.cash.R
import com.overflow.cash.fragment.dummy.DummyContent.DummyItem
import com.overflow.cash.utils.currentLocale
import com.overflow.cash.utils.rupiah
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import kotlinx.android.synthetic.main.adapter_transaction_history.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 */
class TransactionHistoryAdapter(private val translations: Translations) : RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder>() {
    lateinit var context: Context
    val values: MutableList<Data> = mutableListOf()
    var onItemClick: ((Data, ViewHolder) -> Unit)? = null
    fun addValues(payloads:List<Data>) {
        values.addAll(payloads)
        notifyDataSetChanged()
    }

    fun clearValues(){
        values.clear()
    }


    lateinit var dateFormat:SimpleDateFormat
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context = parent.context
        this.dateFormat = SimpleDateFormat("HH:mm", context.currentLocale())
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_transaction_history, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.orderCode.text = "#${item.getString("order_code")}"
        holder.paymentMethod.text = translations.get(item.getString("payment_method").toLowerCase())
        holder.amount.text = context.rupiah(item.getDouble("total_amount"))
        holder.status.text = translations.get(item.getString("status").toString())
        holder.orderTime.text = dateFormat.format(Date(item.getLong("order_at")))
        val statusColor:Int = when(item.getString("status")){
            Constant.TransactionStatus.PENDING -> ContextCompat.getColor(context, android.R.color.holo_orange_light)
            Constant.TransactionStatus.VOID -> ContextCompat.getColor(context, android.R.color.holo_red_light)
            Constant.TransactionStatus.SUCCESS -> ContextCompat.getColor(context, android.R.color.holo_green_light)
            else ->  ContextCompat.getColor(context, android.R.color.black)
        }
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(item, holder)
        }
        holder.status.setTextColor(statusColor)
    }





    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val amount: TextView = view.tv_amount
        val paymentMethod: TextView = view.tv_payment_method
        val orderTime: TextView = view.tv_order_time
        val status: TextView = view.tv_status
        val orderCode = view.tv_order_code
    }
}
