package com.data.di.auth

import android.content.SharedPreferences
import com.data.network.auth.OpenApiAuthService
import com.data.persistance.AccountPropertiesDao
import com.data.persistance.AuthTokenDao
import com.data.repository.auth.AuthRepositoryImp
import com.data.session.SessionManager
import com.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.InternalCoroutinesApi
import retrofit2.Retrofit

@Module
class AuthModule {

    @AuthScope
    @Provides
    fun provideApiService(retrofit: Retrofit): OpenApiAuthService {
        return retrofit
            .create(OpenApiAuthService::class.java)
    }

    @InternalCoroutinesApi
    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        authTokenDao: AuthTokenDao,
        accountPropertiesDao: AccountPropertiesDao,
        openApiAuthService: OpenApiAuthService,
        sharedPreferences: SharedPreferences,
        sharedPreferencesEditor: SharedPreferences.Editor
    ): AuthRepository {
        return AuthRepositoryImp(
            authTokenDao,
            accountPropertiesDao,
            sessionManager,
            openApiAuthService,
            sharedPreferences,
            sharedPreferencesEditor
        )
    }

}