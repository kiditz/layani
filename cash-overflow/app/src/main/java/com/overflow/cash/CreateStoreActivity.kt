package com.overflow.cash

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Patterns
import android.view.MenuItem
import com.jakewharton.rxbinding2.widget.RxTextView
import com.overflow.cash.utils.home
import com.overflow.cash.utils.moveTo
import com.overflow.cash.utils.validateNotEmpty
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.functions.Function3
import kotlinx.android.synthetic.main.activity_create_store.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CreateStoreActivity : AppCompatActivity() {
    @Inject
    lateinit var translations: Translations

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_create_store)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        validate()
        btnNext.setOnClickListener {
            val data = Data()
            data["name"] = edStoreName.text.toString()
            data["address"] = edAddress.text.toString()
            data["email"] = edMail.text.toString()
            data["phone_number"] = edPhone.text.toString()
            val bundle = Bundle()
            bundle.putString("store", data.toString())
            moveTo(RegisterActivity::class.java, bundle)
        }

//        RxTextView.afterTextChangeEvents(edStoreName).debounce(200, TimeUnit.MILLISECONDS).subscribe {
//            runOnUiThread {
//                edStoreName.setText(it.editable().reversed().toString().toUpperCase())
//            }
//        }
    }

    private fun validate(){
        //val phoneObserve = this.validateNotEmpty(edPhone, phoneWrapper,translations.get(Constant.TranslationsKey.REQUIRED_VALUE_STORE_PHONE_NUMBER), skipCount = 0)
        val storeNameObserve = this.validateNotEmpty(edStoreName, storeNameWrapper, translations.get(Constant.TranslationsKey.REQUIRED_VALUE_STORE_NAME),skipCount = 0)
        //val mailObserve = this.validateNotEmpty(edMail, mailWrapper, translations.get(Constant.TranslationsKey.REQUIRED_VALUE_STORE_EMAIL),skipCount = 0)
        val phoneRegexObserve = RxTextView.textChanges(edPhone).map { text -> !Patterns.PHONE.matcher(text).matches() }.skip(1).distinctUntilChanged()

        phoneRegexObserve.subscribe { isValid ->
            phoneWrapper.error = translations.get(Constant.TranslationsKey.INVALID_PHONE_NUMBER)
            phoneWrapper.isErrorEnabled = isValid
        }

        val mailRegexObserve = RxTextView.textChanges(edPhone).map { text -> !Patterns.PHONE.matcher(text).matches() }.skip(1).distinctUntilChanged()

        mailRegexObserve.subscribe { isValid ->
            mailWrapper.error = translations.get(Constant.TranslationsKey.INVALID_EMAIL)
            mailWrapper.isErrorEnabled = isValid
        }



        Observable.combineLatest(phoneRegexObserve, storeNameObserve, mailRegexObserve,
                Function3{  phoneRegexInvalid:Boolean, storeNameInvalid:Boolean, mailInvalid:Boolean ->
                !phoneRegexInvalid && storeNameInvalid && !mailInvalid}).distinctUntilChanged()
                .subscribe{valid:Boolean -> btnNext.isEnabled = valid}
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return home(item!!)
    }
}
