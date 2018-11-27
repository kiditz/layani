package com.overflow.cash.dagger

import com.overflow.cash.*
import com.overflow.cash.account.AccountAuthenticatorService
import com.overflow.cash.account.AccountSyncAdapterService
import com.overflow.cash.fragment.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityBuilder {
    //Activity
    @ContributesAndroidInjector
    internal abstract fun bindMenuActivity(): MenuActivity

    @ContributesAndroidInjector
    internal abstract fun bindCreateStoreActivity(): CreateStoreActivity

    @ContributesAndroidInjector
    internal abstract fun bindRegisterActivity(): RegisterActivity

    @ContributesAndroidInjector
    internal abstract fun bindLoginActivity(): LoginActivity

    @ContributesAndroidInjector
    internal abstract fun bindAddProductActivity(): SaveProductActivity

    @ContributesAndroidInjector
    internal abstract fun bindAccountReceiveablePaymentActivity(): PaymentAccountReceiveableActivity

    @ContributesAndroidInjector
    internal abstract fun bindPaymentTransactionivityPaymentTransaction():PaymentTransactionActivity

    @ContributesAndroidInjector
    internal abstract fun bindPreviewSalesActivity(): PreviewSalesActivity

    @ContributesAndroidInjector
    internal abstract fun bindProductDetailActivity(): ProductDetailActivity

    @ContributesAndroidInjector
    internal abstract fun bindCustomerChooserActivity(): CustomerChooserActivity

    @ContributesAndroidInjector
    internal abstract fun bindReceiptActivity(): ReceiptActivity

    @ContributesAndroidInjector
    internal abstract fun bindReceiptAccountReceiveableActivity(): ReceiptAccountReceiveableActivity

    @ContributesAndroidInjector
    internal abstract fun bindAccountReceiveableDetailActivity(): AccountReceiveableDetailActivity

    //Fragment
    @ContributesAndroidInjector
    internal abstract fun bindProductListFragment(): ProductListFragment

    @ContributesAndroidInjector
    internal abstract fun bindProductFragment(): ProductFragment

    @ContributesAndroidInjector
    internal abstract fun bindSalesListFragment(): SalesListFragment

    @ContributesAndroidInjector
    internal abstract fun bindSalesFragment(): SalesFragment

    @ContributesAndroidInjector
    internal abstract fun bindCustomerFragment(): CustomerFragment

    @ContributesAndroidInjector
    internal abstract fun bindAccountReceiveableFragment(): AccountReceiveableFragment

    @ContributesAndroidInjector
    internal abstract fun bindOrderChartFragment(): OrderChartFragment

    @ContributesAndroidInjector
    internal abstract fun bindProfitChartFragment(): ProfitChartFragment

    @ContributesAndroidInjector
    internal abstract fun bindIncomeChartFragment(): IncomeChartFragment

    @ContributesAndroidInjector
    internal abstract fun bindTopProductFragment(): TopProductFragment

    @ContributesAndroidInjector
    internal abstract fun bindDashboardHeaderFragment(): DashboardHeaderFragment

    @ContributesAndroidInjector
    internal abstract fun bindDashboardFragment(): DashboardFragment

    @ContributesAndroidInjector
    internal abstract fun bindAccountReceiveableChartInAgeFragment(): AccountReceiveableChartInAgeFragment

    @ContributesAndroidInjector
    internal abstract fun bindAccountReceiveableChartOutOfAgeFragment(): AccountReceiveableChartOutOfAgeFragment

    //Service
    @ContributesAndroidInjector
    internal abstract fun bindAuthenticatorService(): AccountAuthenticatorService

    @ContributesAndroidInjector
    internal abstract fun bindAccountSyncAdapterService(): AccountSyncAdapterService


}
