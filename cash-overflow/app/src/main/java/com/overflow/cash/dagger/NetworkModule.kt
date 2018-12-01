package com.overflow.cash.dagger

import android.accounts.AccountManager
import android.app.Application
import android.content.Context
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.overflow.cash.BuildConfig
import com.overflow.cash.net.NetworkExHandler
import com.overflow.cash.net.TokenAuthenticator
import com.overflow.cash.net.TokenInterceptor
import com.overflow.libs.core.Translations
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton


@Module
class NetworkModule {

    val cacheSize = 10 * 1024 * 1024
    @Provides
    internal fun provideAccountManager(context: Context): AccountManager {
        return AccountManager.get(context)
    }

    @Provides
    @Singleton
    internal fun provideHttpCache(application: Application): Cache {
        return Cache(application.cacheDir, cacheSize.toLong())
    }

    @Provides
    internal fun provideObjectMapper(): ObjectMapper {
        val jsonMapper = ObjectMapper()
        jsonMapper.configure(SerializationFeature.INDENT_OUTPUT, true)
        jsonMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        jsonMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
        jsonMapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
        return jsonMapper
    }

    @Provides
    @Named(LOGIN)
    internal fun provideRetrofitLogin(objectMapper: ObjectMapper, cache: Cache): Retrofit {
        val client = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC
        client.addInterceptor(interceptor)
        client.connectTimeout(30, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS).writeTimeout(120, TimeUnit.SECONDS).build()
        client.cache(cache)
        return Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .baseUrl(BuildConfig.base_url)
                .client(client.build())
                .build()
    }

    @Provides
    @Named(AUTHENTICATED)
    internal fun provideRetrofitAuthEnabled(objectMapper: ObjectMapper, accountManager: AccountManager, context: Context): Retrofit {
        val client = OkHttpClient.Builder()
        client.addInterceptor(TokenInterceptor(context, accountManager))
        client.authenticator(TokenAuthenticator(context, accountManager))
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        client.addInterceptor(interceptor)
        client.connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS).build()
        return Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .baseUrl(BuildConfig.base_url)
                .client(client.build())
                .build()
    }

//    @Provides
//    internal fun provideStomp(): Stomp {
//        return Stomp(BuildConfig.ws_url) { state -> Timber.i("Stomp Connected $state") }
//    }

    @Provides
    @Singleton
    internal fun provideNetErrorHandler(translations: Translations): NetworkExHandler {
        return NetworkExHandler(translations)
    }

    companion object {
        const val LOGIN = "login"
        const val AUTHENTICATED = "authenticated"
    }
}
