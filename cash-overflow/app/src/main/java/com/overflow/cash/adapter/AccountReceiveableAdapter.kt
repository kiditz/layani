package com.overflow.cash.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.overflow.cash.R
import com.overflow.cash.utils.rupiah
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import kotlinx.android.synthetic.main.adapter_account_receiveable.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * [RecyclerView.Adapter] that can display a [Data] and makes a call to the
 */
class AccountReceiveableAdapter(private val translations: Translations) : RecyclerView.Adapter<AccountReceiveableAdapter.ViewHolder>() {
    lateinit var context: Context
    private val DATETIME_FORMAT = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    private val values: MutableList<Data> = mutableListOf()
    var onItemClick: ((Data, ViewHolder) -> Unit)? = null
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
                .inflate(R.layout.adapter_account_receiveable, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.customerName.text = item.getString("name")
        holder.totalCredit.text = rupiah(item.getDouble("total_credit"))
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(item, holder)
        }
    }


    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val totalCredit: TextView = view.tvAmount
        val customerName:TextView = view.tvCustomerName
    }
}
