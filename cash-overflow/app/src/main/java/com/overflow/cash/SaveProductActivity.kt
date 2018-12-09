package com.overflow.cash

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Base64
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.jakewharton.rxbinding2.widget.RxTextView
import com.overflow.cash.Constant.Companion.REQUEST_PERMISSION_CODE
import com.overflow.cash.Constant.TranslationsKey.Companion.CATEGORY_CREATED_SUCCESSFULLY
import com.overflow.cash.Constant.TranslationsKey.Companion.INIT_PRICE_MUST_GREATER_THAN_ZERO
import com.overflow.cash.Constant.TranslationsKey.Companion.REQUIRED_VALUE_CATEGORY_NAME
import com.overflow.cash.Constant.TranslationsKey.Companion.REQUIRED_VALUE_PRODUCT_CODE
import com.overflow.cash.Constant.TranslationsKey.Companion.REQUIRED_VALUE_PRODUCT_INIT_PRICE
import com.overflow.cash.Constant.TranslationsKey.Companion.REQUIRED_VALUE_PRODUCT_NAME
import com.overflow.cash.Constant.TranslationsKey.Companion.REQUIRED_VALUE_PRODUCT_QTY
import com.overflow.cash.Constant.TranslationsKey.Companion.REQUIRED_VALUE_PRODUCT_UNIT
import com.overflow.cash.Constant.TranslationsKey.Companion.SELL_PRICE_MUST_GREATER_THAN_ZERO
import com.overflow.cash.Constant.TranslationsKey.Companion.UNIT_MUST_LESS_THAN_THERR
import com.overflow.cash.mvp.product.AddProductContract
import com.overflow.cash.mvp.product.AddProductPresenter
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.utils.*
import com.overflow.libs.core.Data
import com.overflow.libs.core.Translations
import com.overflow.libs.picker.Constants
import com.overflow.libs.picker.DefaultCallback
import com.overflow.libs.picker.EasyImage
import com.yalantis.ucrop.UCrop
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.functions.Function7
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.dialog_add_category.view.*
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject

class SaveProductActivity : AppCompatActivity(), AddProductContract.View {
    lateinit var categoryAdapter: ArrayAdapter<String>
    @Inject
    lateinit var translations: Translations
    @Inject
    lateinit var networkExHandler: NetworkExHandler
    @Inject
    lateinit var presenter: AddProductPresenter
    @Inject
    lateinit var preferences: SharedPreferences
    private lateinit var outlet: Data
    private var categoryView: View? = null
    private var dialog: AlertDialog? = null
    private var categoryList = mutableListOf<Data>()
    private var categoryId: Long? = null
    private var productId: Long? = null
    private var imageBase64String: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        //Easy Image
        shouldRequestPermissions(REQUEST_PERMISSION_CODE)
        EasyImage.configuration(this).setImagesFolderName("Cash Overflow")

        presenter.attach(this)


        this.outlet = Data(preferences.getString("outlet", "{}"))

        this.btnAddCategory.setOnClickListener {
            addCategoryDialog()
        }

        if (intent.hasExtra("category_id")) {
            this.categoryId = intent.getLongExtra("category_id", -1L)
            this.edCategory?.setText(intent.getStringExtra("category_name"))
            this.edProductName?.requestFocus()
        }

        if (intent.hasExtra("product_id")) {
            supportActionBar?.setTitle(R.string.edit_product)
            this.productId = intent.getLongExtra("product_id", -1L)
            this.edProductCode.setText(intent.getStringExtra("product_code"))
            this.edProductName.setText(intent.getStringExtra("product_name"))
            this.edSellPrice.setText(intent.getDoubleExtra("sell_price", 0.0).toInt().toString())
            this.edInitPrice.setText(intent.getDoubleExtra("purchase_price", 0.0).toInt().toString())
            this.edQuantity.setText(intent.getLongExtra("stock", 1L).toInt().toString())
            this.edUnit.setText(intent.getStringExtra("unit"))
            if (intent.hasExtra("image")) {
                val image = intent.getByteArrayExtra("image")
                val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
                product_image?.setImageBitmap(bitmap)
            }
            this.cbUseStock.isChecked = intent.getBooleanExtra("use_stock", true)
            quantityWrapper.isEnabled = this.cbUseStock.isChecked
            unitWrapper.isEnabled = this.cbUseStock.isChecked
        } else {
            supportActionBar?.setTitle(R.string.add_product)
        }

        this.categoryAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mutableListOf<String>())

        edCategory.setAdapter(this.categoryAdapter)
        searchCategory()
        action()
        validate()
        imageAction()
    }

    private fun action() {
        edCategory.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val categoryName = categoryAdapter.getItem(position)
            if (getSelectedItem(categoryName) != null) {
                categoryId = getSelectedItem(categoryName)?.getLong("category_id")!!
            }
        }
        this.btnSubmit.isEnabled = false
        this.btnSubmit.setOnClickListener {
            this.handleAddProductAction()
        }

        this.btnScan.setOnClickListener {
            this.handleScanAction()
        }
    }

    private fun handleScanAction() {
        val intent = Intent(this, ScannerActivity::class.java)
        startActivityForResult(intent, Constant.REQUEST_CODE_SCANNER)
    }

    private fun handleAddProductAction() {
        runOnUiThread {
            val data = Data()
            data["name"] = edProductName.text.toString()
            data["code"] = edProductCode.text.toString()
            if (cbUseStock.isChecked) {
                //Validate use stock
                if(TextUtils.isEmpty(edQuantity.text)){
                    quantityWrapper.error = translations.get(Constant.TranslationsKey.REQUIRED_VALUE_PRODUCT_QTY)
                    quantityWrapper.isErrorEnabled = true
                    return@runOnUiThread
                }
                if(TextUtils.isEmpty(edUnit.text)){
                    quantityWrapper.error = translations.get(Constant.TranslationsKey.REQUIRED_VALUE_PRODUCT_UNIT)
                    quantityWrapper.isErrorEnabled = true
                    return@runOnUiThread
                }
                data["use_stock"] = true
                data["qty"] = edQuantity.text.toString().toLong()
                if(data.getLong("qty") <= 0){
                    quantityWrapper.error = translations.get(Constant.TranslationsKey.QTY_MUST_BE_GREATER_THAN_ZERO)
                    quantityWrapper.isErrorEnabled = true
                    return@runOnUiThread
                }
                data["product_type"] = "PRODUCT"
                data["unit"] = edUnit.text.toString()
            } else {
                data["use_stock"] = false
                data["product_type"] = "SERVICE"
            }



            data["sell_price"] = edSellPrice.text.toString().toDouble()
            data["purchase_price"] = edInitPrice.text.toString().toDouble()
            categoryId?.let {
                if (it != -1L) {
                    data["category_id"] = it
                } else {
                    data["category_id"] = null
                }
            }

            imageBase64String?.let {
                data["image"] = it
            }
            this.progress_bar.visibility = View.VISIBLE

            if (productId != null) {
                data["id"] = productId
                this.presenter.editProduct(data)
            } else {
                this.presenter.addProduct(data)
            }
        }
    }

    private fun imageAction() {
        if (!shouldRequestPermissions(Constant.REQUEST_PERMISSION_CODE)) {
            return
        }
        this.btnCamera.setOnClickListener {
            EasyImage.openCamera(this, Constant.REQUEST_CODE_IMAGE)
        }
        this.btnImage.setOnClickListener {
            EasyImage.openDocuments(this, Constant.REQUEST_CODE_IMAGE)
        }
        this.btnClose.setOnClickListener {

        }
    }

    @SuppressLint("InflateParams")
    private fun addCategoryDialog() {
        this.categoryView = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null, false)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.add_category)
        builder.setView(this.categoryView)
        this.dialog = builder.create()

        this.categoryView!!.btnSubmit.setOnClickListener {
            val category = Data()
            category["outlet_id"] = outlet.getLong("id")
            category["name"] = this.categoryView!!.edName.text.toString()
            presenter.addCategory(category)
            this.categoryView?.progress_bar!!.visibility = View.VISIBLE
        }
        validateAddCategory(this.categoryView!!)
        this.dialog?.show()
    }

    private fun validateAddCategory(view: View) {
        val resIdPrimary = R.style.AppTheme_TextInputLayout_ErrorPrimary
        this.validateNotEmpty(view.edName, view.nameWrapper, translations.get(REQUIRED_VALUE_CATEGORY_NAME), resIdPrimary).subscribe {
            view.btnSubmit.isEnabled = it
        }
    }

    private fun searchCategory() {
        RxTextView.textChanges(edCategory).map { text -> text }.subscribe { input ->
            val categoryData = Data()
            categoryData["name"] = input
            categoryData["page"] = 1
            categoryData["size"] = preferences.getInt("MAX_PAGE", 10)
            categoryData["outlet_id"] = outlet["id"]
            presenter.loadCategory(categoryData)
        }
    }

    override fun onCategoryCreated(category: Data) {
        dismissCategory()
        snack(translations.get(CATEGORY_CREATED_SUCCESSFULLY).replace("{0}", category.getString("name"))).show()
    }

    override fun showError(error: Throwable) {
        dismissCategory()
        dismissProduct()
        networkExHandler.errorHandle(this, error)
    }

    override fun showNoOk(res: String) {
        dismissCategory()
        dismissProduct()
        snack(res).show()
    }

    private fun getSelectedItem(query: String): Data? {
        for (index in 0 until categoryList.size) {
            if (categoryList[index].getString("category_name") == query) {
                return categoryList[index]
            }
        }
        return null
    }

    override fun showEmpty() {

    }

    override fun showNotConnected(res: String) {
        dismissCategory()
        dismissProduct()
        snack(res).show()
    }

    override fun onProductCreated(product: Data) {
        this.progress_bar.visibility = View.GONE
        this.btnSubmit.isEnabled = true
        val bundle = Bundle()
        bundle.putString(Constant.SUCCESS_MESSAGE, translations.get(Constant.TranslationsKey.PRODUCT_CREATED_SUCCESSFULLY).replace("{0}", product.getString("name")))
        bundle.putInt(Constant.GOTO, R.id.nav_product)
        moveTo(MenuActivity::class.java, bundle)

    }

    override fun onCategoryLoaded(categoryList: List<Data>) {
        this.categoryList.clear()
        this.categoryList.addAll(categoryList)
        categoryAdapter.clear()
        categoryList.map {
            categoryAdapter.add(it.getString("category_name"))
        }
        categoryAdapter.notifyDataSetChanged()
    }

    private fun dismissCategory() {
        if (dialog != null && dialog?.isShowing!!) {
            this.categoryView?.progress_bar?.visibility = View.GONE
            this.categoryView?.btnSubmit?.isEnabled = true
            this.dialog?.dismiss()
        }
    }

    private fun dismissProduct() {
        this.progress_bar?.visibility = View.GONE
        this.btnSubmit?.isEnabled = true
    }


    private fun validate() {
        val resIdPrimary = R.style.AppTheme_TextInputLayout_ErrorPrimary
        val productNameObserve = this.validateNotEmpty(edProductName, productNameWrapper, translations.get(REQUIRED_VALUE_PRODUCT_NAME), resIdPrimary, 0)
        val productCodeObserve = this.validateNotEmpty(edProductCode, productCodeWrapper, translations.get(REQUIRED_VALUE_PRODUCT_CODE), resIdPrimary, 0)
        var unitLengthObserve = Observable.just(true)
        var unitObserve = Observable.just(true)
        var qtyObserve = Observable.just(true)

        val sellPriceObserve = this.validateGreaterThan(edSellPrice, sellPriceWrapper, 0, translations.get(SELL_PRICE_MUST_GREATER_THAN_ZERO), resIdPrimary, 0)
        val initPriceObserve = this.validateNotEmpty(edInitPrice, initPriceWrapper, translations.get(REQUIRED_VALUE_PRODUCT_INIT_PRICE), resIdPrimary, 0)

        Observable.combineLatest(unitObserve, unitLengthObserve, productNameObserve, productCodeObserve, qtyObserve, sellPriceObserve, initPriceObserve,
                Function7 { unit: Boolean, unitLength: Boolean, productName: Boolean, productCode: Boolean, qty: Boolean, sellPrice: Boolean, initPrice: Boolean ->
                    unit && unitLength && productName && productCode && qty && sellPrice && initPrice
                }).subscribe { valid: Boolean -> btnSubmit.isEnabled = valid }

        cbUseStock.setOnCheckedChangeListener { _, isChecked ->
            quantityWrapper.isEnabled = isChecked
            unitWrapper.isEnabled = isChecked
            quantityWrapper.isErrorEnabled = false
            unitWrapper.isErrorEnabled = false
            unitLengthObserve = if (isChecked) {
                this.validateLengthLessThan(edUnit, unitWrapper, 4, translations.get(UNIT_MUST_LESS_THAN_THERR), resIdPrimary, 0)
            } else {
                Observable.just(true)
            }
            unitObserve = if (isChecked) {
                this.validateNotEmpty(edUnit, unitWrapper, translations.get(REQUIRED_VALUE_PRODUCT_UNIT), resIdPrimary, 0)
            } else {
                edUnit.setText(Constant.TEXT_EMPTY)
                Observable.just(true)
            }
            qtyObserve = if (isChecked) {
                this.validateGreaterThan(edQuantity, quantityWrapper, 0, translations.get(REQUIRED_VALUE_PRODUCT_QTY), resIdPrimary, 1)
            } else {
                edQuantity.setText(Constant.TEXT_EMPTY)
                Observable.just(true)
            }

            //Combine again to take effect when checked change
            Observable.combineLatest(unitObserve, unitLengthObserve, productNameObserve, productCodeObserve, qtyObserve, sellPriceObserve, initPriceObserve,
                    Function7 { unit: Boolean, unitLength: Boolean, productName: Boolean, productCode: Boolean, qty: Boolean, sellPrice: Boolean, initPrice: Boolean ->
                        unit && unitLength && productName && productCode && qty && sellPrice && initPrice
                    }).subscribe { valid: Boolean -> btnSubmit.isEnabled = valid }
        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.RequestCodes.TAKE_PICTURE || requestCode == Constants.RequestCodes.PICK_PICTURE_FROM_DOCUMENTS) {
                EasyImage.handleActivityResult(requestCode, resultCode, data, this, object : DefaultCallback() {
                    override fun onImagesPicked(imageFiles: MutableList<File>, source: EasyImage.ImageSource?, type: Int) {
                        Timber.i(imageFiles.toString())
                        cropImage(imageFiles[0])
                    }

                    override fun onImagePickerError(e: Exception?, source: EasyImage.ImageSource?, type: Int) {
                        Timber.e(e)
                    }
                })
            } else if (requestCode == UCrop.REQUEST_CROP) {
                val resultUri = UCrop.getOutput(data!!)
                if (resultUri == null) {

                } else {
                    showImage(resultUri)
                }
            } else if (requestCode == Constant.REQUEST_CODE_SCANNER) {
                edProductCode.setText(data?.getStringExtra("barcode"))
            }
        }
    }

    private fun showImage(uri: Uri) {
        val file = uri.path
        val bitmap = BitmapFactory.decodeFile(file)
        product_image?.setImageBitmap(bitmap)
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        this.imageBase64String = Base64.encodeToString(bytes.toByteArray(), Base64.NO_WRAP)
    }

    private fun cropImage(file: File) {
        val uri = Uri.fromFile(file)
        val dest = File("${Environment.getExternalStorageDirectory()}/${getString(R.string.app_name)}", file.name)
        if (!dest.isDirectory) {
            dest.parentFile.mkdirs()
        }
        try {
            val options = UCrop.Options()
            options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
            options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
            options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.colorAccent))
            UCrop.of(uri, Uri.fromFile(dest)).withOptions(options).start(this)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }


    override fun onCategorySelected(category: Data) {
        this.categoryId = category.getLong("id")
        this.edCategory.setText(category.getString("name"))
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return home(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.presenter.detach()
    }
}
