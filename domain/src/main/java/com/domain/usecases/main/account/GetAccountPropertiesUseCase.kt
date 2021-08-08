package com.domain.usecases.main.account

import com.domain.models.AuthTokenDomain
import com.domain.repository.AccountRepository
import javax.inject.Inject

class GetAccountPropertiesUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {

    fun invoke(authToken: AuthTokenDomain) = accountRepository.getAccountProperties(authToken)
}