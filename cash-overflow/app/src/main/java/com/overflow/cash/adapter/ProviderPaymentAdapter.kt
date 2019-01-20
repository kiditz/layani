package com.overflow.cash.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.overflow.cash.R
import com.overflow.cash.fragment.dummy.DummyContent.DummyItem
import com.overflow.cash.net.ImageService
import com.overflow.libs.core.Data
import kotlinx.android.synthetic.main.adapter_pulsa_provider_payment.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 */
class ProviderPaymentAdapter(private val imageService: ImageService) : RecyclerView.Adapter<ProviderPaymentAdapter.ViewHolder>() {
    lateinit var context: Context
    private val values: MutableList<Data> = mutableListOf()
    var onItemClick: ((Data) -> Unit)? = null
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
                .inflate(R.layout.adapter_pulsa_provider_payment, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val provider = values[position]
        this.imageService.loadProviderImage(holder.providerImage, provider.getLong("id"), provider.getString("name"))
        holder.providerName.text = provider["name"].toString()
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(provider)
        }
    }



    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val providerName: TextView = view.tv_provider_name
        val providerImage: ImageView = view.provider_image

    }
}
