package com.overflow.cash.activity

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.BroadcastReceiver
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.overflow.cash.BuildConfig
import com.overflow.cash.dagger.AppComponent
import com.overflow.cash.dagger.DaggerAppComponent
import dagger.android.*
import io.realm.Realm
import timber.log.Timber
import javax.inject.Inject
import io.realm.RealmConfiguration



class RootApplication : Application(), HasActivityInjector, HasBroadcastReceiverInjector, HasServiceInjector {
    @Inject
    lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>
    @Inject
    lateinit var serviceDispatchingAndroidInjector: DispatchingAndroidInjector<Service>
    @Inject
    lateinit var broadcastReceiverInjector: DispatchingAndroidInjector<BroadcastReceiver>
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var appComponent: AppComponent


    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(config)
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        this.sharedPreferences.edit().putInt("MAX_PAGE", 10).apply()
        appComponent = DaggerAppComponent.builder().application(this).build()
        appComponent.inject(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun activityInjector(): AndroidInjector<Activity>? {
        return activityDispatchingAndroidInjector
    }

    override fun broadcastReceiverInjector(): AndroidInjector<BroadcastReceiver>? {
        return broadcastReceiverInjector
    }

    override fun serviceInjector(): AndroidInjector<Service>? {
        return serviceDispatchingAndroidInjector
    }
}
