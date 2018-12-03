package com.overflow.cash.dagger

import android.accounts.AccountManager
import android.content.Context
import android.content.SharedPreferences
import com.overflow.cash.mvp.chart.*
import com.overflow.cash.mvp.customer.CustomerChooserPresenter
import com.overflow.cash.mvp.dashboard.DashboardHeaderPresenter
import com.overflow.cash.mvp.login.LoginPresenter
import com.overflow.cash.mvp.menu.MenuPresenter
import com.overflow.cash.mvp.order.CashboxPresenter
import com.overflow.cash.mvp.order.OrderPresenter
import com.overflow.cash.mvp.order.PreviewSalesPresenter
import com.overflow.cash.mvp.payment.PaymentTransactionPresenter
import com.overflow.cash.mvp.product.*
import com.overflow.cash.mvp.receiveable.AccountReceiveableDetailPresenter
import com.overflow.cash.mvp.receiveable.AccountReceiveablePaymentPresenter
import com.overflow.cash.mvp.receiveable.AccountReceiveablePresenter
import com.overflow.cash.mvp.register.RegisterPresenter
import com.overflow.cash.net.*
import com.overflow.cash.realm.OrderRealm
import com.overflow.libs.core.Translations
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

@Suppress("unused")
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
    internal fun provideLoginPresenter(context: Context, translations: Translations, disposable: CompositeDisposable, accountService: AccountService, merchantService: MerchantService, preferences: SharedPreferences, accountManager: AccountManager): LoginPresenter {
        return LoginPresenter(context, translations, disposable, accountService, merchantService, preferences, accountManager)
    }

    @Provides
    internal fun provideAddProductPresenter(context: Context,translations: Translations, disposable: CompositeDisposable, productService: ProductService, preferences: SharedPreferences): AddProductPresenter {
        return AddProductPresenter(context, translations, disposable, productService, preferences)
    }

    @Provides
    internal fun provideCategoryListPresenter(context: Context,translations: Translations, disposable: CompositeDisposable, productService: ProductService, orderRealm: OrderRealm, preferences: SharedPreferences): CategoryListPresenter {
        return CategoryListPresenter(context, translations, disposable, productService, preferences, orderRealm)
    }

    @Provides
    internal fun provideEditAndRemoveCategoryPresenter(context: Context,translations: Translations, disposable: CompositeDisposable, productService: ProductService, preferences: SharedPreferences): EditAndRemoveCategoryPresenter {
        return EditAndRemoveCategoryPresenter(context, translations, disposable, preferences,productService)
    }

    @Provides
    internal fun provideProductListPresenter(context: Context,translations: Translations, disposable: CompositeDisposable, productService: ProductService, preferences: SharedPreferences): ProductListPresenter {
        return ProductListPresenter(context, translations, disposable, productService, preferences)
    }

    @Provides
    internal fun provideProductDetailPresenter(context: Context,translations: Translations, disposable: CompositeDisposable, productService: ProductService, preferences: SharedPreferences): ProductDetailPresenter {
        return ProductDetailPresenter(context, translations, disposable, productService, preferences)
    }
    @Provides
    internal fun provideOrderPresenter(orderRealm:OrderRealm): OrderPresenter {
        return OrderPresenter(orderRealm)
    }

    @Provides
    internal fun providePreviewSalesPresenter(orderRealm:OrderRealm, context: Context, translations: Translations, disposable: CompositeDisposable, cashboxService: CashBoxService, orderService: OrderService, preferences: SharedPreferences): PreviewSalesPresenter {
        return PreviewSalesPresenter(context, orderRealm,preferences, translations, cashboxService, orderService, disposable)
    }

    @Provides
    internal fun providePaymentTransactionPresenter(context: Context,orderRealm: OrderRealm,  translations: Translations, disposable: CompositeDisposable, orderService: OrderService, preferences: SharedPreferences): PaymentTransactionPresenter {
        return PaymentTransactionPresenter(context,orderRealm, preferences, translations, orderService, disposable)
    }

    @Provides
    internal fun provideCashboxPresenter(context: Context, translations: Translations, disposable: CompositeDisposable, cashboxService: CashBoxService, preferences: SharedPreferences): CashboxPresenter {
        return CashboxPresenter(context,preferences, translations, cashboxService, disposable)
    }

    @Provides
    internal fun provideAccountReceiveablePaymentPresenter(context: Context, translations: Translations, disposable: CompositeDisposable, cashboxService: CashBoxService, orderService:OrderService, preferences: SharedPreferences): AccountReceiveablePaymentPresenter {
        return AccountReceiveablePaymentPresenter(context, preferences, translations, cashboxService, orderService, disposable)
    }

    @Provides
    internal fun provideCustomerChooserPresenter(context: Context, translations: Translations, disposable: CompositeDisposable,customerService:CustomerService,  preferences: SharedPreferences): CustomerChooserPresenter {
        return CustomerChooserPresenter(context, preferences, translations, customerService, disposable)
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
}
