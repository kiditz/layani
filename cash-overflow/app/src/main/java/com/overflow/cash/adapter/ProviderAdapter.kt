package com.overflow.cash.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.overflow.cash.R
import com.overflow.cash.net.ImageService
import com.overflow.libs.core.Data
import kotlinx.android.synthetic.main.adapter_spinner_image.view.*

class ProviderAdapter(private val imageService:ImageService, private val activity:Activity) : BaseAdapter() {
    val providers = mutableListOf<Data>()

    fun addAll(providers:List<Data>){
        this.providers.addAll(providers)
        notifyDataSetChanged()
    }

    fun clear(){
        this.providers.clear()
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return providers.size
    }

    override fun getItem(position: Int): Data {
        return providers[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = convertView ?: LayoutInflater.from(activity).inflate(R.layout.adapter_spinner_image, parent, false)
        val provider = getItem(position)
        v.tv_provider_name.text = provider.getString("name")
        activity.runOnUiThread {
            this.imageService.loadProviderImage(v.provider_image, provider.getLong("id"), provider.getString("name"))
        }
        return v
    }
}
