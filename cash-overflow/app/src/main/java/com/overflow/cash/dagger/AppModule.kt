package com.overflow.cash.dagger

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.overflow.libs.core.Translations
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import io.realm.Realm
import javax.inject.Singleton


@Module(includes = [NetworkModule::class, RetrofitModule::class, PresenterModule::class, RealmModule::class])
class AppModule {
    @Provides
    internal fun provideContext(application: Application): Context {
        return application.baseContext
    }

    @Provides
    internal fun provideSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }


    @Provides
    @Singleton
    internal fun provideTranslation(context: Context): Translations {
        return Translations(context)
    }

    @Provides
    internal fun provideCompositeDisposable(): CompositeDisposable{
        return CompositeDisposable()
    }



//    @Provides
//    @Singleton
//    internal fun provideNetErrorHandler(preferences: SharedPreferences, userRepository: UserRepository, translations: Translations): NetworkExHandler{
//        return NetworkExHandler(preferences, userRepository, translations)
//    }

}
