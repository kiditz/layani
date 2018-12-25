package com.overflow.cash.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.overflow.cash.R
import com.overflow.cash.activity.Constant
import com.overflow.cash.utils.currentLocale
import com.overflow.cash.utils.rupiah
import com.overflow.libs.core.Group
import com.overflow.libs.core.Translations
import kotlinx.android.synthetic.main.adapter_cashbox_summary.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Rifky Aditya Bastara
 * @since 22 Desember 2018
 * [RecyclerView.Adapter] that can display a [Group] called by [com.overflow.cash.fragment.CashboxSummaryFragment]
 */
class CashboxSummaryAdapter(private val translations: Translations, private val format:SimpleDateFormat) : RecyclerView.Adapter<CashboxSummaryAdapter.ViewHolder>() {
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
            inflater.inflate(R.layout.adapter_cashbox_summary, parent, false)
        }else{
            inflater.inflate(R.layout.adapter_cashbox_summary_header, parent, false)
        }
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        val endAt:String? = item["end_at"]?.toString()
        if(holder.itemViewType == Group.GENERAL){

            holder.fullName?.text = item.getString("fullname")
            holder.status?.text = translations.get(item.getString("status").toString())
            holder.endAt.text = if (endAt != null){
                hoursFormat.format(Date(item.getLong("end_at")))
            }else{
                Constant.STRIP
            }
            holder.itemView.setOnClickListener {
                onItemClick?.invoke(item, holder)
            }
        }else{
            if(endAt != null){
                val calendar = Calendar.getInstance()
                val today = format.format(calendar.time)
                calendar.add(Calendar.DATE, -1)
                val yesterday = format.format(calendar.time)
                val orderTime = item.getString("end_at")
                // Print  day of string by checking value for today and yesterday
                val dayOfWeek = when (orderTime) {
                    today -> context.getString(R.string.today)
                    yesterday -> context.getString(R.string.yesterday)
                    else -> orderTime
                }
                holder.endAt.text = dayOfWeek
            }else{
                holder.endAt.text = Constant.STRIP
            }

        }
    }

    override fun getItemCount(): Int = values.size

    override fun getItemViewType(position: Int): Int {
        return values[position].type
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val fullName: TextView? = view.tv_fullname
        val endAt: TextView = view.tv_datetime
        val status: TextView? = view.tv_status
    }
}
