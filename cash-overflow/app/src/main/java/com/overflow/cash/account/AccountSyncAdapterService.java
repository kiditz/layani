package com.overflow.cash.account;

import android.accounts.AccountManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class AccountSyncAdapterService extends Service {
    @Inject
    SharedPreferences preferences;
    @Inject
    AccountManager accountManager;

    private static final Object sSyncAdapterLock = new Object();
    private static AccountSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidInjection.inject(this);
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new AccountSyncAdapter(getApplicationContext(), true);
                sSyncAdapter.setAccountManager(accountManager);
                sSyncAdapter.setPreferences(preferences);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sSyncAdapter.dispose();
    }
}
