package com.overflow.cash.dagger

import com.overflow.cash.realm.OrderItemRealm
import com.overflow.cash.realm.OrderRealm
import dagger.Module
import dagger.Provides
import io.realm.Realm

@Module
class RealmModule {


    @Provides
    internal fun provideOrderItemRealm() : OrderItemRealm{
        return OrderItemRealm(Realm.getDefaultInstance())
    }

    @Provides
    internal fun provideOrderRealm() : OrderRealm{
        return OrderRealm(Realm.getDefaultInstance())
    }
}