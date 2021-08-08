package com.data.di.main

import com.data.network.main.OpenApiMainService
import com.data.persistance.AccountPropertiesDao
import com.data.repository.main.AccountRepositoryImp
import com.data.session.SessionManager
import com.domain.repository.AccountRepository
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.InternalCoroutinesApi
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class MainModule {

    @MainScope
    @Provides
    fun provideOpenApiMainService(retrofit: Retrofit) =
        retrofit.create(OpenApiMainService::class.java)

    @InternalCoroutinesApi
    @MainScope
    @Provides
    fun provideAccountRepository(
        openApiMainService: OpenApiMainService,
        accountPropertiesDao: AccountPropertiesDao,
        sessionManager: SessionManager
    ): AccountRepository {

        return AccountRepositoryImp(openApiMainService, accountPropertiesDao, sessionManager)
    }
}