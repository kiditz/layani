package com.overflow.cash.dagger

import android.accounts.AccountManager
import android.content.Context
import com.overflow.cash.dagger.NetworkModule.Companion.AUTHENTICATED
import com.overflow.cash.dagger.NetworkModule.Companion.LOGIN
import com.overflow.cash.net.*
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named

@Module
class RetrofitModule {

    @Provides
    fun provideAccountService(@Named(LOGIN) retrofit: Retrofit): AccountService {
        return retrofit.create(AccountService::class.java)
    }

    @Provides
    fun provideProductService(@Named(AUTHENTICATED) retrofit: Retrofit): ProductService {
        return retrofit.create(ProductService::class.java)
    }

    @Provides
    fun provideMerchantService(@Named(AUTHENTICATED) retrofit: Retrofit): MerchantService {
        return retrofit.create(MerchantService::class.java)
    }

    @Provides
    fun provideImageService(context: Context, accountManager: AccountManager): ImageService {
        return ImageService(context, accountManager)
    }

    @Provides
    fun provideCashBoxService(@Named(AUTHENTICATED) retrofit: Retrofit): CashBoxService {
        return retrofit.create(CashBoxService::class.java)
    }

    @Provides
    fun provideOrderService(@Named(AUTHENTICATED) retrofit: Retrofit): OrderService {
        return retrofit.create(OrderService::class.java)
    }

    @Provides
    fun provideCustomerService(@Named(AUTHENTICATED) retrofit: Retrofit): CustomerService {
        return retrofit.create(CustomerService::class.java)
    }

}