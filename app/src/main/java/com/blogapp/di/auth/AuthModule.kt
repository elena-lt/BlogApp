package com.blogapp.di.auth

import com.data.network.auth.OpenApiAuthService
import com.data.persistance.AccountPropertiesDao
import com.data.persistance.AuthTokenDao
import com.data.repository.auth.AuthRepositoryImp
import com.data.session.SessionManager
import com.domain.repository.AuthRepository
import com.domain.usecases.LoginUseCase
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
class AuthModule{

    // TEMPORARY
    @AuthScope
    @Provides
    fun provideFakeApiService(): OpenApiAuthService {
        return Retrofit.Builder()
            .baseUrl("https://open-api.xyz")
            .build()
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