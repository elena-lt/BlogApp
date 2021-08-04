package com.blogapp.di

import android.app.Application
import androidx.room.Room
import com.blogapp.R
import com.blogapp.utils.Const.BASE_URL
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.data.persistance.AccountPropertiesDao
import com.data.persistance.AppDatabase
import com.data.persistance.AuthTokenDao
import com.data.utils.Const
import com.data.utils.LiveDataCallAdapter
import com.data.utils.LiveDataCallAdapterFactory
import com.domain.repository.AuthRepository
import com.domain.usecases.LoginUseCase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideRequestOptions(): RequestOptions {
        return RequestOptions
            .placeholderOf(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
    }

    @Singleton
    @Provides
    fun provideGlideInstance(application: Application, requestOptions: RequestOptions): RequestManager {
        return Glide.with(application)
            .setDefaultRequestOptions(requestOptions)
    }
}