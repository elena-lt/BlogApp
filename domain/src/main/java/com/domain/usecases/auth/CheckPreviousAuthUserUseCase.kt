package com.domain.usecases.auth

import com.domain.repository.AuthRepository
import javax.inject.Inject

class CheckPreviousAuthUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    fun invoke () = authRepository.checkPreviousAuthUser()
}