package com.overflow.cash.dagger

import com.overflow.cash.account.AccountAuthenticatorService
import com.overflow.cash.account.AccountSyncAdapterService
import com.overflow.cash.activity.*
import com.overflow.cash.activity.pulsa.CheckPaymentActivity
import com.overflow.cash.activity.pulsa.PayThePaymentActivity
import com.overflow.cash.fcm.LayaniFirebaseMessagingService
import com.overflow.cash.fragment.*
import com.overflow.cash.fragment.pulsa.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityBuilder {
    //Activity
    @ContributesAndroidInjector
    internal abstract fun bindMenuActivity(): MenuActivity

    @ContributesAndroidInjector
    internal abstract fun bindPaymentTransactionDispatcherActivity(): PaymentTransactionDispatcherActivity

    @ContributesAndroidInjector
    internal abstract fun bindCreateStoreActivity(): CreateStoreActivity

    @ContributesAndroidInjector
    internal abstract fun bindRegisterActivity(): RegisterActivity

    @ContributesAndroidInjector
    internal abstract fun bindLoginActivity(): LoginActivity

    @ContributesAndroidInjector
    internal abstract fun bindAddProductActivity(): SaveProductActivity

    @ContributesAndroidInjector
    internal abstract fun bindPaymentTransactionivityPaymentTransaction(): PaymentOtherActivity

    @ContributesAndroidInjector
    internal abstract fun bindPreviewSalesActivity(): SalesOrderPreviewActivity

    @ContributesAndroidInjector
    internal abstract fun bindProductDetailActivity(): ProductDetailActivity

    @ContributesAndroidInjector
    internal abstract fun bindCustomerChooserActivity(): CustomerChooserActivity

    @ContributesAndroidInjector
    internal abstract fun bindCustomerListAddActivity(): CustomerListAddActivity

    @ContributesAndroidInjector
    internal abstract fun bindReceiptActivity(): ReceiptActivity

    @ContributesAndroidInjector
    internal abstract fun bindCategoryListActivity(): CategoryListActivity

    @ContributesAndroidInjector
    internal abstract fun bindTransactionHistoryDetail(): TransactionHistoryDetailActivity

    @ContributesAndroidInjector
    internal abstract fun bindViewSavedOrderActivity(): ViewSavedOrderActivity

    @ContributesAndroidInjector
    internal abstract fun bindSaveCashHistoryActivity(): SaveCashHistoryActivity
    @ContributesAndroidInjector
    internal abstract fun bindCheckPaymentActivity(): CheckPaymentActivity
    @ContributesAndroidInjector
    internal abstract fun bindPayThePaymentActivity(): PayThePaymentActivity
    //Fragment
    @ContributesAndroidInjector
    internal abstract fun bindProductListFragment(): ProductListFragment

    @ContributesAndroidInjector
    internal abstract fun bindProductFragment(): ProductFragment

    @ContributesAndroidInjector
    internal abstract fun bindSalesListFragment(): SalesListFragment

    @ContributesAndroidInjector
    internal abstract fun bindPulsaCategoryListFragment(): PulsaCategoryListFragment

    @ContributesAndroidInjector
    internal abstract fun bindPulsaCheckPaymentFragment(): PulsaCheckPaymentFragment

    @ContributesAndroidInjector
    internal abstract fun bindPulsaProductListFragment(): PulsaProductListFragment

    @ContributesAndroidInjector
    internal abstract fun bindPulsaPaketProductListFragment(): PulsaPaketProductListFragment

    @ContributesAndroidInjector
    internal abstract fun bindPulsaProductsByProviderListFragment(): PulsaProductListByProviderFragment

    @ContributesAndroidInjector
    internal abstract fun bindPulsaProviderPaymentFragment(): PulsaProviderPaymentFragment

    @ContributesAndroidInjector
    internal abstract fun bindSalesFragment(): SalesFragment

    @ContributesAndroidInjector
    internal abstract fun bindSalesMainFragment(): SalesMainFragment

    @ContributesAndroidInjector
    internal abstract fun bindPaymentTransactionFragment(): PaymentTransactionFragment

    @ContributesAndroidInjector
    internal abstract fun bindCustomerFragment(): CustomerFragment

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
    internal abstract fun bindFragmentSetting(): SettingFragment

    @ContributesAndroidInjector
    internal abstract fun bindTransactionHistoryFragment(): TransactionHistoryFragment

    @ContributesAndroidInjector
    internal abstract fun bindReceiptFragment(): ReceiptFragment

    @ContributesAndroidInjector
    internal abstract fun bindOrderItemFragment(): OrderItemsFragment

    @ContributesAndroidInjector
    internal abstract fun bindSalesOtherFragment(): SalesOtherFragment

    @ContributesAndroidInjector
    internal abstract fun bindCashboxSummaryFragment(): CashboxSummaryFragment

    @ContributesAndroidInjector
    internal abstract fun bindCashboxReportView(): CashboxReportViewFragment

    @ContributesAndroidInjector
    internal abstract fun bindCashboxHistoryFragment(): CashboxHistoryFragment

    @ContributesAndroidInjector
    internal abstract fun bindCashboxHistoryDispatcherActivity(): CashboxHistoryDispatcherActivity

    @ContributesAndroidInjector
    internal abstract fun bindCashboxHistoryReceiptActivity(): CashboxHistoryReceiptActivity


    //Service
    @ContributesAndroidInjector
    internal abstract fun bindAuthenticatorService(): AccountAuthenticatorService

    @ContributesAndroidInjector
    internal abstract fun bindAccountSyncAdapterService(): AccountSyncAdapterService

    @ContributesAndroidInjector
    internal abstract fun bindLayaniFirebaseMessagingService(): LayaniFirebaseMessagingService


}
