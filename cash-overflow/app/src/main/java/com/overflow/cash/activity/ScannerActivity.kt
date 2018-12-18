package com.overflow.cash.activity

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.zxing.Result
import com.overflow.cash.R
import com.overflow.cash.utils.MessageButtonHandle
import com.overflow.cash.utils.home
import com.overflow.cash.utils.shouldRequestPermissions
import com.overflow.cash.utils.showMessage
import kotlinx.android.synthetic.main.menu_switch_layout.view.*
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScannerActivity:AppCompatActivity(), ZXingScannerView.ResultHandler {
    lateinit var scannerView :ZXingScannerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.scannerView = ZXingScannerView(this)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        setContentView(scannerView)
        shouldRequestPermissions(Constant.REQUEST_PERMISSION_CODE)
    }

    private fun initScanner(){
        scannerView.setResultHandler(this)
        scannerView.startCamera()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_scanner, menu)
        val flashItem = menu!!.findItem(R.id.action_flash)
        flashItem.actionView?.switcher?.setText(R.string.flash)
        flashItem.actionView?.switcher?.setOnCheckedChangeListener { _, isChecked ->
            this.scannerView.flash = isChecked
        }

        val focusItem = menu.findItem(R.id.action_focus)
        focusItem.actionView?.switcher?.isChecked = true
        this.scannerView.setAutoFocus(true)
        focusItem.actionView?.switcher?.setText(R.string.focus)
        focusItem.actionView?.switcher?.setOnCheckedChangeListener { _, isChecked ->
            this.scannerView.setAutoFocus(isChecked)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        return home(item!!)
    }

    override fun onResume() {
        super.onResume()
        initScanner()
    }

    override fun onPause() {
        super.onPause()
        scannerView.stopCamera()
    }

    override fun handleResult(result: Result?) {
        if (result != null){
            try {
                val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val ringtone = RingtoneManager.getRingtone(this, uri)
                ringtone.play()
                val intent = Intent()
                intent.putExtra("barcode", result.text)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }catch (e:Exception){
                showMessage(getString(R.string.scan_barcode), getString(R.string.cannot_read_barcode), MessageButtonHandle())
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == Constant.REQUEST_PERMISSION_CODE){
                initScanner()
            }
        }
    }
}