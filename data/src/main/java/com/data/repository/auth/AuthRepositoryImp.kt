package com.data.repository.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.data.models.AccountProperties
import com.data.models.AuthToken
import com.data.models.LoginResponse
import com.data.models.RegistrationResponse
import com.data.models.mappers.AuthTokenMapper
import com.data.network.auth.OpenApiAuthService
import com.data.persistance.AccountPropertiesDao
import com.data.persistance.AuthTokenDao
import com.data.repository.JobManager
import com.data.repository.NetworkBoundResource
import com.data.session.SessionManager
import com.data.utils.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import com.data.utils.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.data.utils.ErrorHandling.Companion.UNKNOWN_ERROR
import com.data.utils.PreferenceKeys.Companion.PREVIOUS_AUTH_USER
import com.data.utils.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import com.domain.dataState.DataState
import com.domain.dataState.MessageType
import com.domain.dataState.StateMessage
import com.domain.dataState.UIComponentType
import com.domain.models.AuthTokenDomain
import com.domain.repository.AuthRepository
import com.domain.utils.*
import com.domain.viewState.AuthViewState
import com.domain.viewState.LoginFields
import com.domain.viewState.RegistrationFields
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
class AuthRepositoryImp @Inject constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager,
    val openApiAuthService: OpenApiAuthService,
    private val sharedPreferences: SharedPreferences,
    val sharedPreferencesEditor: SharedPreferences.Editor
) : AuthRepository, JobManager("AuthRepositoryImp") {

    override fun login(email: String, password: String): Flow<DataState<AuthViewState>> = flow {
        val loginFieldsError = LoginFields(email, password).isValidForLogin()

        if (loginFieldsError == LoginFields.LoginError.none()) {
            emitAll(object : NetworkBoundResource<LoginResponse, Any, AuthViewState>(
                IO,
                apiCall = { openApiAuthService.login(email, password) }
            ) {

                override suspend fun handleNetworkSuccess(response: LoginResponse): DataState<AuthViewState>? {
                    if (response.response != GENERIC_AUTH_ERROR) {
                        accountPropertiesDao.insertOrIgnore(
                            AccountProperties(
                                response.pk,
                                response.email,
                                ""
                            )
                        )

                        val result = authTokenDao.insert(AuthToken(response.pk, response.token))

                        return if (result < 0) {
                            buildDialogError(UNKNOWN_ERROR)
                        } else {
                            sharedPreferencesEditor.putString(PREVIOUS_AUTH_USER, email)
                            DataState.SUCCESS(
                                AuthViewState(
                                    authToken = AuthTokenDomain(
                                        response.pk,
                                        response.token
                                    )
                                )
                            )
                        }
                    } else {
                        Log.d("AppDebug", "handleNetworkSuccess: ${response.errorMessage}")
                        return buildDialogError(response.errorMessage)
                    }
                }
            }.result)
        } else {
            emit(
                DataState.ERROR<AuthViewState>(
                    StateMessage(
                        message = loginFieldsError,
                        uiComponentType = UIComponentType.DIALOG,
                        messageType = MessageType.ERROR
                    )
                )
            )
        }
    }

    override fun register(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): Flow<DataState<AuthViewState>> = flow {
        val registrationFieldsError =
            RegistrationFields(email, username, password, confirmPassword).isValidForRegistration()

        if (registrationFieldsError == RegistrationFields.RegistrationError.none()) {
            emitAll(object :
                NetworkBoundResource<RegistrationResponse, Any, AuthViewState>(
                    IO,
                    apiCall = {
                        openApiAuthService.register(
                            email,
                            username,
                            password,
                            confirmPassword
                        )
                    }
                ) {

                override suspend fun handleNetworkSuccess(response: RegistrationResponse): DataState<AuthViewState>? {
                    if (response.response != GENERIC_AUTH_ERROR) {
                        accountPropertiesDao.insertOrIgnore(
                            AccountProperties(
                                response.pk,
                                response.email,
                                ""
                            )
                        )

                        val result = authTokenDao.insert(AuthToken(response.pk, response.token))

                        return if (result < 0) {
                            buildDialogError(UNKNOWN_ERROR)
                        } else {
                            sharedPreferencesEditor.putString(PREVIOUS_AUTH_USER, email)
                            DataState.SUCCESS(
                                AuthViewState(
                                    authToken = AuthTokenDomain(
                                        response.pk,
                                        response.token
                                    )
                                )
                            )
                        }
                    } else {
                        return buildDialogError(response.errorMessage)
                    }
                }

            }.result)
        } else {
            emit(
                DataState.ERROR<AuthViewState>(
                    StateMessage(
                        message = registrationFieldsError,
                        uiComponentType = UIComponentType.DIALOG,
                        messageType = MessageType.ERROR
                    )
                )
            )
        }


    }

    override fun checkPreviousAuthUser(): Flow<DataState<AuthViewState>> = flow {
        val prevAuthUserEmail: String? = sharedPreferences.getString(PREVIOUS_AUTH_USER, null)
        Log.d("AppDebug", "checkPreviousAuthUser: ${prevAuthUserEmail} ")

        if (!prevAuthUserEmail.isNullOrBlank()) {

            emitAll(object : NetworkBoundResource<Any, AccountProperties, AuthViewState>(
                IO,
                cacheCall = { accountPropertiesDao.searchByEmail(prevAuthUserEmail) }
            ) {
                override suspend fun handleCacheSuccess(response: AccountProperties): DataState<AuthViewState>? {
                    return if (response.primaryKey > -1) {
                        authTokenDao.searchByPrimaryKey(response.primaryKey)?.let { authToken ->
                            DataState.SUCCESS(
                                AuthViewState(
                                    authToken = AuthTokenMapper.toAuthTokenDomain(
                                        authToken
                                    )
                                )
                            )
                        }
                    } else {
                        return buildDialogError(UNKNOWN_ERROR)
                    }
                }
            }.result)

        } else {
            emit(
                DataState.ERROR<AuthViewState>(
                    StateMessage(
                        message = "Logged Out user",
                        uiComponentType = UIComponentType.NONE,
                        messageType = MessageType.ERROR
                    )
                )
            )
        }
    }
}

