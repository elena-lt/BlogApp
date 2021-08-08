package com.domain.repository

import androidx.lifecycle.LiveData
import com.domain.models.AuthTokenDomain
import com.domain.utils.DataState
import com.domain.viewState.AccountViewState

interface AccountRepository {

    fun getAccountProperties(authToken: AuthTokenDomain): LiveData<DataState<AccountViewState>>

    fun updateAccountProperties(email: String, username: String): LiveData<DataState<AccountViewState>>
}