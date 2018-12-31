package com.overflow.cash.activity

import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.iid.FirebaseInstanceId
import com.jaeger.library.StatusBarUtil
import com.jakewharton.rxbinding2.widget.RxTextView
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.overflow.cash.R
import com.overflow.cash.fragment.*
import com.overflow.cash.mvp.menu.FirebaseTokenContract
import com.overflow.cash.mvp.menu.FirebaseTokenPresenter
import com.overflow.cash.net.ImageService
import com.overflow.cash.net.RxUtils
import com.overflow.cash.utils.replaceContent
import com.overflow.cash.utils.snack
import com.overflow.libs.core.Data
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.nav_header_menu.view.*
import kotlinx.android.synthetic.main.success_message.*
import kotlinx.android.synthetic.main.toolbar.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * @author Rifky Aditya Bastara
 * @since 15 December 2018 22:49
 * */
class MenuActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, HasSupportFragmentInjector, FirebaseTokenContract.View {


    lateinit var navHeaderView: View
    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var presenter: FirebaseTokenPresenter
    @Inject
    lateinit var preferences: SharedPreferences
    @Inject
    lateinit var imageService: ImageService
    var search: MaterialSearchView? = null
    private var countBackPressed = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        this.presenter.attach(this)
        setContentView(R.layout.activity_menu)
        StatusBarUtil.setColorForDrawerLayout(this, drawer_layout, ContextCompat.getColor(this, android.R.color.transparent))
        this.search = search_view
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.report_outlet)
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        this.navHeaderView = nav_view.getHeaderView(0)
        bindHeader()


    }

    private fun bindHeader() {
        val accountManager = AccountManager.get(this)
        val accountType = accountManager.getAccountsByType(getString(R.string.account_type))
        val outlet = Data(preferences.getString("outlet", "{}"))
        this.navHeaderView.tv_business_name.text = outlet.getString("business_name")
        this.navHeaderView.tv_outlet_name.text = outlet.getString("name")
        this.navHeaderView.tv_fullname.text = outlet.getString("fullname")

        if (!outlet.getString("business_name").isEmpty())
            this.imageService.loadDocument(this.navHeaderView.iv_business, outlet.getLong("document_id"), outlet.getString("business_name"))

        if (accountType == null || accountType.isEmpty() && outlet.isEmpty()) {
            onNotLogin()
            return
        }
        if (intent.hasExtra(Constant.SUCCESS_MESSAGE)) {
            if (intent.hasExtra(Constant.GOTO)) {
                this.onNavigationItemSelected(nav_view.menu.findItem(intent.getIntExtra(Constant.GOTO, R.id.nav_report)))
            }
            tv_success_message?.visibility = View.VISIBLE
            tv_success_message?.text = intent.getStringExtra(Constant.SUCCESS_MESSAGE)
            RxTextView.textChanges(tv_success_message).compose(RxUtils.applyObservableAsync()).debounce(5, TimeUnit.SECONDS).subscribe {
                runOnUiThread {
                    tv_success_message?.visibility = View.GONE
                }
            }
            this.intent.removeExtra(Constant.SUCCESS_MESSAGE)
        } else {
            onNavigationItemSelected(nav_view.menu.findItem(R.id.nav_new_transaction))
        }
        // Saving firebase token
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            val token = it.token
            presenter.saveToken(token)
        }
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if (countBackPressed == 1) {
                super.onBackPressed()
            } else {
                snack(getString(R.string.press_again_to_close)).show()
                countBackPressed++
            }

        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        supportActionBar?.title = item.title
        nav_view.setCheckedItem(item.itemId)
        when (item.itemId) {
            R.id.nav_report -> {
                replaceContent(DashboardFragment())
            }
            R.id.nav_product -> {
                val productListFragment = ProductListFragment()
                replaceContent(productListFragment)
            }
            R.id.nav_new_transaction -> {
                //val salesListFragment = SalesListFragment()
                replaceContent(SalesMainFragment())

            }
            R.id.nav_customer -> {
                replaceContent(CustomerFragment())
            }

            R.id.nav_settings -> {
                replaceContent(SettingFragment())
            }
            R.id.nav_transaction_history -> {
                replaceContent(TransactionHistoryFragment.newInstance(true))
            }
            R.id.nav_cash_summary -> {
                replaceContent(CashboxSummaryFragment())
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }

    fun onNotLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivityForResult(intent, Constant.REQUEST_LOGIN)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_LOGIN) {
            if (resultCode == Activity.RESULT_CANCELED) {
                Timber.i("LOGIN CANCEL")
                finish()
            }
            if (resultCode == Activity.RESULT_OK) {
                Timber.i("LOGIN SUCCESS")
                bindHeader()
            }
        }

    }

    override fun showNoOk(res: String) {
        if (res == Constant.TranslationsKey.USER_NOT_FOUND) {
            onNotLogin()
        }
    }

    override fun showNotConnected(res: String) {
        snack(res).show()
    }

    override fun showError(error: Throwable) {

    }


    override fun showEmpty() {
    }

    // Moving fragment menu
    fun goTo(menuId: Int) {
        onNavigationItemSelected(this.nav_view.menu.findItem(menuId))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //menuInflater.inflate(R.menu.menu, menu)
        return false
    }
    override fun onTokenSaved(data: Data) {
        Timber.i("TOKEN DATA : %s", data.toString())
    }

}
