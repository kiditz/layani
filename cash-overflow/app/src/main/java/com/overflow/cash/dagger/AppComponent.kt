package com.overflow.cash.dagger

import android.app.Application
import com.overflow.cash.activity.RootApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [(AppModule::class), (ActivityBuilder::class), (AndroidSupportInjectionModule::class)])
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: RootApplication)
}
