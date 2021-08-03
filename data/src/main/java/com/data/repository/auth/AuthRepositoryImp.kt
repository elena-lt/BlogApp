package com.data.repository.auth

import com.data.network.auth.OpenApiAuthService
import com.data.persistance.AccountPropertiesDao
import com.data.persistance.AuthTokenDao
import com.data.session.SessionManager
import com.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager,
    val openApiAuthService: OpenApiAuthService
) : AuthRepository{
}