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
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import kotlinx.android.synthetic.main.adapter_customer.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 */
class TransactionHistoryAdapter(translations: Translations) : RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder>() {
    lateinit var context: Context
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
                .inflate(R.layout.adapter_transaction_history, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.name.text = item.getString("name")
        if(item.getString("phone_number").isNotEmpty() && !item.getString("phone_number").equals("-", true)){
            holder.phoneNumber.text = item.getString("phone_number")
            holder.phoneNumber.visibility = View.VISIBLE
        }else{
            holder.phoneNumber.visibility = View.GONE
        }

        if(item.getString("email").isNotEmpty() && !item.getString("email").equals("-", true)){
            holder.email.text = item.getString("email")
            holder.email.visibility = View.VISIBLE
        }else{
            holder.email.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(item, holder)
        }
    }





    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.tvName
        val phoneNumber: TextView = view.tvPhoneNumber
        val email: TextView = view.tvEmail
    }
}
