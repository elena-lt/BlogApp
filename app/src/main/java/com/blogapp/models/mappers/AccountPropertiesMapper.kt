package com.blogapp.models.mappers

import com.blogapp.models.AccountProperties
import com.domain.models.AccountPropertiesDomain

object AccountPropertiesMapper {

    fun toAccountProperties(accProp: AccountPropertiesDomain): AccountProperties {
        return AccountProperties(accProp.primaryKey, accProp.email, accProp.username)
    }
    fun toAccountPropertiesDomain(accProp: AccountProperties): AccountPropertiesDomain {
        return AccountPropertiesDomain(accProp.primaryKey, accProp.email, accProp.username)
    }

}