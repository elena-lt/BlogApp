package com.blogapp

import com.blogapp.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import kotlinx.coroutines.InternalCoroutinesApi

class BaseApplication : DaggerApplication() {
    @InternalCoroutinesApi
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
       return DaggerAppComponent.builder().application(this).build()
    }

}