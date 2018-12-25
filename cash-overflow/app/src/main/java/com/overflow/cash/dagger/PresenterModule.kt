package com.overflow.cash.dagger

import android.accounts.AccountManager
import android.content.Context
import android.content.SharedPreferences
import com.overflow.cash.mvp.cashbox.*
import com.overflow.cash.mvp.chart.*
import com.overflow.cash.mvp.customer.LoadCustomerPresenter
import com.overflow.cash.mvp.chart.DashboardHeaderPresenter
import com.overflow.cash.mvp.customer.EditCustomerPresenter
import com.overflow.cash.mvp.discount.LoadDiscountByBillAmountPresenter
import com.overflow.cash.mvp.discount.LoadDiscountByQuantityPresenter
import com.overflow.cash.mvp.login.LoginPresenter
import com.overflow.cash.mvp.menu.MenuPresenter
import com.overflow.cash.mvp.order.*
import com.overflow.cash.mvp.order.SaveOrderPresenter
import com.overflow.cash.mvp.product.*
import com.overflow.cash.mvp.receiveable.AccountReceiveableDetailPresenter
import com.overflow.cash.mvp.receiveable.AccountReceiveablePaymentPresenter
import com.overflow.cash.mvp.receiveable.AccountReceiveablePresenter
import com.overflow.cash.mvp.register.RegisterPresenter
import com.overflow.cash.net.*
import com.overflow.cash.realm.OrderItemRealm
import com.overflow.libs.core.Translations
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

/**
 * @author Rifky Aditya Bastara
 * @since 15 December 2018 22:49
 * */
@Module
class PresenterModule {
    @Provides
    internal fun provideMenuPresenter(context: Context, accountManager: AccountManager, disposable: CompositeDisposable): MenuPresenter {
        return MenuPresenter(context, accountManager, disposable)
    }

    @Provides
    internal fun provideRegisterPresenter(context: Context,translations: Translations, disposable: CompositeDisposable, accountService: AccountService): RegisterPresenter {
        return RegisterPresenter(context, translations, disposable, accountService)
    }
    @Provides
    internal fun provideLoginPresenter(context: Context, translations: Translations, disposable: CompositeDisposable, accountService: AccountService, outletService: OutletService, preferences: SharedPreferences, accountManager: AccountManager): LoginPresenter {
        return LoginPresenter(context, translations, disposable, accountService, outletService, preferences, accountManager)
    }



    @Provides
    internal fun provideLoadProductPresenter(context: Context,translations: Translations, disposable: CompositeDisposable, productService: ProductService, preferences: SharedPreferences): LoadProductPresenter {
        return LoadProductPresenter(context, translations, disposable, productService, preferences)
    }


    @Provides
    internal fun provideAddProductPresenter(context: Context,translations: Translations, disposable: CompositeDisposable, productService: ProductService, preferences: SharedPreferences): AddProductPresenter {
        return AddProductPresenter(context, translations, disposable, productService, preferences)
    }

    @Provides
    internal fun provideEditProductPresenter(context: Context,translations: Translations, disposable: CompositeDisposable, productService: ProductService, preferences: SharedPreferences): EditProductPresenter {
        return EditProductPresenter(context, translations, disposable, productService, preferences)
    }

    //Category
    @Provides
    internal fun provideLoadCategoryPresenter(context: Context, translations: Translations, disposable: CompositeDisposable, productService: ProductService, orderItemRealm: OrderItemRealm, preferences: SharedPreferences): LoadCategoryPresenter {
        return LoadCategoryPresenter(context, translations, disposable, productService, preferences)
    }

    @Provides
    internal fun provideFindCategoryPresenter(context: Context, translations: Translations, disposable: CompositeDisposable, productService: ProductService, orderItemRealm: OrderItemRealm, preferences: SharedPreferences): FindCategoryPresenter {
        return FindCategoryPresenter(context, translations, disposable, productService, preferences)
    }

    @Provides
    internal fun provideAddCategoryPresenter(context: Context,translations: Translations, disposable: CompositeDisposable, productService: ProductService, preferences: SharedPreferences): AddCategoryPresenter {
        return AddCategoryPresenter(context, translations, disposable, productService, preferences)
    }

    @Provides
    internal fun provideEditCategoryPresenter(context: Context,translations: Translations, disposable: CompositeDisposable, productService: ProductService, preferences: SharedPreferences): EditCategoryPresenter {
        return EditCategoryPresenter(context, translations, disposable, productService, preferences)
    }

    @Provides
    internal fun provideDeleteCategoryPresenter(context: Context,translations: Translations, disposable: CompositeDisposable, productService: ProductService, preferences: SharedPreferences): DeleteCategoryPresenter {
        return DeleteCategoryPresenter(context, translations, disposable, productService, preferences)
    }
    //End Category
    @Provides
    internal fun provideProductDetailPresenter(context: Context,translations: Translations, disposable: CompositeDisposable, productService: ProductService, preferences: SharedPreferences): ProductDetailPresenter {
        return ProductDetailPresenter(context, translations, disposable, productService, preferences)
    }


    @Provides
    internal fun providePaymentTransactionPresenter(context: Context, orderItemRealm: OrderItemRealm, translations: Translations, disposable: CompositeDisposable, orderService: OrderService, preferences: SharedPreferences): SaveOrderPresenter {
        return SaveOrderPresenter(context, orderItemRealm, preferences, translations, orderService, disposable)
    }

    @Provides
    internal fun provideLoadCashboxSummaryPresenter(context: Context, translations: Translations, disposable: CompositeDisposable, cashboxService: CashBoxService, preferences: SharedPreferences): LoadCashboxSummaryPresenter {
        return LoadCashboxSummaryPresenter(context, preferences, translations, cashboxService, disposable)
    }

    @Provides
    internal fun provideLoadCashboxHistoryPresenter(context: Context, translations: Translations, disposable: CompositeDisposable, cashboxService: CashBoxService, preferences: SharedPreferences): LoadCashboxHistoryPresenter {
        return LoadCashboxHistoryPresenter(context, preferences, translations, cashboxService, disposable)
    }

    @Provides
    internal fun provideSaveCashboxHistoryPresenter(context: Context, translations: Translations, disposable: CompositeDisposable, cashboxService: CashBoxService, preferences: SharedPreferences): SaveCashboxHistoryPresenter {
        return SaveCashboxHistoryPresenter(context, preferences, translations, cashboxService, disposable)
    }

    @Provides
    internal fun provideSaveCashboxSummaryPresenter(context: Context, translations: Translations, disposable: CompositeDisposable, cashboxService: CashBoxService, preferences: SharedPreferences): SaveCashboxSummaryPresenter {
        return SaveCashboxSummaryPresenter(context, preferences, translations, cashboxService, disposable)
    }

    @Provides
    internal fun provideSummaryOrderPresenter(context: Context, translations: Translations, disposable: CompositeDisposable, orderService: OrderService, preferences: SharedPreferences): SummaryOrderPresenter {
        return SummaryOrderPresenter(context, preferences, translations, orderService, disposable)
    }

    @Provides
    internal fun provideAccountReceiveablePaymentPresenter(context: Context, translations: Translations, disposable: CompositeDisposable, cashboxService: CashBoxService, orderService:OrderService, preferences: SharedPreferences): AccountReceiveablePaymentPresenter {
        return AccountReceiveablePaymentPresenter(context, preferences, translations, cashboxService, orderService, disposable)
    }

    @Provides
    internal fun provideLoadCustomerPresenter(context: Context, translations: Translations, disposable: CompositeDisposable,customerService:CustomerService,  preferences: SharedPreferences): LoadCustomerPresenter {
        return LoadCustomerPresenter(context, preferences, translations, customerService, disposable)
    }

    @Provides
    internal fun provideEditCustomerPresenter(context: Context, translations: Translations, disposable: CompositeDisposable,customerService:CustomerService,  preferences: SharedPreferences): EditCustomerPresenter {
        return EditCustomerPresenter(context, preferences, translations, customerService, disposable)
    }

    @Provides
    internal fun provideLoadOrderPresenter(context: Context, translations: Translations, disposable: CompositeDisposable, orderService:OrderService, preferences: SharedPreferences): LoadOrderPresenter {
        return LoadOrderPresenter(context, preferences, translations, orderService, disposable)
    }

    @Provides
    internal fun provideLoadOrderItemPresenter(context: Context, translations: Translations, disposable: CompositeDisposable, orderService:OrderService, preferences: SharedPreferences): LoadOrderItemPresenter {
        return LoadOrderItemPresenter(context, preferences, translations, orderService, disposable)
    }

    @Provides
    internal fun provideAccountReceiveablePresenter(context: Context, translations: Translations, disposable: CompositeDisposable,orderService: OrderService,  preferences: SharedPreferences): AccountReceiveablePresenter {
        return AccountReceiveablePresenter(context, preferences, translations, orderService, disposable)
    }

    @Provides
    internal fun provideAccountReceiveableDetailPresenter(context: Context, translations: Translations, disposable: CompositeDisposable,orderService: OrderService,  preferences: SharedPreferences): AccountReceiveableDetailPresenter {
        return AccountReceiveableDetailPresenter(context, preferences, translations, orderService, disposable)
    }

    @Provides
    internal fun provideOrderChartPresenter(context: Context, translations: Translations, disposable: CompositeDisposable,orderService: OrderService,  preferences: SharedPreferences): OrderChartPresenter {
        return OrderChartPresenter(context, preferences, translations, orderService, disposable)
    }
    @Provides
    internal fun provideProfitChartPresenter(context: Context, translations: Translations, disposable: CompositeDisposable,orderService: OrderService,  preferences: SharedPreferences): ProfitChartPresenter {
        return ProfitChartPresenter(context, preferences, translations, orderService, disposable)
    }

    @Provides
    internal fun provideIncomeChartPresenter(context: Context, translations: Translations, disposable: CompositeDisposable,orderService: OrderService,  preferences: SharedPreferences): IncomeChartPresenter {
        return IncomeChartPresenter(context, preferences, translations, orderService, disposable)
    }

    @Provides
    internal fun provideDashboardHeaderPresenter(context: Context, translations: Translations, disposable: CompositeDisposable,orderService: OrderService,  preferences: SharedPreferences): DashboardHeaderPresenter {
        return DashboardHeaderPresenter(context, preferences, translations, orderService, disposable)
    }

    @Provides
    internal fun provideLoadDiscountByBillAmount(context: Context, translations: Translations, disposable: CompositeDisposable,discountService: DiscountService,  preferences: SharedPreferences): LoadDiscountByBillAmountPresenter {
        return LoadDiscountByBillAmountPresenter(context, preferences, translations, discountService, disposable)
    }

    @Provides
    internal fun provideLoadDiscountByQuantityPresenter(context: Context, translations: Translations, disposable: CompositeDisposable,discountService: DiscountService,  preferences: SharedPreferences): LoadDiscountByQuantityPresenter {
        return LoadDiscountByQuantityPresenter(context, preferences, translations, discountService, disposable)
    }

    @Provides
    internal fun provideTopProductChartPresenter(context: Context, translations: Translations, disposable: CompositeDisposable,orderService: OrderService,  preferences: SharedPreferences): TopProductChartPresenter {
        return TopProductChartPresenter(context, preferences, translations, orderService, disposable)
    }

    @Provides
    internal fun provideAccountReceiveableInAgeChartPresenter(context: Context, translations: Translations, disposable: CompositeDisposable,orderService: OrderService,  preferences: SharedPreferences): AccountReceiveableInAgeChartPresenter {
        return AccountReceiveableInAgeChartPresenter(context, preferences, translations, orderService, disposable)
    }

    @Provides
    internal fun provideAccountReceiveableOutOfAgeChartPresenter(context: Context, translations: Translations, disposable: CompositeDisposable,orderService: OrderService,  preferences: SharedPreferences): AccountReceiveableOutOfAgeChartPresenter {
        return AccountReceiveableOutOfAgeChartPresenter(context, preferences, translations, orderService, disposable)
    }

    @Provides
    internal fun provideRefundPresenter(context: Context, translations: Translations, disposable: CompositeDisposable, orderService: OrderService,  preferences: SharedPreferences): RefundPresenter {
        return RefundPresenter(context, preferences, translations, orderService, disposable)
    }

    @Provides
    internal fun provideLoadCountSavedOrderPresenter(context: Context, translations: Translations, disposable: CompositeDisposable, orderService: OrderService, preferences: SharedPreferences): LoadCountSavedOrderPresenter {
        return LoadCountSavedOrderPresenter(context,preferences, translations, orderService, disposable)
    }

    @Provides
    internal fun provideDeleteOrderPresenter(context: Context, translations: Translations, disposable: CompositeDisposable, orderService: OrderService, preferences: SharedPreferences): DeleteOrderPresenter {
        return DeleteOrderPresenter(context,preferences, translations, orderService, disposable)
    }
}
