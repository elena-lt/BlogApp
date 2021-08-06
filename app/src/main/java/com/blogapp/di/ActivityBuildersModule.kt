package com.blogapp.di

import com.blogapp.ui.auth.AuthActivity
import com.blogapp.di.auth.AuthFragmentBuildersModule
import com.blogapp.di.auth.AuthViewModelModule
import com.blogapp.di.main.MainFragmentBuildersModules
import com.blogapp.di.main.MainModule
import com.blogapp.di.main.MainScope
import com.blogapp.di.main.MainViewModelModule
import com.blogapp.ui.main.MainActivity
import com.data.di.auth.AuthModule
import com.data.di.auth.AuthScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @AuthScope
    @ContributesAndroidInjector(
        modules = [AuthModule::class, AuthFragmentBuildersModule::class, AuthViewModelModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity

    @MainScope
    @ContributesAndroidInjector(
        modules = [MainModule::class, MainFragmentBuildersModules::class, MainViewModelModule::class]
    )
    abstract fun contributeMainActivity(): MainActivity

}