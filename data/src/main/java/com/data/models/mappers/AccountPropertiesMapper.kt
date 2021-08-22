package com.data.models.mappers

import com.data.models.AccountProperties
import com.domain.models.AccountPropertiesDomain

object AccountPropertiesMapper {

    fun toAccountPropertiesDomain(accProperties: AccountProperties): AccountPropertiesDomain {
        return AccountPropertiesDomain(accProperties.primaryKey, accProperties.email, accProperties.email)
    }
}