package com.domain.usecases

import com.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: com.domain.repository.AuthRepository
) {
}