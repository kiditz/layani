package com.overflow.cash.activity

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.overflow.cash.R
import com.overflow.cash.mvp.cashbox.SaveCashboxHistoryContract
import com.overflow.cash.mvp.cashbox.SaveCashboxHistoryPresenter
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.*
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import kotlinx.android.synthetic.main.activity_save_cashbank.*
import javax.inject.Inject

/**
 * This class used to create cash bank in or cash bank out by using radio group
 * @author Rifky Aditya Bastara
 * @since 22 Desember 2018
 * */
class SaveCashHistoryActivity : BaseActivity(), SaveCashboxHistoryContract.View {
    @Inject
    lateinit var presenter: SaveCashboxHistoryPresenter
    @Inject
    lateinit var networkExHandler: NetworkExHandler

    @Inject
    lateinit var translations: Translations

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_cashbank)
        presenter.attach(this)
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
        try {
            var temp = this.tv_result.text
            if(temp.isNotEmpty()) {
                temp = temp.substring(0, temp.length - 1)
            }
            val result = parseRupiah(temp)
            this.tv_result?.text = rupiah(result)
        }catch (e:NumberFormatException){
            //Just ignore when formating failed
        }
    }

    private fun getButtonIds() = arrayOf(btn_backspace, btn_clear, btn_0, btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9)

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_checked, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item!!.itemId){
            R.id.action_check -> {

                val message = if(rg_cash_type.checkedRadioButtonId == R.id.rd_cash_in){
                    getString(R.string.are_you_sure_cash_in).replace("{0}", tv_result.text.toString())
                }else{
                    getString(R.string.are_you_sure_cash_out).replace("{0}", tv_result.text.toString())
                }

                showMessage(getString(R.string.cash_in), message, object :MessageButtonHandle(){
                    override fun ok(dialog: DialogInterface, which: Int) {
                        super.ok(dialog, which)
                        saveCashboxHistory()
                    }
                }).show()
                false
            }
            else -> home(item)
        }
    }

    private fun saveCashboxHistory(){
        if(tv_result.text.isNotEmpty()){
            var amount = parseRupiah(this.tv_result.text)
            if(rg_cash_type.checkedRadioButtonId == R.id.rd_cash_out){
                amount *= -1
            }
            val remark = ed_remark.text.toString()
            val data = Data()
            data["remark"] = remark
            data["total_amount"] = amount
            this.presenter.saveCashboxHistory(data)
        }
    }

    override fun onCashboxSaved(data: Data) {
        showSuccessMessage(translations.get(Constant.TranslationsKey.CASHBOX_SAVED_SUCCESSFULLY))
    }

    override fun showEmpty() {
    }


    override fun showError(error: Throwable) {
        networkExHandler.errorHandle(this, error)
    }

    override fun showNoOk(res: String) {
        showErrorMessage(res)
    }

    override fun showNotConnected(res: String) {
        showErrorMessage(res)
    }

}