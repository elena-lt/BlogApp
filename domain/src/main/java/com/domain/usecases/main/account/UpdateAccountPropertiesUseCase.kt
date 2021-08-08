package com.domain.usecases.main.account

import com.domain.repository.AccountRepository
import javax.inject.Inject

class UpdateAccountPropertiesUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {

    fun invoke(email: String, username: String) = accountRepository.updateAccountProperties(email, username)
}