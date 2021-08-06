package com.blogapp.ui.auth

import androidx.lifecycle.LiveData
import com.blogapp.ui.base.BaseViewModel
import com.blogapp.ui.auth.state.AuthStateEvent
import com.blogapp.ui.auth.state.AuthStateEvent.*
import com.domain.models.AuthTokenDomain
import com.domain.usecases.CheckPreviousAuthUserUseCase
import com.domain.usecases.LoginUseCase
import com.domain.usecases.RegisterUseCase
import com.domain.utils.DataState
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


    override fun initNewViewState(): AuthViewState = AuthViewState()
}