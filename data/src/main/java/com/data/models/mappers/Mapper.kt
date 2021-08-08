package com.data.models.mappers

import com.data.models.AccountProperties
import com.data.models.LoginResponse
import com.data.models.RegistrationResponse
import com.domain.models.AccountPropertiesDomain
import com.domain.models.LoginResponseDomain
import com.domain.models.RegisterResponseDomain

object Mapper {

    fun toLoginResponseDomain(lr: LoginResponse): LoginResponseDomain {
        return LoginResponseDomain(lr.email, lr.errorMessage, lr.token, lr.pk, lr.email)
    }

    fun toRegisterResponseDomain(r: RegistrationResponse): RegisterResponseDomain{
        return RegisterResponseDomain(r.response, r.errorMessage, r.email, r.username, r.pk, r.token)
    }

    fun toAccountPropertiesDomain(accProperties: AccountProperties): AccountPropertiesDomain{
        return AccountPropertiesDomain(accProperties.primaryKey, accProperties.email, accProperties.email)
    }
}