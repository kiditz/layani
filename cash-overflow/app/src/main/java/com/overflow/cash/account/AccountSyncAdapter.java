package com.overflow.cash.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;

import com.overflow.cash.R;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class AccountSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final int SYNC_INTERVAL               = 60 * 30; // 30 Minutes
    public static final int SYNC_FLEXTIME               = SYNC_INTERVAL/3;
    private AccountManager accountManager;
    private SharedPreferences preferences;

    public AccountSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public void setAccountManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }


    public void setPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Timber.i("Perform Sync [%s]", account.name);
        try {
            String authToken = accountManager.peekAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
            accountManager.invalidateAuthToken(getContext().getString(R.string.account_type), authToken);
            authToken = accountManager.blockingGetAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, true);
            accountManager.setAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, authToken);
            Timber.d("Sync success");
        } catch (Exception e) {
            Timber.e(e, "Sync failed");
        }
    }

    private static void configurePeriodicSync(Account account, Context context, int syncInterval, int flexTime) {
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder()
                    .syncPeriodic(syncInterval, flexTime)
                    .setSyncAdapter(account, authority)
                    .setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }

    private static void syncImmediately(Account account, Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(account, context.getString(R.string.content_authority), bundle);
    }

    public static void syncAccount(Account account, Context context){
        configurePeriodicSync(account, context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(account, context.getString(R.string.content_authority), true);
        syncImmediately(account, context);
    }

    public void dispose(){
        //disposable.clear();
    }
}
