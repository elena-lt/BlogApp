package com.data.models.mappers

import com.data.models.AuthToken
import com.domain.models.AuthTokenDomain

object  AuthTokenMapper {

    fun toAuthTokenDomain (token: AuthToken): AuthTokenDomain{
        return AuthTokenDomain(token.account_primary_key, token.token)
    }
}