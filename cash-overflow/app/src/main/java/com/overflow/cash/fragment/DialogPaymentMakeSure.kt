package com.overflow.cash.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import com.overflow.cash.R
import com.overflow.cash.activity.Constant
import com.overflow.cash.utils.rupiah
import kotlinx.android.synthetic.main.dialog_pay.view.*


class DialogPaymentMakeSure: DialogFragment(){
    var onDoneClick:((View) -> Unit)? =null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_pay, null, false)
        val dialog = AlertDialog.Builder(context).setView(view).create()
        arguments?.let {
            view.tv_total_payment.text = rupiah(it.getDouble(ARG_TOTAL_PAYMENT))
            view.tv_cashback.text = rupiah(it.getDouble(ARG_CASH_BACK))
            view.tv_pay_type.text = getString(R.string.cashback)
            dialog.setTitle(it.getString(ARG_TITLE))
        }

        view.btn_pay.setOnClickListener {
            dialog.dismiss()
            onDoneClick?.invoke(it)
        }
        return dialog

    }
    companion object {
        const val ARG_TOTAL_PAYMENT = "total_amount"
        const val ARG_CASH_BACK = "cashback"
        const val ARG_TITLE = "title"
        @JvmStatic
        fun newInstance(totalPayment: Double, cashBack:Double, title:String=Constant.TEXT_EMPTY) =
                DialogPaymentMakeSure().apply {
                    arguments = Bundle().apply {
                        putDouble(ARG_TOTAL_PAYMENT, totalPayment)
                        putDouble(ARG_CASH_BACK, cashBack)
                        putString(ARG_TITLE, title)
                    }
                }
    }
}