package com.data.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.data.models.AccountProperties
import com.data.models.AuthToken
import com.data.models.GenericResponse
import com.data.models.mappers.Mapper
import com.data.network.auth.OpenApiAuthService
import com.data.network.main.OpenApiMainService
import com.data.persistance.AccountPropertiesDao
import com.data.repository.JobManager
import com.data.repository.NetworkBoundResource
import com.data.session.SessionManager
import com.data.utils.GenericApiResponse
import com.domain.models.AuthTokenDomain
import com.domain.repository.AccountRepository
import com.domain.utils.AbsentLiveData
import com.domain.utils.DataState
import com.domain.utils.Response
import com.domain.utils.ResponseType
import com.domain.viewState.AccountViewState
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import javax.inject.Inject

@InternalCoroutinesApi
class AccountRepositoryImp @Inject constructor(
    private val openApiAuthService: OpenApiMainService,
    private val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
) : AccountRepository, JobManager("AccountRepositoryImp") {

    override fun getAccountProperties(authToken: AuthTokenDomain): LiveData<DataState<AccountViewState>> {
        return object :
            NetworkBoundResource<AccountProperties, AccountProperties, AccountViewState>(
                sessionManager.isConnectedToInternet(),
                isNetworkRequest = true,
                shouldCancelIfNoInternet = false,
                shouldLoadFromCache = true
            ) {
            //room query returns LiveData<AccountProperties> but LiveData<AccountViewState> is required
            //thus, the return type is transformed in onActive
            override fun loadFromCache(): LiveData<AccountViewState> {
                val query = accountPropertiesDao.searchByPrimaryKey(authToken.pk!!)
                return Transformations.switchMap(query) {
                    object : LiveData<AccountViewState>() {
                        override fun onActive() {
                            super.onActive()
                            value = AccountViewState(Mapper.toAccountPropertiesDomain(it))
                        }
                    }
                }
            }

            //returned when there is no network
            override suspend fun createCashRequestAndReturn() {
                withContext(Main) {
                    result.addSource(loadFromCache()) { viewState ->
                        onCompleteJob(
                            DataState.data(
                                data = viewState,
                                response = null
                            )
                        )
                    }
                }
            }

            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<AccountProperties>) {
                Log.d("AppDebug", "handleApiSuccessResponse: response body: ${response.body}")
                updateLocalDb(response.body)
                createCashRequestAndReturn()
            }

            override fun createCall(): LiveData<GenericApiResponse<AccountProperties>> =
                openApiAuthService.getAccountProperties("Token ${authToken.authToken}")

            override fun setJob(job: Job) {
                addJob("getAccountProperties", job)
            }

            override suspend fun updateLocalDb(cacheObject: AccountProperties?) {
                Log.d("AppDebug", "getAccountProperties, updateLocalDb: $cacheObject")
                cacheObject?.let {
                    accountPropertiesDao.updateAccountProperties(
                        cacheObject.primaryKey,
                        cacheObject.email,
                        cacheObject.username
                    )
                }
                val newVal = accountPropertiesDao.searchByPrimaryKey(cacheObject!!.primaryKey).value
                Log.d(
                    "AppDebug",
                    "getAccountProperties: ${newVal?.primaryKey}, ${newVal?.email}, ${newVal?.username}"
                )

                val searchByEmail = accountPropertiesDao.searchByEmail("elenalutsiuk5@gmail.com")
                Log.d(
                    "AppDebug",
                    "searchByPrimaryKey: ${searchByEmail?.primaryKey}, ${searchByEmail?.username}"
                )
            }

        }.asLiveData()

    }

    override fun updateAccountProperties(
        email: String,
        username: String
    ): LiveData<DataState<AccountViewState>> {
        val authToken = sessionManager.cashedToken.value
        return object : NetworkBoundResource<GenericResponse, Any, AccountViewState>(
            sessionManager.isConnectedToInternet(),
            isNetworkRequest = true,
            shouldCancelIfNoInternet = true,
            shouldLoadFromCache = false
        ) {
            override suspend fun createCashRequestAndReturn() {
                /*No-OPS*/
            }

            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<GenericResponse>) {
                updateLocalDb(null)
                val newDetails = accountPropertiesDao.searchByPrimaryKey(2079).value
                Log.d(
                    "AppDebug",
                    "searchByPrimaryKey: ${newDetails?.email}, ${newDetails?.username}"
                )

                val searchByEmail = accountPropertiesDao.searchByEmail("elenalutsiuk5@gmail.com")
                Log.d(
                    "AppDebug",
                    "searchByPrimaryKey: ${searchByEmail?.primaryKey}, ${searchByEmail?.username}"
                )

                withContext(Main) {
                    onCompleteJob(
                        DataState.data(
                            data = null,
                            response = Response(
                                message = response.body.response,
                                responseType = ResponseType.Toast()
                            )
                        )
                    )
                }
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {

                return authToken?.let {
                    it.token?.let { token ->
                        Log.d("AppDebug", "createCall: $token, $email, $username")
                        openApiAuthService.updateAccountProperties("Token $token", email, username)
                    }
                } ?: AbsentLiveData.create()
            }

            override fun loadFromCache(): LiveData<AccountViewState> {
                /*NO-OPS*/
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: Any?) {
                return authToken!!.let {
                    it.account_primary_key?.let { primaryKey ->
                        Log.d(
                            "AppDebug",
                            "updateLocalDb: updating local db: $primaryKey, $username"
                        )
                        accountPropertiesDao.updateAccountProperties(primaryKey, email, username)
                    }
                }
            }

            override fun setJob(job: Job) {
                addJob("updateAccountProperties", job)
            }

        }.asLiveData()
    }

    override fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ): LiveData<DataState<AccountViewState>> {
        val authToken = sessionManager.cashedToken.value
        authToken?.let {
            return object : NetworkBoundResource<GenericResponse, Any, AccountViewState>(
                isNetworkAvailable = sessionManager.isConnectedToInternet(),
                isNetworkRequest = true,
                shouldCancelIfNoInternet = true,
                shouldLoadFromCache = false
            ) {
                override suspend fun createCashRequestAndReturn() {
                    /*No-OPS*/
                }

                override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<GenericResponse>) {
                    withContext(Main) {
                        onCompleteJob(
                            DataState.data(
                                data = null,
                                response = Response(
                                    response.body.response,
                                    responseType = ResponseType.Toast()
                                )
                            )
                        )
                    }
                }

                override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                    return openApiAuthService.changePassword(
                        "Token ${it.token!!}",
                        oldPassword,
                        newPassword,
                        confirmNewPassword
                    )
                }

                override fun loadFromCache(): LiveData<AccountViewState> {
                    /*No-OPS*/
                    return AbsentLiveData.create()
                }

                override suspend fun updateLocalDb(cacheObject: Any?) {
                    /*No-OPS*/
                }

                override fun setJob(job: Job) {
                   addJob("changePassword", job)
                }

            }.asLiveData()
        } ?: return AbsentLiveData.create()
    }

}