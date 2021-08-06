package com.blogapp.models.mappers

import com.blogapp.models.AuthToken
import com.domain.models.AuthTokenDomain

object AuthTokenMapper {

    fun toAuthToken (token: AuthTokenDomain): AuthToken {
        return AuthToken(token.pk, token.authToken)
    }
}