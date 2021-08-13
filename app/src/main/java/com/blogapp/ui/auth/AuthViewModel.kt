package com.blogapp.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import com.blogapp.ui.base.BaseViewModel
import com.blogapp.ui.auth.state.AuthStateEvent
import com.blogapp.ui.auth.state.AuthStateEvent.*
import com.domain.models.AuthTokenDomain
import com.domain.usecases.auth.CheckPreviousAuthUserUseCase
import com.domain.usecases.auth.LoginUseCase
import com.domain.usecases.auth.RegisterUseCase
import com.domain.utils.AbsentLiveData
import com.domain.utils.DataState
import com.domain.utils.Loading
import com.domain.viewState.AuthViewState
import com.domain.viewState.LoginFields
import com.domain.viewState.RegistrationFields
import javax.inject.Inject


class AuthViewModel @Inject constructor(
    private val login: LoginUseCase,
    private val register: RegisterUseCase,
    private val checkPrevAuthUser: CheckPreviousAuthUserUseCase
) : BaseViewModel<AuthStateEvent, AuthViewState>() {

    override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
        return when (stateEvent) {
            is LoginEvent -> {
                login.invoke(stateEvent.email, stateEvent.password)
            }
            is RegisterEvent -> {
                register.invoke(
                    stateEvent.email,
                    stateEvent.username,
                    stateEvent.password,
                    stateEvent.confirmPassword
                )
            }
            is CheckPreviousAuthEvent -> {
                checkPrevAuthUser.invoke()
            }
            is None -> {
                object : LiveData<DataState<AuthViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        value = DataState(error = null, loading = Loading(false), data = null)
                    }
                }
            }
        }
    }

    fun setRegistrationFields(regFields: RegistrationFields) {
        val update = getCurrentViewStateOrNew()
        if (update.registrationFields == regFields) {
            return
        }
        update.registrationFields = regFields
        _viewState.value = update
    }

    fun setLoginFields(loginFields: LoginFields) {
        val update = getCurrentViewStateOrNew()
        if (update.LoginFields == loginFields) {
            return
        }
        update.LoginFields = loginFields
        _viewState.value = update
    }

    fun setAuthToken(authToken: AuthTokenDomain) {
        val update = getCurrentViewStateOrNew()
        if (update.authToken == authToken as AuthTokenDomain) {
            return
        }
        update.authToken = authToken
        _viewState.value = update
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

    fun handlePendingData(){
        setStateEvent(None)
    }

    fun cancelActiveJobs(){
        handlePendingData()
        Log.d("AppDebug", "AuthViewModel // cancelActiveJobs: cancelling active jobs")
        //repository.canceljob
    }

    override fun initNewViewState(): AuthViewState = AuthViewState()
}