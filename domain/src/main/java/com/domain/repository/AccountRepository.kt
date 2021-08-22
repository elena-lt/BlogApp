package com.domain.repository

import androidx.lifecycle.LiveData
import com.domain.models.AuthTokenDomain
import com.domain.dataState.DataState
import com.domain.viewState.AccountViewState
import kotlinx.coroutines.flow.Flow

interface AccountRepository {

    fun getAccountProperties(authToken: AuthTokenDomain): Flow<DataState<AccountViewState>>

    fun updateAccountProperties(email: String, username: String): Flow<DataState<AccountViewState>>

    fun changePassword (oldPassword: String, newPassword: String, confirmNewPassword: String): Flow<DataState<AccountViewState>>
}