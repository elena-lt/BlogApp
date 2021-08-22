package com.domain.repository

import com.domain.dataState.DataState
import com.domain.viewState.AuthViewState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun login(email: String, password: String): Flow<DataState<AuthViewState>>

    fun register(email: String, username: String, password: String, confirmPassword: String): Flow<DataState<AuthViewState>>

    fun checkPreviousAuthUser(): Flow<DataState<AuthViewState>>
}