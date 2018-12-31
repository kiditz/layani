package com.overflow.cash.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import com.jakewharton.rxbinding2.widget.RxTextView
import com.overflow.cash.R
import com.overflow.cash.utils.parseRupiah
import com.overflow.cash.utils.rupiah
import com.overflow.libs.core.Data
import com.overflow.libs.core.DateUtil
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.dialog_order_summary.view.*
import timber.log.Timber
import java.util.*


class DialogOrderSummary: DialogFragment(){
    var onDoneClick:((Data) -> Unit)? =null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_order_summary, null, false)
        val dialog = AlertDialog.Builder(context).setView(view).create()
        val data = Data()
        data["end_at"] = DateUtil.printDefaultDateTime(Date())

        arguments?.let {
            Timber.i("DATA : %s", it.toString())
            val amount = it.getDouble("success") + it.getDouble("in_progress")
            view.tv_card_should_be.text = rupiah(it.getDouble("card"))
            view.tv_cash_should_be.text = rupiah(it.getDouble("cash") + it.getDouble("cash_in") + it.getDouble("cash_out") - it.getDouble("void"))
            view.tv_total_should_be.text = rupiah(amount + it.getDouble("cash_in") + it.getDouble("cash_out") - it.getDouble("void"))
            data["sales"] = amount
            data["void"] = it.getDouble("void")
            data["pending"] = it.getDouble("created")
            data["id"] = it.getLong("id")
        }
        validate(view)
        view.btn_save.setOnClickListener{
            data["card"] = parseRupiah(view.ed_card.text.toString())
            data["cash"] = parseRupiah(view.ed_cash.text.toString())
            this.onDoneClick?.invoke(data)
        }
        return dialog
    }

    private fun validate(view:View){
        val cardObserve = RxTextView.textChanges(view.ed_card).skipInitialValue().map { text ->
            activity!!.runOnUiThread {
                try {
                    view.tv_total_amount.text = rupiah(parseRupiah(view.ed_cash.text) + parseRupiah(text))
                }catch (e:Exception){}
            }
            text.toString().isNotEmpty()
        }

        cardObserve.subscribe({res ->
            view.card_wrapper.error = getString(R.string.required_value_card)
            view.card_wrapper.isErrorEnabled = !res

        }, {})

        val cashObserve = RxTextView.textChanges(view.ed_cash).skipInitialValue().map { text ->
            activity!!.runOnUiThread {
                try {
                    view.tv_total_amount.text = rupiah(parseRupiah(text))
                }catch (e:Exception){}
            }
            text.toString().isNotEmpty()
        }
        cashObserve.subscribe({ res ->
            view.cash_wrapper.error = getString(R.string.required_value_cash)
            view.cash_wrapper.isErrorEnabled = !res
        }, {})

        // Can be error NumberFormat when backspace clicked until string is ''
        Observable.combineLatest(cashObserve, cardObserve, BiFunction{ isCash:Boolean, isCard:Boolean -> isCash && isCard}).subscribe ({
//            activity!!.runOnUiThread {
//                view.tv_total_amount.text = rupiah(parseRupiah(view.ed_cash.text) + parseRupiah(view.ed_card.text))
//            }
            view.btn_save.isEnabled = it
        }, {
            Timber.e(it)
        })
    }

}