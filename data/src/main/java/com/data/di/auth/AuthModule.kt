package com.data.di.auth

import com.data.network.auth.OpenApiAuthService
import com.data.persistance.AccountPropertiesDao
import com.data.persistance.AuthTokenDao
import com.data.repository.auth.AuthRepositoryImp
import com.data.session.SessionManager
import com.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class AuthModule {

    @AuthScope
    @Provides
    fun provideApiService(retrofit: Retrofit): OpenApiAuthService {
        return retrofit
            .create(OpenApiAuthService::class.java)
    }

    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        authTokenDao: AuthTokenDao,
        accountPropertiesDao: AccountPropertiesDao,
        openApiAuthService: OpenApiAuthService
    ): AuthRepository {
        return AuthRepositoryImp(
            authTokenDao,
            accountPropertiesDao,
            sessionManager,
            openApiAuthService
        )
    }
}