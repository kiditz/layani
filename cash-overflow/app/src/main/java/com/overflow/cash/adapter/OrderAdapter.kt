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
import com.overflow.libs.core.Group
import com.overflow.libs.core.Translations
import kotlinx.android.synthetic.main.adapter_order.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 */
class OrderAdapter(private val translations: Translations, private val format:SimpleDateFormat) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {
    lateinit var context: Context
    val values: MutableList<Group> = mutableListOf()
    var onItemClick: ((Group, ViewHolder) -> Unit)? = null
    lateinit var hoursFormat:SimpleDateFormat

    fun addValues(payloads:List<Group>) {
        values.addAll(payloads)
        notifyDataSetChanged()
    }

    fun clearValues(){
        values.clear()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context = parent.context
        this.hoursFormat = SimpleDateFormat("HH:mm", context.currentLocale())


        val inflater = LayoutInflater.from(parent.context)
        val view = if(viewType == Group.GENERAL){
            inflater.inflate(R.layout.adapter_order, parent, false)
        }else{
            inflater.inflate(R.layout.adapter_order_header, parent, false)
        }
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        if(holder.itemViewType == Group.GENERAL){
            val paymentMethod = translations.get(item.getString("payment_method").toLowerCase())
            holder.paymentMethod?.text = if (paymentMethod == Constant.STRIP){
                "N/A"
            }else{
                paymentMethod
            }
            holder.amount?.text = rupiah(item.getDouble("total_amount"))
            holder.status?.text = translations.get(item.getString("status").toString())
            holder.orderTime.text = hoursFormat.format(Date(item.getLong("order_at")))
            val statusColor:Int = when(item.getString("status")){
                Constant.TransactionStatus.PENDING -> ContextCompat.getColor(context, android.R.color.holo_orange_light)
                Constant.TransactionStatus.VOID -> ContextCompat.getColor(context, android.R.color.holo_red_light)
                Constant.TransactionStatus.SUCCESS -> ContextCompat.getColor(context, android.R.color.holo_green_light)
                else ->  ContextCompat.getColor(context, android.R.color.black)
            }
            holder.itemView.setOnClickListener {
                onItemClick?.invoke(item, holder)
            }
            holder.status?.setTextColor(statusColor)
        }else{
            val calendar = Calendar.getInstance()
            val today = format.format(calendar.time)
            calendar.add(Calendar.DATE, -1)
            val yesterday = format.format(calendar.time)
            val orderTime = item.getString("order_at")

            // Print  day of string by checking value for today and yesterday
            val dayOfWeek = when (orderTime) {
                today -> context.getString(R.string.today)
                yesterday -> context.getString(R.string.yesterday)
                else -> orderTime
            }
            holder.orderTime.text = dayOfWeek
        }
    }

    override fun getItemCount(): Int = values.size

    override fun getItemViewType(position: Int): Int {
        return values[position].type
    }
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val amount: TextView? = view.tv_amount
        val paymentMethod: TextView? = view.tv_payment_method
        val orderTime: TextView = view.tv_order_time
        val status: TextView? = view.tv_status
    }
}
