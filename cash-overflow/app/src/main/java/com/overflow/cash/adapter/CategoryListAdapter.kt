package com.overflow.cash.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import com.overflow.cash.R
import com.overflow.cash.fragment.dummy.DummyContent.DummyItem
import com.overflow.libs.core.Data
import kotlinx.android.synthetic.main.adapter_category.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 */
class CategoryListAdapter() : RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {
    lateinit var context: Context
    private val values: MutableList<Data> = mutableListOf()
    var onRemoveClicked: ((Data, ViewHolder) -> Unit)? = null
    var onSaveClicked: ((Data, ViewHolder) -> Unit)? = null
    fun addValue(payload:Data) {
        values.add(payload)
        notifyDataSetChanged()
    }
    fun setValue(position: Int,payload:Data) {
        values.set(position, payload)
        notifyItemChanged(position)
    }
    fun addValues(payloads:List<Data>) {
        values.addAll(payloads)
        notifyDataSetChanged()
    }

    fun clearValues(){
        values.clear()
    }

    fun removeItems(position:Int){
        this.values.removeAt(position)
        notifyItemRemoved(position)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.context = parent.context
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_category, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.name.setText(item.getString("category_name"))
        //holder.exitImage.tinting(android.R.color.holo_red_dark)
        holder.exitImage.setOnClickListener {
            onRemoveClicked?.invoke(item, holder)
        }
        holder.saveImage.setOnClickListener {
            onSaveClicked?.invoke(item, holder)
        }
    }





    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val name: EditText = view.ed_name
        val exitImage: ImageView = view.exit_image
        val saveImage: ImageView = view.done_image
        val progressBar:ProgressBar = view.progress_bar
    }
}
