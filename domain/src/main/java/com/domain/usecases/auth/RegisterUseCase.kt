package com.domain.usecases.auth

import com.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun invoke(email: String, username: String, password: String, confirmPassword: String) =
        authRepository.register(email, username, password, confirmPassword)
}