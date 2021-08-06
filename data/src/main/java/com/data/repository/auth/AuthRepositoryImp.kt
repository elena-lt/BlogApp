package com.data.repository.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.data.models.AccountProperties
import com.data.models.AuthToken
import com.data.models.LoginResponse
import com.data.models.RegistrationResponse
import com.data.network.auth.OpenApiAuthService
import com.data.persistance.AccountPropertiesDao
import com.data.persistance.AuthTokenDao
import com.data.repository.NetworkBoundResource
import com.data.session.SessionManager
import com.data.utils.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import com.data.utils.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.data.utils.GenericApiResponse
import com.data.utils.PreferenceKeys
import com.data.utils.PreferenceKeys.Companion.PREVIOUS_AUTH_USER
import com.data.utils.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import com.domain.models.AuthTokenDomain
import com.domain.repository.AuthRepository
import com.domain.utils.*
import com.domain.viewState.AuthViewState
import com.domain.viewState.LoginFields
import com.domain.viewState.RegistrationFields
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import javax.inject.Inject

@InternalCoroutinesApi
class AuthRepositoryImp @Inject constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager,
    val openApiAuthService: OpenApiAuthService,
    val sharedPreferences: SharedPreferences,
    val sharedPreferencesEditor: SharedPreferences.Editor
) : AuthRepository {

    var repositoryJob: Job? = null

    override fun login(email: String, password: String): LiveData<DataState<AuthViewState>> {
        Log.d("AUTH_REPOSITORY", "inside login fun")
        val loginFieldsError = LoginFields(email, password).isValidForLogin()
        if (!loginFieldsError.equals(LoginFields.LoginError.none())) {
            return errorResponse(loginFieldsError, ResponseType.Dialog())
        }

        return object :
            NetworkBoundResource<LoginResponse, AuthViewState>(
                sessionManager.isConnectedToInternet(),
                true
            ) {
            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<LoginResponse>) {
                if (response.body.response.equals(GENERIC_AUTH_ERROR)) {
                    Log.d("AUTH_REPOSITORY", "handleApiSuccessResponse: ${response}")
                    onErrorReturned(
                        response.body.errorMessage,
                        shouldUseDialog = true,
                        shouldUseToast = false
                    )
                }

                //save email to sharedPrefs
                accountPropertiesDao.insertOrIgnore(
                    AccountProperties(
                        response.body.pk,
                        response.body.email,
                        ""
                    )
                )

                //-1 if failure
                val result = authTokenDao.insert(AuthToken(response.body.pk, response.body.token))

                if (result < 0) {
                    return onCompleteJob(
                        DataState.error(
                            Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog())
                        )
                    )
                }

                saveAuthenticatedUserToSharedPrefs(email)

                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthTokenDomain(
                                response.body.pk,
                                response.body.token
                            )
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
                Log.d("AUTH_REPOSITORY", "api cal sent")
                return openApiAuthService.login(email, password)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

            override suspend fun createCashRequestAndReturn() {
                /*no-ops*/
            }

        }.asLiveData()
    }

    override fun register(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): LiveData<DataState<AuthViewState>> {
        val registrationFieldsError =
            RegistrationFields(email, username, password, confirmPassword).isValidForRegistration()
        if (registrationFieldsError != RegistrationFields.RegistrationError.none()) {
            return errorResponse(registrationFieldsError, ResponseType.Dialog())
        }

        return object :
            NetworkBoundResource<RegistrationResponse, AuthViewState>(
                sessionManager.isConnectedToInternet(),
                true
            ) {
            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<RegistrationResponse>) {
                if (response.body.response.equals(GENERIC_AUTH_ERROR)) {
                    Log.d("AUTH_REPOSITORY", "handleApiSuccessResponse: ${response}")
                    onErrorReturned(
                        response.body.errorMessage,
                        shouldUseDialog = true,
                        shouldUseToast = false
                    )
                }

                //save email to sharedPrefs
                accountPropertiesDao.insertOrIgnore(
                    AccountProperties(
                        response.body.pk,
                        response.body.email,
                        ""
                    )
                )

                //-1 if failure
                val result = authTokenDao.insert(AuthToken(response.body.pk, response.body.token))

                if (result < 0) {
                    return onCompleteJob(
                        DataState.error(
                            Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog())
                        )
                    )
                }

                saveAuthenticatedUserToSharedPrefs(email)

                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthTokenDomain(
                                response.body.pk,
                                response.body.token
                            )
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<RegistrationResponse>> {
                return openApiAuthService.register(email, username, password, confirmPassword)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

            override suspend fun createCashRequestAndReturn() {
                /*NO-OPS*/
            }

        }.asLiveData()

    }

    private fun errorResponse(
        fieldsError: String,
        responseType: ResponseType
    ): LiveData<DataState<AuthViewState>> {
        return object : LiveData<DataState<AuthViewState>>() {
            override fun onActive() {
                super.onActive()
                value = DataState.error(response = Response(fieldsError, responseType))
            }
        }
    }

    fun cancelActiveJobs() {
        Log.d("AUTH_REPOSITORY", "canceling ongoing jobs.........")
        repositoryJob?.cancel()
    }

    private fun saveAuthenticatedUserToSharedPrefs(email: String) {
        sharedPreferencesEditor.putString(PreferenceKeys.PREVIOUS_AUTH_USER, email)
        sharedPreferencesEditor.apply()
    }

    override fun checkPreviousAuthUser(): LiveData<DataState<AuthViewState>> {
        val prevAuthUserEmail: String? = sharedPreferences.getString(PREVIOUS_AUTH_USER, null)
        if (prevAuthUserEmail.isNullOrBlank()) {
            Log.d("AUTH_REPOSITORY", "checkPreviousAuthUser: No email saved")
            return returnNoTokenFound()
        }
        return object : NetworkBoundResource<Void, AuthViewState>(
            sessionManager.isConnectedToInternet(),
            false
        ) {
            override suspend fun createCashRequestAndReturn() {
                accountPropertiesDao.searchByEmail(prevAuthUserEmail).let { accountProperties ->
                    Log.d(
                        "AppDebug",
                        "createCashRequestAndReturn: searching for token: $accountProperties"
                    )
                    accountProperties?.let {
                        if (accountProperties.primaryKey > -1) {
                            authTokenDao.searchByPrimaryKey(accountProperties.primaryKey)
                                .let { authToken ->
                                    if (authToken != null) {
                                        onCompleteJob(
                                            DataState.data(
                                                data = AuthViewState(
                                                    authToken = AuthTokenDomain(
                                                        authToken.account_primary_key,
                                                        authToken.token
                                                    )
                                                )
                                            )
                                        )
                                        return
                                    }
                                }
                        }
                    }
                    Log.d("AppDebug", "createCashRequestAndReturn: AuthToken not found")
                    onCompleteJob(
                        DataState.data(
                            data = null,
                            response = Response(
                                RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                                ResponseType.None()
                            )
                        )
                    )
                }
            }

            //not applicable since no inet request
            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<Void>) {
                /*NO-OPS*/
            }

            //not applicable since no inet request
            override fun createCall(): LiveData<GenericApiResponse<Void>> {
                /*NO-OPS*/
                return AbsentLiveData.create()
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

        }.asLiveData()
    }

    private fun returnNoTokenFound(): LiveData<DataState<AuthViewState>> {
        return object : LiveData<DataState<AuthViewState>>() {
            override fun onActive() {
                super.onActive()
                value = DataState.data(
                    data = null,
                    response = Response(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE, ResponseType.None())
                )
            }
        }
    }


//    override fun login(email: String, password: String): LiveData<DataState<AuthViewState>> {
//        val result = openApiAuthService.login(email, password)
//        return Transformations.switchMap(result) {
//            object : LiveData<DataState<AuthViewState>>() {
//                override fun onActive() {
//                    super.onActive()
//                    when (it) {
//                        is GenericApiResponse.ApiSuccessResponse -> {
//                            value = DataState.data(
//                                AuthViewState(
//                                    authToken = AuthTokenDomain(
//                                        it.body.pk,
//                                        it.body.token
//                                    )
//                                ),
//                                response = null
//                            )
//                        }
//                        is GenericApiResponse.ApiEmptyResponse -> {
//                            value = DataState.data(
//                                response = Response(
//                                    message = "ERROR_UNKNOWN",
//                                    responseType = ResponseType.Dialog()
//                                )
//                            )
//                        }
//                        is GenericApiResponse.ApiErrorResponse -> {
//                            value = DataState.error(
//                                response = Response(
//                                    message = it.errorMessage,
//                                    responseType = ResponseType.Dialog()
//                                )
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
}
