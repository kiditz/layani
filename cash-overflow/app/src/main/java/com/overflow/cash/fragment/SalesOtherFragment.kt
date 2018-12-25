package com.overflow.cash.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.overflow.cash.R
import com.overflow.cash.activity.Constant
import com.overflow.cash.utils.rupiah
import kotlinx.android.synthetic.main.fragment_cashbank_out.*

/**
 * @author Rifky Aditya Bastara
 * */
class SalesOtherFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cashbank_out, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Make numpad can be write into tv_result
        writeValueNumpad()
    }

    @SuppressLint("SetTextI18n")
    private fun addDigit(value: Int) {
        val currentVal = tv_result.text.toString().replace(Regex("[^0-9]"), "")
        this.tv_result.text = rupiah("$currentVal$value".toDouble())
    }

    private fun clearValues() {
        this.tv_result.text = Constant.TEXT_EMPTY
        addDigit(0)
    }

    private fun writeValueNumpad() {
        getButtonIds().forEach {
            it.setOnClickListener {
                when (it.id) {
                    R.id.btn_clear -> clearValues()
                    R.id.btn_backspace -> backSpace()
                    R.id.btn_0 -> addDigit(0)
                    R.id.btn_1 -> addDigit(1)
                    R.id.btn_2 -> addDigit(2)
                    R.id.btn_3 -> addDigit(3)
                    R.id.btn_4 -> addDigit(4)
                    R.id.btn_5 -> addDigit(5)
                    R.id.btn_6 -> addDigit(6)
                    R.id.btn_7 -> addDigit(7)
                    R.id.btn_8 -> addDigit(8)
                    R.id.btn_9 -> addDigit(9)
                }
            }
        }
    }

    private fun backSpace() {
        var temp = this.tv_result.text
        if(temp.isNotEmpty()) {
            temp = temp.substring(0, temp.length - 1)
        }
        this.tv_result.text = temp
    }

    private fun getButtonIds() = arrayOf(btn_backspace, btn_clear, btn_0, btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9)

    override fun onDetach() {
        super.onDetach()
    }


}