package com.domain.usecases.main.account

import com.domain.repository.AccountRepository
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {

    fun involke (oldPassword: String, newPassword: String, confirmNewPassword: String ) = accountRepository.changePassword(oldPassword, newPassword, confirmNewPassword)
}