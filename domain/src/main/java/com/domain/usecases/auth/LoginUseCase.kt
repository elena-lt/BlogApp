package com.domain.usecases.auth

import android.util.Log
import androidx.lifecycle.LiveData
import com.domain.repository.AuthRepository
import com.domain.dataState.DataState
import com.domain.viewState.AuthViewState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun invoke(email: String, password: String): Flow<DataState<AuthViewState>> {
        Log.d("AppDebug", "usecase // invoke login")
        return authRepository.login(email, password) }
}