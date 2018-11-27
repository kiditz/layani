package com.overflow.cash.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import com.overflow.cash.net.AccountService;

public class AccountAuthenticatorService extends Service{

    @Inject
    AccountService accountService;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        AndroidInjection.inject(this);
        AccountAuthenticator accountAuthenticator = null;
        try {
            accountAuthenticator = new AccountAuthenticator(this);
            accountAuthenticator.setAccountService(accountService);
            return accountAuthenticator.getIBinder();
        }catch (Exception ignore){
            return null;
        }

    }
}
