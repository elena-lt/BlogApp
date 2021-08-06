package com.domain.usecases

import android.util.Log
import androidx.lifecycle.LiveData
import com.domain.repository.AuthRepository
import com.domain.utils.DataState
import com.domain.viewState.AuthViewState
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun invoke(email: String, password: String): LiveData<DataState<AuthViewState>> {
        Log.d ("AUTH_ACTIVITY", "login usecase")
        return authRepository.login(email, password) }
}