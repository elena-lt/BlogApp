package com.data.repository.main.account

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.data.models.AccountProperties
import com.data.models.GenericResponse
import com.data.models.mappers.AccountPropertiesMapper
import com.data.models.mappers.Mapper
import com.data.network.main.OpenApiMainService
import com.data.persistance.AccountPropertiesDao
import com.data.repository.JobManager
import com.data.repository.NetworkBoundResource
import com.data.session.SessionManager
import com.domain.models.AuthTokenDomain
import com.domain.repository.AccountRepository
import com.domain.dataState.DataState
import com.domain.dataState.MessageType
import com.domain.dataState.StateMessage
import com.domain.dataState.UIComponentType
import com.domain.viewState.AccountViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ExperimentalCoroutinesApi
class AccountRepositoryImp @Inject constructor(
    private val openApiAuthService: OpenApiMainService,
    private val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
) : AccountRepository, JobManager("AccountRepositoryImp") {

    val authToken = sessionManager.cashedToken.value!!

    override fun getAccountProperties(authToken: AuthTokenDomain): Flow<DataState<AccountViewState>> {
        return object :
            NetworkBoundResource<AccountProperties, AccountProperties, AccountViewState>(
                IO,
                apiCall = { openApiAuthService.getAccountProperties("Token ${authToken.authToken}") },
                cacheCall = { accountPropertiesDao.searchByPrimaryKey(authToken.pk!!) }
            ) {

            override suspend fun handleCacheSuccess(response: AccountProperties): DataState<AccountViewState>? {
                return DataState.LOADING(
                    true,
                    AccountViewState(AccountPropertiesMapper.toAccountPropertiesDomain(response))
                )
            }

            override suspend fun handleNetworkSuccess(response: AccountProperties): DataState<AccountViewState>? {
                return DataState.SUCCESS(
                    data = AccountViewState(
                        AccountPropertiesMapper.toAccountPropertiesDomain(
                            response
                        )
                    )
                )
            }

            override suspend fun updateCache(networkObject: AccountProperties) {
                accountPropertiesDao.updateAccountProperties(
                    networkObject.primaryKey,
                    networkObject.email,
                    networkObject.username
                )
            }
        }.result

    }

    override fun updateAccountProperties(
        email: String,
        username: String
    ): Flow<DataState<AccountViewState>> {

        return object : NetworkBoundResource<GenericResponse, Any, AccountViewState>(
            IO,
            apiCall = {
                openApiAuthService.updateAccountProperties(
                    "Token ${authToken.token}",
                    email,
                    username
                )
            }
        ) {
            override suspend fun handleNetworkSuccess(response: GenericResponse): DataState<AccountViewState>? {
                return DataState.ERROR(
                    StateMessage(
                        "SUCCESS",
                        UIComponentType.TOAST,
                        MessageType.SUCCESS
                    )
                )
            }

            override suspend fun updateCache(networkObject: GenericResponse) {
                super.updateCache(networkObject)
            }
        }.result
    }

    override fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ): Flow<DataState<AccountViewState>> {

        return object : NetworkBoundResource<GenericResponse, Any, AccountViewState>(
            IO,
            apiCall = {
                openApiAuthService.changePassword(
                    "Token ${authToken.token}",
                    oldPassword,
                    newPassword,
                    confirmNewPassword
                )
            }
        ) {

        }.result
    }
}
