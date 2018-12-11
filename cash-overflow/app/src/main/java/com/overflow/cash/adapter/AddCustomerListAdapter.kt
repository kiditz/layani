package com.overflow.cash.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import com.overflow.cash.activity.Constant
import com.overflow.cash.R
import com.overflow.cash.fragment.dummy.DummyContent.DummyItem
import com.overflow.cash.utils.validateNotEmpty
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import kotlinx.android.synthetic.main.adapter_add_customer.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 */
class AddCustomerListAdapter(private val translations: Translations) : RecyclerView.Adapter<AddCustomerListAdapter.ViewHolder>() {
    lateinit var context: Context
    private val values: MutableList<Data> = mutableListOf()
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
                .inflate(R.layout.adapter_add_customer, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.name.setText(item.getString("name"))
        holder.email.setText(item.getString("email"))
        holder.phoneNumber.setText(item.getString("phone_number"))
        val nameObserve = context.validateNotEmpty(holder.name, holder.nameWrapper, translations.get(Constant.TranslationsKey.REQUIRED_VALUE_NAME))
        nameObserve.subscribe {
            holder.btnDone.isEnabled = it
        }
        holder.btnDone.setOnClickListener {
            onDoneClick?.invoke(item, holder)
        }
        holder.btnDelete.setOnClickListener {
            onDeleteClick?.invoke(item, holder)
        }
    }





    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val name: EditText = view.ed_name
        val phoneNumber: EditText = view.ed_phone
        val email: EditText = view.ed_mail
        val btnDone: ImageView = view.done_image
        val btnDelete: ImageView = view.exit_image
        val progressBar:ProgressBar = view.progress_bar
        val nameWrapper:TextInputLayout = view.ed_name_wrapper
    }
}
